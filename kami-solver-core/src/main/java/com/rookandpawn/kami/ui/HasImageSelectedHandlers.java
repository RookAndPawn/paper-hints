package com.rookandpawn.kami.ui;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 *
 */
public class HasImageSelectedHandlers {

  private final Set<ImageSelectedHandler> handlers = Sets.newHashSet();

  public HandlerRegistration addHandler(ImageSelectedHandler handler) {
    handlers.add(handler);
    return () -> handlers.remove(handler);
  }

  /**
   * Trigger the touch up inside event
   */
  public void selectImage(KamiImage image) {
    for (ImageSelectedHandler handler : handlers) {
      handler.onImageSelected(image);
    }
  }

}
