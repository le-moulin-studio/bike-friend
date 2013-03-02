package com.lemoulinstudio.bikefriend.webapp.cbike;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Vincent Cantin
 */
@XmlRootElement(name = "BIKEStationData")
public class BIKEStationData {
  @XmlElement(name = "BIKEStation")
  public BIKEStation bikeStation;
}
