package com.lemoulinstudio.bikefriend;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Vincent Cantin
 */
public interface StationProvider<T extends Station> {
  public List<T> getStations() throws IOException, InternetStationProvider.ParsingException;
}
