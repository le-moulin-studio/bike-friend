package com.lemoulinstudio.bikefriend.webapp.vo;

import com.lemoulinstudio.bikefriend.webapp.entity.Station;
import java.util.Map;

/**
 *
 * @author Vincent Cantin
 */
public class StationVo {
  public String id;
  public Station.Provider provider;
  public Map<String, Station.Info> languageToInfo;
  public double[] location;
}
