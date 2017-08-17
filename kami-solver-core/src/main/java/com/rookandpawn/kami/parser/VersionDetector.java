package com.rookandpawn.kami.parser;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.j2objc.annotations.AutoreleasePool;
import com.rookandpawn.kami.EventPump;
import com.rookandpawn.kami.ui.KamiImage;

/**
 * Class to determine the version of a puzzle based on the edges in the play
 * area.
 */
public class VersionDetector {

  /**
   * Multiply this number by the width of the play area to produce the magnitude
   * coefficient
   */
  private static final double magnitudeThresholdFractionOfWidthInv = 0.7 * 750;

  /**
   * Multiply this value by the play area width to get the blur radius
   */
  private static final double blurRadiusCoeff = 3.0 / 750;

  /**
   * Detect the version of the puzzle and set it in the puzzle context
   * @param ctx
   * @param pump
   */
  @AutoreleasePool
  public void detectVersion(PuzzleContext ctx, EventPump pump) {
    pump.setStatus("Detecting Version");

    int height = ctx.getPlayAreaImage().getHeight();
    int width = ctx.getPlayAreaImage().getWidth();

    KamiImage image = ctx.getPlayAreaImage();
    
    EdgeMap edges = EdgeDetector.runSobelDetection(image);

    ctx.setPlayAreaEdgeMap(edges);

    double magnitudeThreshold = magnitudeThresholdFractionOfWidthInv / width;

    pump.setStatus("Calculating Histogram");

    Map<Integer,int[]> histogram = Maps.newHashMap();

    int[] horizontalCounter = new int[] { 0 };
    int[] verticalCounter = new int[] { 0 };
    int[] diagRightCounter = new int[] { 0 };
    int[] diagLeftCounter = new int[] { 0 };

    int[] otherAnglesCounter = new int[] { 0 };

    histogram.put(90, horizontalCounter);
    histogram.put(-90, horizontalCounter);
    histogram.put(0, verticalCounter);
    histogram.put(-180, verticalCounter);
    histogram.put(180, verticalCounter);
    histogram.put(60, diagLeftCounter);
    histogram.put(-120, diagLeftCounter);
    histogram.put(-60, diagRightCounter);
    histogram.put(120, diagRightCounter);

    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        if (edges.getMagnitude(col, row) < magnitudeThreshold) {
          continue;
        }

        double angleInRadians = edges.getDirection(col, row);

        double angleInDegrees = angleInRadians * 180 / Math.PI;

        int roundedAngle = (int)Math.round(angleInDegrees / 10) * 10;

        int[] counter = histogram.get(roundedAngle);

        if (counter == null) {
          counter = otherAnglesCounter;
          histogram.put(roundedAngle, counter);
        }

        counter[0]++;
      }
    }

    if (horizontalCounter[0] > (diagLeftCounter[0] + diagRightCounter[0]) / 2) {
      ctx.setVersion(KamiVersion.VERSION_1);
    }
    else {
      ctx.setVersion(KamiVersion.VERSION_2);
    }
  }
}
