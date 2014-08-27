package com.lemoulinstudio.bikefriend;

public interface BikeStationListener {
    public void onServerNotReachable(BikeStationProvider bikeStationProvider);
    public void onParseError(BikeStationProvider bikeStationProvider);
    public void onBikeStationUpdated(BikeStationProvider bikeStationProvider);
}
