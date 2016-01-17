package com.lemoulinstudio.bikefriend.parser;

import com.lemoulinstudio.bikefriend.Utils;
import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * This parser extracts information from a data source in JSON.
 *
 * @author Vincent Cantin
 */
public class YouBikeStationJsonParserV2 implements BikeStationParser {

  private final SimpleDateFormat dateFormat;
  private DataSourceEnum dataSource;

  public YouBikeStationJsonParserV2() {
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
      JSONObject jsonStations = jsonRoot.getJSONObject("retVal");
      for (Iterator<String> it = jsonStations.keys(); it.hasNext();) {
        try {
          JSONObject jsonStation = jsonStations.getJSONObject(it.next());
          /* {
          "snaen":"Heping Chongqing Intersection",
          "sno":"0089",
          "aren":"The N.E. side of Sec. 3, Chongqing S. Rd. & Sec. 1, Heping W. Rd.",
          "mday":"20160117110935",
          "sbi":"20",
          "sareaen":"Zhongzheng Dist.",
          "ar":"重慶南路三段\/和平西路一段(東北側)",
          "lng":"121.516385",
          "sna":"和平重慶路口",
          "tot":"44",
          "bemp":"24",
          "act":"1",
          "lat":"25.027323",
          "sarea":"中正區"}
          */
          BikeStation station = new BikeStation();
          station.id             = dataSource.idPrefix + jsonStation.getString("sno");
          station.dataSource     = dataSource;
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
