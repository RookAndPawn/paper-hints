
package com.rookandpawn.kami.parser;

/**
 * Exception specific to parsing
 */
public class ParseException extends RuntimeException {

  public ParseException(String message) {
    super(message);
  }

  public ParseException(String message, Throwable cause) {
    super(message, cause);
  }

}
