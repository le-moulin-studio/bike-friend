package com.lemoulinstudio.bikefriend;

import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vincent Cantin
 */
public abstract class InternetStationProvider<T extends Station> implements StationProvider<T> {
  
  private final URL url;
  private final StationParser<T> stationParser;
  private GoogleMap map;
  private StationInfoWindowAdapter siwa;
  private LatLngBounds bounds;
  private final List<Marker> markers;
  private boolean stationsNeedRefresh;
  private boolean isFetchingStations;
  private boolean isVisible;
  
  protected InternetStationProvider(String urlString, StationParser<T> stationParser) {
    this.url = Utils.toUrl(urlString);
    this.stationParser = stationParser;
    this.stationsNeedRefresh = true;
    this.isFetchingStations = false;
    this.isVisible = true;
    this.markers = new ArrayList<Marker>();
  }
  
  @Override
  public void setMap(GoogleMap map) {
    this.map = map;
  }
  
  @Override
  public void setStationInfoWindowAdapter(StationInfoWindowAdapter siwa) {
    this.siwa = siwa;
  }
  
  @Override
  public void notifyCameraChanged() {
    if (!isVisible || !stationsNeedRefresh || map == null) {
      return;
    }
    
    LatLngBounds cameraBoundingBox = map.getProjection().getVisibleRegion().latLngBounds;
    if (bounds == null || Utils.intersects(cameraBoundingBox, bounds)) {
      fetchStations();
    }
  }
  
  @Override
  public void refreshData() {
    // Mark the current data as obsolete, and let the lazy loading do its work.
    stationsNeedRefresh = true;
    
    // Check if we are viewing the area right now, and refresh if it is the case.
    notifyCameraChanged();
  }
  
  @Override
  public LatLngBounds getLatLngBounds() {
    return bounds;
  }
  
  @Override
  public void notifyVisibilityChanged(boolean isVisible) {
    this.isVisible = isVisible;
    for (Marker marker : markers) {
      marker.setVisible(isVisible);
    }
    
    notifyCameraChanged();
  }
  
  @SuppressWarnings({"unchecked"})
  private synchronized void fetchStations() {
    if (!isFetchingStations) {
      isFetchingStations = true;
      stationsNeedRefresh = false;
      new DownloadStationDataAsyncTask().execute(this);
    }
  }
  
  protected InputStream getDataStream() throws IOException {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setReadTimeout(10000 /* milliseconds */);
      connection.setConnectTimeout(15000 /* milliseconds */);
      connection.setRequestMethod("GET");
      connection.setDoInput(true);
      connection.connect();
      
      return connection.getInputStream();
  }
  
  private class DownloadStationDataAsyncTask extends AsyncTask<StationProvider<T>, Void, List<T>> {

    private boolean networkProblem;
    private boolean parsingProblem;

    @Override
    protected List<T> doInBackground(StationProvider<T>... stationProviders) {
      try {
        InputStream dataStream = getDataStream();
        if (dataStream == null) {
          throw new IOException();
        }
        
        List<T> stations = stationParser.parse(dataStream);
        bounds = Utils.computeBounds(stations);
        return stations;
      }
      catch (IOException e) {
        Log.d(StationMapActivity.LOG_TAG, "Network problem.", e);
        networkProblem = true;
      }
      catch (ParsingException e) {
        Log.d(StationMapActivity.LOG_TAG, "Parsing problem.", e);
        parsingProblem = true;
      }

      return null;
    }

    @Override
    protected void onPostExecute(List<T> stations) {
      if (stations == null) {
        // TODO: Notification to tell the user that there is a problem getting the data.
        if (networkProblem) {

        }
        else if (parsingProblem) {

        }
      }
      else {
        // Remove the old markers.
        for (Marker marker : markers) {
          marker.remove();
          siwa.unbindMarker(marker);
        }
        
        markers.clear();
        
        for (Station station : stations) {
          MarkerOptions markerOptions = new MarkerOptions()
                  .position(station.getLocation())
                  .icon(BitmapDescriptorFactory.defaultMarker(
                    station.isTestStation() ?
                      BitmapDescriptorFactory.HUE_RED :
                      (station.getNbBikes() == 0 || station.getNbEmptySlots() == 0) ?
                        BitmapDescriptorFactory.HUE_ORANGE :
                        BitmapDescriptorFactory.HUE_GREEN));

          Marker marker = map.addMarker(markerOptions);
          markers.add(marker);
          siwa.bindMarkerToStation(marker, station);
        }
      }
      
      isFetchingStations = false;
    }
  }
}
