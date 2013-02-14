package com.lemoulinstudio.bikefriend;

import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import static com.lemoulinstudio.bikefriend.StationMapActivity.LOG_TAG;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vincent Cantin
 */
public class DownloadStationDataAsyncTask extends AsyncTask<StationProvider, Void, List<Station>> {

  private final GoogleMap map;
  private final StationInfoWindowAdapter siwa;
  private boolean networkProblem;
  private boolean parsingProblem;

  public DownloadStationDataAsyncTask(GoogleMap map, StationInfoWindowAdapter siwa) {
    this.map = map;
    this.siwa = siwa;
  }

  @Override
  protected List<Station> doInBackground(StationProvider... stationProviders) {
    try {
      List<Station> stations = new ArrayList<Station>();
      for (StationProvider stationProvider : stationProviders) {
        stationProvider.fetchStations();
        stations.addAll(stationProvider.getStations());
      }
      return stations;
    } catch (IOException e) {
      Log.d(LOG_TAG, "Network problem.", e);
      networkProblem = true;
    } catch (InternetStationProvider.ParsingException e) {
      Log.d(LOG_TAG, "Parsing problem.", e);
      parsingProblem = true;
    }

    return null;
  }

  @Override
  protected void onPostExecute(List<Station> stations) {
    if (stations == null) {
      // TODO: Notification to tell the user that there is a problem getting the data.
      if (networkProblem) {
        
      }
      else if (parsingProblem) {
        
      }
    }
    else {
      map.clear();
      siwa.unbindAllMarkers();
      
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
        siwa.bindMarkerToStation(marker, station);
      }
    }
  }
}
