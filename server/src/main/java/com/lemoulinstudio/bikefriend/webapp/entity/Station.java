package com.lemoulinstudio.bikefriend.webapp.entity;

import java.util.Map;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Vincent Cantin
 */
@Document
public class Station {
  
  public static class Info {
    public String name;
    public String address;
    public String description;

    public Info() {
    }

    public Info(String name, String address, String description) {
      this.name = name;
      this.address = address;
      this.description = description;
    }
  }
  
  public static enum Provider {
    YouBike,
    CBike;
  }
  
  // MongoDB Id.
  private ObjectId id;
  
  @Indexed
  private Provider provider;
  
  // Country code -> station info.
  private Map<String, Info> languageToInfo;
  
  // {longitude, latitude}
  @GeoSpatialIndexed
  private double[] location;
  
  // True if this station is not for public use (yet).
  private boolean isTestStation;

  public Station() {
  }

  public Station(Provider provider, Map<String, Info> languageToInfo, double[] location, boolean isTestStation) {
    this.id = new ObjectId();
    this.provider = provider;
    this.languageToInfo = languageToInfo;
    this.location = location;
    this.isTestStation = isTestStation;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public Provider getProvider() {
    return provider;
  }

  public void setProvider(Provider provider) {
    this.provider = provider;
  }

  public Map<String, Info> getLanguageToInfo() {
    return languageToInfo;
  }

  public void setLanguageToInfo(Map<String, Info> languageToInfo) {
    this.languageToInfo = languageToInfo;
  }

  public double[] getLocation() {
    return location;
  }

  public void setLocation(double[] location) {
    this.location = location;
  }

  public boolean isTestStation() {
    return isTestStation;
  }

  public void setIsTestStation(boolean isTestStation) {
    this.isTestStation = isTestStation;
  }

}
