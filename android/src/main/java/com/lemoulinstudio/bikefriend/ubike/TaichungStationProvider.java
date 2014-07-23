package com.lemoulinstudio.bikefriend.ubike;

import com.lemoulinstudio.bikefriend.InternetStationProvider;

/**
 *
 * @author Vincent Cantin
 */
public class TaichungStationProvider extends InternetStationProvider<YouBikeStation> {
  
  public TaichungStationProvider() {
    //super("http://chcg.youbike.com.tw/en/f12.php?loc=taichung",
    //      new YouBikeStationHtmlParserV2());
    super("http://chcg.youbike.com.tw/cht/f12.php?loc=taichung",
          new YouBikeStationHtmlParserV2());
  }
  
}
