package com.lemoulinstudio.bikefriend.ubike;

import android.util.Log;
import android.util.Xml;
import com.google.android.gms.maps.model.LatLng;
import com.lemoulinstudio.bikefriend.InternetStationProvider;
import com.lemoulinstudio.bikefriend.StationMapActivity;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
public class YouBikeStationProvider extends InternetStationProvider<YouBikeStation> {
  
  private static URL getServiceURL() {
    try {
      return new URL("http://www.youbike.com.tw/genxml9.php");
    }
    catch (MalformedURLException ex) {
      return null;
    }
  }

  // No namespace.
  private final String ns = null;
  
  // Helpful for parsing the date.
  private SimpleDateFormat dateFormat;
  private int thisYear;
  private Date now;

  public YouBikeStationProvider() {
    super(getServiceURL());
    TimeZone taiwanTimeZone = TimeZone.getTimeZone("Asia/Taipei");
    this.dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    this.dateFormat.setTimeZone(taiwanTimeZone);
    this.thisYear = new GregorianCalendar(taiwanTimeZone).get(Calendar.YEAR);
  }
  
  private static URL stupidJokeUrl;
  
  static {
    try {
      stupidJokeUrl = new URL("http://www.youbike.com.tw/info.php");
    }
    catch (MalformedURLException ex) {
    }
  }
  
  @Override
  protected InputStream getDataStream() throws IOException {
    HttpURLConnection connection = (HttpURLConnection) stupidJokeUrl.openConnection();
    connection.setReadTimeout(10000 /* milliseconds */);
    connection.setConnectTimeout(15000 /* milliseconds */);
    connection.setRequestMethod("GET");
    connection.setDoInput(true);
    connection.connect();
    
    String cookieLine = connection.getHeaderField("Set-Cookie");
    if (cookieLine == null) {
      return null;
    }
    
    String[] cookies = cookieLine.split(";");
    String sessionCookie = null;
    for (String cookie : cookies) {
      if (cookie.startsWith("PHPSESSID=")) {
        sessionCookie = cookie.trim();
      }
    }
    
    connection.disconnect();
    
    if (sessionCookie == null) {
      return null;
    }
    
    connection = (HttpURLConnection) url.openConnection();
    connection.setReadTimeout(10000 /* milliseconds */);
    connection.setConnectTimeout(15000 /* milliseconds */);
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Cookie", sessionCookie);
    connection.setDoInput(true);
    connection.connect();

    return connection.getInputStream();
  }
  
  public List<YouBikeStation> parseStations(InputStream in) throws IOException, ParsingException {
    try {
      XmlPullParser parser = Xml.newPullParser();
      parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
      parser.setInput(in, null);
      parser.nextTag();

      now = new Date();
      
      return readMarkers(parser);
    }
    catch (XmlPullParserException e) {
      throw new ParsingException(e);
    }
    finally {
      in.close();
    }
  }

  private List<YouBikeStation> readMarkers(XmlPullParser parser) throws XmlPullParserException, IOException {
    List<YouBikeStation> stations = new ArrayList<YouBikeStation>();

    parser.require(XmlPullParser.START_TAG, ns, "markers");
    while (parser.next() != XmlPullParser.END_TAG) {
      if (parser.getEventType() == XmlPullParser.START_TAG) {
        if (parser.getName().equals("marker")) {
          YouBikeStation station = new YouBikeStation();

          // Too bad the designers of the YouBike API didn't think of having an ID field.
          station.chineseName = parser.getAttributeValue(ns, "name");
          station.chineseAddress = parser.getAttributeValue(ns, "address");
          station.englishName = parser.getAttributeValue(ns, "nameen");
          station.englishAddress = parser.getAttributeValue(ns, "addressen");
          station.location = new LatLng(
                  parseFloat(parser.getAttributeValue(ns, "lat"), 0.0f),
                  parseFloat(parser.getAttributeValue(ns, "lng"), 0.0f));
          station.nbBikes = parseInt(parser.getAttributeValue(ns, "tot"), -1);
          station.nbEmptySlots = parseInt(parser.getAttributeValue(ns, "sus"), -1);
          station.nbTotalPlaces = parseInt(parser.getAttributeValue(ns, "qqq"), -1);
          station.date = parseDate(parser.getAttributeValue(ns, "mday"));
          station.isTestStation = parseInt(parser.getAttributeValue(ns, "icon_type"), -1) == 1;
          
          if (station.isValid()) {
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

  private Date parseDate(String text) {
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
