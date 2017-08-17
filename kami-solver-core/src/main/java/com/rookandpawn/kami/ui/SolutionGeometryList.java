package com.rookandpawn.kami.ui;

import java.util.List;

/**
 *
 */
public class SolutionGeometryList {

  private final int paletteTileSize;
  private final List<SolutionGeometry> actualList;

  public SolutionGeometryList(List<SolutionGeometry> actualList
      , int paletteTileSize) {
    this.actualList = actualList;
    this.paletteTileSize = paletteTileSize;
  }

  public int size() {
    return actualList.size();
  }

  public SolutionGeometry get(int index) {
    return actualList.get(index);
  }

  /**
   * @return the paletteTileSize
   */
  public int getPaletteTileSize() {
    return paletteTileSize;
  }

}
