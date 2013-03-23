package com.lemoulinstudio.bikefriend;

import com.lemoulinstudio.bikefriend.ev.EVLayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
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
  
  private BikeLayer bikeLayer;
  private EVLayer evLayer;
  private List<MapLayer> layers;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.station_map);
    siwa = new StationInfoWindowAdapter(this);
    
    bikeLayer = new BikeLayer();
    evLayer = new EVLayer();
    layers = Arrays.asList(bikeLayer, evLayer);
    
    bikeLayer.setDisplayed(true);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_station_map, menu);
    
    for (MapLayer layer : layers) {
      MenuItem menuItem = menu.findItem(layer.getMenuId());
      menuItem.setChecked(layer.isDisplayed());
    }
    
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int menuItemId = item.getItemId();
    
    switch (menuItemId) {
      case R.id.menu_refresh: {
        for (MapLayer layer : layers) {
          layer.refresh();
        }
        return true;
      }
      case R.id.menu_place_taipei: {
        // TODO: change to fixed bouding box.
        animateCameraToBoundingBox(bikeLayer.youBikeProvider.getLatLngBounds());
        return true;
      }
      case R.id.menu_place_taichung: {
        // TODO: change to fixed bouding box.
        //animateCameraToBoundingBox(...);
        return true;
      }
      case R.id.menu_place_kaohsiung: {
        // TODO: change to fixed bouding box.
        animateCameraToBoundingBox(bikeLayer.cBikeProvider.getLatLngBounds());
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
    }
    
    // Check if the menuItem is for one of the layer.
    for (MapLayer layer : layers) {
      if (layer.getMenuId() == menuItemId) {
        layer.setDisplayed(!item.isChecked());
        item.setChecked(layer.isDisplayed());
        return true;
      }
    }
    
    return super.onOptionsItemSelected(item);
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
        
        for (MapLayer layer : layers) {
          layer.setMap(map);
          layer.setStationInfoWindowsAdapter(siwa);
        }
        
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
          public void onCameraChange(CameraPosition cp) {
            for (MapLayer layer : layers) {
              layer.onCameraChanged();
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
