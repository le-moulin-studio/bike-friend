package com.lemoulinstudio.bikefriend.webapp.cbike;

import com.lemoulinstudio.bikefriend.webapp.StationParser;
import com.lemoulinstudio.bikefriend.webapp.entity.Station;
import com.lemoulinstudio.bikefriend.webapp.entity.StationLog;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 *
 * @author Vincent Cantin
 */
@Component
public class CBikeStationParser implements StationParser {
  
  @Autowired
  private MongoTemplate db;
  
  private final URI uri;
  
  public CBikeStationParser() throws Exception {
    this.uri = new URI("http://www.c-bike.com.tw/xml/stationlist.aspx");
  }
  
  @Override
  public URI getDataSourceUri() {
    return uri;
  }

  @Override
  public void parseAndLogData(InputStream in) throws Exception {
    JAXBContext context = JAXBContext.newInstance(BIKEStationData.class);
    Unmarshaller unmarshaller = context.createUnmarshaller();
    Reader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
    BIKEStationData stationData = (BIKEStationData) unmarshaller.unmarshal(reader);
    
    long now = new Date().getTime();
    
    for (XmlStation xmlStation : stationData.bikeStation.stations) {
      try {
        double[] location = new double[] {xmlStation.latitude, xmlStation.longitude};

        Station station = db.findOne(new Query(Criteria
                .where("provider").is(Station.Provider.CBike)
                .and("location").is(location)),
                Station.class);
        
        // If the station doesn't exist, we create and save it.
        if (station == null) {
          Map<String, Station.Info> languageToInfo = new HashMap<>();
          languageToInfo.put("zh", new Station.Info(
                  xmlStation.name,
                  xmlStation.address,
                  xmlStation.description));

          station = new Station(Station.Provider.CBike, languageToInfo, location, false);

          db.insert(station);
        }

        db.insert(new StationLog(station.getId(), now, xmlStation.nbBikes, xmlStation.nbEmptySlots));
      }
      catch (Exception e) {
        // We got a problem with the date parsing.
      }
    }
  }
  
}
