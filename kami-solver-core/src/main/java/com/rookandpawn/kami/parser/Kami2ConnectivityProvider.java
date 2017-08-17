package com.rookandpawn.kami.parser;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Connectivity provider for kami 1 puzzles.  
 */
public class Kami2ConnectivityProvider implements ConnectivityProvider {

  private static final int[][] leftDeltas = {
    {-1, 0}, // up
    {1, 0}, // down
    {0, -1} // left
  };

  private static final int[][] rightDeltas = {
    {-1, 0}, // up
    {1, 0}, // down
    {0, 1} // right
  };

  /**
   * Get a list of the coordinates of the neighboring tiles for the tile whose
   * row and column are given
   * @param row
   * @param col
   * @return
   */
  @Override
  public List<int[]> getNeighboringTiles(int row, int col) {
    List<int[]> result = Lists.newArrayList();

    boolean isLeft = (col + (row % 2 == 0 ? 0 : 1)) % 2 == 0;

    for (int[] delta : isLeft ? leftDeltas : rightDeltas) {
      int newRow = row + delta[0];
      int newCol = col + delta[1];

      if (newRow < 0 || newRow >= 29) {
        continue;
      }
      if (newCol < 0 || newCol >= 10) {
        continue;
      }

      result.add(new int[] {newRow, newCol});
    }

    return result;
  }

}
