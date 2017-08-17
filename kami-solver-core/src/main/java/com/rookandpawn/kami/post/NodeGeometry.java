package com.rookandpawn.kami.post;

import java.util.List;

import com.rookandpawn.kami.ui.Point;

/**
 *
 */
public class NodeGeometry {

  private final Point center;
  private final List<List<Point>> borders;

  public NodeGeometry(Point center, List<List<Point>> borders) {
    this.center = center;
    this.borders = borders;
  }

  /**
   * @return the center
   */
  public Point getCenter() {
    return center;
  }

  /**
   * @return the vertices
   */
  public List<List<Point>> getBorders() {
    return borders;
  }

  
  
}
