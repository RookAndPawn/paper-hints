
package com.rookandpawn.kami.parser;

import com.rookandpawn.kami.EventPump;
import com.rookandpawn.kami.ui.KamiImage;

/**
 * Entrypoint for the puzzle parsing/analyzing system
 */
public class PuzzleParser {

  private final PlayAreaDetector playAreaDetector
      = new PlayAreaDetector();

  private final VersionDetector versionDetector = new VersionDetector();

  private final HudParser hudParser = new HudParser();

  private final Kami1PuzzleAreaClassifier kami1Parser = new Kami1PuzzleAreaClassifier();
  private final Kami2PuzzleAreaClassifier kami2Parser = new Kami2PuzzleAreaClassifier();

  private final GraphExtractor graphExtractor = new GraphExtractor();

  /**
   * Parse the puzzle from the given image
   * @param image screenshot of a kami 1 or 2 puzzle from an iphone or ipad
   * @param pump sink for events that need to go to the user
   * @return the puzzle context ready to be solved
   */
  public PuzzleContext parse(KamiImage image, EventPump pump) {
    PuzzleContext ctx = new PuzzleContext(image);

    pump.setStatus("Detecting Orientation");

    playAreaDetector.detectPlayArea(ctx, pump);
    versionDetector.detectVersion(ctx, pump);
    hudParser.parseHud(ctx, pump);

    switch(ctx.getVersion()) {
      case VERSION_1: {
        kami1Parser.parsePuzzleArea(ctx, pump);
        break;
      }
      case VERSION_2: {
        kami2Parser.parsePuzzleArea(ctx, pump);
        break;
      }
    }

    graphExtractor.extractGraph(ctx, pump);

    return ctx;
  }

}
