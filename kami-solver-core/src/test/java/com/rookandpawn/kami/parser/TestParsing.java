
package com.rookandpawn.kami.parser;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Optional;
import com.rookandpawn.kami.EventPump;
import com.rookandpawn.kami.solver.DumbSearch;
import com.rookandpawn.kami.solver.Graph;
import com.rookandpawn.kami.solver.Move;
import com.rookandpawn.kami.solver.Search;

public class TestParsing {

  PuzzleParser parser = new PuzzleParser();
  EventPump pump = new EventPump(null);

  /**
   * Get the String representation of the tile classification
   * @param ctx context to pull the image classification string from
   * @return a string representing the classification of tiles
   */
  private String getClassificationString(PuzzleContext ctx) {
    StringBuilder result = new StringBuilder();

    int[][] tileClassifications = ctx.getTileClassification();

    if (tileClassifications == null) {
      return null;
    }

    for (int[] row : tileClassifications) {
      if (result.length() > 0) {
        result.append("\n");
      }
      
      for (int val : row) {

        result.append(val >= 0 ? Integer.toString(val) : " ");
      }
    }

    return result.toString();
  }

  @Test
  public void testParsing() {
    for (TestPuzzle puzzle : TestPuzzle.values()) {
      PuzzleContext ctx = parser.parse(puzzle.loadImage(), pump);

      HudOrientation orientation = ctx.getHudOrientation();
      KamiVersion version = ctx.getVersion();
      Palette palette = ctx.getPalette();

      Assert.assertEquals("For " + puzzle.resourceName
          , puzzle.hudOrientation, orientation);

      Assert.assertEquals("For " + puzzle.resourceName
          , puzzle.version, version);

      Assert.assertEquals("For " + puzzle.resourceName
          , puzzle.paletteSize, palette.size());

      String classifications = getClassificationString(ctx);

      System.out.println(classifications);

      if (classifications != null && puzzle.classifications != null) {
        Assert.assertEquals(puzzle.classifications, classifications);
      }

      Graph graph = ctx.getGraph();

      if (graph != null && puzzle.graph != null) {
        Assert.assertEquals(puzzle.graph, graph);
      }

      System.out.println(puzzle.resourceName + " succeeded");
    }
  }


  @Test
  //@Ignore
  public void testAndSolveK1_p8_7_iPhone7() {
    PuzzleContext ctx = parser.parse(TestPuzzle.k1_P8_7_Iphone7.loadImage()
        , pump);

    String classifications = getClassificationString(ctx);

    Assert.assertEquals(TestPuzzle.k1_P8_7_Iphone7.classifications
        , classifications);

    List<Move> moves = new DumbSearch(ctx.getGraph()).go();

    Assert.assertEquals(11,moves.size());
  }

  @Test
  //@Ignore
  public void testAndSolveK1_p8_8_iPhone7() {
    PuzzleContext ctx = parser.parse(TestPuzzle.k1_P8_8_Iphone7.loadImage()
        , pump);

//    String classifications = getClassificationString(ctx);
//
//    Assert.assertEquals(TestPuzzle.k1_P8_7_Iphone7.classifications
//        , classifications);

    List<Move> moves = new DumbSearch(ctx.getGraph()).go(13);

    Assert.assertEquals(13,moves.size());
  }


  @Test
  @Ignore
  public void testAndSolveK1_p8_9_iPhone7() {
    PuzzleContext ctx = parser.parse(TestPuzzle.k1_P8_9_Iphone7.loadImage()
        , pump);

    List<Move> moves = new DumbSearch(ctx.getGraph()).go(16);

    Assert.assertEquals(16,moves.size());
  }

  @Test
  public void testAndSolveK2_p4_6_iPhone7() {
    PuzzleContext ctx = parser.parse(TestPuzzle.K2_P4_6_Iphone7.loadImage()
        , pump);

    List<Move> moves = new DumbSearch(ctx.getGraph()).go();

    Assert.assertEquals(5,moves.size());
  }

  @Test
  public void testAndSolveK2_p10_5_iphone_se() {
    PuzzleContext ctx = parser.parse(TestPuzzle.K2_P10_5_Iphone7.loadImage()
        , pump);

    List<Move> moves = new DumbSearch(ctx.getGraph()).go();

    Assert.assertEquals(9,moves.size());
  }

  @Test
  public void testAndSolveK2_p11_6_iphone_7() {
    PuzzleContext ctx = parser.parse(TestPuzzle.K2_P11_6_Iphone7.loadImage()
        , pump);

    Optional<List<Move>> moves = new Search(ctx.getGraph()).go(11);

    Assert.assertTrue(moves.isPresent());
    Assert.assertEquals(11, moves.get().size());
  }

  @Test
  public void testAndSolveK2_pu_1_iphone_se() {
    PuzzleContext ctx = parser.parse(TestPuzzle.K2_PU_1_IphoneSE.loadImage()
        , pump);

    Optional<List<Move>> moves = new Search(ctx.getGraph()).go(6);

    Assert.assertTrue(moves.isPresent());
    Assert.assertEquals(6, moves.get().size());
  }

  @Test
  public void testAndSolveK2_pu_2_iphone7() {
    PuzzleContext ctx = parser.parse(TestPuzzle.K2_PU_2_Iphone7.loadImage()
        , pump);

    Optional<List<Move>> moves = new Search(ctx.getGraph()).go(6);

    Assert.assertTrue(moves.isPresent());
    Assert.assertEquals(6, moves.get().size());
  }


  @Test
  public void testAndSolveK2_pu_4_iphone7() {
    PuzzleContext ctx = parser.parse(TestPuzzle.K2_PU_4_Iphone7.loadImage()
        , pump);

    System.out.println(getClassificationString(ctx));

    Optional<List<Move>> moves = new Search(ctx.getGraph()).go(9);

    Assert.assertTrue(moves.isPresent());
    Assert.assertEquals(9, moves.get().size());
  }


}
