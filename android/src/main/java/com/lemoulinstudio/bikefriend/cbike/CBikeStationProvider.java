package com.lemoulinstudio.bikefriend.cbike;

import android.util.Xml;
import com.google.android.gms.maps.model.LatLng;
import com.lemoulinstudio.bikefriend.InternetStationProvider;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Vincent Cantin
 */
public class CBikeStationProvider extends InternetStationProvider<CBikeStation> {
  
  private static URL getServiceURL() {
    try {
      return new URL("http://www.c-bike.com.tw/xml/stationlist.aspx");
    }
    catch (MalformedURLException ex) {
      return null;
    }
  }

  // No namespace.
  private final String ns = null;
  
  public CBikeStationProvider() {
    super(getServiceURL());
  }
  
  public List<CBikeStation> parseStations(InputStream in) throws IOException, ParsingException {
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

  private List<CBikeStation> readStations(XmlPullParser parser) throws XmlPullParserException, IOException {
    List<CBikeStation> stations = new ArrayList<CBikeStation>();

    parser.require(XmlPullParser.START_TAG, ns, "BIKEStation");
    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() == XmlPullParser.START_TAG) {
        if (parser.getName().equals("Station")) {
          CBikeStation station = readStation(parser);
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
  
  private CBikeStation readStation(XmlPullParser parser) throws XmlPullParserException, IOException {
    parser.require(XmlPullParser.START_TAG, ns, "Station");
    
    CBikeStation station = new CBikeStation();
    float latitude = 0.0f;
    float longitude = 0.0f;
    while (parser.nextTag() != XmlPullParser.END_TAG) {
      String tagName = parser.getName();
      
      if (tagName.equals("StationID")) {
        station.id = parseInt(safeNextText(parser), -1);
        parser.require(XmlPullParser.END_TAG, ns, "StationID");
      }
      else if (tagName.equals("StationName")) {
        station.name = safeNextText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "StationName");
      }
      else if (tagName.equals("StationAddress")) {
        station.address = safeNextText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "StationAddress");
      }
      else if (tagName.equals("StationDescription")) {
        station.description = safeNextText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "StationDescription");
      }
      else if (tagName.equals("StationLon")) {
        latitude = parseFloat(safeNextText(parser), 0.0f);
        parser.require(XmlPullParser.END_TAG, ns, "StationLon");
      }
      else if (tagName.equals("StationLat")) {
        longitude = parseFloat(safeNextText(parser), 0.0f);
        parser.require(XmlPullParser.END_TAG, ns, "StationLat");
      }
      else if (tagName.equals("StationNums1")) {
        station.nbBikes = parseInt(safeNextText(parser), -1);
        parser.require(XmlPullParser.END_TAG, ns, "StationNums1");
      }
      else if (tagName.equals("StationNums2")) {
        station.nbEmptySlots = parseInt(safeNextText(parser), -1);
        parser.require(XmlPullParser.END_TAG, ns, "StationNums2");
      }
      else {
        skip(parser);
      }
    }
    
    parser.require(XmlPullParser.END_TAG, ns, "Station");
    
    // Note: In the xml, latitude and longitude are mixed up.
    station.location = new LatLng(latitude, longitude);
    
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
