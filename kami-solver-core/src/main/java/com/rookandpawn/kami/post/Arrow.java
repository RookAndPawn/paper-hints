package com.rookandpawn.kami.post;

import com.google.common.collect.ImmutableList;
import com.rookandpawn.kami.ui.Point;
import com.rookandpawn.kami.ui.PointList;

/**
 *
 */
public class Arrow extends PointList {

  public Arrow(Point from, Point to) {
    super(ImmutableList.of(from, to));
  }

}
