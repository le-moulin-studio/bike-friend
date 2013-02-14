package com.lemoulinstudio.bikefriend;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Vincent Cantin
 */
public interface StationProvider<T extends Station> {
  public void fetchStations() throws IOException, InternetStationProvider.ParsingException;
  public List<T> getStations();
}
