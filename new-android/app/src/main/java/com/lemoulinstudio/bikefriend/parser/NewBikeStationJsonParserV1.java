package com.lemoulinstudio.bikefriend.parser;

import android.util.Log;

import com.lemoulinstudio.bikefriend.Utils;
import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This parser extracts information related to the NewBike stations,
 * from New Taipei City's opendata website.
 *
 * @author Vincent Cantin
 */
public class NewBikeStationJsonParserV1 implements BikeStationParser {

  private DataSourceEnum dataSource;

  public void setDataSource(DataSourceEnum dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<BikeStation> parse(InputStream in) throws IOException, ParsingException {
      List<BikeStation> result = new ArrayList<BikeStation>();
      Date now = new Date();

      String jsonString = Utils.readToString(in);

      try {
          JSONArray jsonStations = new JSONArray(jsonString);
          for (int i = 0; i < jsonStations.length(); i++) {
              try {
                  JSONObject jsonStation = jsonStations.getJSONObject(i);
                  BikeStation station = new BikeStation();
                  station.id             = dataSource.idPrefix + jsonStation.getString("staNo");
                  station.dataSource     = dataSource;
                  station.lastUpdate     = now;
                  station.chineseName    = jsonStation.getString("staName");
                  station.chineseAddress = jsonStation.getString("staAddress");
                  station.englishName    = null;
                  station.englishAddress = null;
                  station.latitude = Float.parseFloat(jsonStation.getString("staLon"));
                  station.longitude = Float.parseFloat(jsonStation.getString("staLat"));
                  station.nbBicycles = jsonStation.getInt("nowNum");
                  station.nbEmptySlots = jsonStation.getInt("maxNum") - station.nbBicycles;

                  result.add(station);
              }
              catch (Exception e) {
                  // If we cannot read this station, we just skip it.
              }
          }
      }
      catch (Exception e) {
          throw new ParsingException(jsonString, e);
      }
      finally {
          in.close();
      }

      return result;
    }

}
