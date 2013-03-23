package com.lemoulinstudio.bikefriend.ev;

import com.google.android.gms.maps.model.LatLng;

public class RechargeStation {
  
  public static RechargeStation[] rechargeStations = new RechargeStation[] {
    // Taipei city ...
    RechargeStation.create("大安森林公園地下停車場", "臺北市大安區建國南路2段2號地下", new LatLng(25.031864, 121.537285), 2),
    RechargeStation.create("龍門國中地下停車場", "臺北市大安區建國南路2段269號地下", new LatLng(25.024073, 121.538010), 2),
    RechargeStation.create("長安國小地下停車場", "臺北市中山區吉林路15號地下", new LatLng(25.050467, 121.531319), 2),
    RechargeStation.create("榮星公園地下停車場", "臺北市中山區建國北路3段39號地下", new LatLng(25.064283, 121.538269), 2),
    RechargeStation.create("東湖國小地下停車場", "臺北市內湖區東湖路115號地下", new LatLng(25.068581, 121.615524), 2),
    RechargeStation.create("臺北花木批發市場地下停車場", "臺北市文山區興隆路1段15號地下", new LatLng(25.005257, 121.539635), 2),
    RechargeStation.create("振興公園地下停車場", "臺北市北投區天母西路112號地下", new LatLng(25.118759, 121.523201), 2),
    RechargeStation.create("三張里地下停車場", "臺北市信義區松平路81號地下", new LatLng(25.030556, 121.566978), 2),
    RechargeStation.create("忠信地下停車場", "臺北市信義區松仁路2號地下", new LatLng(25.040426, 121.568275), 2),
    RechargeStation.create("信義廣場地下停車場", "臺北市信義區信義路5段11號地下", new LatLng(25.033867, 121.566498), 2),
    RechargeStation.create("春光公園地下停車場", "臺北市信義區忠孝東路5段666號地下", new LatLng(25.042486, 121.581032), 2),
    RechargeStation.create("臺北市災害應變中心地下停車場", "臺北市信義區莊敬路391巷11弄2號地下", new LatLng(25.028975, 121.565865), 2),
    RechargeStation.create("興中立體停車場", "臺北市南港區興中路44巷1號", new LatLng(25.056021, 121.606354), 2),
    RechargeStation.create("青年公園高爾夫球場地下停車場", "臺北市萬華區國興路5號地下", new LatLng(25.024857, 121.507469), 2),
    RechargeStation.create("青年公園棒球場地下停車場", "臺北市萬華區青年路69號地下", new LatLng(25.023785, 121.502907), 2),
    RechargeStation.create("峨嵋立體停車場", "臺北市萬華區峨眉街83號", new LatLng(25.044308, 121.505211), 2),
    RechargeStation.create("艋舺公園地下停車場", "臺北市萬華區西園路1段145號地下", new LatLng(25.036131, 121.499489), 2),
    RechargeStation.create("大湖公園地下停車場", "臺北市內湖區成功路5段7號", new LatLng(25.083670, 121.602112), 2),
    RechargeStation.create("民有市場地下停車場", "臺北市士林區民權東路3段140巷15號", new LatLng(25.060865, 121.546822), 2),
    RechargeStation.create("文昌國小地下停車場", "臺北市大同區文林路615巷20號地下", new LatLng(25.099293, 121.521912), 2),
    RechargeStation.create("永盛公園地下停車場", "臺北市中山區中山北路2段93巷30號地下", new LatLng(25.059277, 121.524834), 2),
    RechargeStation.create("濱江國中地下停車場", "臺北市北投區樂群2路266巷1號地下", new LatLng(25.079618, 121.561775), 2),
    RechargeStation.create("石牌國小地下停車場", "臺北市士林區致遠2路1段80號地下", new LatLng(25.114546, 121.513100), 2),
    RechargeStation.create("社子國小地下停車場", "臺北市南港區延平北路6段308號地下", new LatLng(25.091780, 121.502563), 2),
    RechargeStation.create("南港國小地下停車場", "臺北市文山區興東街59號 (地點尚待確認)", new LatLng(25.056335, 121.611404), 2),
    RechargeStation.create("景美國小地下停車場", "臺北市行政區景文街112巷2號地下", new LatLng(24.988523, 121.540306), 2),
    RechargeStation.create("興隆公園地下停車場", "臺北市北投區仙岩路128號地下 (地點尚待確認)", new LatLng(25.000647, 121.551140), 2),
    RechargeStation.create("立農公園地下停車場", "臺北市萬華區承德路7段372號地下", new LatLng(25.118484, 121.503014), 2),
    RechargeStation.create("萬華國中地下停車場", "臺北市萬華區西藏路201號地下", new LatLng(25.029261, 121.499603), 2),
    RechargeStation.create("府前地下停車場", "臺北市信義區市府路1號", new LatLng(25.036121, 121.563118), 15),
    RechargeStation.create("洛陽停車場", "臺北市萬華區環河南路1段1號", new LatLng(25.047552, 121.505005), 16),
    RechargeStation.create("新店捷運站地下停車場", "", new LatLng(24.956942, 121.537460), 6),
  };
  
  public static RechargeStation create(String name, String address) {
    RechargeStation spot = new RechargeStation();
    spot.name = name;
    spot.address = address;
    return spot;
  }

  public static RechargeStation create(String name, String address, LatLng geoLocation) {
    RechargeStation spot = new RechargeStation();
    spot.name = name;
    spot.address = address;
    spot.location = geoLocation;
    return spot;
  }

  public static RechargeStation create(String name, String address, LatLng location, int nbPlaces) {
    RechargeStation spot = new RechargeStation();
    spot.name = name;
    spot.address = address;
    spot.location = location;
    spot.nbPlaces = nbPlaces;
    return spot;
  }

  public String name;
  public String address;
  public String phoneNumber;
  
  public LatLng location;
  public Integer nbPlaces;
  
}
