package com.lemoulinstudio.bikefriend.webapp.cbike;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Vincent Cantin
 */
@XmlRootElement(name = "Station")
public class XmlStation {
  
  @XmlElement(name = "StationName")
  public String name;
  
  @XmlElement(name = "StationAddress")
  public String address;
  
  @XmlElement(name = "StationDesc")
  public String description;
  
  @XmlElement(name = "StationLon")
  public double longitude;
  
  @XmlElement(name = "StationLat")
  public double latitude;
  
  @XmlElement(name = "StationNums1")
  public int nbBikes;
  
  @XmlElement(name = "StationNums2")
  public int nbEmptySlots;
  
}
