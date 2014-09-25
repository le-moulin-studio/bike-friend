package com.lemoulinstudio.bikefriend.db;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.lemoulinstudio.bikefriend.parser.BikeStationParser;
import com.lemoulinstudio.bikefriend.R;
import com.lemoulinstudio.bikefriend.Utils;
import com.lemoulinstudio.bikefriend.parser.CityBikeStationXmlParserV1;
import com.lemoulinstudio.bikefriend.parser.NewBikeStationHtmlParserV1;
import com.lemoulinstudio.bikefriend.parser.NewBikeStationJsonParserV1;
import com.lemoulinstudio.bikefriend.parser.YouBikeStationHtmlParserV2;
import com.lemoulinstudio.bikefriend.parser.YouBikeStationJsonParserV1;

import java.net.URL;

public enum DataSourceEnum {

    YouBike_Taipei(
            "TPE",
            R.string.menu_place_taipei,
            BikeSystem.YouBike,
            24.979649f, 121.493065f, 25.137976f, 121.662750f,
            "http://opendata.dot.taipei.gov.tw/opendata/gwjs_cityhall.json",
            new YouBikeStationJsonParserV1(),
            0),

    NewBike_NewTaipei(
            "NTP",
            R.string.menu_place_new_taipei,
            BikeSystem.NewBike,
            24.997747f, 121.452415f, 25.029684f, 121.480370f,
            "http://data.ntpc.gov.tw/NTPC/od/data/api/2120500427/?$format=json",
            new NewBikeStationJsonParserV1(),
            0),

    YouBike_Taichung(
            "TCH",
            R.string.menu_place_taichung,
            BikeSystem.YouBike,
            24.161438f, 120.638893f, 24.178696f, 120.648705f,
            "http://chcg.youbike.com.tw/cht/f12.php?loc=taichung",
            new YouBikeStationHtmlParserV2(),
            0),

    YouBike_Changhua(
            "CHH",
            R.string.menu_place_changhua,
            BikeSystem.YouBike,
            23.956450f, 120.527466f, 24.093710f, 120.579697f,
            "http://chcg.youbike.com.tw/cht/f12.php?loc=chcg",
            new YouBikeStationHtmlParserV2(),
            0),

    CityBike_Kaohsiung(
            "KHS",
            R.string.menu_place_kaohsiung,
            BikeSystem.CityBike,
            22.554138f, 120.213776f, 22.877678f, 120.427391f,
            "http://www.c-bike.com.tw/xml/stationlist.aspx",
            new CityBikeStationXmlParserV1(),
            2 * 60 * 1000 /* 2 min */);

    /**
     * The prefix of this data source, for the bike station IDs in the DB.
     */
    public final String idPrefix;

    /**
     * The resource of the name of the place.
     */
    public final int placeNameRes;

    /**
     * The bike system used.
     */
    public final BikeSystem bikeSystem;

    /**
     * The array resource containing the bounds of the area of the data source.
     */
    public final LatLngBounds bounds;

    /**
     * The URL from which to parse the data.
     */
    public final URL url;

    /**
     * The parser used to extract the data.
     */
    public final BikeStationParser parser;

    /**
     * The minimum duration between 2 reloads.
     *
     * This limit might be requested by the server's owner, e.g. KaoHsiung for the CityBike service.
     */
    public final long noReloadDuration;

    private DataSourceEnum(
            String idPrefix,
            int placeNameRes,
            BikeSystem bikeSystem,
            float south,
            float west,
            float north,
            float east,
            String url,
            BikeStationParser parser,
            long noReloadDuration) {
        this.idPrefix = idPrefix;
        this.placeNameRes = placeNameRes;
        this.bikeSystem = bikeSystem;
        this.bounds = new LatLngBounds(new LatLng(south, west), new LatLng(north, east));
        this.url = Utils.toUrl(url);
        this.parser = parser;
        this.noReloadDuration = noReloadDuration;

        parser.setDataSource(this);
    }

}
