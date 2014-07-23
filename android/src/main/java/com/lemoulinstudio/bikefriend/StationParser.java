package com.lemoulinstudio.bikefriend;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Vincent Cantin
 */
public interface StationParser<T extends Station> {
  // Note: this method should close the stream after it finished using it.
  public List<T> parse(InputStream in) throws IOException, ParsingException;
}
