package com.rookandpawn.kami.post;

import com.rookandpawn.kami.parser.PuzzleContext;

/**
 *
 */
public interface NodeGeometryProvider {

  NodeGeometry getGeometry(PuzzleContext ctx, short node);

}
