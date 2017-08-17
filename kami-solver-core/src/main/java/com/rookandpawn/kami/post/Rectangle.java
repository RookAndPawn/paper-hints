package com.rookandpawn.kami.post;

import com.google.common.collect.ImmutableList;
import com.rookandpawn.kami.ui.Point;
import com.rookandpawn.kami.ui.PointList;

/**
 *
 */
public class Rectangle extends PointList {

  public static Rectangle withCenter(Point center, int height, int width) {
    return new Rectangle(center.decRow(height / 2).decCol(width / 2)
        , height
        , width);
  }

  private final Point topLeft;
  private final int height;
  private final int width;
  private final Point bottomRight;

  public Rectangle(Point topLeft, int height, int width) {
    super(ImmutableList.of(
        topLeft
        , topLeft.incCol(width)
        , topLeft.incCol(width).incRow(height)
        , topLeft.incRow(height)));

    this.topLeft = topLeft;
    this.height = height;
    this.width = width;
    this.bottomRight = topLeft.incRow(height).incCol(width);
  }

  /**
   * @return the topLeft
   */
  public Point getTopLeft() {
    return topLeft;
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

  public Point getBottomRight() {
    return bottomRight;
  }

  public Point getCenter() {
    return topLeft.incCol(width / 2).incRow(height / 2);
  }

  public int getOverlArea(Rectangle other) {
    if (other.getTopLeft().getRow() > bottomRight.getRow()
        || other.getTopLeft().getCol() > bottomRight.getCol()
        || other.getBottomRight().getRow() < topLeft.getRow()
        || other.getBottomRight().getCol() < topLeft.getCol()) {
      return 0;
    }

    int heightOverlap = Math.min(
        bottomRight.getRow(), other.bottomRight.getRow())
        - Math.max(topLeft.getRow(), other.getTopLeft().getRow());

    int widthOverlap = Math.min(
        bottomRight.getCol(), other.bottomRight.getCol())
        - Math.max(topLeft.getCol(), other.getTopLeft().getCol());

    return heightOverlap * widthOverlap;
  }
}
