package com.lemoulinstudio.bikefriend.webapp.ubike;

import com.lemoulinstudio.bikefriend.webapp.StationParser;
import com.lemoulinstudio.bikefriend.webapp.entity.Station;
import com.lemoulinstudio.bikefriend.webapp.entity.StationLog;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
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
public class YouBikeStationParser implements StationParser {
  
  @Autowired
  private MongoTemplate db;
  
  private final URI uri;
  
  // Helpful for parsing the date.
  private TimeZone taiwanTimeZone;
  private SimpleDateFormat dateFormat;
  private int thisYear;
  private long now;
  
  public YouBikeStationParser() throws Exception {
    this.uri = new URI("http://www.youbike.com.tw/genxml.php");
    
    this.taiwanTimeZone = TimeZone.getTimeZone("Asia/Taipei");
    this.dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    this.dateFormat.setTimeZone(taiwanTimeZone);
  }
  
  @Override
  public URI getDataSourceUri() {
    return uri;
  }

  @Override
  public void parseAndLogData(InputStream in) throws Exception {
    this.thisYear = new GregorianCalendar(taiwanTimeZone).get(Calendar.YEAR);
    this.now = new Date().getTime();
    
    JAXBContext context = JAXBContext.newInstance(MarkerList.class);
    Unmarshaller unmarshaller = context.createUnmarshaller();
    Reader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
    MarkerList markerList = (MarkerList) unmarshaller.unmarshal(reader);
    
    for (Marker marker : markerList.elements) {
      try {
        double[] location = new double[] {marker.latitude, marker.longitude};

        Station station = db.findOne(new Query(Criteria
                .where("provider").is(Station.Provider.YouBike)
                .and("location").is(location)),
                Station.class);

        // If the station doesn't exist, we create and save it.
        if (station == null) {
          Map<String, Station.Info> languageToInfo = new HashMap<>();
          languageToInfo.put("zh", new Station.Info(
                  marker.chineseName,
                  marker.chineseAddress,
                  null));
          languageToInfo.put("en", new Station.Info(
                  marker.englishName,
                  marker.englishAddress,
                  null));

          station = new Station(Station.Provider.YouBike, languageToInfo, location, marker.iconType == 1);

          db.insert(station);
        }

        db.insert(new StationLog(station.getId(), parseDate(marker.mday), marker.nbBikes, marker.nbEmptySlots));
      }
      catch (Exception e) {
        // We got a problem with the date parsing.
      }
    }
  }
  
  private long parseDate(String text) throws Exception {
    // Because the year is missing from the data from YouBike,
    // we assume that the year is this year.
    long date = dateFormat.parse("" + thisYear + "/" + text).getTime();
    
    if (date <= now + 60 * 1000) {
      return date;
    }

    // Handles the case where the request was made on the evening of a 31th december,
    // right before midnight. Also rejects data which is more than 7 days old.
    date = dateFormat.parse("" + (thisYear - 1) + "/" + text).getTime();
    if ((date >= now - 7 * 24 * 60 * 60 * 1000) && (date <= now + 60 * 1000)) {
      return date;
    }
    else {
      // This date doesn't seem to make sense, or is too old to be useful.
      throw new Exception();
    }
  }

}
