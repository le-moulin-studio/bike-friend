package com.lemoulinstudio.bikefriend;

import com.google.android.gms.maps.model.LatLngBounds;
import com.j256.ormlite.dao.Dao;
import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface BikeStationProvider {

    /**
     * The id of this bike station provider.
     * @return The id of this bike station provider.
     */
    public DataSourceEnum getDataSourceEnum();

    /**
     * The area which covers the stations of this region.
     * @return the area which covers the stations of this provider.
     */
    public LatLngBounds getBounds();

    /**
     * The date of the last successful data update.
     * @return The date of the last time this provider successfully accessed the server to update its data.
     */
    public Date getLastUpdateDate();

    /**
     * Notifies the provider that its area is watched.
     * The provider may update its data if it thinks that it is too old.
     */
    public void notifyStationsAreWatched();

    /**
     * Explicitly requests the data to be updated.
     * This might mean that the delays between 2 updates will be disregarded.
     *
     * Note: We don't want the server to be overloaded with requests, so this
     * action should only be executed as a direct consequence of the user pressing
     * the "refresh" button. It should not be used as an automatic background update.
     */
    public void updateData();

    /**
     * Registers a listener.
     *
     * @param listener The object to be registered.
     */
    public void addListener(BikeStationListener listener);

    /**
     * Unregisters a listener.
     *
     * @param listener The object to be unregistered.
     */
    public void removeListener(BikeStationListener listener);

    /**
     *
     * @return The bike station list.
     */
    public Collection<BikeStation> getBikeStations();

}
