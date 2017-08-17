package com.rookandpawn.kami.parser;

import java.util.Queue;

import com.google.common.collect.Queues;
import com.google.j2objc.annotations.AutoreleasePool;
import com.rookandpawn.kami.ui.Color;
import com.rookandpawn.kami.ui.KamiImage;

/**
 * Perform a convolution to find the edges in the given image
 */
public class EdgeDetector {

  private static final double _1_PI_8 = Math.PI / 8;
  private static final double _3_PI_8 = Math.PI * 3 / 8;
  private static final double _5_PI_8 = Math.PI * 5 / 8;
  private static final double _7_PI_8 = Math.PI * 7 / 8;

  private static final int[][] northSouth = new int[][] { { 0, 1 }, { 0, -1 } };
  private static final int[][] eastWest = new int[][] { { 1, 0 }, { -1, 0 } };
  private static final int[][] ne_sw = new int[][] { { 1, 1 }, { -1, -1 } };
  private static final int[][] nw_se = new int[][] { { -1, 1 }, { 1, -1 } };

  private static final double maxMagnitude = Math.sqrt(255.0 * 255.0 * 2);

  private static final int[][] allNeighbors = new int[][] {
      {-1, 1}
      , {0, 1}
      , {1, 1}
      , {1, 0}
      , {1, -1}
      , {0, -1}
      , {-1, -1}
      , {-1, 0}
  };

  private static final short[][] xSobelKernel = new short[][] {
    {-1, 0, 1}
    , {-2, 0, 2}
    , {-1, 0, 1}
  };

  private static final short[][] ySobelKernel = new short[][] {
    {1, 2, 1}
    , {0, 0, 0}
    , {-1, -2, -1}
  };

  /**
   * return the average of the magnitude of the sobel edge detection across an 
   * image
   * @param image
   * @return 
   */
  public static double getAverageRoughness(KamiImage image) {
    EdgeMap edges = runSobelDetection(image);

    double sum = 0;
    int count = 0;

    for (int col = 0; col < image.getWidth(); col++) {
      for (int row = 0; row < image.getHeight(); row++) {
        if (image.getPixel(row, col) != null) {
          count++;
          sum+= edges.getMagnitude(col, row);
        }
      }
    }

    return sum / count;
  }

  /**
   * Run sobel edge detection on the given image and return an edge map which
   * tells the magnitude of the edge and the orientation at each point
   * @param image
   * @return
   */
  public static EdgeMap runSobelDetection(KamiImage image) {

    int height = image.getHeight();
    int width = image.getWidth();

    EdgeMap result = new EdgeMap(height, width);

    {
      @AutoreleasePool
      KamiImage xSobel = Convoluter.convolute(image, xSobelKernel);
      KamiImage ySobel = Convoluter.convolute(image, ySobelKernel);

      for (int col = 0; col < width; col++) {
        for (@AutoreleasePool int row = 0; row < height; row++) {

          Color xPixel = xSobel.getPixel(row, col);
          Color yPixel = ySobel.getPixel(row, col);

          double[] magnitudeAndDirection
              = getMagnitudeAndDirection(xPixel, yPixel);

          result.setMagnitude(col, row, magnitudeAndDirection[0]);
          result.setDirection(col, row, magnitudeAndDirection[1]);
        }
      }
    }
    return result;
  }

  /**
   * Run Canny edge detection on the given edge map (produced by sobel edge
   * detection) and return a 2d boolean array where each entry represents
   * whether or not a canny edge appears at the given point
   * @param edgeMap sobel edge detecetion result
   * @param magnitudeThreshold magnitude threshold for edges kept in final map
   * @return
   */
  public static boolean[][] runCannyDetection(EdgeMap edgeMap
      , double magnitudeThreshold) {

    int height = edgeMap.getHeight();
    int width = edgeMap.getWidth();
    boolean[][] localMaxima = new boolean[height][width];
    boolean[][] result = new boolean[height][width];

    // go through edge map and determine if each point is a local maximum by
    // looking at the sobel edge info

    for (int row = 0; row < height; row++) {
      for (int col = 0; col <width; col++) {
        double angle = edgeMap.getDirection(col, row);
        int[][] dps = getNeighborLocationDiffs(angle);

        boolean localMax = true;

        for (int[] dp : dps) {
          int dx = dp[0];
          int dy = dp[1];
          int sRow = row + dy;
          int sCol = col + dx;
          sRow = sRow < 0 ? 0 : sRow;
          sRow = sRow >= height ? height - 1 : sRow;
          sCol = sCol < 0 ? 0 : sCol;
          sCol = sCol >= width ? width - 1 : sCol;
          if (edgeMap.getMagnitude(col, row)
              <= edgeMap.getMagnitude(sCol, sRow)) {
            localMax = false;
            break;
          }
        }

        localMaxima[row][col] = localMax;
      }
    }

    Queue<int[]> toSearch = Queues.newArrayDeque();

    // Now go through and include any pixels that have edge magnitudes above
    // the threshold and are local maxima and any pixel that is a local maxima
    // that is connected to a node that has already been included

    for (int row = 0; row < height; row++) {
      for (int col = 0; col <width; col++) {
        if (!localMaxima[row][col]) {
          continue;
        }
        if (edgeMap.getMagnitude(col, row) > magnitudeThreshold) {
          result[row][col] = true;

          toSearch.add(new int[] { row, col });
        }
      }
    }

    while (!toSearch.isEmpty()) {
      int[] curr = toSearch.poll();

      int row = curr[0];
      int col = curr[1];

      for (int[] neighbor : allNeighbors) {
        int nCol = col + neighbor[0];
        int nRow = row + neighbor[1];

        if (nRow < 0
            || nCol < 0
            || nRow >= height
            || nCol >= width) {
          continue;
        }

        if (!localMaxima[nRow][nCol]) {
          continue;
        }

        if (result[nRow][nCol]) {
          continue;
        }

        result[nRow][nCol] = true;

        toSearch.add(new int[] { nRow, nCol });
      }
    }

    return result;
  }

  /**
   * Given an angel between -pi and pi, this method will return an 2d-array of
   * size 2x2 which gives the 2 points that represents the 2 points most inline
   * with the given angle
   * @param angle angle between -pi and pi
   * @return 2 x-y vectors in the form of a 2-d array like:
   *        {{dx1, dy1}, {dx2, dy2}}
   */
  private static int[][] getNeighborLocationDiffs(double angle) {
    int[][] result = new int[2][2];

    if ((angle < _1_PI_8 && angle > - _1_PI_8)
        || angle < - _7_PI_8
        || angle > _7_PI_8) {
      return eastWest;
    }

    if ((angle < _3_PI_8 && angle > _1_PI_8)
        || (angle < - _5_PI_8 && angle > - _7_PI_8)) {
      return ne_sw;
    }

    if ((angle < _5_PI_8 && angle > _3_PI_8)
        || (angle < - _3_PI_8 && angle > - _5_PI_8)) {
      return northSouth;
    }

    return nw_se;
  }

  /**
   * Get the magnitude and direction of the sobel result colors.  The result
   * values will be based on the color change with the biggest magnitude
   * @param xc x color direction
   * @param yc y color direction
   * @return a tuple of the form { magnitude, direction }
   */
  private static double[] getMagnitudeAndDirection(Color xc, Color yc) {

    if (xc == null || yc == null) {
      return new double[] {0, 0};
    }

    double rx = xc.getR();
    double gx = xc.getG();
    double bx = xc.getB();
    double ry = yc.getR();
    double gy = yc.getG();
    double by = yc.getB();

    double max = 0;
    double x = 0;
    double y = 0;
    double curr;

    curr = rx * rx + ry * ry;

    if (curr > max) {
      max = curr;
      x = rx;
      y = ry;
    }

    curr = gx * gx + gy * gy;
    if (curr > max) {
      max = curr;
      x = gx;
      y = gy;
    }

    curr = bx * bx + by * by;
    if (curr > max) {
      max = curr;
      x = bx;
      y = by;
    }

    return new double [] { Math.sqrt(max) / maxMagnitude, Math.atan2(y, x)};
  }

}
