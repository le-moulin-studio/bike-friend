package com.lemoulinstudio.bikefriend.ubike;

import com.lemoulinstudio.bikefriend.InternetStationProvider;

/**
 *
 * @author Vincent Cantin
 */
public class TaipeiStationProvider extends InternetStationProvider<YouBikeStation> {
  
  public TaipeiStationProvider() {
    //super("http://opendata.dot.taipei.gov.tw/opendata/gwjs_cityhall.json",
    //      new YouBikeStationJsonParserV1());
    
    super("http://its.taipei.gov.tw/atis_index/aspx/Youbike.aspx?Mode=1",
          new YouBikeStationCSVParserV1());
  }
  
}
