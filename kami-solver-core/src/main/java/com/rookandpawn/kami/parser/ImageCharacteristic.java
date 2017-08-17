package com.rookandpawn.kami.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Distillation of the color information in an image in terms of colors.
 * An image (in the type of images this application deals with) have discrete
 * colors with minor variations like noise and subtly cross-image gradients.
 * We can use this class to make a fingerprint of an image by looking at what
 * colors dominate the image and in what proportions.  This makes comparing
 * image contents easier that checking one entire image against another
 */
public class ImageCharacteristic {

  /** 
   * @param i1 image 1
   * @param i2 image 2
   * @return the distance from 1 to 2
   */
  private double compare(ImageCharacteristic i1, ImageCharacteristic i2) {
    
    Map<ColorCharacteristic,ColorCharacteristic> bestOneWayMatches
        = Maps.newHashMap();

    for (ColorCharacteristic c1 : i1.colors) {
       double minDist = Double.MAX_VALUE;
       ColorCharacteristic bestColor = null;

       for (ColorCharacteristic c2: i2.colors) {
         double dist = c1.getMean().getHsvDistanceSquaredTo(c2.getMean());
         if (minDist > dist) {
           minDist = dist;
           bestColor = c2;
         }
       }

       bestOneWayMatches.put(c1, bestColor);
    }

    Map<ColorCharacteristic,ColorCharacteristic> bestTwoWayMatches
        = Maps.newHashMap();

    for (ColorCharacteristic c2 : i2.colors) {
       double minDist = Double.MAX_VALUE;
       ColorCharacteristic bestColor = null;

       for (ColorCharacteristic c1: i1.colors) {
         double dist = c1.getMean().getHsvDistanceSquaredTo(c2.getMean());
         if (minDist - dist > 0.001) {
           minDist = dist;
           bestColor = c1;
         }
       }

       if (bestOneWayMatches.get(bestColor) == c2) {
         bestTwoWayMatches.put(bestColor, c2);
       }
    }

    double result = 0;

    for (Map.Entry<ColorCharacteristic,ColorCharacteristic> e
        : bestTwoWayMatches.entrySet()) {
      ColorCharacteristic c1 = e.getKey();
      ColorCharacteristic c2 = e.getValue();

      double p1 = ((double)c1.getCount()) / i1.sampleCount;
      double p2 = ((double)c2.getCount()) / i2.sampleCount;

      double dist = c1.getMean().getHsvDistanceSquaredTo(c2.getMean());

      result += Math.max(Math.max(p1 - p2, p2 - p1), 0.01) * dist;
    }

    return result;
  }

  private final List<ColorCharacteristic> colors;
  private final int sampleCount;

  public ImageCharacteristic(ColorCharacteristic[] colors) {
    this.colors = Arrays.asList(colors);

    int rawSampleCount = 0;

    for (ColorCharacteristic color : colors) {
      rawSampleCount += color.getCount();
    }

    sampleCount = rawSampleCount;
  }

  public ImageCharacteristic shiftColorValues(double deltaValue) {
    ColorCharacteristic[] newColors
        = new ColorCharacteristic[this.colors.size()];

    for (int i = 0; i < newColors.length; i++) {
      newColors[i] = colors.get(i).shiftValue(deltaValue);
    }

    return new ImageCharacteristic(newColors);
  }

  public List<ColorCharacteristic> getColors() {
    return colors;
  }

  /**
   * Get the "distance" between this image characteristic and the given
   * image characteristic
   * @param o other image
   * @return distance
   */
  public double compareTo(ImageCharacteristic o) {
    return compare(this, o);
  }


}
