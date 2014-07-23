package com.lemoulinstudio.bikefriend.ubike;

import com.google.android.gms.maps.model.LatLng;
import com.lemoulinstudio.bikefriend.StationParser;
import com.lemoulinstudio.bikefriend.ParsingException;
import com.lemoulinstudio.bikefriend.Utils;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URLDecoder;
import org.json.JSONArray;
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
public class YouBikeStationHtmlParserV2 implements StationParser<YouBikeStation> {

  private final SimpleDateFormat dateFormat;

  public YouBikeStationHtmlParserV2() {
    TimeZone taiwanTimeZone = TimeZone.getTimeZone("Asia/Taipei");
    this.dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    this.dateFormat.setTimeZone(taiwanTimeZone);
  }
  
  @Override
  public List<YouBikeStation> parse(InputStream in) throws IOException, ParsingException {
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
  
  private List<YouBikeStation> parseStations(String jsonText) throws JSONException {
    List<YouBikeStation> result = new ArrayList<YouBikeStation>();
    
    JSONObject jsonRoot = new JSONObject(jsonText);
    Iterator iter = jsonRoot.keys();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      try {
        JSONObject jsonStation = jsonRoot.getJSONObject(key);
        //Log.i(jsonStation.toString(2));

        YouBikeStation station = new YouBikeStation();
        station.date           = dateFormat.parse(jsonStation.getString("mday"));
        station.chineseName    = jsonStation.optString("sna", "n/a");
        station.chineseAddress = jsonStation.optString("ar", "n/a");
        station.englishName    = jsonStation.optString("sna", "n/a");
        station.englishAddress = jsonStation.optString("ar", "n/a");
        station.location = new LatLng(
                Double.parseDouble(jsonStation.getString("lat")),
                Double.parseDouble(jsonStation.getString("lng")));
        station.nbBikes = jsonStation.getInt("sbi");
        station.nbEmptySlots = jsonStation.getInt("bemp");
        station.nbTotalPlaces = jsonStation.getInt("tot");

        result.add(station);
      }
      catch (Exception e) {
        // If we cannot read this station, we just skip it.
      }
    }
    
    return result;
  }
  
}
