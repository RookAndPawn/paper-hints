package com.rookandpawn.kami.parser;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.j2objc.annotations.AutoreleasePool;
import com.rookandpawn.kami.EventPump;
import com.rookandpawn.kami.ui.Color;
import com.rookandpawn.kami.ui.KamiImage;

/**
 *
 */
public class Kami2PuzzleAreaClassifier {

  private static final double _1_sqrt_3 = 1.0 / Math.sqrt(3);
  private static final double sqrt_3_2 = Math.sqrt(3) / 2;
  private static final double tileInsetCoef = 3.0 / 750;

  private static final double nonTileMaxRoughness = .05;
  private static final double maxNonTileSaturationThreshold = 0.3;
  private static final double minNonTileValueThreshold = 0.7;

  private static final int puzzleHeight = 29;
  private static final int puzzleWidth = 10;

  private static enum Line {
    vertical(9, Math.PI / 2)
    , positiveSlope(19, Math.PI / 180 * 30)
    , negativeSlope(19, - Math.PI / 180 * 30)

    ;

    final int count;
    final double angle;

    Line(int count, double angle) {
      this.count = count;
      this.angle = angle;
    }
  }

  public static double getColorValueOffsetForTile(int row, int col) {
    int x = col - puzzleWidth / 2;
    int y = puzzleHeight / 2 - row;

    double xSlope = -0.05 / puzzleWidth;
    double ySlope = -0.05 / puzzleHeight;

    return xSlope * x + ySlope * y;
  }

  public void parsePuzzleArea(PuzzleContext ctx, EventPump pump) {

    pump.setStatus("Classifying Tiles");

    KamiImage workingArea = new KamiImage(ctx.getPlayAreaImage());

    int height = workingArea.getHeight();
    int width = workingArea.getWidth();

    int[][] tileClassification = new int[puzzleHeight][puzzleWidth];

    // This just draws the lines on the grid
    for (Line line : Line.values()) {
      for (int[] coord : getLineCoordinates(line, height, width)) {
        int row = coord[0];
        int col = coord[1];

        if (col >= 0 && col < width) {
          workingArea.setPixel(row, col, Color.WHITE);
        }
      }
    }

    pump.showImage(workingArea);

    int total = puzzleHeight * puzzleWidth - 1;

    for (int row = 0; row < puzzleHeight; row++) {
      for (@AutoreleasePool int col = 0; col < puzzleWidth; col++) {

        pump.setStatus("Classifying Tiles"
            , 1.0 / total * (puzzleWidth * row + col));

        KamiImage tile = getTileImage(ctx.getPlayAreaImage()
            , row, col, height, width);

        if (detectNonTile(tile)) {
          tileClassification[row][col] = -1;
          continue;
        }

        tile = GaussianBlurrer.blur(tile, 3);

        //pump.showImage(tile);

        int classification = ctx.getPalette().getBestMatch(tile
            , getColorValueOffsetForTile(row, col));

        tileClassification[row][col] = classification;
      }
    }
    
    ctx.setTileClassification(tileClassification);
  }

  /**
   * Determine if the given tile image is a non tile meaning its part of the
   * puzzle area but with no color
   * @param tile image to check
   * @return true if the given image is determined to be a non-tile, and false
   *        otherwise
   */
  private boolean detectNonTile(KamiImage tile) {
    Color average = Color.average(tile);

    if (average.getS() > maxNonTileSaturationThreshold) {
      return false;
    }

    if (average.getV() < minNonTileValueThreshold) {
      return false;
    }

    double roughness = EdgeDetector.getAverageRoughness(tile);

    return roughness <= nonTileMaxRoughness;
  }

  /**
   * Get entire region of interest around the tile at the given coordinate with
   * all pixels not inside the tile set to null
   * @param pRow
   * @param pCol
   * @return
   */
  private KamiImage getTileImage(KamiImage playArea
      , int pRow, int pCol, int height, int width) {

    double tileWidth = 1.0 / puzzleWidth * width;
    double tileHeight = tileWidth / sqrt_3_2;

    int colMin = (int)Math.round(pCol * tileWidth);
    int colMax = (int)Math.round((pCol + 1) * tileWidth);

    double tileMidpoint
        = 0.5 * height + (pRow - puzzleHeight / 2) * (tileHeight / 2);

    int rowMin = (int)Math.round(tileMidpoint - tileHeight / 2);
    int rowMax = (int)Math.round(tileMidpoint + tileHeight / 2);

    if (rowMin < 0) {
      rowMin = 0;
    }
    if (rowMax > height) {
      rowMax = height;
    }

    KamiImage result = playArea.getRoi(rowMin, colMin, rowMax, colMax);

    // Now remove any pixels that are not inside the tile

    boolean isLeft = (pCol + (pRow % 2 == 0 ? 0 : 1)) % 2 == 0;

    // Determine the point in the roiImage where the middle point of the
    // triangle falls
    double pointCol = isLeft ? tileWidth : 0;
    double pointRow = tileMidpoint - rowMin;

    double columnInset = tileInsetCoef * width;
    double rowInset = 2 * columnInset; // 2 = 1 / sin(30º)

    for (int col = 0; col < result.getWidth(); col++) {
      int distanceToColBase = isLeft ? col : result.getWidth() - col - 1;

      double r1 = (col - pointCol) * _1_sqrt_3 + pointRow;
      double r2 = - (col - pointCol) * _1_sqrt_3 + pointRow;

      double rMin = Math.min(r1, r2) + rowInset;
      double rMax = Math.max(r1, r2) - rowInset;

      for (int row = 0; row < result.getHeight(); row++) {
        if (distanceToColBase < columnInset
            || row > rMax
            || row < rMin) {
          result.setPixel(row, col, null);
        }
      }
    }

    return result;
  }

  /**
   * Get the formulae for each line of the given type
   * @param line line type
   * @param width puzzle area pixel width
   * @return an iterable of arrays with the invSlope (deltaCol/deltaRow) and
   *        col intercept (col coordinate where row = 0)
   */
  private List<double[]> getLineFormulae(Line line, int width) {

    List<double[]> result = Lists.newArrayList();

    double interceptIncrement
        = Math.abs(1.0 / puzzleWidth * width / Math.sin(line.angle));

    for (int i = (- line.count / 2); i <= line.count / 2; i++) {

      result.add(new double[] {
        Math.abs(line.angle - Math.PI / 2) < 0.00001 
            ? 0
            : (1 / Math.tan(line.angle))
        , i * interceptIncrement
      });
    }

    return result;
  }

  /**
   * Get all the coordinates for the line whose coordinates are given in a play
   * area with the given dimensions
   * @param line
   * @param height
   * @param width
   * @return
   */
  private Iterable<int[]> getLineCoordinates(Line line
      , final int height
      , final int width) {

    List<double[]> formulae = getLineFormulae(line, width);

    Iterator<double[]> fit = formulae.iterator();

    return () -> {
      return new Iterator<int[]>() {
        double[] currFormula;
        int row;

        @Override
        public boolean hasNext() {
          if (fit.hasNext()) {
            return true;
          }

          return row < height;
        }

        @Override
        public int[] next() {
          if (currFormula == null || row >= height) {
            currFormula = fit.next();
            row = 0;
          }

          double r = row - 0.5 * height;
          double c = currFormula[0] * r + currFormula[1];

          return new int[] { row++, (int) Math.round(c + 0.5 * width) };
        }
      };
    };
  }
}
