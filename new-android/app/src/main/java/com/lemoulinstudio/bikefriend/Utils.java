package com.lemoulinstudio.bikefriend;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.lemoulinstudio.bikefriend.db.BikeStation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Locale;

/**
 *
 * @author Vincent Cantin
 */
public class Utils {
  
  /**
   * Reads fully a stream of UTF-8 text, and make it a String object.
   */
  public static String readToString(InputStream in) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    int nbRead;
    byte[] buffer = new byte[1024];
    while ((nbRead = in.read(buffer)) != -1) {
      baos.write(buffer, 0, nbRead);
    }
    
    return new String(baos.toByteArray(), "UTF-8");
  }
  
  public static LatLngBounds computeBounds(Collection<BikeStation> stations) {
    if (stations.isEmpty()) {
      return null;
    }
    
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    for (BikeStation station : stations) {
      builder.include(new LatLng(station.latitude, station.longitude));
    }
    return builder.build();
  }
  
  public static boolean intersects(LatLngBounds bb1, LatLngBounds bb2) {
    // b1:  +------+
    // b2:     +------+
    // intersection: b1.min < b2.max && b2.min < b1.max
    return (bb1.southwest.latitude < bb2.northeast.latitude &&
            bb2.southwest.latitude < bb1.northeast.latitude &&
            bb1.southwest.longitude < bb2.northeast.longitude &&
            bb2.southwest.longitude < bb1.northeast.longitude);
  }
  
  public static int parseInt(String text, int defaultValue) {
    try {
      return Integer.parseInt(text);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public static float parseFloat(String text, float defaultValue) {
    try {
      return Float.parseFloat(text);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
  
  public static URL toUrl(String urlString) {
    try {
      return new URL(urlString);
    }
    catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static boolean shouldDisplayChineseLocationsAndAddresses() {
    String languageCode = Locale.getDefault().getLanguage();
    return "zh".equals(languageCode) || "ja".equals(languageCode);
  }
  
}
