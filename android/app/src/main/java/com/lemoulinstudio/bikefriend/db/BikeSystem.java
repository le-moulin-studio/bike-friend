package com.lemoulinstudio.bikefriend.db;

import com.lemoulinstudio.bikefriend.R;

public enum BikeSystem {

    YouBike(R.drawable.map_marker_youbike),
    NewBike(R.drawable.map_marker_newbike),
    CityBike(R.drawable.map_marker_citybike);

    public final int mapMarkerResource;

    BikeSystem(int mapMarkerResource) {
        this.mapMarkerResource = mapMarkerResource;
    }

}
