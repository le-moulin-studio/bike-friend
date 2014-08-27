package com.lemoulinstudio.bikefriend.parser;

public class ParsingException extends Exception {
  
  public ParsingException(Throwable throwable) {
    super(throwable);
  }
  
  public ParsingException(String message) {
    super(message);
  }
  
}
  
