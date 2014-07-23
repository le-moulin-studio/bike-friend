package com.lemoulinstudio.bikefriend;

/**
 *
 * @author Vincent Cantin
 */
public class ParsingException extends Exception {
  
  public ParsingException(Throwable throwable) {
    super(throwable);
  }
  
  public ParsingException(String message) {
    super(message);
  }
  
}
  