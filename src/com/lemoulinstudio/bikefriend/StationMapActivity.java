package com.lemoulinstudio.bikefriend;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
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
  private YouBikeStationProvider youBikeProvider = new YouBikeStationProvider();
  private CBikeStationProvider cBikeProvider = new CBikeStationProvider();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.station_map);
    siwa = new StationInfoWindowAdapter(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_station_map, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_refresh: {
        if (map != null) {
          refreshData();
        }
        return true;
      }
      case R.id.menu_place_taipei: {
        animateCameraToBoundingBox(youBikeProvider);
        return true;
      }
      case R.id.menu_place_kaohsiung: {
        animateCameraToBoundingBox(cBikeProvider);
        return true;
      }
      case R.id.menu_settings: {
        return true;
      }
      case R.id.menu_about: {
        return true;
      }
      default: {
        return super.onOptionsItemSelected(item);
      }
    }
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
        map.setInfoWindowAdapter(siwa);

        refreshData();
      }
    }
  }

  private void refreshData() {
    DownloadStationDataAsyncTask task = new DownloadStationDataAsyncTask(map, siwa);
    task.execute(youBikeProvider, cBikeProvider);
  }

  private void animateCameraToBoundingBox(StationProvider<?> stationProvider) {
    if (map != null) {
      LatLngBounds.Builder builder = new LatLngBounds.Builder();
      for (Station station : stationProvider.getStations()) {
        builder.include(station.getLocation());
      }

      CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 50);
      map.animateCamera(cameraUpdate);
    }
  }
}
