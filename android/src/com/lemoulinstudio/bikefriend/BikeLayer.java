package com.lemoulinstudio.bikefriend;

import com.google.android.gms.maps.GoogleMap;
import com.lemoulinstudio.bikefriend.cbike.CBikeStationProvider;
import com.lemoulinstudio.bikefriend.ubike.YouBikeStationProvider;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Vincent Cantin
 */
public class BikeLayer extends MapLayer {

  public YouBikeStationProvider youBikeProvider = new YouBikeStationProvider();
  public CBikeStationProvider cBikeProvider = new CBikeStationProvider();
  private List<StationProvider<?>> stationProviders = Arrays.<StationProvider<?>>asList(
          youBikeProvider, cBikeProvider);

  public BikeLayer() {
    super(R.id.menu_layer_bike);
  }

  @Override
  public void setDisplayed(boolean b) {
    super.setDisplayed(b);
    
    for (StationProvider stationProvider : stationProviders) {
      stationProvider.notifyVisibilityChanged(b);
    }
  }

  @Override
  public void setMap(GoogleMap map) {
    super.setMap(map);

    for (StationProvider stationProvider : stationProviders) {
      stationProvider.setMap(map);
    }
  }

  @Override
  public void setStationInfoWindowsAdapter(StationInfoWindowAdapter siwa) {
    super.setStationInfoWindowsAdapter(siwa);
    
    for (StationProvider stationProvider : stationProviders) {
      stationProvider.setStationInfoWindowAdapter(siwa);
    }
  }

  @Override
  public void refresh() {
    if (map != null) {
      for (StationProvider stationProvider : stationProviders) {
        stationProvider.refreshData();
      }
    }
  }

  @Override
  public void onCameraChanged() {
    for (StationProvider stationProvider : stationProviders) {
      stationProvider.notifyCameraChanged();
    }
  }
}
