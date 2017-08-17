package com.rookandpawn.kami.solver;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

/**
 *
 */
public class PathSetTest {

  private final Graph simpleGraph = Graph.builder()
      .withNode(0, "2")
      .withNode(1, "0")
      .withNode(2, "1")
      .withNode(3, "0")
      .withNode(4, "2")
      .withNode(5, "3")
      .withEdge(0, 1)
      .withEdge(1, 2)
      .withEdges(2, 3, 4)
      .withEdge(3, 4)
      .withEdge(4, 5)
      .build();

  PathSet p = new PathSet(simpleGraph);
  
  @Test
  public void testPathSet() {
    Assert.assertEquals(4, p.getMaxPathLength());
    Assert.assertEquals(2, p.getCenterNode());

    List<List<Short>> centerPaths = p.getAllShortestPathsFrom((short)2);

    Assert.assertEquals(5, centerPaths.size());

    Map<Short,List<Short>> pathsByEndNodes = Maps.newHashMap();

    centerPaths.forEach(nodes -> {
      short last = nodes.get(nodes.size() - 1);
      pathsByEndNodes.put(last, nodes);
    });

    Assert.assertEquals(ImmutableList.of((short)1, (short)0)
        , pathsByEndNodes.get((short)0));

    Assert.assertEquals(ImmutableList.of((short)1)
        , pathsByEndNodes.get((short)1));

    Assert.assertEquals(ImmutableList.of((short)3)
        , pathsByEndNodes.get((short)3));

    Assert.assertEquals(ImmutableList.of((short)4)
        , pathsByEndNodes.get((short)4));

    Assert.assertEquals(ImmutableList.of((short)4, (short)5)
        , pathsByEndNodes.get((short)5));
  }



}
