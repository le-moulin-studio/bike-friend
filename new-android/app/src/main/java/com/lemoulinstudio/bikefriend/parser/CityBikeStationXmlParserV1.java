package com.lemoulinstudio.bikefriend.parser;

import android.util.Xml;

import com.lemoulinstudio.bikefriend.Utils;
import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * This parser extracts information from a data source in XML.
 *
 * @author Vincent Cantin
 */
public class CityBikeStationXmlParserV1 implements BikeStationParser {

    // No namespace.
  private final String ns = null;

  private DataSourceEnum dataSource;

  public void setDataSource(DataSourceEnum dataSource) {
    this.dataSource = dataSource;
  }
  
  @Override
  public List<BikeStation> parse(InputStream in) throws IOException, ParsingException {
    try {
      XmlPullParser parser = Xml.newPullParser();
      parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
      parser.setInput(in, null);
      
      // Move to the interesting part.
      while (parser.getEventType() != XmlPullParser.START_TAG ||
              !parser.getName().equals("BIKEStation")) {
        parser.next();
      }
      
      return readStations(parser);
    }
    catch (XmlPullParserException e) {
      throw new ParsingException(e);
    }
    finally {
      in.close();
    }
  }

  private List<BikeStation> readStations(XmlPullParser parser) throws XmlPullParserException, IOException {
    List<BikeStation> stations = new ArrayList<BikeStation>();

    parser.require(XmlPullParser.START_TAG, ns, "BIKEStation");
    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() == XmlPullParser.START_TAG) {
        if (parser.getName().equals("Station")) {
            BikeStation station = readStation(parser);
          if (station.isValid()) {
            stations.add(station);
          }
        }
        else {
          skip(parser);
        }
      }
    }

    return stations;
  }
  
  private BikeStation readStation(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, ns, "Station");
    
    BikeStation station = new BikeStation();
    station.dataSource = dataSource;
    station.lastUpdate = new Date();

    while (parser.nextTag() != XmlPullParser.END_TAG) {
      String tagName = parser.getName();
      
      if (tagName.equals("StationID")) {
        station.id = dataSource.idPrefix + safeNextText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "StationID");
      }
      else if (tagName.equals("StationName")) {
        station.chineseName = safeNextText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "StationName");
      }
      else if (tagName.equals("StationAddress")) {
        station.chineseAddress = safeNextText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "StationAddress");
      }
      else if (tagName.equals("StationDescription")) {
        station.chineseDescription = safeNextText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "StationDescription");
      }
      else if (tagName.equals("StationLon")) {
        // Note: In the xml, latitude and longitude are mixed up.
        station.latitude = Utils.parseFloat(safeNextText(parser), 0.0f);
        parser.require(XmlPullParser.END_TAG, ns, "StationLon");
      }
      else if (tagName.equals("StationLat")) {
        // Note: In the xml, latitude and longitude are mixed up.
        station.longitude = Utils.parseFloat(safeNextText(parser), 0.0f);
        parser.require(XmlPullParser.END_TAG, ns, "StationLat");
      }
      else if (tagName.equals("StationNums1")) {
        station.nbBicycles = Utils.parseInt(safeNextText(parser), -1);
        parser.require(XmlPullParser.END_TAG, ns, "StationNums1");
      }
      else if (tagName.equals("StationNums2")) {
        station.nbEmptySlots = Utils.parseInt(safeNextText(parser), -1);
        parser.require(XmlPullParser.END_TAG, ns, "StationNums2");
      }
      else {
        skip(parser);
      }
    }
    
    parser.require(XmlPullParser.END_TAG, ns, "Station");
    
    return station;
  }
  
  private String safeNextText(XmlPullParser parser)
          throws XmlPullParserException, IOException {
      String result = parser.nextText();
      if (parser.getEventType() != XmlPullParser.END_TAG) {
          parser.nextTag();
      }
      return result;
  }
  
  private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
    if (parser.getEventType() != XmlPullParser.START_TAG) {
      throw new IllegalStateException();
    }
    int depth = 1;
    while (depth != 0) {
      switch (parser.next()) {
        case XmlPullParser.END_TAG:
          depth--;
          break;
        case XmlPullParser.START_TAG:
          depth++;
          break;
      }
    }
  }
  
}
