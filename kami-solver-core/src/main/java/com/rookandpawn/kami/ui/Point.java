package com.rookandpawn.kami.ui;

/**
 *
 */
public class Point {

  private final int row;
  private final int col;

  public Point(double row, double col) {
    this.row = (int)Math.round(row);
    this.col = (int)Math.round(col);
  }

  public Point(int row, int col) {
    this.row = row;
    this.col = col;
  }

  /**
   * @return the row
   */
  public int getRow() {
    return row;
  }

  /**
   * @return the col
   */
  public int getCol() {
    return col;
  }

  public Point incCol() {
    return new Point(row, col + 1);
  }

  public Point decCol() {
    return new Point(row, col - 1);
  }

  public Point incRow() {
    return new Point(row + 1, col);
  }

  public Point decRow() {
    return new Point(row - 1, col);
  }

  public Point incCol(int amt) {
    return new Point(row, col + amt);
  }

  public Point decCol(int amt) {
    return new Point(row, col - amt);
  }

  public Point incRow(int amt) {
    return new Point(row + amt, col);
  }

  public Point decRow(int amt) {
    return new Point(row - amt, col);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Point other = (Point) obj;
    if (this.row != other.row) {
      return false;
    }
    if (this.col != other.col) {
      return false;
    }
    return true;
  }

  public int getDistanceSquared(Point other) {
    int dx = col - other.col;
    int dy = row - other.row;

    return dx * dx + dy * dy;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 17 * hash + this.row;
    hash = 17 * hash + this.col;
    return hash;
  }

  @Override
  public String toString() {
    return String.format("[%d,%d]", row, col);
  }


}
