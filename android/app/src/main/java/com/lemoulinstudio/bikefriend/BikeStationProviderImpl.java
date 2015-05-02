package com.lemoulinstudio.bikefriend;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;
import com.lemoulinstudio.bikefriend.parser.ParsingException;
import com.lemoulinstudio.bikefriend.preference.BikefriendPreferences_;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class BikeStationProviderImpl implements BikeStationProvider,
        SharedPreferences.OnSharedPreferenceChangeListener {

    protected final DataSourceEnum dataSource;
    protected final Dao<BikeStation, String> bikeStationDao;
    protected final Collection<BikeStation> bikeStations;
    protected LatLngBounds bounds;
    protected Date lastUpdateDate;

    protected BikefriendPreferences_ preferences;
    protected long autoRefreshMinPeriod;

    public BikeStationProviderImpl(
            DataSourceEnum dataSource,
            BikefriendPreferences_ preferences,
            Dao<BikeStation, String> bikeStationDao) {
        this.dataSource = dataSource;
        this.bikeStationDao = bikeStationDao;
        this.bikeStations = new ArrayList<BikeStation>();

        this.preferences = preferences;
        this.autoRefreshMinPeriod = Long.parseLong(preferences.autoRefreshMinPeriod().get());
        preferences.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        this.lastUpdateDate = new Date(0);

        new LoadStationsFromDbAsyncTask().execute(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(preferences.autoRefreshMinPeriod().key())) {
            autoRefreshMinPeriod = Long.parseLong(preferences.autoRefreshMinPeriod().get());
            //Log.i(BikefriendApplication.TAG, "autoRefreshMinPeriod = " + autoRefreshMinPeriod);
        }
    }

    private void updateMemFromDb() throws SQLException {
        Log.d(BikefriendApplication.TAG, dataSource.name() + " updateMemFromDb()");

        List<BikeStation> dbBikeStations = bikeStationDao.queryForEq("dataSource", dataSource);
        synchronized (bikeStations) {
            updateMemFrom(dbBikeStations, true);
        }
    }

    private void updateDbFromMem() throws SQLException {
        Log.d(BikefriendApplication.TAG, dataSource.name() + " updateDbFromMem()");

        TransactionManager.callInTransaction(bikeStationDao.getConnectionSource(),
            new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    // Wipe the previous stations of this data source from the DB.
                    DeleteBuilder<BikeStation, String> deleteBuilder = bikeStationDao.deleteBuilder();
                    deleteBuilder.where().eq("dataSource", dataSource);
                    deleteBuilder.delete();

                    // Write the stations from the memory to the DB.
                    synchronized (bikeStations) {
                        for (BikeStation bikeStation : bikeStations) {
                            bikeStationDao.createOrUpdate(bikeStation);
                        }
                    }

                    return null;
                }
            });
    }

    private void updateMemFromServers(Collection<BikeStation> otherStations) {
        updateMemFrom(otherStations, false);
    }

    private void updateMemFrom(Collection<BikeStation> otherStations, boolean isFromDb) {
        Log.d(BikefriendApplication.TAG, dataSource.name() + " updateMemFromServers()");

        for (BikeStation otherStation : otherStations) {
            BikeStation memStation = findBikeStationFromId(otherStation.id, bikeStations);

            if (memStation == null) {
                // This is a new station, we add it to the list.
                bikeStations.add(otherStation);
            } else {
                // We update the state of the memStation from the attributes of the netStation.
                memStation.updateFrom(otherStation, isFromDb);
            }
        }

        for (BikeStation memStation : bikeStations) {
            BikeStation otherStation = findBikeStationFromId(memStation.id, otherStations);

            if (otherStation == null) {
                // This station is no longer there, we can delete it.
                bikeStations.remove(memStation);
            }
        }
    }

    private BikeStation findBikeStationFromId(String id, Collection<BikeStation> stations) {
        if (id == null) {
            return null;
        }

        for (BikeStation station : stations) {
            if (id.equals(station.id)) {
                return station;
            }
        }

        return null;
    }

    protected Set<BikeStationListener> listeners = new HashSet<BikeStationListener>();

    @Override
    public void addListener(BikeStationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(BikeStationListener listener) {
        listeners.remove(listener);
    }

    private void fireOnServerNotReachable() {
        for (BikeStationListener listener : listeners) {
            listener.onServerNotReachable(this);
        }
    }

    private void fireOnParseError() {
        for (BikeStationListener listener : listeners) {
            listener.onParseError(this);
        }
    }

    private void fireOnBikeStationUpdated() {
        for (BikeStationListener listener : listeners) {
            listener.onBikeStationUpdated(this);
        }
    }

    @Override
    public DataSourceEnum getDataSourceEnum() {
        return dataSource;
    }

    @Override
    public LatLngBounds getBounds() {
        return bounds == null ? dataSource.bounds : bounds;
    }

    @Override
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    @Override
    public void notifyStationsAreWatched() {
        long now = System.currentTimeMillis();
        long lastUpdate = lastUpdateDate == null ? 0 : lastUpdateDate.getTime();
        long duration = now - lastUpdate;

        if (duration >= autoRefreshMinPeriod && duration >= dataSource.noReloadDuration) {
            //Log.d(BikefriendApplication.TAG, dataSource.name() + " duration = " + duration);
            updateData();
        }
    }

    private volatile boolean isFetchingStations;

    @Override
    public synchronized void updateData() {
        if (!isFetchingStations) {
            Log.d(BikefriendApplication.TAG, dataSource.name() + " updateData()");
            isFetchingStations = true;
            new DownloadStationDataAsyncTask().execute(this);
        }
    }

    @Override
    public Collection<BikeStation> getBikeStations() {
        return bikeStations;
    }

    private class LoadStationsFromDbAsyncTask extends AsyncTask<BikeStationProvider, Void, Void> {

        @Override
        protected Void doInBackground(BikeStationProvider... params) {
            try {
                updateMemFromDb();
            }
            catch (SQLException e) {
                Log.e(BikefriendApplication.TAG, "SQL exception", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            fireOnBikeStationUpdated();
        }
    }

    private class DownloadStationDataAsyncTask extends AsyncTask<BikeStationProvider, Void, List<BikeStation>> {

        private volatile boolean networkProblem;
        private volatile boolean parsingProblem;

        protected InputStream getDataStream() throws IOException {
            HttpURLConnection connection = (HttpURLConnection) dataSource.urlProvider.getUrl().openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(10000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            return connection.getInputStream();
        }

        @Override
        protected List<BikeStation> doInBackground(BikeStationProvider... stationProviders) {
            try {
                InputStream dataStream = getDataStream();
                if (dataStream == null) {
                    throw new IOException();
                }

                List<BikeStation> stations = dataSource.parser.parse(dataStream);

                try {
                    synchronized (bikeStations) {
                        updateMemFromServers(stations);
                        updateDbFromMem();
                    }
                }
                catch (SQLException e) {
                    Log.e(BikefriendApplication.TAG, "SQL exception", e);
                }
                bounds = Utils.computeBounds(bikeStations);
                lastUpdateDate = new Date();
                isFetchingStations = false;

                return stations;
            }
            catch (IOException e) {
                //Log.d(BikefriendApplication.TAG, "Network problem on " + dataSource.name(), e);
                networkProblem = true;
            }
            catch (ParsingException e) {
                Log.e(BikefriendApplication.TAG, "Problem while parsing " + dataSource.name() + ": " + e.getMessage(), e);
                parsingProblem = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<BikeStation> stations) {
            //Log.d(BikefriendApplication.TAG, dataSource.name() + " DownloadStationDataAsyncTask.onPostExecute()");

            if (networkProblem) {
                fireOnServerNotReachable();
            }
            else if (parsingProblem) {
                fireOnParseError();
            }
            else if (stations != null) {
                fireOnBikeStationUpdated();
            }
        }
    }

}
