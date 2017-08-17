package com.rookandpawn.kami.post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.rookandpawn.kami.ui.Point;

/**
 * Graph representation that knows about physical aspects of how nodes are
 * connected
 */
public class VisualGraph {

  public static enum Direction implements Comparable<Direction> {
    N
    , NE
    , E
    , SE
    , S
    , SW
    , W
    , NW

    ;

    public Direction opposite() {
      switch (this) {
        case N: return S;
        case NE: return SW;
        case E: return W;
        case SE: return NW;
        case S: return N;
        case SW: return NE;
        case W: return E;
        case NW: return SE;
      }

      throw new RuntimeException(); // This won't happen;
    }

    public Direction next() {
      switch (this) {
        case N: return NE;
        case NE: return E;
        case E: return SE;
        case SE: return S;
        case S: return SW;
        case SW: return W;
        case W: return NW;
        case NW: return N;
      }

      throw new RuntimeException(); // This won't happen;
    }

    public Direction previous() {
      switch (this) {
        case N: return NW;
        case NE: return N;
        case E: return NE;
        case SE: return E;
        case S: return SE;
        case SW: return S;
        case W: return SW;
        case NW: return W;
      }

      throw new RuntimeException(); // This won't happen;
    }
  }


  public static class Node {
    private final Point loc;
    private final Map<Direction,Node> neighbors;

    private final String id;

    public Node(String id, Point loc, Map<Direction, Node> neighbors) {
      this.id = id;
      this.loc = loc;
      this.neighbors = neighbors;
    }

    /**
     * @return the loc
     */
    public Point getLoc() {
      return loc;
    }

    /**
     * @return the neighbors
     */
    public Map<Direction,Node> getNeighbors() {
      return neighbors;
    }


    @Override
    public int hashCode() {
      int hash = 3;
      hash = 89 * hash + Objects.hashCode(this.id);
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Node other = (Node) obj;
      return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
      StringBuilder result = new StringBuilder(loc.toString())
          .append(" :");

      neighbors.forEach((dir, n) -> {
        result.append(" ")
            .append(dir)
            .append("->")
            .append(n.loc);
      });

      return result.toString();
    }
  }

  public static class Edge {
    private final Node n1;
    private final Node n2;

    private final String id;

    public Edge(Node n1, Node n2) {
      this.n1 = n1;
      this.n2 = n2;

      id = (n1.id.compareTo(n2.id) < 0)
          ? (n1.id + "-" + n2.id)
          : (n2.id + "-" + n1.id);

    }

    @Override
    public int hashCode() {
      return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Edge other = (Edge) obj;

      if (n1.equals(other.n1) && n2.equals(other.n2)) {
        return true;
      }

      return n2.equals(other.n1) && n1.equals(other.n2);
    }


  }

  private final Map<String,Node> nodes;

  public VisualGraph(Map<String,Node> nodes) {
    this.nodes = nodes;
  }

  private List<Point> getBorder(Node startingPoint
      , Set<Edge> seenEdges) {

    List<Point> result = Lists.newArrayList();

    Node curr = startingPoint;

    Direction dir = Direction.NW;

    do {
      for (;;) {
        dir = dir.next();
        
        Node next = curr.getNeighbors().get(dir);

        if (next != null && seenEdges.add(new Edge(curr, next))) {
          result.add(curr.getLoc());
          curr = next;
          dir = dir.opposite();
          break;
        }
      }

    } while (!curr.equals(startingPoint));

    return result;
  }


  public List<List<Point>> getBorders() {

    Set<Point> seen = Sets.newHashSet();
    List<List<Point>> result = Lists.newArrayList();

    Set<Edge> seenEdges = Sets.newHashSet();

    for (Node node : nodes.values()) {
      if (seen.contains(node.getLoc())) {
        continue;
      }

      List<Point> border = getBorder(node, seenEdges);

      border.forEach(p -> seen.add(p));


      result.add(border);
    }

    return result;
  }

  public static class Builder {

    private final Map<String,Node> nodes = new HashMap<>();
    private final Set<Edge> edges = Sets.newHashSet();

    public Node addNode(String id, Point location) {
      return nodes.computeIfAbsent(id, (id_) -> {
        return new Node(id_, location, Maps.newHashMap());
      });
    }

    public Builder addEdge(String id1, Point loc1, String id2, Point loc2, Direction dir1To2) {
      Node n1 = addNode(id1, loc1);
      Node n2 = addNode(id2, loc2);

      Edge edge = new Edge(n1, n2);

      if (!edges.add(edge)) {
        n1.getNeighbors().remove(dir1To2);
        n2.getNeighbors().remove(dir1To2.opposite());
        return this;
      }

      n1.getNeighbors().put(dir1To2, n2);
      n2.getNeighbors().put(dir1To2.opposite(), n1);

      return this;
    }

    public VisualGraph build() {
      return new VisualGraph(nodes);
    }
  }

}
