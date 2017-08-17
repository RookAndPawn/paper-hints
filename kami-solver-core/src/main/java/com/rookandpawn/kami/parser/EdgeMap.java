package com.rookandpawn.kami.parser;

/**
 *
 */
public class EdgeMap {

  private final int height;
  private final int width;
  private final double[][] magnitudes;
  private final double[][] directions;

  public EdgeMap(int height, int width) {
    this.height = height;
    this.width = width;
    magnitudes = new double[height][width];
    directions = new double[height][width];
  }

  public void setMagnitude(int column, int row, double magnitude) {
    magnitudes[row][column] = magnitude;
  }

  public void setDirection(int column, int row, double direction) {
    directions[row][column] = direction;
  }

  public double getMagnitude(int column, int row) {
    return magnitudes[row][column];
  }

  public double getDirection(int column, int row) {
    return directions[row][column];
  }

  /**
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * @return the width
   */
  public int getWidth() {
    return width;
  }

}
