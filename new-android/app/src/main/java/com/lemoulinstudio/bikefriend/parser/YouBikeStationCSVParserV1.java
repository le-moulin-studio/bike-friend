package com.lemoulinstudio.bikefriend.parser;

import com.lemoulinstudio.bikefriend.BikeStationParser;
import com.lemoulinstudio.bikefriend.ParsingException;
import com.lemoulinstudio.bikefriend.Utils;
import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;

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
public class YouBikeStationCSVParserV1 implements BikeStationParser {

  private DataSourceEnum dataSource;

  public void setDataSource(DataSourceEnum dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<BikeStation> parse(InputStream in) throws IOException, ParsingException {
    List<BikeStation> result = new ArrayList<BikeStation>();
    
    try {
      Date now = new Date();
      
      String rawData = Utils.readToString(in);
      String[] lines = rawData.split("\\|");
      for (String line : lines) {
        //Log.i("bikefriend", "line = " + line);
        String[] elements = line.split("_", -1);
        //Log.i("bikefriend", "elements = " + Arrays.toString(elements));
        
        if (elements.length >= 11) {
          BikeStation station = new BikeStation();
          station.id = dataSource.idPrefix + elements[0];
          station.dataSource = dataSource;
          station.chineseName = elements[1];
          int nbTotalPlaces = Integer.parseInt(elements[2]);
          station.nbBicycles = Integer.parseInt(elements[3]);
          station.nbEmptySlots = nbTotalPlaces - station.nbBicycles;
          station.latitude = Float.parseFloat(elements[5]);
          station.longitude = Float.parseFloat(elements[6]);
          station.chineseAddress = elements[7];
          station.englishName = elements[9];
          station.englishAddress = elements[10];

          station.lastUpdate = now;

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
