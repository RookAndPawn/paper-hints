package com.rookandpawn.kami.parser;

import java.util.Map;

import com.google.common.collect.Maps;
import com.rookandpawn.kami.ui.KamiImage;

/**
 * Mechanism for blurring an image.
 */
public class GaussianBlurrer {

  private static final Map<Integer,double[][]> kernels = Maps.newHashMap();

  /**
   * @param radius radius that is guaranteed to be odd
   * @return the  blur kernel for the given radius
   */
  private static double[][] generateKernel(int radius) {

    int size = radius * 2 - 1;

    double sigma = 0.5 * radius - 1;
    double sigma_2 = sigma * sigma;

    double[][] result = new double[size][size];

    double sum = 0;

    for (int row = 0; row < size; row++) {
      int y = row - radius + 1;

      for (int col = 0; col < size; col++) {
        int x = col - radius + 1;

        double val = Math.exp(- (x * x + y * y) / (2.0 * sigma_2));
        sum += val;
        result[row][col] = val;
      }
    }

    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        result[row][col] /= sum;
      }
    }

    return result;
  }

  /**
   * @param rawRadius
   * @return the blur kernel for the given radius
   */
  private static double[][] getKernel(int rawRadius) {

    // Round up for even radii
    int radius = rawRadius % 2 == 0 ? rawRadius + 1 : rawRadius;

    double[][] result = kernels.get(radius);

    if (result == null) {
      result = generateKernel(radius);
      kernels.put(radius, result);
    }

    return result;
  }

  /**
   * Blur the given image with the given radius.  A sigma of radius / 2 + 1 is
   * always used
   * @param toBlur image to blur
   * @param radius raidus of the blur kernel
   * @return the blurred image
   */
  public static KamiImage blur(KamiImage toBlur, int radius) {
    return Convoluter.convolute(toBlur, getKernel(radius));
  }

}
