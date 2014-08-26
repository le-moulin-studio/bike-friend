package com.lemoulinstudio.bikefriend;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.internal.du;
import com.google.android.gms.maps.model.LatLngBounds;
import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BikeStationProviderImpl implements BikeStationProvider {

    protected final DataSourceEnum dataSource;
    protected long dataObsolescenceInMs; // This is tweaked by the user in the settings.

    protected List<BikeStation> bikeStationList;
    protected LatLngBounds bounds;
    protected Date lastUpdateDate;

    public BikeStationProviderImpl(
            DataSourceEnum dataSource,
            Date lastUpdateDate) {
        this.dataSource = dataSource;
        this.dataObsolescenceInMs = 60 * 1000; // 1 min
        this.bikeStationList = new ArrayList<BikeStation>();
        this.lastUpdateDate = lastUpdateDate;
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

        if (duration >= dataObsolescenceInMs && duration >= dataSource.noReloadDurationInMs) {
            Log.d(BikefriendApplication.TAG, "duration = " + duration);
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

    // This is called by the download task.
    private synchronized void setUpdatedData(List<BikeStation> stations) {
        Log.d(BikefriendApplication.TAG, dataSource.name() + " setUpdatedData()");

        if (stations != null) {
            bikeStationList = stations;
            bounds = Utils.computeBounds(stations);
            lastUpdateDate = new Date();
        }
        isFetchingStations = false;

        for (BikeStationListener listener : listeners) {
            listener.onBikeStationUpdated(BikeStationProviderImpl.this);
        }
    }

    @Override
    public List<BikeStation> getBikeStationList() {
        return bikeStationList;
    }

    private class DownloadStationDataAsyncTask extends AsyncTask<BikeStationProvider, Void, List<BikeStation>> {

        private boolean networkProblem;
        private boolean parsingProblem;

        protected InputStream getDataStream() throws IOException {
            HttpURLConnection connection = (HttpURLConnection) dataSource.url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
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

                return dataSource.parser.parse(dataStream);
            }
            catch (IOException e) {
                Log.d(BikefriendApplication.TAG, "Network problem.", e);
                networkProblem = true;
            }
            catch (ParsingException e) {
                Log.d(BikefriendApplication.TAG, "Parsing problem.", e);
                parsingProblem = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<BikeStation> stations) {
            // TODO: Notification to tell the user that there is a problem getting the data.
            if (networkProblem) {
            }
            else if (parsingProblem) {
            }

            setUpdatedData(stations);
        }
    }

}
