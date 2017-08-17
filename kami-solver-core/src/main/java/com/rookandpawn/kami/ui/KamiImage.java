package com.rookandpawn.kami.ui;

import java.util.Iterator;

/**
 *
 */
public class KamiImage implements Iterable<Color>{

  public static interface PixelOerator {
    void accept(int row, int col, Color value);
  }

  private final int width;
  private final int height;

  private final Color[][] pixels;

  public KamiImage(KamiImage image) {
    this.width = image.getWidth();
    this.height = image.getHeight();

    this.pixels = new Color[height][width];

    for (int column = 0; column < width; column++) {
      for (int row = 0; row < height; row++) {
        pixels[row][column] = image.getPixel(row, column);
      }
    }
  }

  public KamiImage(int width, int height) {
    this.width = width;
    this.height = height;
    this.pixels = new Color[height][width];
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public Color getPixel(int row, int column) {
    return pixels[row][column];
  }

  public void setPixel(int row, int column, Color pixel) {
    pixels[row][column] = pixel;
  }

  /**
   * Get a sub image in the given region of interest.  Top left corner is
   * inclusive, bottom right is exclusive
   * @param topRow
   * @param leftColumn
   * @param bottomRow
   * @param rightColumn
   * @return
   */
  public KamiImage getRoi(int topRow, int leftColumn
      , int bottomRow, int rightColumn) {
    int newHeight = bottomRow - topRow;
    int newWidth = rightColumn - leftColumn;

    KamiImage result = new KamiImage(newWidth, newHeight);

    int newRow = 0;

    for (int row = topRow; row < bottomRow; row++) {
      int newCol = 0;
      for (int col = leftColumn; col < rightColumn; col++) {
        result.setPixel(newRow, newCol++, getPixel(row, col));
      }
      newRow++;
    }

    return result;
  }

  /**
   * @return a copy of this image with the rows and columns reversed
   */
  public KamiImage getTranspose() {
    KamiImage result = new KamiImage(height, width);

    foreachPixel((row, col, pixel) -> {
      result.setPixel(col, row, pixel);
    });

    return result;
  }

  /**
   * reverse the columns of this image in place
   */
  public void flipHorizontally() {
    Color swap;

    for (int row = 0; row < height; row++) {
      for (int leftCol = 0; leftCol < width / 2; leftCol++) {
        int rightCol = width - 1 - leftCol;
        swap = getPixel(row, leftCol);
        setPixel(row, leftCol, getPixel(row, rightCol));
        setPixel(row, rightCol, swap);
      }
    }
  }

  /**
   * Operate on each pixel in the image with the given operator
   * @param operator
   */
  public void foreachPixel(PixelOerator operator) {
    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        operator.accept(row, col, pixels[row][col]);
      }
    }
  }

  /**
   * Create a new Kami
   * @return 
   */
  public KamiImage reduceSizeByHalf() {
    KamiImage result = new KamiImage(width / 2, height / 2);

    for (int row = 0; row < (height - 1); row += 2) {
      for (int col = 0; col < (width - 1); col += 2) {
        result.setPixel(row / 2, col / 2, pixels[row][col]);
      }
    }

    return result;
  }

  @Override
  public Iterator<Color> iterator() {
     return new Iterator<Color>() {

        int row;
        int col;

        @Override
        public boolean hasNext() {
          return row < getHeight();
        }

        @Override
        public Color next() {
          Color result = getPixel(row, col);

          if (++col >= getWidth()) {
            row++;
            col = 0;
          }

          return result;
        }
      };
  }

  /**
   * @return the pixels
   */
  public Color[][] getPixels() {
    return pixels;
  }

}
