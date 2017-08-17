package com.rookandpawn.kami.ui;

import java.util.List;

/**
 *
 */
public class PaletteList {

  private final List<KamiImage> paletteTiles;

  public PaletteList(List<KamiImage> paletteTiles) {
    this.paletteTiles = paletteTiles;
  }

  public int size() {
    return paletteTiles.size();
  }

  public KamiImage get(int index) {
    return paletteTiles.get(index);
  }
}
