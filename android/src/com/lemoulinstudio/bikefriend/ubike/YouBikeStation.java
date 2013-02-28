package com.lemoulinstudio.bikefriend.ubike;

import com.google.android.gms.maps.model.LatLng;
import com.lemoulinstudio.bikefriend.Station;
import java.util.Date;

/**
 *
 * @author Vincent Cantin
 */
public class YouBikeStation extends Station {
  
  public String chineseName;
  public String chineseAddress;
  public String englishName;
  public String englishAddress;
  public LatLng location;
  public int nbBikes;
  public int nbEmptySlots;
  public int nbTotalPlaces; // <-- Not necessarily = nbBikes + nbEmptySlots
  public Date date;
  public boolean isTestStation;
  
  public String getName(String languageCode) {
    return "zh".equals(languageCode) ? chineseName : englishName;
  }

  public String getAddress(String languageCode) {
    return "zh".equals(languageCode) ? chineseAddress : englishAddress;
  }

  public LatLng getLocation() {
    return location;
  }

  public int getNbBikes() {
    return nbBikes;
  }

  public int getNbEmptySlots() {
    return nbEmptySlots;
  }

  public int getNbTotalPlaces() {
    return nbTotalPlaces;
  }

  public Date getDate() {
    return date;
  }

  public boolean isTestStation() {
    return isTestStation;
  }

}
