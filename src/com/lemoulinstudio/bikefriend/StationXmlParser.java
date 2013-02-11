package com.lemoulinstudio.bikefriend;

import android.util.Log;
import android.util.Xml;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Vincent Cantin
 */
public class StationXmlParser {

  // No namespace.
  private final String ns = null;
  
  // Helpful for parsing the date.
  private SimpleDateFormat dateFormat;
  private int thisYear;
  private Date now;

  public StationXmlParser() {
    TimeZone taiwanTimeZone = TimeZone.getTimeZone("Asia/Taipei");
    this.dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    this.dateFormat.setTimeZone(taiwanTimeZone);
    this.thisYear = new GregorianCalendar(taiwanTimeZone).get(Calendar.YEAR);
    this.now = new Date();
  }
  
  public List parse(InputStream in) throws XmlPullParserException, IOException {
    try {
      XmlPullParser parser = Xml.newPullParser();
      parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
      parser.setInput(in, null);
      parser.nextTag();

      return readMarkers(parser);
    } finally {
      in.close();
    }
  }

  private List<Station> readMarkers(XmlPullParser parser) throws XmlPullParserException, IOException {
    List<Station> stations = new ArrayList<Station>();

    parser.require(XmlPullParser.START_TAG, ns, "markers");
    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() == XmlPullParser.START_TAG) {
        if (parser.getName().equals("marker")) {
          Station station = new Station();

          // Too bad the designers of the YouBike API didn't think of having an ID field.
          station.chineseName = parser.getAttributeValue(ns, "name");
          station.chineseAddress = parser.getAttributeValue(ns, "address");
          station.englishName = parser.getAttributeValue(ns, "nameen");
          station.englishAddress = parser.getAttributeValue(ns, "addressen");
          station.latitude = readFloat(parser.getAttributeValue(ns, "lat"), -1.0f);
          station.longitude = readFloat(parser.getAttributeValue(ns, "lng"), -1.0f);
          station.date = readDate(parser.getAttributeValue(ns, "mday"));
          station.isTestStation = readInt(parser.getAttributeValue(ns, "icon_type"), -1) == 1;
          station.nbBikes = readInt(parser.getAttributeValue(ns, "tot"), -1);
          station.nbEmptySlots = readInt(parser.getAttributeValue(ns, "sus"), -1);
          station.nbTotalPlaces = readInt(parser.getAttributeValue(ns, "qqq"), -1);
          
          if (isValid(station)) {
            stations.add(station);
          }

          parser.nextTag();
          parser.require(XmlPullParser.END_TAG, ns, "marker");
        } else {
          skip(parser);
        }
      }
    }

    return stations;
  }

  private int readInt(String text, int defaultValue) {
    try {
      return Integer.parseInt(text);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  private float readFloat(String text, float defaultValue) {
    try {
      return Float.parseFloat(text);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  private Date readDate(String text) {
    try {
      // We assume that the year is this year.
      Date date = dateFormat.parse("" + thisYear + "/" + text);
      long deltaTime = now.getTime() - date.getTime();
      
      if (deltaTime >= 0) {
        return date;
      }
      
      // Handles the case where the request was made on the evening of a 31th december,
      // right before midnight. Also rejects data which is more than 7 days old.
      date = dateFormat.parse("" + (thisYear - 1) + "/" + text);
      deltaTime = now.getTime() - date.getTime();
      if (deltaTime >= 0 && deltaTime < 7 * 24 * 60 * 60 * 1000) {
        return date;
      }
    } catch (ParseException ex) {
      Log.d(StationMapActivity.LOG_TAG, "Error while parsing the date.", ex);
    }
    
    return null;
  }

  private boolean isValid(Station station) {
    return ((station.date != null) &&
            (station.latitude != -1.0f) &&
            (station.longitude != -1.0f) &&
            (station.nbBikes != -1) &&
            (station.nbEmptySlots != -1) &&
            (station.nbTotalPlaces != -1));
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
