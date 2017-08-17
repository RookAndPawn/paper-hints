package com.rookandpawn.kami.solver;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.j2objc.annotations.AutoreleasePool;

/**
 * Absolute BruteForce search
 */
public class DumbSearch {

  private final Graph graph;
  private final PathSet paths;

  private final int colorWrapNumber;
  private final int colorCount;
  private final int minSolutionLength;
  private final short center;

  private short solutionNode;
  private byte[] solution;
  private int lastSolutionIndex;

  public DumbSearch(Graph graph) {
    this.graph = graph;
    this.paths = new PathSet(graph);
    this.colorCount = graph.getColors().length;
    this.colorWrapNumber = colorCount - 1;
    center = paths.getCenterNode();
    minSolutionLength = paths.getMaxDistanceFrom(center);
  }

  public List<Move> go() {
    return go(minSolutionLength);
  }

  public List<Move> go(int startSolutionLength) {

    int solutionLength = Math.max(startSolutionLength, minSolutionLength);

    if (graph.getActualNodeCount() >= startSolutionLength) {
      int minCenterPathLength = paths.getMaxDistanceFrom(center);

      if (minCenterPathLength > solutionLength) {
        solutionLength = minCenterPathLength;
      }

      if (solutionLength < colorWrapNumber) {
        solutionLength = colorWrapNumber;
      }
    }
    
    boolean found = false;

    for (; !found; solutionLength++) {
      Optional<List<Move>> result = go(solutionLength, (byte)-1);

      if (result.isPresent()) {
        return result.get();
      }
    }

    return null;
  }

  public Optional<List<Move>> go(int solutionLength, byte finalColor) {

    if (solutionLength < minSolutionLength) {
      return Optional.absent();
    }

    // Handle graphs with a single node
    if (graph.getActualNodeCount() == 1) {
      if (finalColor < 0) {
        return Optional.of(Collections.EMPTY_LIST);
      }

      for (short n = 0; n < graph.getNodes().length; n++) {
        byte color = graph.getNodes()[n];
        if (color < 0) {
          continue;
        }

        if (color != finalColor) {
          return solutionLength == 0
              ? Optional.absent()
              : Optional.of(ImmutableList.of(graph.createMove(n, finalColor)));
        }

        return Optional.of(Collections.EMPTY_LIST);
      }
    }

    Queue<Short> q = Queues.newArrayDeque();
    Set<Short> seen = Sets.newHashSet();

    boolean found = false;

    System.out.println("Search Length = " + solutionLength);

    q.add(center);
    seen.clear();

    solution = new byte[solutionLength];
    lastSolutionIndex = solutionLength - 1;

    while (!q.isEmpty()) {

      solutionNode = q.poll();

      if (!seen.add(solutionNode)) {
        continue;
      }

      int minDist = paths.getMaxDistanceFrom(solutionNode);

      if (minDist > solutionLength) {
        continue;
      }

      System.out.println("At node " + solutionNode);

      found = testAllSolutions(solutionNode, finalColor);

      if (found) {
        break;
      }

      for (short n : graph.getEdges()[solutionNode]) {
        q.add(n);
      }
    }

    if (!found) {
      return Optional.absent();
    }

    List<Move> result = Lists.newArrayList();

    byte prevC = graph.getNodes()[solutionNode];

    for (int i = 0; i < solution.length; i++) {
      byte c = solution[i];

      byte cp = (byte)(prevC + c + 1);

      if (cp >= colorCount) {
        cp -= colorCount;
      }

      if (finalColor >= 0 && i == solution.length - 1) {
        cp = finalColor;
      }

      String color = graph.getNameForColor(cp);

      Move m = graph.createMove(solutionNode, color);

      result.add(m);
      prevC = cp;
    }

    return Optional.of(result);
  }

  @AutoreleasePool
  private boolean testAllSolutions(short node, byte finalColor) {
    Arrays.fill(solution, (byte)0);
    FastGraph f = new FastGraph(graph);

    int solutionLength = solution.length;
    int defaultIncrementPos = lastSolutionIndex;

    int incrementAt;

    do {

      //System.out.println("\nTesting " + Arrays.toString(solution));

      int result = f.evaluate(node, solution, finalColor);

      if (result == solutionLength) {
        return true;
      }

      if (result < 0) {
        incrementAt = defaultIncrementPos;
      }
      else {
        incrementAt = result;
      }

    } while(increment(solution, incrementAt));

    return false;
  }

  private boolean increment(byte[] solution, int position) {
    int index;

    //System.out.println("increment at " + position);

    for (index = position + 1; index < solution.length; index++) {
      solution[index] = 0;
    }

    index = position;
    
    while (index >= 0) {
      byte newVal = ++solution[index];

      if (newVal < colorWrapNumber) {
        return true;
      }

      solution[index] = 0;
      index--;
    }

    return false;
  }
}
