package com.rookandpawn.kami.ui;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 *
 */
public class PointList {

  private final List<Point> actualList;

  public PointList(Iterable<Point> actualList) {
    this.actualList = ImmutableList.copyOf(actualList);
  }

  public int size() {
    return actualList.size();
  }

  public Point get(int index) {
    return actualList.get(index);
  }

  public List<Point> getActualList() {
    return actualList;
  }

}
