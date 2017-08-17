package com.rookandpawn.kami.solver;

/**
 * Implementation of a kami graph that is designed to evaluate an entire
 * solution at once very quickly
 */
public class FastGraph {

  private final int colorCount;
  private final byte[] nodes;
  private final short[] q;
  private final short[][] edges;
  private final Graph g;
  private final short[] colorCounts;
  private final short[] originalColorCounts;
  private final int actualNodeCount;
  private final int startingActiveColorCount;

  private int qRead;
  private int qWrite;

  public FastGraph(Graph g) {
    this.g = g;
    this.q = new short[g.getNodes().length * 2];
    this.nodes = new byte[g.getNodes().length];
    this.edges = g.getEdges();
    this.colorCount = g.getColors().length;
    this.colorCounts = new short[colorCount];
    this.originalColorCounts = new short[colorCount];

    for (byte color : g.getNodes()) {
      if (color >= 0) {
        originalColorCounts[color]++;
      }
    }

    int activeColorCount = 0;

    for (short count : originalColorCounts) {
      if (count > 0) {
        activeColorCount++;
      }
    }

    this.startingActiveColorCount = activeColorCount;
    this.actualNodeCount = g.getActualNodeCount();
  }

  private void reset() {
    qRead = 0;
    qWrite = 0;

    System.arraycopy(g.getNodes(), 0, nodes, 0, nodes.length);
    System.arraycopy(originalColorCounts, 0, colorCounts, 0, colorCount);
  }

  private void addToQ(short n) {
    q[qWrite++] = n;

    if (qWrite >= q.length) {
      qWrite = 0;
    }
  }

  private short pollQ() {
    short result = q[qRead++];

    if (qRead >= q.length) {
      qRead = 0;
    }

    return result;
  }

  private boolean qIsEmpty() {
    return qRead == qWrite;
  }

  public int evaluate(short node, byte[] colorSeq, byte finalColor) {

    reset();

    byte oldColor = nodes[node];

    byte prevColor = oldColor;

    int nodesChanged;
    int prevNodesChanges = 0;

    byte color;

    int index;
    int movesLeft = colorSeq.length;
    int activeColorCount = startingActiveColorCount;

    for (index = 0; index < colorSeq.length; index++) {

      if (movesLeft < activeColorCount - 1) {
        return index;
      }

      if (finalColor >= 0 && movesLeft == 1) {
        color = finalColor;
      }
      else {
        color = (byte)(1 + prevColor + colorSeq[index]);
      }

      if (color >= colorCount) {
        color -= colorCount;
      }

      if (color == prevColor && actualNodeCount > 1) {
        return index - 1;
      }

      //System.out.print(g.getNameForColor(color) + ",");

      addToQ(node);

      nodesChanged = 0;

      while (!qIsEmpty()) {
        short curr = pollQ();
        byte c = nodes[curr];

        if (c == prevColor) {
          nodes[curr] = color;

          nodesChanged++;

          colorCounts[prevColor]--;
          colorCounts[color]++;

          for (short n : edges[curr]) {
            if (prevColor == nodes[n]) {
              addToQ(n);
            }
          }
        }
      }

      if (index > 0 && nodesChanged == prevNodesChanges) {
        return index - 1;
      }
      
      activeColorCount = 0;
      
      for (short count : colorCounts) {
        if (count > 0) {
          activeColorCount++;
        }
      }
      
      movesLeft--;
      prevColor = color;
      prevNodesChanges = nodesChanged;
    }

    return activeColorCount == 1
        ? index
        : index - (finalColor < 0 ? 1 : 2);
  }


}
