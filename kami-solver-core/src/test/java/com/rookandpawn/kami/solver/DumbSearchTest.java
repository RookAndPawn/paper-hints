package com.rookandpawn.kami.solver;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class DumbSearchTest {

  private static final String red = "red";
  private static final String green = "green";
  private static final String yellow = "yellow";
  private static final String gray = "gray";
  private static final String orange = "orange";
  private static final String teal = "teal";

  Graph p1_2 = Graph.builder()
      .withNode(1, red)
      .withNode(2, green)
      .withNode(3, yellow)
      .withEdge(1, 2)
      .withEdge(2, 3)
      .build();


  Graph p1_6 = Graph.builder()
      .withNode(1, green)
      .withNode(2, red)
      .withNode(3, yellow)
      .withNode(4, green)
      .withNode(5, yellow)
      .withNode(6, red)
      .withNode(7, green)
      .withEdge(1, 2)
      .withEdge(2, 3)
      .withEdge(2, 4)
      .withEdge(2, 5)
      .withEdge(3, 4)
      .withEdge(3, 6)
      .withEdge(4, 5)
      .withEdge(4, 6)
      .withEdge(5, 6)
      .withEdge(6, 7)
      .build();

  Graph p2_6 = Graph.builder()
      .withNode(1, red)
      .withNode(2, green)
      .withNode(3, gray)
      .withNode(4, green)
      .withNode(5, red)
      .withNode(6, green)
      .withNode(7, gray)
      .withNode(8, green)
      .withEdge(1, 2)
      .withEdge(2, 3)
      .withEdge(3, 4)
      .withEdge(4, 5)
      .withEdge(5, 6)
      .withEdge(5, 7)
      .withEdge(7, 8)
      .build();


  Graph p4_6 = Graph.builder()
      .withNode(1, gray)
      .withNode(2, orange)
      .withNode(3, green)
      .withNode(4, gray)
      .withNode(5, orange)
      .withNode(6, green)
      .withNode(7, orange)
      .withNode(8, gray)
      .withNode(9, teal)
      .withNode(10, gray)
      .withNode(11, orange)
      .withNode(12, green)
      .withNode(13, gray)
      .withNode(14, orange)
      .withNode(15, teal)
      .withNode(16, teal)
      .withNode(17, orange)
      .withNode(18, orange)
      .withNode(19, teal)
      .withNode(20, teal)
      .withNode(21, gray)
      .withNode(22, gray)
      .withNode(23, orange)
      .withNode(24, gray)
      .withNode(25, green)
      .withNode(26, gray)
      .withEdges(1, 2, 3, 9)
      .withEdges(2, 3)
      .withEdges(3, 4, 9)
      .withEdges(4, 9, 5, 6)
      .withEdges(5, 6)
      .withEdges(6, 7, 8, 14, 15, 21, 13, 19, 20, 18)
      .withEdges(7, 8)
      .withEdges(8, 15)
      .withEdges(9, 10, 11, 12, 13, 14)
      .withEdges(10, 11, 16)
      .withEdges(11, 12, 16)
      .withEdges(12, 13, 16, 17, 18, 24, 23, 22)
      .withEdges(13, 14, 19, 18)
      .withEdges(15, 21)
      .withEdges(16, 17)
      .withEdges(17, 22)
      .withEdges(18, 19, 20, 26, 25, 24)
      .withEdges(20, 21)
      .withEdges(23, 24)
      .withEdges(24, 25)
      .withEdges(25, 26)
      .build();

  @Test
  public void testSimpleDumbSearch() {
    DumbSearch search = new DumbSearch(p1_2);

    List<Move> solution = search.go();

    Assert.assertEquals(2, solution.size());
  }


  @Test
  public void testDumbChallenge1() {
    List<Move> solution = new DumbSearch(p1_6).go();

    Assert.assertEquals(3, solution.size());
  }

  @Test
  public void testDumbChallenge2() {
    List<Move> solution = new DumbSearch(p2_6).go();

    Assert.assertEquals(4, solution.size());
  }

  @Test
  public void testDumbChallenge4() {
    List<Move> solution = new DumbSearch(p4_6).go();

    Assert.assertEquals(5, solution.size());
  }

  @Test
  public void testDifficultPuzzle() {
    int index = 1;

    String g = "green";
    String r = "red";
    String b = "brown";

    Graph p14_5 = Graph.builder()
        .withNode(index++, g) //1
        .withNode(index++, r) //2
        .withNode(index++, b) //3

        .withNode(index++, g) //4
        .withNode(index++, b) //5
        .withNode(index++, r) //6

        .withNode(index++, g) //7
        .withNode(index++, b) //8
        .withNode(index++, r) //9

        .withNode(index++, g) //10
        .withNode(index++, b) //11
        .withNode(index++, r) //12

        .withNode(index++, g) //13
        .withNode(index++, b) //14
        .withNode(index++, b) //15
        .withNode(index++, r) //16

        .withNode(index++, g) //17
        .withNode(index++, b) //18
        .withNode(index++, r) //19

        .withNode(index++, g) //20
        .withNode(index++, b) //21
        .withNode(index++, r) //22

        .withNode(index++, g) //23
        .withNode(index++, g) //24
        .withNode(index++, b) //25
        .withNode(index++, r) //26

        .withNode(index++, g) //27
        .withNode(index++, b) //28
        .withNode(index++, r) //29

        .withNode(index++, g) //30
        .withNode(index++, b) //31
        .withNode(index++, r) //32

        .withNode(index++, g) //33
        .withNode(index++, g) //34
        .withNode(index++, b) //35
        .withNode(index++, r) //36
        .withNode(index++, b) //37
        .withNode(index++, r) //38
        .withNode(index++, g) //39
        .withNode(index++, b) //40

        .withNode(index++, g) //41
        .withNode(index++, b) //42
        .withNode(index++, r) //43

        .withNode(index++, g) //44
        .withNode(index++, r) //45
        .withNode(index++, b) //46

        .withNode(index++, g) //47
        .withNode(index++, b) //48
        .withNode(index++, r) //49

        .withNode(index++, g) //50
        .withNode(index++, b) //51
        .withNode(index++, r) //52

        .withEdges(1, 2, 3)
        .withEdges(2, 3, 4)
        .withEdges(3, 7)
        .withEdges(4, 5, 6)
        .withEdges(5, 6)
        .withEdges(6, 13)
        .withEdges(7, 8, 9)
        .withEdges(8, 9, 10)
        .withEdges(10, 11, 12)
        .withEdges(11, 12)
        .withEdges(12, 23)
        .withEdges(13, 14, 15, 16)
        .withEdges(15, 16, 19)
        .withEdges(16, 20)
        .withEdges(17, 18, 19)
        .withEdges(18, 19, 26)
        .withEdges(19, 20, 21)
        .withEdges(20, 21, 22)
        .withEdges(21, 22)
        .withEdges(22, 30)
        .withEdges(23, 25, 26)
        .withEdges(24, 25)
        .withEdges(25, 26, 27, 29)
        .withEdges(26, 41)
        .withEdges(27, 28, 29)
        .withEdges(28, 29)
        .withEdges(29, 44)
        .withEdges(30, 31, 32)
        .withEdges(31, 32, 33)
        .withEdges(32, 34)
        .withEdges(33, 35, 36)
        .withEdges(34, 37, 38)
        .withEdges(35, 36)
        .withEdges(36, 37, 39)
        .withEdges(37, 38, 39)
        .withEdges(38, 39, 40)
        .withEdges(39, 40)
        .withEdges(40, 50)
        .withEdges(41, 42, 43)
        .withEdges(42, 43)
        .withEdges(43, 47)
        .withEdges(44, 45, 46)
        .withEdges(45, 46, 47)
        .withEdges(47, 48, 49)
        .withEdges(48, 49)
        .withEdges(50, 51, 52)
        .withEdges(51, 52)
        .build();

    List<Move> solution = new DumbSearch(p14_5).go();

    Assert.assertEquals(10, solution.size());
  }


}
