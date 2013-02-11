package com.lemoulinstudio.bikefriend;

import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import static com.lemoulinstudio.bikefriend.StationMapActivity.LOG_TAG;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Vincent Cantin
 */
public class DownloadStationDataAsyncTask extends AsyncTask<URL, Void, List<Station>> {

  private final GoogleMap map;
  private final StationInfoWindowAdapter siwa;
  private boolean networkProblem;
  private boolean parsingProblem;

  public DownloadStationDataAsyncTask(GoogleMap map, StationInfoWindowAdapter siwa) {
    this.map = map;
    this.siwa = siwa;
  }

  @Override
  protected List<Station> doInBackground(URL... urls) {
    try {
      URL url = new URL("http://www.youbike.com.tw/genxml.php?lat=25.041282&lng=121.54089&radius=5&mode=0");

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setReadTimeout(10000 /* milliseconds */);
      connection.setConnectTimeout(15000 /* milliseconds */);
      connection.setRequestMethod("GET");
      connection.setDoInput(true);
      connection.connect();

      StationXmlParser parser = new StationXmlParser();
      return parser.parse(connection.getInputStream());
    } catch (IOException e) {
      Log.d(LOG_TAG, "Network problem.", e);
      networkProblem = true;
    } catch (XmlPullParserException e) {
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
    } else {
      map.clear();
      siwa.unbindAllMarkers();
      
      for (Station station : stations) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(station.latitude, station.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(
                  station.isTestStation ?
                    BitmapDescriptorFactory.HUE_RED :
                    (station.nbBikes == 0 || station.nbEmptySlots == 0) ?
                      BitmapDescriptorFactory.HUE_ORANGE :
                      BitmapDescriptorFactory.HUE_GREEN));
        
        Marker marker = map.addMarker(markerOptions);
        siwa.bindMarkerToStation(marker, station);
      }
    }
  }
}
