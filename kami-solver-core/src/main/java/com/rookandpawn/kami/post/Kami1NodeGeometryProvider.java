package com.rookandpawn.kami.post;

import java.util.List;

import com.google.common.collect.Lists;
import com.rookandpawn.kami.parser.PuzzleContext;
import com.rookandpawn.kami.ui.Point;

/**
 *
 */
public class Kami1NodeGeometryProvider implements NodeGeometryProvider {

  private static final int puzzleHeight = 16;
  private static final int puzzleWidth = 10;

  private static enum Corner {
    TopLeft
    , TopRight
    , BottomLeft
    , BottomRight
  }

  @Override
  public NodeGeometry getGeometry(PuzzleContext ctx, short node) {
    
    int width = ctx.getPlayAreaImage().getWidth();
    int height = ctx.getPlayAreaImage().getHeight();

    double tileHeight = 1.0 / puzzleHeight * height;
    double tileWidth = 1.0 / puzzleWidth * width;

    int rowTotal = 0;
    int colTotal = 0;

    List<Point> tileCenters = Lists.newArrayList();

    VisualGraph.Builder graph = new VisualGraph.Builder();

    for (int row = 0; row < puzzleHeight; row++) {
      for (int col = 0; col < puzzleWidth; col++) {
        if (ctx.isTileInNode(row, col, node)) {
          Point tl = new Point(tileHeight * (row), tileWidth * (col));
          Point tr = new Point(tileHeight * (row), tileWidth * (col + 1));
          Point bl = new Point(tileHeight * (row + 1), tileWidth * (col));
          Point br = new Point(tileHeight * (row + 1), tileWidth * (col + 1));

          boolean tlBorder = isOnBorder(row, col, Corner.TopLeft, ctx, node);
          boolean trBorder = isOnBorder(row, col, Corner.TopRight, ctx, node);
          boolean blBorder = isOnBorder(row, col, Corner.BottomLeft, ctx, node);
          boolean brBorder = isOnBorder(row, col, Corner.BottomRight, ctx, node);

          String tlId = row + "," + col;
          String trId = row + "," + (col + 1);
          String blId = (row + 1) + "," + col;
          String brId = (row + 1) + "," + (col + 1);

          if (tlBorder && trBorder) {
            graph.addEdge(tlId, tl, trId, tr, VisualGraph.Direction.E);
          }
          if (trBorder && brBorder) {
            graph.addEdge(trId, tr, brId, br, VisualGraph.Direction.S);
          }
          if (brBorder && blBorder) {
            graph.addEdge(brId, br, blId, bl, VisualGraph.Direction.W);
          }
          if (blBorder && tlBorder) {
            graph.addEdge(blId, bl, tlId, tl, VisualGraph.Direction.N);
          }

          Point center = new Point((0.5 + row) * tileHeight
              , (0.5 + col) * tileWidth);

          rowTotal += center.getRow();
          colTotal += center.getCol();

          tileCenters.add(center);
        }
      }
    }

    double centroidRow = 1.0 / tileCenters.size() * rowTotal;
    double centroidCol = 1.0 / tileCenters.size() * colTotal;

    double minDist = Double.MAX_VALUE;
    Point center = null;

    for (Point tileCenter : tileCenters) {
      double dr = tileCenter.getRow() - centroidRow;
      double dc = tileCenter.getCol() - centroidCol;
      double dist = dr * dr + dc * dc;

      if (minDist > dist) {
        center = tileCenter;
        minDist = dist;
      }
    }

    return new NodeGeometry(center, graph.build().getBorders());
  }


  private boolean isOnBorder(int row, int col
      , Corner corner
      , PuzzleContext ctx
      , short node) {

    switch(corner) {
      case TopLeft: {
        return ! (ctx.isTileInNode(row, col, node)
            && ctx.isTileInNode(row - 1, col, node)
            && ctx.isTileInNode(row, col - 1, node)
            && ctx.isTileInNode(row - 1, col - 1, node));
      }
      case TopRight: {
        return ! (ctx.isTileInNode(row, col, node)
            && ctx.isTileInNode(row - 1, col, node)
            && ctx.isTileInNode(row, col + 1, node)
            && ctx.isTileInNode(row - 1, col + 1, node));
      }
      case BottomLeft: {
        return ! (ctx.isTileInNode(row, col, node)
            && ctx.isTileInNode(row + 1, col, node)
            && ctx.isTileInNode(row, col - 1, node)
            && ctx.isTileInNode(row + 1, col - 1, node));
      }
      case BottomRight: {
        return ! (ctx.isTileInNode(row, col, node)
            && ctx.isTileInNode(row + 1, col, node)
            && ctx.isTileInNode(row, col + 1, node)
            && ctx.isTileInNode(row + 1, col + 1, node));
      }
    }

    throw new IllegalStateException(); // This won't happen
  }
}
