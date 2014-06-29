package com.lemoulinstudio.bikefriend.ubike;

import com.google.android.gms.maps.model.LatLng;
import com.lemoulinstudio.bikefriend.InternetStationProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Vincent Cantin
 */
public class YouBikeStationProvider extends InternetStationProvider<YouBikeStation> {
  
  private static URL getServiceURL() {
    try {
      return new URL("http://opendata.dot.taipei.gov.tw/opendata/gwjs_cityhall.json");
    }
    catch (MalformedURLException ex) {
      return null;
    }
  }

  private final SimpleDateFormat dateFormat;

  public YouBikeStationProvider() {
    super(getServiceURL());
    
    TimeZone taiwanTimeZone = TimeZone.getTimeZone("Asia/Taipei");
    this.dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    this.dateFormat.setTimeZone(taiwanTimeZone);
  }
  
  private String readToString(InputStream in) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    int nbRead;
    byte[] buffer = new byte[1024];
    while ((nbRead = in.read(buffer)) != -1) {
      baos.write(buffer, 0, nbRead);
    }
    
    return new String(baos.toByteArray(), "UTF-8");
  }

  @Override
  public List<YouBikeStation> parseStations(InputStream in) throws IOException, ParsingException {
    List<YouBikeStation> result = new ArrayList<YouBikeStation>();
    
    try {
      String jsonString = readToString(in);
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
