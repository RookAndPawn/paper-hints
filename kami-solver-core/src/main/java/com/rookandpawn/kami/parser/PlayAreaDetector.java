
package com.rookandpawn.kami.parser;

import com.google.j2objc.annotations.AutoreleasePool;
import com.rookandpawn.kami.EventPump;
import com.rookandpawn.kami.ui.Color;
import com.rookandpawn.kami.ui.KamiImage;

/**
 * Class capable of detecting whether a given puzzle is oriented with the hud
 * on the bottom or the hud on the right side and adding the image of the hud
 * and the play area to the context
 */
public class PlayAreaDetector {

  /**
   * coefficient that determines the canny edge threshold for a given scale by
   * multiplying this number by one over width
   */
  private static final double cannyEdgeThresholdFractionOfInvWidth
      = 750.0 * 0.8;

  /** scale factor to produce a hud search margin based on approximate width */
  private static final double hudSearchMarginFractionOfWidth = 4.0 / 750;

  /**
   * Aspect ratios for different game versions that all have the hud on the
   * bottom of the screen
   */
  private static final double[] gameAreaAspectRatiosHob = {
      1024.0 / 640      // kami 1 iphone 5
      , 1030.0 / 640    // kami 2 iphone 5
      , 1200.0 / 750    // kami 1 iphone 7
      , 1210.0 / 750    // kami 2 iphone 7
  };

  /**
   * Aspect ratios for different game versions that all have the hud on the
   * right side of the screen
   */
  private static final double[] gameAreaAspectRatiosHor = {
      2048.0 / 1268      // kami 2 ipad pro 9.7
      , 2048.0 / 1280    // kami 1 ipad pro 9.7
  };

  private static final double minAspectRatio;
  private static final double maxAspectRatio;

  private static final double minAspectRatioHob;
  private static final double maxAspectRatioHob;

  private static final double minAspectRatioHor;
  private static final double maxAspectRatioHor;

  static {
    double tempMax = 0;
    double tempMin = Double.MAX_VALUE;

    for (double aspectRatio : gameAreaAspectRatiosHob) {
      tempMax = tempMax < aspectRatio ? aspectRatio : tempMax;
      tempMin = tempMin > aspectRatio ? aspectRatio : tempMin;
    }

    minAspectRatioHob = tempMin;
    maxAspectRatioHob = tempMax;

    tempMax = 0;
    tempMin = Double.MAX_VALUE;

    for (double aspectRatio : gameAreaAspectRatiosHor) {
      tempMax = tempMax < aspectRatio ? aspectRatio : tempMax;
      tempMin = tempMin > aspectRatio ? aspectRatio : tempMin;
    }

    minAspectRatioHor = tempMin;
    maxAspectRatioHor = tempMax;

    minAspectRatio = minAspectRatioHob < minAspectRatioHor
        ? minAspectRatioHob
        : minAspectRatioHor;

    maxAspectRatio = maxAspectRatioHob > maxAspectRatioHor
        ? maxAspectRatioHob
        : maxAspectRatioHor;
  }

  /**
   * Determine the orientation of the hud in the puzzle image
   * @param ctx
   * @param pump
   */
  public void detectPlayArea(PuzzleContext ctx, EventPump pump) {

    int height = ctx.getRawImage().getHeight();
    int width = ctx.getRawImage().getWidth();

    double aspectRatio = ((double)height) / width;

    if (aspectRatio > maxAspectRatio) {
      ctx.setHudOrientation(HudOrientation.Bottom);
    }
    else if (aspectRatio < minAspectRatio) {
      ctx.setHudOrientation(HudOrientation.Side);
    }
    else {
      throw new ParseException("Unable to determine orientation based on "
          + "aspect ratio " + height + " / " + width + " = " + aspectRatio);
    }

    pump.setStatus("Looking For Board");
    switch (ctx.getHudOrientation()) {
      case Bottom: {
        detectHudAtBottom(ctx, pump);
        break;
      }
      case Side: {
        detectHudOnRight(ctx, pump);
        break;
      }
    }

    pump.showImage(ctx.getPlayAreaImage());
  }

  /**
   * Determine the likelihood of the puzzle's hud being at the bottom of the
   * image
   * @param ctx
   */
  @AutoreleasePool
  private void detectHudAtBottom(PuzzleContext ctx, EventPump pump) {

    KamiImage image = ctx.getRawImage();

    int width = image.getWidth();
    int height = image.getHeight();

    int hudSearchMargin 
        = (int)Math.round(hudSearchMarginFractionOfWidth * width);

    int minHudRow = (int)(minAspectRatioHob * width) - hudSearchMargin;
    int maxHudRow = (int)Math.ceil(maxAspectRatioHob * width) + hudSearchMargin;

    KamiImage hudTop = image.getRoi(minHudRow, 0, maxHudRow, width);

    pump.showImage(hudTop);

    double cannyEdgeThreshold = cannyEdgeThresholdFractionOfInvWidth / width;

    EdgeMap edgeMap = EdgeDetector.runSobelDetection(hudTop);
    boolean[][] edgePixels = EdgeDetector.runCannyDetection(edgeMap,
        cannyEdgeThreshold);


    for (int row = 0; row < hudTop.getHeight(); row++) {
      for (int col = 0; col < hudTop.getWidth(); col++) {
        if (edgePixels[row][col]) {
          hudTop.setPixel(row, col, Color.WHITE);
        }
      }
    }

    pump.showImage(hudTop);

    int maxEdgeRow = 0;
    int maxEdges = 0;

    for (int row = 0; row < hudTop.getHeight(); row++) {

      int currEdgeCount = 0;

      for (int col = 0; col < hudTop.getWidth(); col++) {
        if (edgePixels[row][col]) {
          currEdgeCount++;
        }
      }

      if (currEdgeCount > maxEdges) {
        maxEdges = currEdgeCount;
        maxEdgeRow = row;
      }
    }

    int hudTopRow = minHudRow + maxEdgeRow;

    ctx.setHudImage(ctx.getRawImage().getRoi(
        hudTopRow + 1, 0
        , height, width));

    ctx.setPlayAreaImage(ctx.getRawImage().getRoi(
        0, 0
        , hudTopRow, width));
  }

  /**
   * Determine the likelihood of the puzzle's hud being on the right of the
   * image
   * @param ctx
   */
  @AutoreleasePool
  private void detectHudOnRight(PuzzleContext ctx, EventPump pump) {

    KamiImage image = ctx.getRawImage();
    int height = image.getHeight();
    int width = image.getWidth();

    double approximatePlayAreaWidth = height / minAspectRatioHor;

    int hudSearchMargin = (int)Math.round(hudSearchMarginFractionOfWidth
        * approximatePlayAreaWidth);

    int minHudCol = (int)(1.0 / maxAspectRatioHor * height) - hudSearchMargin;
    int maxHudCol = (int)Math.ceil(
        1.0 / minAspectRatioHor * height) + hudSearchMargin;

    KamiImage hudTop = image.getRoi(0, minHudCol, height, maxHudCol);

    pump.showImage(hudTop);


    double cannyEdgeThreshold
        = cannyEdgeThresholdFractionOfInvWidth / approximatePlayAreaWidth;

    EdgeMap edgeMap = EdgeDetector.runSobelDetection(hudTop);
    boolean[][] edgePixels = EdgeDetector.runCannyDetection(edgeMap,
        cannyEdgeThreshold);

    pump.showImage(hudTop);

    int maxEdgeCol = 0;
    int maxEdges = 0;

    for (int col = 0; col < hudTop.getWidth(); col++) {

      int currEdgeCount = 0;

      for (int row = 0; row < hudTop.getHeight(); row++) {

        if (edgePixels[row][col]) {
          currEdgeCount++;
        }
      }

      if (currEdgeCount > maxEdges) {
        maxEdges = currEdgeCount;
        maxEdgeCol = col;
      }
    }

    int hudLeftCol = minHudCol + maxEdgeCol;

    ctx.setHudImage(ctx.getRawImage().getRoi(
        0, hudLeftCol + 1
        , height, width));

    ctx.setPlayAreaImage(ctx.getRawImage().getRoi(
        0, 0
        , height, hudLeftCol));
  }
}
