package com.rookandpawn.kami.parser;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.j2objc.annotations.AutoreleasePool;
import com.rookandpawn.kami.ui.Color;
import com.rookandpawn.kami.ui.KamiImage;

/**
 * Class that will perform image convolution on a given image and kernel
 */
public class Convoluter {

  private static final int[][] nonNullSearchPath;

  static {
    int maxDist = 10;

    List<int[]> dists = Lists.newArrayList();

    for (int row = -maxDist; row <= maxDist; row++) {
      for (int col = -maxDist; col <= maxDist; col++) {
        dists.add(new int[] {row, col});
      }
    }

    Collections.sort(dists, (a1, a2) -> {
      return (a1[0] * a1[0] + a1[1] * a1[1]) - (a2[0] * a2[0] + a2[1] * a2[1]);
    });

    nonNullSearchPath = dists.toArray(new int[][]{});
  }

  private static Color getClosestNonNullPixel(int row
      , int column
      , int height
      , int width
      , Color[][] pixels
      ) {
    
    for (int[] dp : nonNullSearchPath) {
      int r = row + dp[0];
      int c = column + dp[1];

      if (r >= height) {
        r = height - 1;
      }
      if (r < 0) {
        r = 0;
      }
      if (c >= width) {
        c = width - 1;
      }
      if (c < 0) {
        c = 0;
      }

      Color result = pixels[r][c];

      if (result != null) {
        return result;
      }
    }

    throw new RuntimeException("No non-null pixel within 10 pixels");
  }

  public static KamiImage convolute(KamiImage original, short[][] kernel) {
    int kernelHeight = kernel.length;
    int kernelWidth = kernel[0].length;

    int height = original.getHeight();
    int width = original.getWidth();

    int borderHeight = (kernelHeight / 2);
    int borderWidth =(kernelWidth / 2);

    KamiImage result = new KamiImage(width, height);

    for (int tColumn = 0; tColumn < width; tColumn++) {
      for (@AutoreleasePool int tRow = 0; tRow < height; tRow++) {

        if (original.getPixel(tRow, tColumn) == null) {
          continue;
        }

        short r = 0;
        short g = 0;
        short b = 0;

        for (int kColumn = 0; kColumn < kernelWidth; kColumn++) {

          int sColumn = tColumn + kColumn - borderWidth;

          sColumn = sColumn < 0 ? 0 : sColumn;
          sColumn = sColumn >= width ? width - 1 : sColumn;

          for (int kRow = 0; kRow < kernelHeight; kRow++) {

            int sRow = tRow + kRow - borderHeight;
            sRow = sRow < 0 ? 0 : sRow;
            sRow = sRow >= height ? height - 1 : sRow;

            Color pixel = getClosestNonNullPixel(sRow, sColumn, height, width
                , original.getPixels());

            short k = kernel[kRow][kColumn];

            r += pixel.getR() * k;
            g += pixel.getG() * k;
            b += pixel.getB() * k;

          }
        }

        result.setPixel(tRow, tColumn, new Color(r, g, b));
      }
    }

    return result;
  }



  public static KamiImage convolute(KamiImage original, double[][] kernel) {
    int kernelHeight = kernel.length;
    int kernelWidth = kernel[0].length;

    int height = original.getHeight();
    int width = original.getWidth();

    int borderHeight = (kernelHeight / 2);
    int borderWidth =(kernelWidth / 2);

    KamiImage result = new KamiImage(width, height);

    for (int tColumn = 0; tColumn < width; tColumn++) {
      for (int tRow = 0; tRow < height; tRow++) {

        if (original.getPixel(tRow, tColumn) == null) {
          continue;
        }

        double r = 0;
        double g = 0;
        double b = 0;

        for (int kColumn = 0; kColumn < kernelWidth; kColumn++) {

          int sColumn = tColumn + kColumn - borderWidth;

          sColumn = sColumn < 0 ? 0 : sColumn;
          sColumn = sColumn >= width ? width - 1 : sColumn;

          for (int kRow = 0; kRow < kernelHeight; kRow++) {

            int sRow = tRow + kRow - borderHeight;
            sRow = sRow < 0 ? 0 : sRow;
            sRow = sRow >= height ? height - 1 : sRow;

            Color pixel = getClosestNonNullPixel(sRow, sColumn, height, width
                , original.getPixels());

            double k = kernel[kRow][kColumn];

            r += pixel.getR() * k;
            g += pixel.getG() * k;
            b += pixel.getB() * k;

          }
        }

        result.setPixel(tRow, tColumn
            , new Color((short)r, (short)g, (short)b));
      }
    }

    return result;
  }

}
