package com.rookandpawn.kami.post;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.rookandpawn.kami.parser.KamiVersion;
import com.rookandpawn.kami.parser.PuzzleContext;
import com.rookandpawn.kami.ui.KamiImage;
import com.rookandpawn.kami.ui.Point;

/**
 *
 */
public class GeometryProviderTest {

  Kami1NodeGeometryProvider geometryProvider1 = new Kami1NodeGeometryProvider();

  @Test
  public void testKami1Geometry1() {
    PuzzleContext ctx = new PuzzleContext(new KamiImage(20, 32));

    ctx.setPlayAreaImage(ctx.getRawImage());

    ctx.setVersion(KamiVersion.VERSION_1);

    Integer[][] memberships = new Integer[16][10];

    for (Integer[] row : memberships) {
      Arrays.fill(row, 1);
    }

    memberships[0][0] = 0;

    ctx.setMemberships(memberships);

    NodeGeometry g = geometryProvider1.getGeometry(ctx, (short)0);

    Assert.assertEquals(new Point(1, 1), g.getCenter());

    Assert.assertEquals(1, g.getBorders().size());
    Assert.assertEquals(4, g.getBorders().get(0).size());
  }

  @Test
  public void testKami1Geometry2() {
    PuzzleContext ctx = new PuzzleContext(new KamiImage(10, 16));

    ctx.setPlayAreaImage(ctx.getRawImage());

    ctx.setVersion(KamiVersion.VERSION_1);

    Integer[][] memberships = new Integer[16][10];

    for (Integer[] row : memberships) {
      Arrays.fill(row, 1);
    }

    memberships[1][0] = 0;
    memberships[0][1] = 0;
    memberships[0][2] = 0;
    memberships[1][2] = 0;
    memberships[2][0] = 0;
    memberships[2][1] = 0;
    memberships[2][2] = 0;


    ctx.setMemberships(memberships);

    NodeGeometry g = geometryProvider1.getGeometry(ctx, (short)0);

    AtomicInteger edgeCount = new AtomicInteger();

    g.getBorders().forEach(map -> {
      edgeCount.addAndGet(map.size());
    });

    Assert.assertEquals(16, edgeCount.get());

  }


  @Test
  public void testKami1Geometry3() {
    PuzzleContext ctx = new PuzzleContext(new KamiImage(10, 16));

    ctx.setPlayAreaImage(ctx.getRawImage());

    ctx.setVersion(KamiVersion.VERSION_1);

    Integer[][] memberships = new Integer[16][10];

    for (Integer[] row : memberships) {
      Arrays.fill(row, 1);
    }

    memberships[1][0] = 0;
    memberships[0][1] = 0;
    memberships[0][2] = 0;
    memberships[1][2] = 0;
    memberships[2][0] = 0;
    memberships[2][1] = 0;
    memberships[2][2] = 0;

    memberships[0][3] = 0;
    memberships[1][3] = 0;
    memberships[3][1] = 0;
    memberships[3][0] = 0;

    memberships[0][4] = 0;
    memberships[1][4] = 0;
    memberships[2][4] = 0;
    memberships[3][4] = 0;
    memberships[4][4] = 0;
    memberships[4][3] = 0;
    memberships[4][2] = 0;
    memberships[4][1] = 0;
    memberships[4][0] = 0;

    ctx.setMemberships(memberships);

    NodeGeometry g = geometryProvider1.getGeometry(ctx, (short)0);

    AtomicInteger edgeCount = new AtomicInteger();

    g.getBorders().forEach(map -> {
      edgeCount.addAndGet(map.size());
    });

    Assert.assertEquals(32, edgeCount.get());

  }
}
