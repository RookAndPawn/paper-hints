package com.rookandpawn.kami.post;

import java.util.List;

import com.google.common.collect.Lists;
import com.rookandpawn.kami.parser.PuzzleContext;
import com.rookandpawn.kami.ui.Point;

/**
 *
 */
public class Kami2NodeGeometryProvider implements NodeGeometryProvider {

  private static final double sqrt_3_6 = Math.sqrt(3) / 6;
  private static final double sqrt_3_2 = Math.sqrt(3) / 2;
  private static final int puzzleHeight = 29;
  private static final int puzzleWidth = 10;

  private static enum Corner {
    TopLeft
    , TopRight
    , Right
    , Left
    , BottomLeft
    , BottomRight
  }

  @Override
  public NodeGeometry getGeometry(PuzzleContext ctx, short node) {

    int width = ctx.getPlayAreaImage().getWidth();
    int height = ctx.getPlayAreaImage().getHeight();

    double tileWidth = 1.0 / puzzleWidth * width;
    double tileHeight = tileWidth / sqrt_3_2;

    double centroidOffset = tileWidth * sqrt_3_6;

    int rowTotal = 0;
    int colTotal = 0;

    List<Point> tileCenters = Lists.newArrayList();

    VisualGraph.Builder graph = new VisualGraph.Builder();

    for (int row = 0; row < puzzleHeight; row++) {
      for (int col = 0; col < puzzleWidth; col++) {
        if (ctx.isTileInNode(row, col, node)) {

          int colMin = (int)Math.round(col * tileWidth);
          int colMax = (int)Math.round((col + 1) * tileWidth);

          double tileMidpoint
              = 0.5 * height + (row - puzzleHeight / 2) * (tileHeight / 2);

          int rowMin = (int)Math.round(tileMidpoint - tileHeight / 2);
          int rowMax = (int)Math.round(tileMidpoint + tileHeight / 2);

          boolean pointsRight = (col + (row % 2 == 0 ? 0 : 1)) % 2 == 0;

          // Determine the point in the roiImage where the middle point of the
          // triangle falls
          double pointCol = (pointsRight ? tileWidth : 0) + colMin;
          double pointRow = tileMidpoint;

          Point p = new Point(pointRow, pointCol);
          Point t = new Point(rowMin, pointsRight ? colMin : colMax);
          Point b = new Point(rowMax, pointsRight ? colMin : colMax);

          Corner pCorn = pointsRight ? Corner.Right : Corner.Left;
          Corner tCorn = pointsRight ? Corner.TopLeft : Corner.TopRight;
          Corner bCorn = pointsRight ? Corner.BottomLeft : Corner.BottomRight;

          boolean pOnBorder = isOnBorder(row, col, pCorn, ctx, node);
          boolean tOnBorder = isOnBorder(row, col, tCorn, ctx, node);
          boolean bOnBorder = isOnBorder(row, col, bCorn, ctx, node);

          String pId = (row + 1) + "," + (pointsRight ? col + 1 : col);
          String tId = row + "," + (pointsRight ? col : col + 1);
          String bId = (row + 2) + "," + (pointsRight ? col : col + 1);

          if (pOnBorder && tOnBorder) {
            graph.addEdge(pId, p, tId, t
                , pointsRight
                    ? VisualGraph.Direction.NW
                    : VisualGraph.Direction.NE);
          }
          if (pOnBorder && bOnBorder) {
            graph.addEdge(pId, p, bId, b
                , pointsRight
                    ? VisualGraph.Direction.SW
                    : VisualGraph.Direction.SE);
          }
          if (tOnBorder && bOnBorder) {
            graph.addEdge(bId, b, tId, t, VisualGraph.Direction.N);
          }

          double centerCol = tileWidth * col +
              (pointsRight ? centroidOffset : (tileWidth - centroidOffset));

          Point center = new Point(pointRow, centerCol);

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

    switch (corner) {
      case TopLeft: {
        return ! (ctx.isTileInNode(row, col, node)
            && ctx.isTileInNode(row - 1, col, node)
            && ctx.isTileInNode(row - 2, col, node)
            && ctx.isTileInNode(row - 2, col - 1, node)
            && ctx.isTileInNode(row - 1, col - 1, node)
            && ctx.isTileInNode(row, col - 1, node));
      }
      case TopRight: {
        return ! (ctx.isTileInNode(row, col, node)
            && ctx.isTileInNode(row - 1, col, node)
            && ctx.isTileInNode(row - 2, col, node)
            && ctx.isTileInNode(row - 2, col + 1, node)
            && ctx.isTileInNode(row - 1, col + 1, node)
            && ctx.isTileInNode(row, col + 1, node));
      }
      case BottomLeft: {
        return ! (ctx.isTileInNode(row, col, node)
            && ctx.isTileInNode(row + 1, col, node)
            && ctx.isTileInNode(row + 2, col, node)
            && ctx.isTileInNode(row + 2, col - 1, node)
            && ctx.isTileInNode(row + 1, col - 1, node)
            && ctx.isTileInNode(row, col - 1, node));
      }
      case BottomRight: {
        return ! (ctx.isTileInNode(row, col, node)
            && ctx.isTileInNode(row + 1, col, node)
            && ctx.isTileInNode(row + 2, col, node)
            && ctx.isTileInNode(row + 2, col + 1, node)
            && ctx.isTileInNode(row + 1, col + 1, node)
            && ctx.isTileInNode(row, col + 1, node));
      }
      case Left: {
        return ! (ctx.isTileInNode(row, col, node)
            && ctx.isTileInNode(row - 1, col, node)
            && ctx.isTileInNode(row + 1, col, node)
            && ctx.isTileInNode(row + 1, col - 1, node)
            && ctx.isTileInNode(row - 1, col - 1, node)
            && ctx.isTileInNode(row, col - 1, node));
      }
      case Right: {
        return ! (ctx.isTileInNode(row, col, node)
            && ctx.isTileInNode(row - 1, col, node)
            && ctx.isTileInNode(row + 1, col, node)
            && ctx.isTileInNode(row + 1, col + 1, node)
            && ctx.isTileInNode(row - 1, col + 1, node)
            && ctx.isTileInNode(row, col + 1, node));
      }
    }

    throw new IllegalStateException();
  }

  
}
