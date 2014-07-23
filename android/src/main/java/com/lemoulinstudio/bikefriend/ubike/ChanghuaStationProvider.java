package com.lemoulinstudio.bikefriend.ubike;

import com.lemoulinstudio.bikefriend.InternetStationProvider;

/**
 *
 * @author Vincent Cantin
 */
public class ChanghuaStationProvider extends InternetStationProvider<YouBikeStation> {
  
  public ChanghuaStationProvider() {
    //super("http://chcg.youbike.com.tw/en/f12.php?loc=chcg",
    //      new YouBikeStationHtmlParserV2());
    super("http://chcg.youbike.com.tw/cht/f12.php?loc=chcg",
          new YouBikeStationHtmlParserV2());
  }
  
}
