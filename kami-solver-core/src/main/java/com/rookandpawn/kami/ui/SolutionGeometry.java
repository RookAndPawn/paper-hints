package com.rookandpawn.kami.ui;

import java.util.List;

/**
 *
 */
public class SolutionGeometry {

  private final List<PointList> bordersToHighlight;
  private final List<PointList> labelBoxes;
  private final List<PointList> paletteTiles;
  private final List<Integer> paletteTileNumbers;
  private final List<PointList> arrows;

  public SolutionGeometry(List<PointList> bordersToHighlight
      , List<PointList> labelBoxes
      , List<PointList> paletteTiles
      , List<Integer> tileImages
      , List<PointList> arrows) {
    this.bordersToHighlight = bordersToHighlight;
    this.labelBoxes = labelBoxes;
    this.paletteTiles = paletteTiles;
    this.paletteTileNumbers = tileImages;
    this.arrows = arrows;
  }

  public int borderCount() {
    return bordersToHighlight.size();
  }

  public PointList getBorder(int index) {
    return bordersToHighlight.get(index);
  }

  public int getLabelBoxCount() {
    return labelBoxes.size();
  }

  public PointList getLabelBox(int index) {
    return labelBoxes.get(index);
  }

  public int getPaletteTileBoxCount() {
    return paletteTiles.size();
  }

  public PointList getPaletteTileBox(int index) {
    return paletteTiles.get(index);
  }

  public int getPaletteTileNumber(int index) {
    return paletteTileNumbers.get(index);
  }

  public PointList getArrow(int index) {
    return arrows.get(index);
  }
}
