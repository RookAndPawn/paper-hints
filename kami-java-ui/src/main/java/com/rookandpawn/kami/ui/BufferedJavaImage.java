package com.rookandpawn.kami.ui;

import java.awt.image.BufferedImage;

/**
 *
 */
public class BufferedJavaImage {

  public static Color createColor(int rgb) {
    java.awt.Color color = new java.awt.Color(rgb);

    return new Color((short)color.getRed()
        , (short)color.getGreen()
        , (short)color.getBlue());
  }

  public static int colorToInt(Color color) {
    if (color == null) {
      return 0;
    }

    java.awt.Color temp = new java.awt.Color(
        color.getR()
        , color.getG()
        , color.getB());

    return temp.getRGB();
  }

  private final BufferedImage image;

  public BufferedJavaImage(BufferedImage awtImage) {
    this.image = awtImage;
  }

  public BufferedJavaImage(KamiImage image) {
    this.image = new BufferedImage(image.getWidth(), image.getHeight()
        , BufferedImage.TYPE_INT_RGB);

    for (int column = 0; column < image.getWidth(); column++) {
      for (int row = 0; row < image.getHeight(); row++) {
        this.image.setRGB(column, row
            , colorToInt(image.getPixel(row, column)));
      }
    }

  }

  public BufferedImage getAwtImage() {
    return image;
  }

  public int getHeight() {
    return image.getHeight();
  }

  public int getWidth() {
    return image.getWidth();
  }

  public KamiImage toKamiImage() {
    int height = image.getHeight();
    int width = image.getWidth();
    KamiImage result = new KamiImage(width, height);

    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        result.setPixel(row, col, createColor(image.getRGB(col, row)));
      }
    }

    return result;
  }

}
