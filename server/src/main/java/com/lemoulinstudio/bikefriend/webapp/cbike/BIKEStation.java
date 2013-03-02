package com.lemoulinstudio.bikefriend.webapp.cbike;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Vincent Cantin
 */
@XmlRootElement(name = "BIKEStation")
public class BIKEStation {
  @XmlElement(name = "Station")
  public List<XmlStation> stations;
}
