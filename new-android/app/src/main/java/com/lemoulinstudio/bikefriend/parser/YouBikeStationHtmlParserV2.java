package com.lemoulinstudio.bikefriend.parser;

import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URLDecoder;

import org.json.JSONObject;
import org.json.JSONException;

/**
 * This parser extracts information from the website of YouBike.
 * This is a temporary solution until I find a data source which is easier to read (JSON or XML).
 * 
 * If you work for YouBike, please let me know if you know which data source I should use, thank you.
 * I can be contacted by email: "vincent.cantin at le-moulin-studio.com" (please replace "at" by "@").
 *
 * @author Vincent Cantin
 */
public class YouBikeStationHtmlParserV2 implements BikeStationParser {

  private final SimpleDateFormat dateFormat;
  private DataSourceEnum dataSource;

  public YouBikeStationHtmlParserV2() {
    TimeZone taiwanTimeZone = TimeZone.getTimeZone("Asia/Taipei");
    this.dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    this.dateFormat.setTimeZone(taiwanTimeZone);
  }

  public void setDataSource(DataSourceEnum dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<BikeStation> parse(InputStream in) throws IOException, ParsingException {
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    
    try {
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        if (line.contains("JSON.parse(")) {
          Pattern p = Pattern.compile("'([^']*)'");
          Matcher m = p.matcher(line);

          if (m.find()) {
            String encodedData = m.group(1);
            String jsonText = URLDecoder.decode(encodedData, "UTF-8");
            return parseStations(jsonText);
          }
        }
      }
    }
    catch (Exception e) {
      throw new ParsingException(e);
    }
    finally {
      br.close();
    }
    
    // If we cannot find the list, we throw an exception.
    throw new ParsingException("Data not found");
  }
  
  private List<BikeStation> parseStations(String jsonText) throws JSONException {
    List<BikeStation> result = new ArrayList<BikeStation>();
    
    JSONObject jsonRoot = new JSONObject(jsonText);
    Iterator iter = jsonRoot.keys();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      try {
        JSONObject jsonStation = jsonRoot.getJSONObject(key);
        //Log.i(jsonStation.toString(2));

        BikeStation station = new BikeStation();
        //station.id             = dataSource.idPrefix + "";
        station.dataSource     = dataSource;
        station.lastUpdate     = dateFormat.parse(jsonStation.getString("mday"));
        station.chineseName    = jsonStation.optString("sna");
        station.chineseAddress = jsonStation.optString("ar");
        station.englishName    = jsonStation.optString("snaen");
        station.englishAddress = jsonStation.optString("aren");
        station.latitude = Float.parseFloat(jsonStation.getString("lat"));
        station.longitude = Float.parseFloat(jsonStation.getString("lng"));
        station.nbBicycles = jsonStation.getInt("sbi");
        station.nbEmptySlots = jsonStation.getInt("bemp");

        // TMP hack, until there are normal IDs.
        station.id = dataSource.idPrefix + station.latitude + station.longitude;

        result.add(station);
      }
      catch (Exception e) {
        // If we cannot read this station, we just skip it.
      }
    }
    
    return result;
  }
  
}
