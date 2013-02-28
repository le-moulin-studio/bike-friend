package com.lemoulinstudio.bikefriend;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 *
 * @author Vincent Cantin
 */
public interface StationProvider<T extends Station> {
  public void setMap(GoogleMap map);
  public void setStationInfoWindowAdapter(StationInfoWindowAdapter siwa);
  public void notifyCameraChanged();
  public void refreshData();
  public LatLngBounds getLatLngBounds();
  public void notifyVisibilityChanged(boolean isVisible);
}
