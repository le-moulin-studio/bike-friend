package com.lemoulinstudio.bikefriend;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vincent Cantin
 */
public abstract class InternetStationProvider<T extends Station> implements StationProvider<T> {
  
  private List<T> stations = new ArrayList<T>();
  
  public static class ParsingException extends Exception {
    public ParsingException(Throwable throwable) {
      super(throwable);
    }
  }
  
  private final URL url;

  public InternetStationProvider(URL url) {
    this.url = url;
  }
  
  protected InputStream getDataStream() throws IOException {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setReadTimeout(10000 /* milliseconds */);
      connection.setConnectTimeout(15000 /* milliseconds */);
      connection.setRequestMethod("GET");
      connection.setDoInput(true);
      connection.connect();
      
      return connection.getInputStream();
  }
  
  protected int parseInt(String text, int defaultValue) {
    try {
      return Integer.parseInt(text);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  protected float parseFloat(String text, float defaultValue) {
    try {
      return Float.parseFloat(text);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
  
  // Note: this method should close the stream after it finished using it.
  protected abstract List<T> parseStations(InputStream in) throws IOException, ParsingException;

  public void fetchStations() throws IOException, ParsingException {
    InputStream in = getDataStream();
    stations = parseStations(in);
  }
  
  public List<T> getStations() {
    return stations;
  }
  
}
