package com.rookandpawn.kami.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.rookandpawn.kami.ui.Color;
import com.rookandpawn.kami.ui.KamiImage;

/**
 * Static class implementation of K-Means clustering for colors in an image
 */
public class KMeansClusterer {

  private static final Random random = new Random();

  /**
   * Cluster the colors in the given image into k groups
   * @param image to analyze
   * @param k number of partitions
   * @return an array of length k with the results of the classification
   */
  public static ColorCharacteristic[] cluster(KamiImage image, int k) {

    ArrayList<Color>[] partitions = new ArrayList[k];

    for (int i = 0; i < k; i++) {
      partitions[i] = Lists.newArrayList();
    }

    Set<Color> randomStart = initializeColors(k);

    Color[] newMeans = new Color[k];

    int index = 0;
    for (Color color : randomStart) {
      newMeans[index++] = color;
    }

    for (; index < k; index++) {
      newMeans[index] = newMeans[index - 1];
    }

    Color[] oldMeans;

    do {
      oldMeans = newMeans;
      for (List<Color> partition : partitions) {
        partition.clear();
      }
      newMeans = iterate(image, oldMeans, partitions);
    } while (!Arrays.equals(newMeans, oldMeans));

    ColorCharacteristic[] result = new ColorCharacteristic[k];

    for (int i = 0; i < k; i++) {
      result[i] = new ColorCharacteristic(newMeans[i], partitions[i]);
    }

    return result;
  }

  private static Color[] iterate(KamiImage image
      , Color[] means
      , List<Color>[] partitions) {

    int k = means.length;
    Color[] result = new Color[k];

    image.foreachPixel((row, col, color) -> {
      if (color == null) {
        return;
      }

      double minDistance = Double.MAX_VALUE;
      int partition = -1;

      for (int i = 0; i < k; i++) {
        double dSquared = color.getRgbDistanceSquaredTo(means[i]);

        if (dSquared < minDistance) {
          minDistance = dSquared;
          partition = i;
        }
      }

      partitions[partition].add(color);
    });

    for (int i = 0; i < k; i++) {
      if (partitions[i].isEmpty()) {
        result[i] = Color.average(Arrays.asList(means));
      }
      else {
        result[i] = Color.average(partitions[i]);
      }
    }

    return result;
  }

  /**
   * Randomly initialize the means based on a random partitioning of the colors
   * @param image image whose colors to partition
   * @param k number of means
   * @return randomly generated means
   */
  private static Set<Color> initializeColors(int k) {
    Set<Color> result = Sets.newHashSet();

    for (int i = 0; i < k; i++) {
      result.add(new Color(360.0 / k * i, .5, .5));
    }

    return result;
  }
}
