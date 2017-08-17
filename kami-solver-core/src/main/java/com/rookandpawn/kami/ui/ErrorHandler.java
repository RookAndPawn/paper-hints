package com.rookandpawn.kami.ui;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

/**
 *
 */
public class ErrorHandler implements SubscriberExceptionHandler {

  @Override
  public void handleException(Throwable exception,
      SubscriberExceptionContext context) {
    System.out.println(exception.getMessage());
    exception.printStackTrace(System.out);
  }

}
