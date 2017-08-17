
package com.rookandpawn.kami.parser;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.j2objc.annotations.AutoreleasePool;
import com.rookandpawn.kami.EventPump;
import com.rookandpawn.kami.ui.Color;
import com.rookandpawn.kami.ui.KamiImage;

/**
 * Parser that extracts the information about the puzzle form the hud like
 * palette
 */
public class HudParser {

  /**
   * multiply this value by the width of the normalized hud to get the size
   * tolerance for dividers
   */
  private static final double dividerSizeTolerance = 0.01;

  /**
   * Multiply this number by the width of the normalized hud to get the size
   * of the minimum gam between dividers
   */
  private static final double minDividerGapFraction = 10.0 / 750;

  /**
   * Divide this number by the width of the normalized hud to get the canny
   * edge threshold
   */
  private static final double cannyEdgeThresholdFractionWidthInv = 0.6 * 750;

  /**
   * Fraction of vertical space on the normalized hud that must be edge in order
   * to count as a divider
   */
  private static final double dividerLengthThreshold = 0.6;

  /**
   * Edge markers can appear split up in multiple columns, so we can capture
   * edges across a small band.  The width of that band is given by this value
   * multiplied by the width of the normalized hud
   */
  private static final double dividerGlomWidthFraction = 3.0 / 750;

  /**
   * Multiply this number by the width to get the blur radius
   */
  private static final double blurRadiusWidthFraction = 3.0 / 750;

  /**
   * Multiply this value by the width of the normalized hud to tell the max
   * column where a palette divider can occur
   */
  private static final double maxDividerColumnFraction = 0.8;


  private static final double paletteInsetCoefficent = 2.0 / 750;

  /**
   * Determine the pallete used in the given ctx based on the orientation,
   * version, and some edge detection
   * @param ctx puzzle context
   * @param pump sink for events
   */
  @AutoreleasePool
  public void parseHud(PuzzleContext ctx, EventPump pump) {

    KamiImage originalNormalizedHudImage = getNormalizedHudImage(ctx, pump);
    KamiImage normalizedHud = new KamiImage(originalNormalizedHudImage);

    int height = normalizedHud.getHeight();
    int width = normalizedHud.getWidth();

    pump.showImage(normalizedHud);



    pump.setStatus("Detecting Palette");

    double cannyEdgeThreshold = cannyEdgeThresholdFractionWidthInv / width;

    EdgeMap edgeMap = EdgeDetector.runSobelDetection(normalizedHud);
    boolean[][] edges = EdgeDetector.runCannyDetection(edgeMap
        , cannyEdgeThreshold);

    normalizedHud.foreachPixel((row, col, pixel) -> {
      if (edges[row][col]) {
        normalizedHud.setPixel(row, col, Color.WHITE);
      }
    });

    pump.showImage(normalizedHud);

    int thresholdPixels = (int)(height * dividerLengthThreshold);
    List<Integer> dividerColumns = Lists.newArrayList();

    int minDividerGap = (int)Math.round(minDividerGapFraction * width);

    int edgeCaptureWidth = (int)Math.round(dividerGlomWidthFraction * width);

    int maxDividerColumn = (int)Math.round(maxDividerColumnFraction * width);

    // look for columns in the normalized image that have edges that have a
    // length >= the threshold
    for (int col = minDividerGap; col <= maxDividerColumn; col++) {
      int colEdgeCount = 0;

      for (int row = 0; row < height; row++) {

        // Scan the area behind this column for an edge in the capture band
        for (int capDelta = 0; capDelta < edgeCaptureWidth; capDelta++) {
          if (edges[row][col - capDelta]) {
            colEdgeCount++;
            break;
          }
        }
      }

      if (colEdgeCount >= thresholdPixels) {
        // add a column at the approximate center of the capture band
        dividerColumns.add(col - (edgeCaptureWidth - 1) / 2 );

        for (int row = 0; row < height; row++) {
          normalizedHud.setPixel(row, col, Color.RED);
        }

        col += minDividerGap;
      }
    }

    pump.showImage(normalizedHud);

    dividerColumns.add(width - 1);

    parsePalette(dividerColumns, originalNormalizedHudImage, ctx, pump);
  }

  /**
   * Transform the image of the hud in the context so that it is horizontal
   * and has the palette on the left
   * @param ctx puzzle context containing the hud image and info already parsed
   * @param pump sink for events
   * @return an image of the hud that is horizontal and has the palette on the
   *        left
   */
  private KamiImage getNormalizedHudImage(PuzzleContext ctx
      , EventPump pump) {

    KamiImage result;

    if (ctx.getHudOrientation() == HudOrientation.Side) {
      result = ctx.getHudImage().getTranspose();
    }
    else if (ctx.getVersion() == KamiVersion.VERSION_2) {
      result = new KamiImage(ctx.getHudImage());
      result.flipHorizontally();
    }
    else {
      result = new KamiImage(ctx.getHudImage());
    }

    return result;
  }

  /**
   * Takes in an image that has been normalized and transforms it back to its
   * original state
   * @param ctx puzzle context
   * @param toDenormalize image to denormalize
   * @return
   */
  private KamiImage denormalizeImage(PuzzleContext ctx
      , KamiImage toDenormalize) {

    KamiImage result;

    if (ctx.getHudOrientation() == HudOrientation.Side) {
      result = toDenormalize.getTranspose();
    }
    else if (ctx.getVersion() == KamiVersion.VERSION_2) {
      result = new KamiImage(toDenormalize);
      result.flipHorizontally();
    }
    else {
      result = new KamiImage(toDenormalize);
    }

    return result;


  }

  /**
   * Parse the hud and produce a palette from the results
   * @param dividerColumns probable locations of the columns in the hud that
   *        divide the different regions.
   * @param normalizedHudImage normalized image
   * @param ctx
   * @param pump
   */
  private void parsePalette(List<Integer> dividerColumns
      , KamiImage normalizedHudImage
      , PuzzleContext ctx
      , EventPump pump) {

    int height = normalizedHudImage.getHeight();
    int pixelSizeTolerance = (int)(normalizedHudImage.getWidth()
        * dividerSizeTolerance);

    int paletteCount = 0;
    int lastGapSize = -1;
    int lastCol = -1;
    int paletteDivider = -1;

    pump.setStatus("Building Palette");

    int maxDividerColumn
        = (int)Math.round(maxDividerColumnFraction * normalizedHudImage.getWidth());

    for (Integer col : dividerColumns) {
      paletteCount++;

      if (lastCol > 0) {

        int gapSize = col - lastCol;

        if (lastGapSize > 0) {
          if (lastGapSize - gapSize > pixelSizeTolerance
              || col > maxDividerColumn) {
            paletteDivider = lastCol;
            paletteCount--;
            break;
          }
          if (gapSize - lastGapSize > pixelSizeTolerance) {
            paletteDivider = lastCol +  col / paletteCount;
            break;
          }
        }

        lastGapSize = gapSize;
      }

      lastCol = col;
    }

    if (paletteDivider < 0) {
      throw new ParseException("Failed to parse palette");
    }

    Palette.Builder palette = Palette.builder();
    lastCol = 0;

    int paletteSample = 0;

    int paletteInset = (int)Math.round(
        paletteInsetCoefficent * ctx.getRawImage().getWidth());

    int prevDenormSampleWidth = -1;

    for (Integer dividerCol : dividerColumns) {

      if (paletteSample++ >= paletteCount) {
        break;
      }

      int denormSampleWidth = dividerCol - lastCol;

      if (prevDenormSampleWidth > 0
          && denormSampleWidth > 1.1 * prevDenormSampleWidth) {
        dividerCol = prevDenormSampleWidth + lastCol;
      }

      KamiImage sample = GaussianBlurrer.blur(
          normalizedHudImage.getRoi(
              (int)Math.round(0.4 * height) + paletteInset
              , lastCol + paletteInset
              , height - paletteInset
              , dividerCol - paletteInset)
          , 3);

      sample = denormalizeImage(ctx, sample);

      pump.showImage(sample);

      int sampleHeight = sample.getHeight();
      int sampleWidth = sample.getWidth();

      // Only use the borrom 60% of the palette sample
      palette.addSample(sample.getRoi(sampleHeight * 4 / 10, 0
          , sampleHeight, sampleWidth));

      lastCol = dividerCol;
      prevDenormSampleWidth = denormSampleWidth;
      if (lastCol == paletteDivider) {
        break;
      }
    }

    ctx.setPalette(palette.build());
  }
}
