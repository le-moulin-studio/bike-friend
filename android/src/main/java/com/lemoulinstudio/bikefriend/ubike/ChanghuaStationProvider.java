package com.lemoulinstudio.bikefriend.ubike;

import com.lemoulinstudio.bikefriend.InternetStationProvider;
import com.lemoulinstudio.bikefriend.Utils;

/**
 *
 * @author Vincent Cantin
 */
public class ChanghuaStationProvider extends InternetStationProvider<YouBikeStation> {
  
  public ChanghuaStationProvider() {
    super(Utils.shouldDisplayChineseLocationsAndAddresses() ?
            "http://chcg.youbike.com.tw/cht/f12.php?loc=chcg" :
            "http://chcg.youbike.com.tw/en/f12.php?loc=chcg",
          new YouBikeStationHtmlParserV2());
  }
  
}
