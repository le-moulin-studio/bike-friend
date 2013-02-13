package com.lemoulinstudio.bikefriend;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.lemoulinstudio.bikefriend.cbike.CBikeStationProvider;
import com.lemoulinstudio.bikefriend.ubike.YouBikeStationProvider;

/**
 *
 * @author Vincent Cantin
 */
public class StationMapActivity extends FragmentActivity {
  
  public static final String LOG_TAG = "bikeFriend";

  private GoogleMap map;
  private StationInfoWindowAdapter siwa;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.station_map);
  }

  @Override
  protected void onResume() {
    super.onResume();
    setupMap();
  }

  private void setupMap() {
    if (map == null) {
      map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.station_map)).getMap();
      
      if (map != null) {
        map.setMyLocationEnabled(true);
        
        siwa = new StationInfoWindowAdapter(this);
        map.setInfoWindowAdapter(siwa);
        
        DownloadStationDataAsyncTask task = new DownloadStationDataAsyncTask(map, siwa);
        task.execute(new YouBikeStationProvider(), new CBikeStationProvider());
      }
    }
  }
  
}
