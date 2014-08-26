package com.lemoulinstudio.bikefriend.parser;

import com.lemoulinstudio.bikefriend.BikeStationParser;
import com.lemoulinstudio.bikefriend.ParsingException;
import com.lemoulinstudio.bikefriend.Utils;
import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;

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
public class YouBikeStationJsonParserV1 implements BikeStationParser {

  private final SimpleDateFormat dateFormat;
  private DataSourceEnum dataSource;

  public YouBikeStationJsonParserV1() {
    TimeZone taiwanTimeZone = TimeZone.getTimeZone("Asia/Taipei");
    this.dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    this.dateFormat.setTimeZone(taiwanTimeZone);
  }

  public void setDataSource(DataSourceEnum dataSource) {
      this.dataSource = dataSource;
  }

  @Override
  public List<BikeStation> parse(InputStream in) throws IOException, ParsingException {
    List<BikeStation> result = new ArrayList<BikeStation>();
    
    try {
      String jsonString = Utils.readToString(in);
      JSONObject jsonRoot = new JSONObject(jsonString);
      JSONArray jsonStations = jsonRoot.getJSONArray("retVal");
      for (int i = 0; i < jsonStations.length(); i++) {
        try {
          JSONObject jsonStation = jsonStations.getJSONObject(i);
          BikeStation station = new BikeStation();
          station.lastUpdate     = dateFormat.parse(jsonStation.getString("mday"));
          station.chineseName    = jsonStation.getString("sna");
          station.chineseAddress = jsonStation.getString("ar");
          station.englishName    = jsonStation.getString("snaen");
          station.englishAddress = jsonStation.getString("aren");
          station.latitude = Float.parseFloat(jsonStation.getString("lat"));
          station.longitude = Float.parseFloat(jsonStation.getString("lng"));
          station.nbBicycles = jsonStation.getInt("sbi");
          station.nbEmptySlots = jsonStation.getInt("bemp");

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
