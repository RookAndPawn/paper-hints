package com.rookandpawn.kami.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * This is mostly taken from
 * http://stackoverflow.com/questions/11959758
 */
public class ImagePanel extends JPanel {

  private BufferedJavaImage image;
  private Image scaled;
  private Dimension lastDim;


  private int width;
  private int height;

  private int x;
  private int y;


  public synchronized void setImage(BufferedJavaImage image) {
    this.image = image;
    this.lastDim = null;
  }

  private double getScaleFactor(int iMasterSize, int iTargetSize) {

    double dScale = 1;
    if (iMasterSize > iTargetSize) {

      dScale = (double) iTargetSize / (double) iMasterSize;

    }
    else {

      dScale = (double) iTargetSize / (double) iMasterSize;

    }

    return dScale;

  }

  private double getScaleFactorToFit(Dimension original, Dimension toFit) {

    double dScale = 1d;

    if (original != null && toFit != null) {

      double dScaleWidth = getScaleFactor(original.width, toFit.width);
      double dScaleHeight = getScaleFactor(original.height, toFit.height);

      dScale = Math.min(dScaleHeight, dScaleWidth);

    }

    return dScale;

  }

  @Override
  protected synchronized void paintComponent(Graphics g) {

    super.paintComponent(g);

    if (image == null) {
      return;
    }

    Dimension size = getSize();

    if (!size.equals(lastDim)) {
      double scaleFactor = getScaleFactorToFit(new Dimension(image.
          getWidth(), image.getHeight()), size);

      int scaleWidth = (int) Math.round(image.getWidth() * scaleFactor);
      int scaleHeight = (int) Math.round(image.getHeight() * scaleFactor);

      scaled = image.getAwtImage().getScaledInstance(
          scaleWidth, scaleHeight, Image.SCALE_SMOOTH);

      width = getWidth() - 1;
      height = getHeight() - 1;

      x = (width - scaled.getWidth(this)) / 2;
      y = (height - scaled.getHeight(this)) / 2;

      lastDim = size;
    }

    g.drawImage(scaled, x, y, this);

  }

}
