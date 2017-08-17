package com.rookandpawn.kami.parser;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Connectivity provider for kami 1 puzzles.  
 */
public class Kami1ConnectivityProvider implements ConnectivityProvider {

  private static final int[][] deltas = {
    {-1, 0},
    {0, -1},
    {1, 0},
    {0, 1}
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

    for (int[] delta : deltas) {
      int newRow = row + delta[0];
      int newCol = col + delta[1];

      if (newRow < 0 || newRow >= 16) {
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
