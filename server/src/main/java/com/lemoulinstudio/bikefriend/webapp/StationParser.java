package com.lemoulinstudio.bikefriend.webapp;

import java.io.InputStream;
import java.net.URI;

/**
 *
 * @author Vincent Cantin
 */
public interface StationParser {
  public URI getDataSourceUri();
  public void parseAndLogData(InputStream in) throws Exception;
}
