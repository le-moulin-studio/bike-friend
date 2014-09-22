package com.lemoulinstudio.bikefriend.parser;

import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * This parser extracts information from the website of NewBike.
 * This is a temporary solution until I find a data source which is easier to read (JSON or XML).
 * 
 * If you work for NewBike, please let me know if you know which data source I should use, thank you.
 * I can be contacted by email: "vincent.cantin at le-moulin-studio.com" (please replace "at" by "@").
 *
 * @author Vincent Cantin
 */
public class NewBikeStationHtmlParserV1 implements BikeStationParser {

  private final SimpleDateFormat dateFormat;
  private DataSourceEnum dataSource;

  public NewBikeStationHtmlParserV1() {
    TimeZone taiwanTimeZone = TimeZone.getTimeZone("Asia/Taipei");
    this.dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    this.dateFormat.setTimeZone(taiwanTimeZone);
  }

  public void setDataSource(DataSourceEnum dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<BikeStation> parse(InputStream in) throws IOException, ParsingException {
      List<BikeStation> result= new ArrayList<BikeStation>();
      BufferedReader br = new BufferedReader(new InputStreamReader(in));

      try {
          boolean inMarkerArray = false;

          for (String line = br.readLine(); line != null; line = br.readLine()) {
              line = line.trim();

              if (!inMarkerArray) {
                  if (line.equals("var markers = [")) {
                      inMarkerArray = true;
                  }
              }
              else {
                  if (line.endsWith("];")) {
                      line.substring(0, line.length() - 2);
                      inMarkerArray = false;
                  }
                  else if (line.endsWith(",")) {
                      line.substring(0, line.length() - 1);
                  }

                  result.add(parseStation(line));
              }
          }
      }
      catch (Exception e) {
          throw new ParsingException(e);
      }
      finally {
          br.close();
      }

      return result;
  }

    private BikeStation parseStation(String jsonText) throws JSONException {
        JSONObject jsonStation = new JSONObject(jsonText);
        //Log.i(jsonStation.toString(2));

        BikeStation station = new BikeStation();
        //station.id             = dataSource.idPrefix + "";
        station.dataSource     = dataSource;
        station.lastUpdate     = new Date();
        station.chineseName    = jsonStation.optString("name", null);
        station.chineseAddress = null;
        station.englishName    = null;
        station.englishAddress = null;
        station.latitude = Float.parseFloat(jsonStation.getString("lat"));
        station.longitude = Float.parseFloat(jsonStation.getString("lng"));
        station.nbBicycles = 0;
        station.nbEmptySlots = 0;

        // TMP hack, until there are normal IDs.
        station.id = dataSource.idPrefix + station.latitude + station.longitude;

        return station;
    }

}
