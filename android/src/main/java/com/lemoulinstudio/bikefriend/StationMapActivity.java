package com.lemoulinstudio.bikefriend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.lemoulinstudio.bikefriend.cbike.CBikeStationProvider;
import com.lemoulinstudio.bikefriend.ubike.YouBikeStationProvider;
import com.lemoulinstudio.bikefriend.ubike.YouBikeStationProvider2;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Vincent Cantin
 */
public class StationMapActivity extends FragmentActivity {

  // Tag for the debug messages of the app.
  public static final String LOG_TAG = "bikeFriend";
  
  // Keys for the settings of the app.
  private static final String CAMERA_TARGET_LAT = "cameraTargetLat";
  private static final String CAMERA_TARGET_LNG = "cameraTargetLng";
  private static final String CAMERA_ZOOM = "cameraZoom";
  private static final String CAMERA_TILT = "cameraTilt";
  private static final String CAMERA_BEARING = "cameraBearing";
  
  private GoogleMap map;
  private StationInfoWindowAdapter siwa;
  
  private StationProvider youBikeProvider = new YouBikeStationProvider2();
  private StationProvider cBikeProvider = new CBikeStationProvider();
  private List<StationProvider<?>> stationProviders = Arrays.<StationProvider<?>>asList(
          youBikeProvider, cBikeProvider);

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // Create the view.
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
        // Reads the settings.
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        float cameraTargetLat = settings.getFloat(CAMERA_TARGET_LAT, 25.030142f);
        float cameraTargetLng = settings.getFloat(CAMERA_TARGET_LNG, 121.53549f);
        float cameraZoom = settings.getFloat(CAMERA_ZOOM, 13.0f);
        float cameraTilt = settings.getFloat(CAMERA_TILT, 0.0f);
        float cameraBearing = settings.getFloat(CAMERA_BEARING, 0.0f);

        // Restores the state of the camera on the map.
        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(cameraTargetLat, cameraTargetLng),
                cameraZoom, cameraTilt, cameraBearing);
        
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        
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
  
  protected void onPause() {
    super.onPause();
    
    // Saves the settings.
    SharedPreferences settings = getPreferences(MODE_PRIVATE);
    SharedPreferences.Editor editor = settings.edit();
    
    if (map != null) {
      // Saves the state of the camera on the map.
      CameraPosition cameraPosition = map.getCameraPosition();
      editor.putFloat(CAMERA_TARGET_LAT, (float) cameraPosition.target.latitude);
      editor.putFloat(CAMERA_TARGET_LNG, (float) cameraPosition.target.longitude);
      editor.putFloat(CAMERA_ZOOM, cameraPosition.zoom);
      editor.putFloat(CAMERA_TILT, cameraPosition.tilt);
      editor.putFloat(CAMERA_BEARING, cameraPosition.bearing);
    }
    
    editor.commit();
  }

  private void animateCameraToBoundingBox(LatLngBounds bounds) {
    if (bounds != null) {
      map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }
  }
  
}
