package com.rookandpawn.kami.post;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.rookandpawn.kami.EventPump;
import com.rookandpawn.kami.parser.PuzzleContext;
import com.rookandpawn.kami.solver.Move;
import com.rookandpawn.kami.ui.Point;
import com.rookandpawn.kami.ui.PointList;
import com.rookandpawn.kami.ui.SolutionGeometry;
import com.rookandpawn.kami.ui.SolutionGeometryList;

/**
 *
 */
public class SolutionGeometryMaker {

  private static final double maxLabelWidthFraction = 0.5;
  private static final double paletteTileSizeFraction = 1.0 / 20;
  private static final double labelRadiusExtensionFraction = 0.8 / 10;
  private static final double tileExclusionRadiusFraction = 0.5 / 10;

  private static final List<Double> placementSearchAngles;
  private static final List<Double> placementRadiusMultipliers;

  private static final double tileInsetFraction = 10.0 / 750;

  static {
    ImmutableList.Builder<Double> angleBuilder = ImmutableList.builder();
    ImmutableList.Builder<Double> radiiBuilder = ImmutableList.builder();

    int segmentCount = 36;
    int radiiStops = 10;
    double radiiMax = 5;

    for (int i = 0; i < segmentCount; i++) {
      angleBuilder.add(Math.PI * 2 / segmentCount * i + 3 * Math.PI / 2);
    }

    for (int i = 0; i < radiiStops; i++) {
      radiiBuilder.add(1.0 + radiiMax * (1.0 / radiiStops * i));
    }

    placementSearchAngles = angleBuilder.build();
    placementRadiusMultipliers = radiiBuilder.build();
  }

  public SolutionGeometryList generateSolutionGeometries(PuzzleContext ctx
      , List<Move> solution
      , EventPump pump) {

    SolutionState state = new SolutionState();

    List<SolutionState> solutionStates = Lists.newArrayList();

    for (Move move : solution) {
      state = state.cloneWithNewMove(move);
      solutionStates.add(state);
    }

    List<SolutionGeometry> result = Lists.newArrayList();

    for (SolutionState currState : solutionStates) {
      result.add(computeSolutionGeometry(ctx, currState));
    }
    
    return new SolutionGeometryList(result
        , (int)Math.round(
            paletteTileSizeFraction * ctx.getPlayAreaImage().getWidth()));
  }


  private SolutionGeometry computeSolutionGeometry(PuzzleContext ctx
      , SolutionState state) {

    int height = ctx.getPlayAreaImage().getHeight();
    int width = ctx.getPlayAreaImage().getWidth();

    int maxLabelWidth = (int)Math.round(maxLabelWidthFraction * width);
    int paletteTileSize = (int)Math.round(paletteTileSizeFraction * width);
    int paletteTilePadding = paletteTileSize / 3;
    int maxPaletteTilesInRow = (maxLabelWidth - paletteTilePadding)
        / (paletteTileSize + paletteTilePadding);
    int labelRadiusExtension 
        = (int)Math.round(labelRadiusExtensionFraction * width);

    int tileExlusionRadius
        = (int)Math.round(tileExclusionRadiusFraction * width);

    int labelInset = (int)Math.round(tileInsetFraction * width);

    List<PointList> borders = Lists.newArrayList();
    List<PointList> labelBoxes = Lists.newArrayList();
    List<PointList> tiles = Lists.newArrayList();
    List<PointList> arrows = Lists.newArrayList();
    List<Integer> paletteTileNumbers = Lists.newArrayList();

    List<Rectangle> takenRects = Lists.newArrayList();

    state.getShownMoves().keySet().forEach(node -> {
      NodeGeometry nodeGeometry = ctx.getNodeGeometry(node);

      Rectangle centerRect = Rectangle.withCenter(nodeGeometry.getCenter()
          , tileExlusionRadius, tileExlusionRadius);

      takenRects.add(centerRect);
    });

    state.getShownMoves().forEach((node, moves) -> {
      NodeGeometry nodeGeometry = ctx.getNodeGeometry(node);

      for (List<Point> rawList : nodeGeometry.getBorders()) {
        borders.add(new PointList(rawList));
      }

      int rowCount = (int)Math.round(
          Math.ceil(1.0 / maxPaletteTilesInRow * moves.size()));

      int colCount = moves.size() > maxPaletteTilesInRow
          ? maxPaletteTilesInRow
          : moves.size();

      int labelHeight = rowCount * (paletteTileSize + paletteTilePadding)
          + paletteTilePadding;
      int labelWidth = colCount * (paletteTileSize + paletteTilePadding)
          + paletteTilePadding;

      int labelRadius = (int)Math.round(
          Math.sqrt(labelHeight * labelHeight + labelWidth * labelWidth)) / 2
          + labelRadiusExtension;

      Point center = nodeGeometry.getCenter();
      Rectangle placedLabel = null;

      int minOverlap = Integer.MAX_VALUE;
      Rectangle rectWithLeastOverlap = null;

      for (Double radiusMult : placementRadiusMultipliers) {
        for (Double angle : placementSearchAngles) {
          Rectangle proposed = proposeRectangleAt(center
              , radiusMult * labelRadius
              , angle
              , labelHeight
              , labelWidth);

          int overlap = getTotalOverlap(proposed, takenRects
              , height, width, labelInset);

          if (overlap == 0) {
            placedLabel = proposed;
            break;
          }

          if (overlap < minOverlap) {
            rectWithLeastOverlap = proposed;
            minOverlap = overlap;
          }
        }

        if (placedLabel != null) {
          break;
        }
      }

      if (placedLabel == null) {
        placedLabel = rectWithLeastOverlap;
      }

      if (placedLabel == null) {
        throw new IllegalStateException("Not able to place label");
      }

      takenRects.add(placedLabel);
      labelBoxes.add(placedLabel);

      arrows.add(new Arrow(placedLabel.getCenter(), center));

      int moveNumber = 0;

      Point topLeftOfTiles = placedLabel
          .getTopLeft()
          .incCol(paletteTilePadding)
          .incRow(paletteTilePadding);

      for (Integer move : moves) {
        int row = moveNumber / maxPaletteTilesInRow;
        int col = moveNumber % maxPaletteTilesInRow;

        paletteTileNumbers.add(move);

        Point currTopLeft = topLeftOfTiles
            .incRow((paletteTileSize + paletteTilePadding) * row)
            .incCol((paletteTileSize + paletteTilePadding) * col);

        Rectangle tileRect = new Rectangle(currTopLeft
            , paletteTileSize, paletteTileSize);

        tiles.add(tileRect);

        moveNumber++;
      }
    });

    return new SolutionGeometry(borders
        , labelBoxes
        , tiles
        , paletteTileNumbers
        , arrows);
  }


  private Rectangle proposeRectangleAt(Point origin, double radius
      , double angle, int height, int width) {
    int dx = (int)Math.round(Math.cos(angle) * radius);
    int dy = (int)Math.round(Math.sin(angle) * radius);

    Point center = origin.incCol(dx).incRow(dy);

    return Rectangle.withCenter(center, height, width);
  }

  /**
   * Check for overlaps between the given rectangle and the established
   * rectangles or the border of the puzzle
   * @param proposed
   * @param existingRects
   * @return interference area
   */
  private int getTotalOverlap(Rectangle proposed
      , List<Rectangle> existingRects, int borderHeight, int borderWidth
      , int borderPadding) {

    for (Point p 
        : ImmutableList.of(proposed.getTopLeft(), proposed.getBottomRight())) {
      
      int row = p.getRow();
      int col = p.getCol();

      if (row < borderPadding || row >= borderHeight - borderPadding) {
        return Integer.MAX_VALUE;
      }

      if (col < borderPadding || col >= borderWidth - borderPadding) {
        return Integer.MAX_VALUE;
      }
    }

    int result = 0;

    for (Rectangle existing : existingRects) {
      int overlap = existing.getOverlArea(proposed);

      if (overlap == Integer.MAX_VALUE) {
        return Integer.MAX_VALUE;
      }

      result += overlap;
    }

    return result;
  }
}
