package com.lemoulinstudio.bikefriend.db;

import com.lemoulinstudio.bikefriend.R;

public enum BikeSystem {

    YouBike(R.drawable.map_marker_youbike),
    CityBike(R.drawable.map_marker_citybike),
    PingtungBike(R.drawable.map_marker_pingtungbike);

    public final int mapMarkerResource;

    BikeSystem(int mapMarkerResource) {
        this.mapMarkerResource = mapMarkerResource;
    }

}
