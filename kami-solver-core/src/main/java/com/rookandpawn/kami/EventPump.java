package com.rookandpawn.kami;

import com.google.common.base.Optional;
import com.rookandpawn.kami.ui.KamiImage;
import com.rookandpawn.kami.ui.KamiSolverView;

/**
 * Just a class to make passing info to the ui easier
 */
public class EventPump {

  private final Optional<KamiSolverView> view;

  public EventPump(KamiSolverView view) {
    this.view = Optional.fromNullable(view);
  }

  public void showImage(KamiImage image) {
    if (view.isPresent()) {
      view.get().showImage(image);
    }
  }

  public void setStatus(String message) {
    setStatus(message, -1);
  }

  public void setStatus(String message, double progress) {
    if (view.isPresent()) {
      view.get().setStatus(message, progress);
    }
  }

  public void error(String message) {
    if (view.isPresent()) {
      view.get().indicateNotWorking();
      view.get().setStatus(message, -1);
    }
  }

}
