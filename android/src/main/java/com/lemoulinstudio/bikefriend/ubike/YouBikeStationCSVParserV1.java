package com.lemoulinstudio.bikefriend.ubike;

import com.google.android.gms.maps.model.LatLng;
import com.lemoulinstudio.bikefriend.StationParser;
import com.lemoulinstudio.bikefriend.ParsingException;
import com.lemoulinstudio.bikefriend.Utils;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This parser extracts information from a data source in a format similar to CSV.
 * In this format, the lines are separated by a "|" and the values are separated by a "_".
 *
 * @author Vincent Cantin
 */
public class YouBikeStationCSVParserV1 implements StationParser<YouBikeStation> {

  @Override
  public List<YouBikeStation> parse(InputStream in) throws IOException, ParsingException {
    List<YouBikeStation> result = new ArrayList<YouBikeStation>();
    
    try {
      Date now = new Date();
      
      String rawData = Utils.readToString(in);
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
