package com.rookandpawn.kami.solver;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class GraphTest {

  @Test
  public void testChangeColor() {
    Graph start = Graph.builder()
        .withNode(1, "blue")
        .withNode(2, "green")
        .withNode(3, "blue")
        .withEdge(1, 2)
        .withEdge(2, 3)
        .build();

    Graph end = start.changeNodeColor((short)2, "blue");

    Assert.assertNotNull(end);

    Assert.assertEquals(1, end.getActualNodeCount());


  }

}
