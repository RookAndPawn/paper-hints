
package com.rookandpawn.kami.parser;

import java.util.Map;

import com.google.common.collect.Maps;
import com.rookandpawn.kami.post.Kami1NodeGeometryProvider;
import com.rookandpawn.kami.post.Kami2NodeGeometryProvider;
import com.rookandpawn.kami.post.NodeGeometry;
import com.rookandpawn.kami.solver.Graph;
import com.rookandpawn.kami.ui.KamiImage;

/**
 * Context that can be passed around that contains information about the state
 * of the puzzle as it is being parsed and solved
 */
public class PuzzleContext {


  private static final int puzzleWidth = 10;
  private static final int puzzleHeight1 = 16;
  private static final int puzzleHeight2 = 29;


  private final KamiImage rawImage;

  private HudOrientation hudOrientation;
  private KamiVersion version;

  private KamiImage hudImage;
  private KamiImage playAreaImage;

  private Palette palette;
  
  private EdgeMap playAreaEdgeMap;
  private boolean[][] playAreadEdges;

  private int[][] tileClassification;
  private Integer[][] memberships;

  private Graph graph;

  private final Map<Short,NodeGeometry> nodeGeometryCache = Maps.newHashMap();

  public PuzzleContext(KamiImage rawImage) {
    this.rawImage = new KamiImage(rawImage);
  }

  /**
   * @return the rawImage
   */
  public KamiImage getRawImage() {
    return rawImage;
  }

  /**
   * @return the hudOrientation
   */
  public HudOrientation getHudOrientation() {
    return hudOrientation;
  }

  /**
   * @param hudOrientation the hudOrientation to set
   */
  public void setHudOrientation(
      HudOrientation hudOrientation) {
    this.hudOrientation = hudOrientation;
  }

  /**
   * @return the hudImage
   */
  public KamiImage getHudImage() {
    return hudImage;
  }

  /**
   * @param hudImage the hudImage to set
   */
  public void setHudImage(KamiImage hudImage) {
    this.hudImage = hudImage;
  }

  /**
   * @return the playAreaImage
   */
  public KamiImage getPlayAreaImage() {
    return playAreaImage;
  }

  /**
   * @param playAreaImage the playAreaImage to set
   */
  public void setPlayAreaImage(
      KamiImage playAreaImage) {
    this.playAreaImage = playAreaImage;
  }

  /**
   * @return the version
   */
  public KamiVersion getVersion() {
    return version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(KamiVersion version) {
    this.version = version;
  }

  /**
   * @return the palette
   */
  public Palette getPalette() {
    return palette;
  }

  /**
   * @param palette the palette to set
   */
  public void setPalette(Palette palette) {
    this.palette = palette;
  }

  /**
   * @return the playAreaEdgeMap
   */
  public EdgeMap getPlayAreaEdgeMap() {
    return playAreaEdgeMap;
  }

  /**
   * @param playAreaEdgeMap the playAreaEdgeMap to set
   */
  public void setPlayAreaEdgeMap(
      EdgeMap playAreaEdgeMap) {
    this.playAreaEdgeMap = playAreaEdgeMap;
  }

  /**
   * @return the playAreadEdges
   */
  public boolean[][] getPlayAreadEdges() {
    return playAreadEdges;
  }

  /**
   * @param playAreadEdges the playAreadEdges to set
   */
  public void setPlayAreadEdges(boolean[][] playAreadEdges) {
    this.playAreadEdges = playAreadEdges;
  }

  /**
   * @return the tileClassification
   */
  public int[][] getTileClassification() {
    return tileClassification;
  }

  /**
   * @param tileClassification the tileClassification to set
   */
  public void setTileClassification(int[][] tileClassification) {
    this.tileClassification = tileClassification;
  }

  /**
   * @return the memberships
   */
  public Integer[][] getMemberships() {
    return memberships;
  }

  /**
   * @param memberships the memberships to set
   */
  public void setMemberships(Integer[][] memberships) {
    this.memberships = memberships;
  }

  /**
   * @return the graph
   */
  public Graph getGraph() {
    return graph;
  }

  /**
   * @param graph the graph to set
   */
  public void setGraph(Graph graph) {
    this.graph = graph;
  }

  /**
   * Get the geometry for the given node
   * @param node
   * @return
   */
  public NodeGeometry getNodeGeometry(short node) {
    return nodeGeometryCache.computeIfAbsent(node, (Short nodeToCheck) -> {
      switch (version) {
        case VERSION_1: {
            return new Kami1NodeGeometryProvider().getGeometry(
                PuzzleContext.this
                , nodeToCheck);
        }
        case VERSION_2: {
          return new Kami2NodeGeometryProvider().getGeometry(
              PuzzleContext.this
              , nodeToCheck);
        }
      }
      return null;
    });
  }

  /**
   * @param row row of the tile in the puzzle
   * @param col column of the tile in the puzzle
   * @param node node id
   * @return true if the the tile's membership equals the given node
   */
  public boolean isTileInNode(int row, int col, short node) {

    if (row < 0 || col < 0 || col >= puzzleWidth) {
      return false;
    }

    if ((version == KamiVersion.VERSION_1 && row >= puzzleHeight1)
        || (version == KamiVersion.VERSION_2 && row >= puzzleHeight2)) {
      return false;
    }

    Integer membership = memberships[row][col];

    if (membership == null) {
      return false;
    }

    return membership == node;
  }
}
