package com.lemoulinstudio.bikefriend.cbike;

import android.util.Xml;
import com.google.android.gms.maps.model.LatLng;
import com.lemoulinstudio.bikefriend.InternetStationProvider;
import com.lemoulinstudio.bikefriend.ParsingException;
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
public class KaohsiungStationProvider extends InternetStationProvider<CBikeStation> {
  
  public KaohsiungStationProvider() {
    super("http://www.c-bike.com.tw/xml/stationlist.aspx",
          new CBikeStationXmlParserV1());
  }
  
}
