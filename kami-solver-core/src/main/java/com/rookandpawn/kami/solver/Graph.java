package com.rookandpawn.kami.solver;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

/**
 *
 */
public class Graph {


  public static Builder builder() {
    return new Builder();
  }

  private final int actualNodeCount;
  private final int actualColorCount;
  private final byte[] nodes;
  private final short[][] edges;
  private final String[] colors;
  private final short[] colorCounts;
  private final Map<String,Byte> colorsByName;

  private Graph(int actualNodeCount
      , byte[] nodes
      , short[][] edges
      , String[] colors
      , short[] colorCounts
      , int actualColorCount
      , Map<String,Byte> colorByName) {
    this.actualNodeCount = actualNodeCount;
    this.nodes = nodes;
    this.edges = edges;
    this.colors = colors;
    this.actualColorCount = actualColorCount;
    this.colorCounts = colorCounts;
    this.colorsByName = colorByName;
  }

  private Graph(Builder builder) {
    this.actualNodeCount = builder.nodes.size();
    this.actualColorCount = builder.colors.size();

    this.colors = builder.colors.toArray(new String[builder.colors.size()]);

    colorCounts = new short[colors.length];

    colorsByName = Maps.newHashMap();

    for (int i = 0; i < colors.length; i++) {
      colorsByName.put(colors[i], (byte)i);
    }

    this.nodes = new byte[builder.maxId + 1];

    Arrays.fill(nodes, (byte)-1);

    builder.nodes.forEach((id, color) -> {
      byte colorIndex = colorsByName.get(color);
      nodes[id] = colorIndex;
      colorCounts[colorIndex]++;
    });

    this.edges = new short[nodes.length][] ;

    int[] edgesCounter = new int[] { 0 };

    builder.edges.forEach((node, neighbors) -> {
      edgesCounter[0] += neighbors.size();

      short[] neighborsArray = new short[neighbors.size()];
      int index = 0;

      for (Integer neighbor : neighbors) {
        neighborsArray[index++] = neighbor.shortValue();
      }

      edges[node] = neighborsArray;
    });

  }


  public Move createMove(short node, byte color) {
    return new Move(node, getNameForColor(color), color);
  }

  public Move createMove(short node, String color) {
    return new Move(node, color, colorsByName.get(color));
  }

  /**
   * @return the nodes
   */
  public byte[] getNodes() {
    return nodes;
  }

  /**
   * @return the edges
   */
  public short[][] getEdges() {
    return edges;
  }

  /**
   * @return the colors
   */
  public String[] getColors() {
    return colors;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("\nNodes:\n")
        .append(Arrays.toString(nodes))
        .append("\nEdges:\n")
        .append(Arrays.deepToString(edges))
        .toString();
  }

  /**
   * @return a list of all the subgraphs that have no connections to each other
   */
  public List<Graph> getSubGraphs() {
    int[] subGraphMembership = new int[nodes.length];

    Arrays.fill(subGraphMembership, -1);

    int subGraphNum = 0;

    for (short node = 0; node < nodes.length; node++) {
      if (nodes[node] < 0) {
        continue;
      }

      if (subGraphMembership[node] >= 0) {
        continue;
      }

      Queue<Short> queue = Queues.newArrayDeque();
      queue.add(node);

      while (!queue.isEmpty()) {
        short curr = queue.poll();

        if (subGraphMembership[curr] >= 0) {
          continue;
        }

        subGraphMembership[curr] = subGraphNum;

        for (short n : edges[curr]) {
          queue.add(n);
        }
      }

      subGraphNum++;
    }

    int subGraphCount = subGraphNum;

    // If there is only one sub graph, then the only graph is this
    if (subGraphCount == 1) {
      return Lists.newArrayList(this);
    }

    List<Graph> result = Lists.newArrayList();

    for (subGraphNum = 0; subGraphNum < subGraphCount; subGraphNum++) {
      int newActualNodeCount = 0;
      int newActualColorCount = 0;
      byte[] newNodes = new byte[nodes.length];
      short[][] newEdges = new short[edges.length][];
      short[] newColorCounts = new short[colorCounts.length];

      Arrays.fill(newNodes, (byte)-1);

      for (short node = 0; node < nodes.length; node++) {
        byte color = nodes[node];

        if (color < 0) {
          continue;
        }

        if (subGraphMembership[node] != subGraphNum) {
          continue;
        }

        newActualNodeCount++;
        newColorCounts[color]++;
        newNodes[node] = color;
        newEdges[node] = edges[node];
      }

      result.add(new Graph(
          newActualNodeCount
          , newNodes
          , newEdges
          , colors
          , newColorCounts
          , actualColorCount
          , colorsByName));
    }

    return result;
  }

  /**
   * Create a new graph that is the result of changeing the given node to the
   * given color
   * @param nodeId node to change
   * @param color color to change it to
   * @return
   */
  public Graph changeNodeColor(short nodeId, String color) {
    return changeNodeColor(nodeId, colorsByName.get(color));
  }

  /**
   * Create a new graph that is the result of changeing the given node to the 
   * given color
   * @param nodeId node to change
   * @param color color to change it to
   * @return 
   */
  public Graph changeNodeColor(short nodeId, byte color) {
    byte[] newNodes = new byte[nodes.length];

    System.arraycopy(nodes, 0, newNodes, 0, nodes.length);

    newNodes[nodeId] = color;

    short[][] newEdges = new short[edges.length][];

    System.arraycopy(edges, 0, newEdges, 0, edges.length);

    short[] neighbors = edges[nodeId];

    short[] newColorCounts = new short[colorCounts.length];

    System.arraycopy(colorCounts, 0, newColorCounts, 0, colorCounts.length);

    Set<Short> removedNodes = Sets.newHashSet();
    Set<Short> newNeighborsSet = Sets.newHashSet();
    Map<Short,List<Short>> changedNeighbors = Maps.newHashMap();

    byte oldColor = nodes[nodeId];

    newColorCounts[oldColor]--;

    newColorCounts[color]++;

    for (int ndx = 0; ndx < neighbors.length; ndx++) {
      short neighbor = neighbors[ndx];
      byte nColor = nodes[neighbor];

      if (nColor == color) {
        newNodes[neighbor] = -1;
        removedNodes.add(neighbor);
        newColorCounts[color]--;
      }
      else {
        newNeighborsSet.add(neighbor);
      }
    }
    
    for (Short neighbor : removedNodes) {

      newEdges[neighbor] = null;

      for (short nn : edges[neighbor]) {
        if (removedNodes.contains(nn) || nn == nodeId) {
          continue;
        }

        newNeighborsSet.add(nn);

        List<Short> newNeighborNeighbors = Lists.newArrayList(nodeId);

        for (short nnn : edges[nn]) {
          if (!removedNodes.contains(nnn) || nnn == nodeId) {
            newNeighborNeighbors.add(nnn);
          }
        }


        changedNeighbors.put(nn, newNeighborNeighbors);
      }

    }

    short[] newNeighbors = new short[newNeighborsSet.size()];

    newEdges[nodeId] = newNeighbors;

    int mainIndex = 0;

    for (Short n : newNeighborsSet) {
      newNeighbors[mainIndex++] = n;

      List<Short> newNeighborNeighborsList = changedNeighbors.get(n);

      // This happens for existing neighbors that don't change
      if (newNeighborNeighborsList == null) {
        continue;
      }

      short[] newNeighborNeighbors = new short[newNeighborNeighborsList.size()];

      newEdges[n] = newNeighborNeighbors;

      int neighborIndex = 0;

      for (Short nn : newNeighborNeighborsList) {
        newNeighborNeighbors[neighborIndex++] = nn;
      }
    }

    return new Graph(this.actualNodeCount - removedNodes.size()
        , newNodes
        , newEdges
        , colors
        , newColorCounts
        , actualColorCount
        , colorsByName);
  }

  /**
   * @return the acutalNodeCount
   */
  public int getActualNodeCount() {
    return actualNodeCount;
  }


  @Override
  public int hashCode() {
    int hash = 7;
    hash = 23 * hash + this.actualNodeCount;
    hash = 23 * hash + Arrays.hashCode(this.nodes);
    hash = 23 * hash + Arrays.deepHashCode(this.edges);
    hash = 23 * hash
        + Objects.hash(Sets.newTreeSet(Arrays.asList(this.colors)));
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
    final Graph other = (Graph) obj;
    if (this.actualNodeCount != other.actualNodeCount) {
      return false;
    }
    if (!Arrays.equals(this.nodes, other.nodes)) {
      return false;
    }
    if (!Arrays.deepEquals(this.edges, other.edges)) {
      return false;
    }

    Set<String> sColors = Sets.newTreeSet(Arrays.asList(colors));
    Set<String> osColors = Sets.newTreeSet(Arrays.asList(other.colors));

    if (!Objects.equals(sColors, osColors)) {
      return false;
    }
    return true;
  }

  /**
   * @return the actualColorCount
   */
  public int getActualColorCount() {
    return actualColorCount;
  }

  /**
   * Get all the nodes that are within the given distance of the given node
   * @param node starting node
   * @param distance distance
   * @return a set of all nodes within the given distance of the given node
   */
  public Set<Short> getNodesWithin(short node, int distance) {

    Set<Short> result = Sets.newHashSet(node);

    if (distance == 0) {
      return result;
    }

    for (short neighbor : edges[node]) {
      result.addAll(getNodesWithin(neighbor, distance - 1));
    }

    return result;
  }

  public byte[] getColorsForNodes(List<Short> nodeList) {
    byte[] result = new byte[nodeList.size()];

    for (int i = 0; i < result.length; i++) {
      result[i] = nodes[nodeList.get(i)];
    }

    return result;
  }

  public String getNameForColor(byte color) {
    return colors[color];
  }

  /**
   * Mechanism for fluidly building a graph
   */
  public static class Builder {

    private int maxId = Integer.MIN_VALUE;
    private final Map<Integer,String> nodes = Maps.newHashMap();
    private final Map<Integer,Set<Integer>> edges = Maps.newHashMap();
    private final Set<String> colors = Sets.newHashSet();

    private Builder() {}

    public Builder withNode(int id, String color) {
      if (maxId < id) {
        maxId = id;
      }
      nodes.put(id, color);
      edges.put(id, Sets.newHashSet());
      colors.add(color);
      return this;
    }

    /**
     * Add a non-directional edge between the 2 nodes
     * @param id1
     * @param id2
     * @return
     */
    public Builder withEdge(int id1, int id2) {
      edges.get(id2).add(id1);
      edges.get(id1).add(id2);
      return this;
    }


    public Builder withEdges(int id1, int ... neighbors) {
      for (int neighbor : neighbors) {
        withEdge(id1, neighbor);
      }

      return this;
    }

    public Builder withEdges(int id1, Iterable<Integer> neighbors) {
      for (int neighbor : neighbors) {
        withEdge(id1, neighbor);
      }

      return this;
    }

    public Graph build() {
      return new Graph(this);
    }
    
  }

}
