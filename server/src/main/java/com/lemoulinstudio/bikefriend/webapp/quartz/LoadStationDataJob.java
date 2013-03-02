package com.lemoulinstudio.bikefriend.webapp.quartz;

import com.lemoulinstudio.bikefriend.webapp.StationParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.IOException;
import java.io.InputStream;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author Vincent Cantin
 */
public class LoadStationDataJob extends QuartzJobBean {
  
  private Client jerseyClient;
  
  public LoadStationDataJob() {
    ClientConfig jerseyClientConfig = new DefaultClientConfig();
    this.jerseyClient = Client.create(jerseyClientConfig);
  }

  private StationParser[] stationParserList;

  public void setStationParserList(StationParser[] stationParserList) {
    this.stationParserList = stationParserList;
  }

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    for (StationParser stationParser : stationParserList) {
      WebResource webResource = jerseyClient.resource(stationParser.getDataSourceUri());
      try (InputStream inputStream = webResource.get(InputStream.class)) {
        stationParser.parseAndLogData(inputStream);
      }
      catch (Exception e) {}
    }
  }
  
}
