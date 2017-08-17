package com.rookandpawn.kami.solver;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.j2objc.annotations.AutoreleasePool;

/**
 *
 */
public class Search {

  private static class Point {
    
    public final byte finalColor;
    public final int graphNum;

    public Point(byte finalColor, int graphNum) {
      this.finalColor = finalColor;
      this.graphNum = graphNum;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 67 * hash + this.finalColor;
      hash = 67 * hash + this.graphNum;
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
      final Point other = (Point) obj;
      if (this.finalColor != other.finalColor) {
        return false;
      }
      if (this.graphNum != other.graphNum) {
        return false;
      }
      return true;
    }
  }

  /**
   * Find all the nodes in the given graph where changing their color before 
   * running the dumb search might improve the solution
   * @param graph
   * @return 
   */
  private static List<Move> getAllowedExtraMoveNodes(Graph graph) {

    List<Move> result = Lists.newArrayList();
    Map<Move,Short> moveCounts = Maps.newHashMap();

    for (short node = 0; node < graph.getNodes().length; node++) {
      if (graph.getNodes()[node] < 0) {
        continue;
      }

      short[] colorCount = new short[graph.getActualColorCount()];

      for (short neighbor : graph.getEdges()[node]) {
        colorCount[graph.getNodes()[neighbor]]++;
      }

      for (byte color = 0; color < colorCount.length; color++) {
        short count = colorCount[color];
        if (count >= 2) {
          Move move = graph.createMove(node, color);
          result.add(move);
          moveCounts.put(move, count);
        }
      }
    }

    Collections.sort(result, (m1, m2) -> {
      return moveCounts.get(m2) - moveCounts.get(m1);
    });

    return result;
  }

  private final Map<Point,List<Move>> bestSolutions = Maps.newHashMap();
  private final Graph originalGraph;

  public Search(Graph originalGraph) {
    this.originalGraph = originalGraph;
  }

  public Optional<List<Move>> go(int rawMoveCount) {
    List<Graph> subGraphs = originalGraph.getSubGraphs();

    if (subGraphs.size() == 1) {
      return solveConnectedGraph(originalGraph, rawMoveCount);
    }

    // Sort subgraphs by size
    Collections.sort(subGraphs, (g1, g2) -> {
      return g1.getActualNodeCount() - g2.getActualNodeCount();
    });

    int extraMoveCount = 0;


    // With multiple subgraphs we have to decide the final color ahead of time,
    // so here we are iterating over all colors to use as the last color
    while (extraMoveCount < rawMoveCount) {

      System.out.println("Searching for Solutions with " + extraMoveCount
          + " extra moves");

      for (byte finalColor = 0
          ; finalColor < originalGraph.getColors().length
          ; finalColor++) {

        System.out.println("Searching for solutions that end in " + finalColor);

        int movesLeft = rawMoveCount;
        List<Move> moves = Lists.newArrayList();

        int subGraphNum = 0;
        boolean allSolutionsFound = true;

        for (Graph graph : subGraphs) {

          System.out.println("Solving subgraph: " + subGraphNum);

          List<Move> currSolution = getCurrentBestSolution(
              subGraphNum, finalColor);


          int maxSearchMoves = currSolution != null
              ? currSolution.size() - 1
              : movesLeft;

          for (int solutionLength = extraMoveCount
              ; solutionLength <= maxSearchMoves
              ; solutionLength++) {
            Optional<List<Move>> currResult = solveConnectedGraph(graph
                , solutionLength, finalColor, extraMoveCount);

            if (currResult.isPresent()) {

              currSolution = storeIfBestSolution(subGraphNum
                  , finalColor, currResult.get());

              break;
            }
          }

          if (currSolution == null) {
            allSolutionsFound = false;
            break;
          }

          moves.addAll(currSolution);
          movesLeft -= currSolution.size();

          subGraphNum++;
        }

        if (allSolutionsFound && moves.size() <= rawMoveCount) {
          return Optional.of(moves);
        }
      }

      extraMoveCount++;
    }
    
    return Optional.absent();
  }


  private List<Move> getCurrentBestSolution(
      int graphNum, byte finalColor) {
    return bestSolutions.get(new Point(finalColor, graphNum));
  }

  private List<Move> storeIfBestSolution(int graphNum, byte finalColor
      , List<Move> solution) {
    Point key = new Point(finalColor, graphNum);

    List<Move> currSolution = bestSolutions.get(key);

    if (currSolution == null || solution.size() < currSolution.size()) {
      bestSolutions.put(key, solution);
      return solution;
    }

    return currSolution;
  }

  private Optional<List<Move>> solveConnectedGraph(
      Graph connectedGraph
      , int rawMoveCount) {

    for (int extraMoveCount = 0
        ; extraMoveCount < rawMoveCount
        ; extraMoveCount++) {

      Optional<List<Move>> result = solveConnectedGraph(connectedGraph
        , rawMoveCount, (byte)-1, extraMoveCount);

      if (result.isPresent()) {
        return result;
      }
    }

    return Optional.absent();
  }


  private Optional<List<Move>> solveConnectedGraph(Graph connectedGraph
      , int rawMoveCount
      , byte finalColor
      , int extraMoveCount) {
    
    int moveCount = rawMoveCount;
    int dumbMoveCount = moveCount - extraMoveCount;

    Optional<List<Move>> result = forEachGraphWithExtraMoves(
        connectedGraph
        , extraMoveCount
        , dumbMoveCount
        , finalColor);

    if (result.isPresent()) {
      return result;
    }


    return Optional.absent();
  }

  /**
   * This method will run the solver on every combination of the given number of
   * potential extra moves
   * @param extraMoveCount number of extra moves
   * @param solver function that takes in a graph and returns a solution or
   *        absent
   * @return a solution or absent
   */
  private Optional<List<Move>> forEachGraphWithExtraMoves(Graph graph
      , int extraMoveCount
      , int dumbMoveCount
      , byte finalColor) {

    if (extraMoveCount == 0) {
      return new DumbSearch(graph).go(dumbMoveCount, finalColor);
    }

    return forEachGraphWithExtraMovesHelper(extraMoveCount
        , Collections.EMPTY_LIST
        , graph
        , dumbMoveCount
        , finalColor);
  }

  /**
   * Recursive helper function for apply thing the solver to all the graphs with
   * every combination of the given number of extra moves applied
   * @param extraMoveCount extra move count
   * @param extraMoves extra moves already applied
   * @param graph graph to apply current extra moves to
   * @param solver function that takes in a graph and returns a solution or
   *        absent
   * @return a solution or absent
   */
  private Optional<List<Move>> forEachGraphWithExtraMovesHelper(
      int extraMoveCount
      , List<Move> extraMoves
      , Graph graph
      , int dumbMoveCount
      , byte finalColor) {

    for (@AutoreleasePool Move extraMove : getAllowedExtraMoveNodes(graph)) {
      List<Move> newExtraMoves = Lists.newArrayList(extraMoves);
      newExtraMoves.add(extraMove);

      Graph newGraph = graph.changeNodeColor(extraMove.getNode()
          , extraMove.getColorIndex());

      if (extraMoveCount == 1) {
        Optional<List<Move>> dumbResult 
            = new DumbSearch(newGraph).go(dumbMoveCount, finalColor);

        if (dumbResult.isPresent()) {
          List<Move> result = Lists.newArrayList(newExtraMoves);
          result.addAll(dumbResult.get());

          return Optional.of(result);
        }
      }
      else {
        Optional<List<Move>> result = forEachGraphWithExtraMovesHelper(
            extraMoveCount - 1
            , newExtraMoves
            , newGraph
            , dumbMoveCount
            , finalColor);

        if (result.isPresent()) {
          return result;
        }
      }
    }

    return Optional.absent();
  }
}
