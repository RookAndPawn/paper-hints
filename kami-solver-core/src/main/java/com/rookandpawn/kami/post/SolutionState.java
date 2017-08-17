package com.rookandpawn.kami.post;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rookandpawn.kami.solver.Move;

/**
 *
 */
public class SolutionState {

  private final Map<Short,List<Integer>> shownMoves;

  public SolutionState() {
    this.shownMoves = ImmutableMap.of();
  }

  public SolutionState(Map<Short, List<Integer>> shownMoves) {
    this.shownMoves = shownMoves;
  }

  public SolutionState cloneWithNewMove(Move move) {
    Map<Short,List<Integer>> result = Maps.newLinkedHashMap(shownMoves);

    List<Integer> currMoves = result.get(move.getNode());

    List<Integer> tempMoves = Lists.newArrayList();

    if (currMoves != null) {
      tempMoves.addAll(currMoves);
    }

    tempMoves.add(Integer.parseInt(move.getColor()));

    result.put(move.getNode(), ImmutableList.copyOf(tempMoves));

    return new SolutionState(result);
  }


  /**
   * @return the shownMoves
   */
  public Map<Short,List<Integer>> getShownMoves() {
    return shownMoves;
  }


}
