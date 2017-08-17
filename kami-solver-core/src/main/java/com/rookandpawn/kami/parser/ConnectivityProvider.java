package com.rookandpawn.kami.parser;

import java.util.List;

/**
 * definition of a class that provides the neighbors of tiles in row/col form
 */
public interface ConnectivityProvider {

  /**
   * @param row
   * @param col
   * @return coordinates of the tiles to the left and above the tile at the given
   * coordinates
   */
  List<int[]> getNeighboringTiles(int row, int col);

}
