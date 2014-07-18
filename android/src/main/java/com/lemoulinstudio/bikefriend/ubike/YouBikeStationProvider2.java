package com.lemoulinstudio.bikefriend.ubike;

import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.lemoulinstudio.bikefriend.InternetStationProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Vincent Cantin
 */
public class YouBikeStationProvider2 extends InternetStationProvider<YouBikeStation> {
  
  private static URL getServiceURL() {
    try {
      return new URL("http://its.taipei.gov.tw/atis_index/aspx/Youbike.aspx?Mode=1");
    }
    catch (MalformedURLException ex) {
      return null;
    }
  }

  public YouBikeStationProvider2() {
    super(getServiceURL());
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
      Date now = new Date();
      
      String rawData = readToString(in);
      String[] lines = rawData.split("\\|");
      for (String line : lines) {
        //Log.i("bikefriend", "line = " + line);
        String[] elements = line.split("_");
        //Log.i("bikefriend", "elements = " + Arrays.toString(elements));
        
        if (elements.length >= 11) {
          YouBikeStation station = new YouBikeStation();
          station.id = elements[0];
          station.chineseName = elements[1];
          station.nbTotalPlaces = Integer.parseInt(elements[2]);
          station.nbBikes = Integer.parseInt(elements[3]);
          station.location = new LatLng(
                    Double.parseDouble(elements[5]),
                    Double.parseDouble(elements[6]));
          station.chineseAddress = elements[7];
          station.englishName = elements[9];
          station.englishAddress = elements[10];

          station.nbEmptySlots = station.nbTotalPlaces - station.nbBikes;
          station.date = now;
          station.isTestStation = false;

          result.add(station);
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
