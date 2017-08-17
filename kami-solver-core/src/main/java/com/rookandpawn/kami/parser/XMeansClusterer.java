package com.rookandpawn.kami.parser;

import com.rookandpawn.kami.ui.KamiImage;

/**
 * Class for partitioning color samples from an image into groups and
 * determining the best number of clusters
 */
public class XMeansClusterer {


  private static final int maxK = 6;
  private static final double minColorDistance = 100;

  /**
   * Cluster the colors in the given image into k groups
   * @param image to analyze
   * @return an array of length k with the results of the classification
   */
  public static ImageCharacteristic cluster(KamiImage image) {

    int k = 1;
    double prevMinDistance = Double.MAX_VALUE;
    ColorCharacteristic[] prevColorChar = null;
    ColorCharacteristic[] newColorCar = null;

    while (prevMinDistance > minColorDistance && k <= maxK) {
      prevColorChar = newColorCar;
      newColorCar = KMeansClusterer.cluster(image, k++);
      prevMinDistance = getMinimumColorDistance(newColorCar);
    }

    return new ImageCharacteristic(prevColorChar);
  }

  /**
   * Get the minimum distance between any color in the array
   * @param colors
   * @return
   */
  private static double getMinimumColorDistance(ColorCharacteristic[] colors) {
    int k = colors.length;

    double minDistanceSquared = Double.MAX_VALUE;

    for (int i = 1; i < k; i++) {
      for (int j = 0; j < i; j++) {
        double distanceSquared
            = colors[i].getMean().getRgbDistanceSquaredTo(colors[j].getMean());

        if (distanceSquared < minDistanceSquared) {
          minDistanceSquared = distanceSquared;
        }
      }
    }

    return Math.sqrt(minDistanceSquared);
  }
}
