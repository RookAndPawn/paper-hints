package com.rookandpawn.kami.parser;

import java.util.List;

import com.rookandpawn.kami.ui.Color;

/**
 * representation of a color and its minor variations in the scope of an image
 */
public class ColorCharacteristic {

  private static double calculateAverageError(Color mean
      , List<Color> samples) {
    double result = 0;

    if (samples.isEmpty()) {
      return 0;
    }

    for (Color sample : samples) {
      result += mean.getRgbDistanceTo(sample);
    }

    return result / samples.size();
  }

  private final Color mean;
  private final double averageError;
  private final int count;

  public ColorCharacteristic(Color mean, List<Color> samples) {
    this(mean, calculateAverageError(mean, samples), samples.size());
  }

  public ColorCharacteristic(Color mean, double averageError, int count) {
    this.mean = mean;
    this.averageError = averageError;
    this.count = count;
  }

  /**
   * Create a new color characteristic with a shifted mean color by the given 
   * value
   * @param deltaValue
   * @return 
   */
  public ColorCharacteristic shiftValue(double deltaValue) {
    return new ColorCharacteristic(
        mean.plusValue(deltaValue), averageError, count);
  }

  /**
   * @return the mean
   */
  public Color getMean() {
    return mean;
  }

  /**
   * @return the averageError
   */
  public double getAverageError() {
    return averageError;
  }

  /**
   * @return the count
   */
  public int getCount() {
    return count;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append(mean)
        .append(" - ")
        .append(count)
        .append(" E- ")
        .append(averageError)
        .toString();
  }

}
