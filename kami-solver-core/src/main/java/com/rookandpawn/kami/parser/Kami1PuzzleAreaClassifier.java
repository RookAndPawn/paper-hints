package com.rookandpawn.kami.parser;

import com.google.j2objc.annotations.AutoreleasePool;
import com.rookandpawn.kami.EventPump;
import com.rookandpawn.kami.ui.Color;
import com.rookandpawn.kami.ui.KamiImage;

/**
 * Classifier for each tile in the a kami1 puzzle area
 */
public class Kami1PuzzleAreaClassifier {

  private static final double tileInsetCoef = 3.0 / 750;

  private static final int puzzleHeight = 16;
  private static final int puzzleWidth = 10;

  public void parsePuzzleArea(PuzzleContext ctx, EventPump pump) {

    pump.setStatus("Classifying Tiles");

    KamiImage workingArea = new KamiImage(ctx.getPlayAreaImage());

    int height = workingArea.getHeight();
    int width = workingArea.getWidth();

    double tileHeight = 1.0 / puzzleHeight * height;
    double tileWidth = 1.0 / puzzleWidth * width;

    int[][] tileClassification = new int[puzzleHeight][puzzleWidth];

    for (int puzzleRow = 1; puzzleRow < puzzleHeight; puzzleRow++) {

      // row in image to draw line
      int row = (int)Math.round(tileHeight * puzzleRow);

      for (int col = 0; col < width; col++) {
        workingArea.setPixel(row, col, Color.WHITE);
      }
    }

    for (int puzzleCol = 1; puzzleCol < puzzleWidth; puzzleCol++) {

      // col in image to draw line
      int col = (int)Math.round(tileWidth * puzzleCol);

      for (int row = 0; row < height; row++) {
        workingArea.setPixel(row, col, Color.WHITE);
      }
    }

    pump.showImage(workingArea);

    int borderSize = (int)Math.round(tileInsetCoef * width);

    int total = puzzleHeight * puzzleWidth - 1;

    for (int row = 0; row < puzzleHeight; row++) {
      for (@AutoreleasePool int col = 0; col < puzzleWidth; col++) {
        
        pump.setStatus("Classifying Tiles"
            , 1.0 / total * (puzzleWidth * row + col));

        int topRow = (int)Math.round(row * tileHeight) + borderSize;
        int leftCol = (int)Math.round(col * tileWidth) + borderSize;
        int bottomRow = (int)Math.round((row + 1) * tileHeight) - borderSize;
        int rightCol = (int)Math.round((col + 1) * tileWidth) - borderSize;

        KamiImage tile = GaussianBlurrer.blur(ctx.getPlayAreaImage().getRoi(
            topRow
            , leftCol
            , bottomRow
            , rightCol), 3);

        //pump.showImage(tile);

        int classification = ctx.getPalette().getBestMatch(tile, 0);

        tileClassification[row][col] = classification;
      }
    }

    ctx.setTileClassification(tileClassification);
  }
}
