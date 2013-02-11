package com.lemoulinstudio.bikefriend;

import java.util.Date;

/**
 *
 * @author Vincent Cantin
 */
public class Station {

  public String chineseName;
  public String chineseAddress;
  
  public String englishName;
  public String englishAddress;
  
  public float latitude;
  public float longitude;
  
  public Date date;
  
  public boolean isTestStation;
  public int nbBikes;
  public int nbEmptySlots;
  public int nbTotalPlaces; // <-- Not necessarily = nbBikes + nbEmptySlots

}
