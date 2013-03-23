package com.lemoulinstudio.bikefriend;

import com.google.android.gms.maps.GoogleMap;

/**
 *
 * @author Vincent Cantin
 */
public class MapLayer {
  
  protected int menuId;
  protected boolean isDisplayed;
  protected GoogleMap map;
  protected StationInfoWindowAdapter siwa;

  protected MapLayer(int menuId) {
    this.menuId = menuId;
  }

  public int getMenuId() {
    return menuId;
  }

  public boolean isDisplayed() {
    return isDisplayed;
  }

  public void setDisplayed(boolean b) {
    this.isDisplayed = b;
  }
  
  public void setMap(GoogleMap map) {
    this.map = map;
  }

  public void setStationInfoWindowsAdapter(StationInfoWindowAdapter siwa) {
    this.siwa = siwa;
  }
  
  public void refresh() {
  }

  public void onCameraChanged() {
  }
  
}
