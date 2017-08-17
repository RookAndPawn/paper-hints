package com.rookandpawn.kami.solver;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Optional;

/**
 *
 */
public class SearchTest {

  /**
   * The graph being solved in this test cannot be solved with 6 moves by
   * selecting a starting node and changing the color repeatedly, so we have to
   * use a full search technique to get to the 6-move solution
   */
  @Test
  public void testConjectureBreaker() {

    String p = "purple";
    String y = "yellow";
    String r = "red";
    String g = "green";
    String m = "maroon";

    Graph graph = Graph.builder()
        .withNode(1, p)
        .withNode(2, y)
        .withNode(3, r)
        .withNode(4, y)
        .withNode(5, g)
        .withNode(6, p)
        .withNode(7, g)
        .withNode(8, p)
        .withNode(9, r)
        .withNode(10, m)
        .withEdge(1, 2)
        .withEdge(2, 3)
        .withEdge(3, 4)
        .withEdge(4, 5)
        .withEdge(5, 6)
        .withEdge(6, 7)
        .withEdge(7, 8)
        .withEdge(8, 9)
        .withEdge(9, 10)
        .withEdge(10, 1)
        .build();

    Optional<List<Move>> solution = new Search(graph).go(6);

    Assert.assertTrue(solution.isPresent());
    Assert.assertEquals(6, solution.get().size());

  }

  @Test
  public void testTrivialDisjointGraph() {
    Graph graph = Graph.builder()
        .withNode(1, "a")
        .withNode(2, "b")
        .build();

    Optional<List<Move>> solution = new Search(graph).go(1);

    Assert.assertTrue(solution.isPresent());
    Assert.assertEquals(1, solution.get().size());
  }
  

}
