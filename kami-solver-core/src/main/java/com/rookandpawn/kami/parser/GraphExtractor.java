package com.rookandpawn.kami.parser;

import java.util.Queue;

import com.google.common.collect.Lists;
import com.rookandpawn.kami.EventPump;
import com.rookandpawn.kami.solver.Graph;

/**
 * Given a puzzle context with a valid palette and tile classification, this
 * class will extract the kami puzzle graph that can be used by the solver
 */
public class GraphExtractor {

  private final ConnectivityProvider kami1ConnectivityProvider
      = new Kami1ConnectivityProvider();

  private final ConnectivityProvider kami2ConnectivityProvider
      = new Kami2ConnectivityProvider();

  public void extractGraph(PuzzleContext ctx, EventPump pump) {
    ConnectivityProvider connectivityProvider =
        ctx.getVersion() == KamiVersion.VERSION_1
        ? kami1ConnectivityProvider
        : kami2ConnectivityProvider;

    int[][] classifications = ctx.getTileClassification();

    if (connectivityProvider == null || classifications == null) {
      return;
    }

    pump.setStatus("Extracting Graph");

    extractGraph(classifications, connectivityProvider, ctx);
  }

  /**
   * 
   * @param classifications
   * @param neighborProvider
   * @param ctx
   * @param pump
   */
  private void extractGraph(int[][] classifications
      , ConnectivityProvider neighborProvider
      , PuzzleContext ctx) {

    int puzzleHeight = ctx.getVersion() == KamiVersion.VERSION_1 ? 16 : 29;
    int puzzleWidth = 10;

    Integer[][] memberships = new Integer[puzzleHeight][puzzleWidth];

    Graph.Builder graph = Graph.builder();

    int nextShape = 1;

    // Identify the shape/node for each tile in the puzzle
    for (int row = 0; row < puzzleHeight; row++) {
      for (int col = 0; col < puzzleWidth; col++) {
        int color = classifications[row][col];
        Integer membership = memberships[row][col];

        // If there is no color or if the tile already has a membership, then
        // skip this tile
        if (color < 0 || membership != null) {
          continue;
        }

        int currShape = nextShape++;

        findShapeContaining(new int[] {row, col}
            , color
            , classifications
            , memberships
            , neighborProvider
            , currShape);

        graph.withNode(currShape, Integer.toString(color));
      }
    }

    // Now identify the connections between the shapes

    for (int row = 0; row < puzzleHeight; row++) {
      for (int col = 0; col < puzzleWidth; col++) {
        Integer membership = memberships[row][col];

        if (membership == null) {
          continue;
        }

        for (int[] neighbor : neighborProvider.getNeighboringTiles(row, col)) {
          int nRow = neighbor[0];
          int nCol = neighbor[1];
          Integer nMembership = memberships[nRow][nCol];

          if (nMembership == null) {
            continue;
          }

          if (nMembership.equals(membership)) {
            continue;
          }

          graph.withEdge(membership, nMembership);
        }
      }
    }

    ctx.setGraph(graph.build());
    ctx.setMemberships(memberships);
  }

  /**
   * Staring from the coordinate given, find all the connected tiles that have
   * the same color and mark them as having membership in the given shape
   * @param coordinate
   * @param colorToMatch
   * @param classifications
   * @param memberships
   * @param neighborProvider
   * @param shapeId
   */
  private void findShapeContaining(int[] coordinate
      , int colorToMatch
      , int[][] classifications
      , Integer[][] memberships
      , ConnectivityProvider neighborProvider
      , int shapeId) {

    Queue<int[]> toSearch = Lists.newLinkedList();
    toSearch.add(coordinate);

    while (!toSearch.isEmpty()) {
      int[] curr = toSearch.poll();

      int row = curr[0];
      int col = curr[1];
      int color = classifications[row][col];

      if (color != colorToMatch) {
        continue;
      }

      if (memberships[row][col] != null) {
        continue;
      }

      memberships[row][col] = shapeId;

      toSearch.addAll(neighborProvider.getNeighboringTiles(row, col));
    }
  }
}
