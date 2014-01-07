package com.lemoulinstudio.bikefriend;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.lemoulinstudio.bikefriend.cbike.CBikeStationProvider;
import com.lemoulinstudio.bikefriend.ubike.YouBikeStationProvider;
import java.util.Arrays;
import java.util.List;

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
  private List<StationProvider<?>> stationProviders = Arrays.<StationProvider<?>>asList(
          youBikeProvider, cBikeProvider);

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
          for (StationProvider stationProvider : stationProviders) {
            stationProvider.refreshData();
          }
        }
        return true;
      }
      case R.id.menu_place_taipei: {
        // TODO: change to fixed bouding box.
        animateCameraToBoundingBox(youBikeProvider.getLatLngBounds());
        return true;
      }
      case R.id.menu_place_taichung: {
        // TODO: change to fixed bouding box.
        //animateCameraToBoundingBox(...);
        return true;
      }
      case R.id.menu_place_kaohsiung: {
        // TODO: change to fixed bouding box.
        animateCameraToBoundingBox(cBikeProvider.getLatLngBounds());
        return true;
      }
      case R.id.menu_place_tainan: {
        // TODO: change to fixed bouding box.
        //animateCameraToBoundingBox(...);
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
        
        for (StationProvider stationProvider : stationProviders) {
          stationProvider.setMap(map);
          stationProvider.setStationInfoWindowAdapter(siwa);
        }
        
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
          public void onCameraChange(CameraPosition cp) {
            for (StationProvider stationProvider : stationProviders) {
              stationProvider.notifyCameraChanged();
            }
          }
        });
      }
    }
  }

  private void animateCameraToBoundingBox(LatLngBounds bounds) {
    if (bounds != null) {
      map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }
  }
  
}
