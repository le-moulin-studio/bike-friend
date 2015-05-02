package com.lemoulinstudio.bikefriend.db;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.lemoulinstudio.bikefriend.parser.BikeStationParser;
import com.lemoulinstudio.bikefriend.R;
import com.lemoulinstudio.bikefriend.parser.CityBikeStationXmlParserV1;
import com.lemoulinstudio.bikefriend.parser.PingtungBikeStationJsonParserV1;
import com.lemoulinstudio.bikefriend.parser.PingtungUrlProvider;
import com.lemoulinstudio.bikefriend.parser.StaticUrlProvider;
import com.lemoulinstudio.bikefriend.parser.UrlProvider;
import com.lemoulinstudio.bikefriend.parser.YouBikeStationHtmlParserV2;
import com.lemoulinstudio.bikefriend.parser.YouBikeStationJsonParserV1;

import java.net.URL;

public enum DataSourceEnum {

    YouBike_Taipei(
            "TPE",
            R.string.menu_place_taipei,
            BikeSystem.YouBike,
            24.970415f, 121.414665f, 25.137976f, 121.674339f,
            new StaticUrlProvider("http://opendata.dot.taipei.gov.tw/opendata/gwjs_cityhall.json"),
            new YouBikeStationJsonParserV1(),
            0),

    YouBike_Taichung(
            "TCH",
            R.string.menu_place_taichung,
            BikeSystem.YouBike,
            24.136539f, 120.638893f, 24.185213f, 120.696701f,
            new StaticUrlProvider("http://chcg.youbike.com.tw/cht/f12.php?loc=taichung"),
            new YouBikeStationHtmlParserV2(),
            0),

    YouBike_Changhua(
            "CHH",
            R.string.menu_place_changhua,
            BikeSystem.YouBike,
            23.949144f, 120.427139f, 24.093710f, 120.581413f,
            new StaticUrlProvider("http://chcg.youbike.com.tw/cht/f12.php?loc=chcg"),
            new YouBikeStationHtmlParserV2(),
            0),

    CityBike_Kaohsiung(
            "KHS",
            R.string.menu_place_kaohsiung,
            BikeSystem.CityBike,
            22.554138f, 120.213776f, 22.877678f, 120.427391f,
            new StaticUrlProvider("http://www.c-bike.com.tw/xml/stationlist.aspx"),
            new CityBikeStationXmlParserV1(),
            2 * 60 * 1000 /* 2 min */),

    PingtungBike_Pingtung(
            "PGT",
            R.string.menu_place_pingtung,
            BikeSystem.PingtungBike,
            22.657633f, 120.477760f, 22.686634f, 120.512093f,
            new PingtungUrlProvider(),
            new PingtungBikeStationJsonParserV1(),
            0);

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
    public final UrlProvider urlProvider;

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
            UrlProvider urlProvider,
            BikeStationParser parser,
            long noReloadDuration) {
        this.idPrefix = idPrefix;
        this.placeNameRes = placeNameRes;
        this.bikeSystem = bikeSystem;
        this.bounds = new LatLngBounds(new LatLng(south, west), new LatLng(north, east));
        this.urlProvider = urlProvider;
        this.parser = parser;
        this.noReloadDuration = noReloadDuration;

        parser.setDataSource(this);
    }

}
