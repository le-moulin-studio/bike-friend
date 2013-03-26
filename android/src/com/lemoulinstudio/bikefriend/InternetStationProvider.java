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
  
  public static class ParsingException extends Exception {
    public ParsingException(Throwable throwable) {
      super(throwable);
    }
  }
  
  protected final URL url;
  private GoogleMap map;
  private StationInfoWindowAdapter siwa;
  private LatLngBounds bounds;
  private List<Marker> markers;
  private boolean stationsNeedRefresh;
  private boolean isFetchingStations;
  private boolean isVisible;

  
  protected InternetStationProvider(URL url) {
    this.url = url;
    this.stationsNeedRefresh = true;
    this.isFetchingStations = false;
    this.isVisible = true;
    this.markers = new ArrayList<Marker>();
  }
  
  public void setMap(GoogleMap map) {
    this.map = map;
  }
  
  public void setStationInfoWindowAdapter(StationInfoWindowAdapter siwa) {
    this.siwa = siwa;
  }
  
  public void notifyCameraChanged() {
    if (!isVisible || !stationsNeedRefresh || map == null) {
      return;
    }
    
    LatLngBounds cameraBoundingBox = map.getProjection().getVisibleRegion().latLngBounds;
    if (bounds == null || intersects(cameraBoundingBox, bounds)) {
      fetchStations();
    }
  }
  
  public void refreshData() {
    // Mark the current data as obsolete, and let the lazy loading do its work.
    stationsNeedRefresh = true;
    
    // Check if we are viewing the area right now, and refresh if it is the case.
    notifyCameraChanged();
  }
  
  public LatLngBounds getLatLngBounds() {
    return bounds;
  }
  
  public void notifyVisibilityChanged(boolean isVisible) {
    this.isVisible = isVisible;
    for (Marker marker : markers) {
      marker.setVisible(isVisible);
    }
    
    notifyCameraChanged();
  }
  
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
  
  protected int parseInt(String text, int defaultValue) {
    try {
      return Integer.parseInt(text);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  protected float parseFloat(String text, float defaultValue) {
    try {
      return Float.parseFloat(text);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
  
  protected LatLngBounds computeBounds(List<T> stations) {
    if (stations.isEmpty()) {
      return null;
    }
    
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    for (Station station : stations) {
      builder.include(station.getLocation());
    }
    return builder.build();
  }
  
  protected boolean intersects(LatLngBounds bb1, LatLngBounds bb2) {
    // b1:  +------+
    // b2:     +------+
    // intersection: b1.min < b2.max && b2.min < b1.max
    return (bb1.southwest.latitude < bb2.northeast.latitude &&
            bb2.southwest.latitude < bb1.northeast.latitude &&
            bb1.southwest.longitude < bb2.northeast.longitude &&
            bb2.southwest.longitude < bb1.northeast.longitude);
  }
  
  // Note: this method should close the stream after it finished using it.
  protected abstract List<T> parseStations(InputStream in) throws IOException, ParsingException;
  
  private class DownloadStationDataAsyncTask extends AsyncTask<StationProvider, Void, List<T>> {

    private boolean networkProblem;
    private boolean parsingProblem;

    @Override
    protected List<T> doInBackground(StationProvider... stationProviders) {
      try {
        List<T> stations = parseStations(getDataStream());
        bounds = computeBounds(stations);
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
