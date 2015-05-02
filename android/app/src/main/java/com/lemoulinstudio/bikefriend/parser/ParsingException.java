package com.lemoulinstudio.bikefriend.parser;

public class ParsingException extends Exception {

  public final String textToParse;

  public ParsingException(String textToParse, Throwable throwable) {
    super(throwable);
      this.textToParse = textToParse;
  }
  
  public ParsingException(Throwable throwable) {
    super(throwable);
    this.textToParse = null;
  }

  public ParsingException(String message) {
    super(message);
    this.textToParse = null;
  }

}
