package com.lemoulinstudio.bikefriend;

import com.google.android.gms.maps.model.LatLng;
import java.util.Date;

/**
 *
 * @author Vincent Cantin
 */
public abstract class Station {
  
  public abstract String getName(String languageCode);
  public abstract String getAddress(String languageCode);
  public abstract LatLng getLocation();
  public abstract int getNbBikes();
  public abstract int getNbEmptySlots();
  public abstract Date getDate();
  public abstract boolean isTestStation();

  public boolean isValid() {
    return (getDate() != null) &&
            (getLocation().latitude != 0.0f) &&
            (getLocation().longitude != 0.0f) &&
            (getNbBikes() != -1) &&
            (getNbEmptySlots() != -1);
  }

}
