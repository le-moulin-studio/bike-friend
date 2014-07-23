package com.lemoulinstudio.bikefriend.ubike;

import com.google.android.gms.maps.model.LatLng;
import com.lemoulinstudio.bikefriend.StationParser;
import com.lemoulinstudio.bikefriend.ParsingException;
import com.lemoulinstudio.bikefriend.Utils;
import java.io.InputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This parser extracts information from a data source in JSON.
 *
 * @author Vincent Cantin
 */
public class YouBikeStationJsonParserV1 implements StationParser<YouBikeStation> {

  private final SimpleDateFormat dateFormat;

  public YouBikeStationJsonParserV1() {
    TimeZone taiwanTimeZone = TimeZone.getTimeZone("Asia/Taipei");
    this.dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    this.dateFormat.setTimeZone(taiwanTimeZone);
  }
  
  @Override
  public List<YouBikeStation> parse(InputStream in) throws IOException, ParsingException {
    List<YouBikeStation> result = new ArrayList<YouBikeStation>();
    
    try {
      String jsonString = Utils.readToString(in);
      JSONObject jsonRoot = new JSONObject(jsonString);
      JSONArray jsonStations = jsonRoot.getJSONArray("retVal");
      for (int i = 0; i < jsonStations.length(); i++) {
        try {
          JSONObject jsonStation = jsonStations.getJSONObject(i);
          YouBikeStation station = new YouBikeStation();

          station.date           = dateFormat.parse(jsonStation.getString("mday"));
          station.chineseName    = jsonStation.getString("sna");
          station.chineseAddress = jsonStation.getString("ar");
          station.englishName    = jsonStation.getString("snaen");
          station.englishAddress = jsonStation.getString("aren");
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
    }
    catch (Exception e) {
      throw new ParsingException(e);
    }
    finally {
      in.close();
    }
    
    return result;
  }
  
}
