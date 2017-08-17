package com.rookandpawn.kami.event;

import com.rookandpawn.kami.ui.KamiImage;

/**
 *
 */
public class ImageSelectedEvent {

  private final KamiImage image;

  public ImageSelectedEvent(KamiImage image) {
    this.image = image;
  }

  /**
   * @return the image
   */
  public KamiImage getImage() {
    return image;
  }

  

}
