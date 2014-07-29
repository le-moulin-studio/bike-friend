package com.lemoulinstudio.bikefriend.ubike;

import com.lemoulinstudio.bikefriend.InternetStationProvider;
import com.lemoulinstudio.bikefriend.Utils;

/**
 *
 * @author Vincent Cantin
 */
public class TaichungStationProvider extends InternetStationProvider<YouBikeStation> {
  
  public TaichungStationProvider() {
    super(Utils.shouldDisplayChineseLocationsAndAddresses() ?
            "http://chcg.youbike.com.tw/cht/f12.php?loc=taichung" :
            "http://chcg.youbike.com.tw/en/f12.php?loc=taichung",
          new YouBikeStationHtmlParserV2());
  }
  
}
