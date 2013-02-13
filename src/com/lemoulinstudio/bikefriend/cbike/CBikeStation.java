package com.lemoulinstudio.bikefriend.cbike;

import com.google.android.gms.maps.model.LatLng;
import com.lemoulinstudio.bikefriend.Station;
import java.net.URL;
import java.util.Date;

/**
 *
 * @author Vincent Cantin
 */
public class CBikeStation extends Station {
  
  public int id;
  public String name;
  public String address;
  public String description;
  public URL[] pictureUrls;      // [small, medium, large]
  public LatLng location;
  public int nbBikes;
  public int nbEmptySlots;
  public Date date;

  public CBikeStation() {
    this.pictureUrls = new URL[3];
    this.date = new Date();
  }

  public int getId() {
    return id;
  }

  public String getName(String languageCode) {
    return name;
  }

  public String getAddress(String languageCode) {
    return address;
  }

  public String getDescription() {
    return description;
  }

  public URL[] getPictureUrls() {
    return pictureUrls;
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

  public Date getDate() {
    return date;
  }

  public boolean isTestStation() {
    return false;
  }
  
}
