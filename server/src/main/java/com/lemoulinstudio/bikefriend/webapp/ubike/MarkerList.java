package com.lemoulinstudio.bikefriend.webapp.ubike;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Vincent Cantin
 */
@XmlRootElement(name = "markers")
public class MarkerList {
  
  @XmlElement(name = "marker")
  public List<Marker> elements;
}
