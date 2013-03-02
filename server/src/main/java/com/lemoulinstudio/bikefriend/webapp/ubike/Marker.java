package com.lemoulinstudio.bikefriend.webapp.ubike;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Vincent Cantin
 */
@XmlRootElement(name = "marker")
public class Marker {
  
  @XmlAttribute(name = "name")
  public String chineseName;
  
  @XmlAttribute(name = "address")
  public String chineseAddress;
  
  @XmlAttribute(name = "nameen")
  public String englishName;
  
  @XmlAttribute(name = "addressen")
  public String englishAddress;
  
  @XmlAttribute(name = "lat")
  public double latitude;
  
  @XmlAttribute(name = "lng")
  public double longitude;
  
  @XmlAttribute(name = "tot")
  public int nbBikes;
  
  @XmlAttribute(name = "sus")
  public int nbEmptySlots;
  
  @XmlAttribute(name = "distance")
  public double distance;
  
  @XmlAttribute(name = "mday")
  public String mday;
  
  @XmlAttribute(name = "icon_type")
  public int iconType;
  
  @XmlAttribute(name = "qqq")
  public int qqq;
  
}
