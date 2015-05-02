package com.lemoulinstudio.bikefriend.parser;

import com.lemoulinstudio.bikefriend.Utils;
import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * This parser extracts information from a data source in JSON.
 *
 * @author Vincent Cantin
 */
public class PingtungBikeStationJsonParserV1 implements BikeStationParser {

  private DataSourceEnum dataSource;

  public void setDataSource(DataSourceEnum dataSource) {
      this.dataSource = dataSource;
  }

  @Override
  public List<BikeStation> parse(InputStream in) throws IOException, ParsingException {
    List<BikeStation> result = new ArrayList<BikeStation>();
    
    try {
      String jsonString = Utils.readToString(in);
      JSONArray jsonStations = new JSONArray(jsonString);
      for (int i = 0; i < jsonStations.length(); i++) {
        try {
          JSONObject jsonStation = jsonStations.getJSONObject(i);
          BikeStation station = new BikeStation();
          station.id             = dataSource.idPrefix + i; // + jsonStation.getString("StationName");
          station.dataSource     = dataSource;
          station.lastUpdate     = new Date();
          station.chineseName    = jsonStation.getString("StationName");
          station.chineseAddress = jsonStation.getString("StationLocation");
          station.latitude = Float.parseFloat(jsonStation.getString("Latitude"));
          station.longitude = Float.parseFloat(jsonStation.getString("Longitude"));
          station.nbBicycles = jsonStation.getInt("BikeNum");
          station.nbEmptySlots = jsonStation.getInt("FreePlace");

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
