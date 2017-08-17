package com.rookandpawn.kami.solver;

import java.util.Arrays;
import java.util.Collections;
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
 * Representation of a group of shortest paths between all nodes in a graph
 */
public class PathSet {

  
  public static class Path {
    private final short start;
    private final short end;
    private final List<Short> nodes;

    public Path(short start, short end, List<Short> nodes) {
      this.start = start;
      this.end = end;
      this.nodes = nodes;
    }

    @Override
    public int hashCode() {
      int hash = 3;
      hash = 79 * hash + this.start;
      hash = 79 * hash + this.end;
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
      final Path other = (Path) obj;
      if (this.start != other.start) {
        return false;
      }
      if (this.end != other.end) {
        return false;
      }
      return true;
    }

    public int length() {
      return nodes.size() - 1;
    }

    /**
     * @return the start
     */
    public short getStart() {
      return start;
    }

    /**
     * @return the end
     */
    public short getEnd() {
      return end;
    }

    /**
     * @return the edges
     */
    public List<Short> getNodes() {
      return nodes;
    }

    @Override
    public String toString() {
      return new StringBuilder()
          .append(start)
          .append("->")
          .append(end)
          .append(" by ")
          .append(Objects.toString(nodes, ""))
          .toString();
    }

    public Path getRevese() {
      List<Short> newNodes = Lists.newArrayList(this.nodes);
      Collections.reverse(newNodes);
      return new Path(end, start, newNodes);
    }
  }

  private final List<Path> paths;
  private final int maxPathLength;
  private final int minSingleColorMaxPathLength;
  private final short centerNode;

  /**
   * Create a new pathset directly from a graph.  This will do the full
   * calculation of paths, so don't do this very often
   * @param graph
   */
  public PathSet(Graph graph) {
    byte[] nodes = graph.getNodes();
    short[][] edges = graph.getEdges();

    int maxPathLenghtTemp = 0;
    Map<Path,Path> pathsTemp = Maps.newHashMap();

    for (short from = 0; from < nodes.length; from++) {
      if (nodes[from] < 0) {
        continue;
      }
      for (short to = 0; to < from; to++) {
        if (nodes[to] < 0) {
          continue;
        }
        int length = getShortestPath(from, to, edges, pathsTemp);

        if (length > maxPathLenghtTemp && length != Integer.MAX_VALUE) {
          maxPathLenghtTemp = length;
        }
      }
    }

    paths = Lists.newArrayList(pathsTemp.values());
    maxPathLength = maxPathLenghtTemp;

    minSingleColorMaxPathLength
        = determineMinMaxPathLengthForColors(paths, nodes);

    centerNode = determineMostCentralNode(nodes, paths);
  }

  /**
   * Get the length of the longest path from the given node
   * @param node
   * @return 
   */
  public int getMaxDistanceFrom(short node) {
    int maxDistance = -1;

    for (Path path : paths) {
      if (path.start == node) {
        if (maxDistance < path.length()) {
          maxDistance = path.length();
        }
      }
    }

    return maxDistance < 0 ? 0 : maxDistance;
  }

  /**
   * Get the maximum length of min-path starting from the given node
   * @param node
   * @return 
   */
  public int getMaxPathDistanceFrom(short node) {
    if (node < 0) {
      return 100;
    }

    int maxDistance = -1;

    for (Path path : paths) {
      if (path.end == node || path.start == node) {
        if (maxDistance < path.length()) {
          maxDistance = path.length();
        }
      }
    }

    return maxDistance;
  }

  public PathSet(PathSet old, Set<Short> removedNeighbors, short newNode
      , Graph graph) {
    int maxPathLengthTemp = 0;

    paths = Lists.newArrayList();

    for (Path path : old.paths) {
      if (removedNeighbors.contains(path.start)
          || removedNeighbors.contains(path.end)) {
        continue;
      }

      boolean affected = false;

      for (Short neighbor : removedNeighbors) {
        affected = affected || path.nodes.contains(neighbor);
      }

      if (!affected) {
        if (path.getNodes().size() - 1 > maxPathLengthTemp) {
          maxPathLengthTemp = path.getNodes().size() - 1;
        }
        paths.add(path);
        continue;
      }

      List<Short> newPathNodes = Lists.newArrayList(path.nodes);

      newPathNodes.removeAll(removedNeighbors);

      newPathNodes.add(newNode);

      int pathLength = newPathNodes.size() - 1;

      if (pathLength > maxPathLengthTemp) {
        maxPathLengthTemp = pathLength;
      }

      Path newPath = new Path(path.start, path.end, newPathNodes);

      paths.add(newPath);

    }

    maxPathLength = maxPathLengthTemp;
    minSingleColorMaxPathLength
        = determineMinMaxPathLengthForColors(paths, graph.getNodes());

    centerNode = determineMostCentralNode(graph.getNodes(), paths);
  }

  /**
   * Get the shortest path between the given nodes in the given set of edges
   * @param from
   * @param to
   * @param edges
   */
  private int getShortestPath(short from, short to, short[][] edges
      , Map<Path,Path> pathMap) {

    Queue<Path> q = Queues.newArrayDeque();
    Set<Short> seen = Sets.newHashSet();

    Path existingPath = pathMap.get(new Path(from, to, null));

    if (existingPath != null) {
      return existingPath.nodes.size() - 1;
    }

    List<Short> initPathNodes = Lists.newArrayList();

    initPathNodes.add(from);

    q.add(new Path(from, from, initPathNodes));

    while (!q.isEmpty()) {
      Path curr = q.poll();

      if (!seen.add(curr.end)) {
        continue;
      }

      if (curr.end != curr.start) {
        pathMap.put(curr, curr);
        Path rev = curr.getRevese();
        pathMap.put(rev, rev);
      }

      if (curr.end == to) {
        return curr.nodes.size() - 1;
      }

      for (short neighbor : edges[curr.end]) {
        List<Short> nextPathEdges = Lists.newArrayList(curr.nodes);
        nextPathEdges.add(neighbor);
        Path next = new Path(from, neighbor, nextPathEdges);

        q.add(next);
      }

    }

    return Integer.MAX_VALUE;

  }

  /**
   * Get all the shortest paths originating at the given node
   * @param node
   * @return 
   */
  public List<List<Short>> getAllShortestPathsFrom(short node) {
    List<List<Short>> result = Lists.newArrayList();

    for (Path path : paths) {
      if (path.start == node) {
        result.add(path.nodes.subList(1, path.nodes.size()));
      }
    }

    return result;
  }


  private short determineMostCentralNode(byte[] nodes, Iterable<Path> paths) {
    int[] maxPathLengths = new int[nodes.length];

    Arrays.fill(maxPathLengths, -1);

    for (Path path : paths) {
      if (maxPathLengths[path.start] < path.length()) {
        maxPathLengths[path.start] = path.length();
      }
      if (maxPathLengths[path.end] < path.length()) {
        maxPathLengths[path.end] = path.length();
      }
    }

    short result = -1;
    int minPathLength = Integer.MAX_VALUE;

    for (short i = 0; i < maxPathLengths.length; i++) {
      if (maxPathLengths[i] < minPathLength && maxPathLengths[i] > 0) {
        result = i;
        minPathLength = maxPathLengths[i];
      }
    }

    if (result < 0) {
      for (short n = 0; n < nodes.length; n++) {
        if (nodes[n] >= 0) {
          return n; // This will happen if there are no edges
        }
      }
    }

    return result;
  }

  private int determineMinMaxPathLengthForColors(
      Iterable<Path> paths
      , byte[] colors) {
    int[] maxPathLengths = new int[colors.length];

    for (Path path : paths) {
      if (colors[path.end] != colors[path.start]) {
        continue;
      }

      if (path.getNodes().size() - 1 > maxPathLengths[colors[path.end]]) {
        maxPathLengths[colors[path.end]] = path.getNodes().size() - 1;
      }
    }

    int minMaxPathLength = Integer.MAX_VALUE;

    for (int colorMaxPathLength : maxPathLengths) {
      if (minMaxPathLength > colorMaxPathLength
          && colorMaxPathLength > 0) {
        minMaxPathLength = colorMaxPathLength;
      }
    }

    return minMaxPathLength;
  }

  /**
   * @return the maxPathLength
   */
  public int getMaxPathLength() {
    return maxPathLength;
  }

  /**
   * @return the minSingleColorMaxPathLength
   */
  public int getMinSingleColorMaxPathLength() {
    return minSingleColorMaxPathLength;
  }

  /**
   * @return the centerNode
   */
  public short getCenterNode() {
    return centerNode;
  }

}
