package com.lemoulinstudio.bikefriend.ev;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lemoulinstudio.bikefriend.MapLayer;
import com.lemoulinstudio.bikefriend.R;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vincent Cantin
 */
public class EVLayer extends MapLayer {

  private List<Marker> markers;

  public EVLayer() {
    super(R.id.menu_layer_ev);

    this.markers = new ArrayList<Marker>();
  }

  @Override
  public void setDisplayed(boolean b) {
    super.setDisplayed(b);
    
    for (Marker marker : markers) {
      marker.setVisible(isDisplayed);
    }
  }
  
  @Override
  public void setMap(GoogleMap map) {
    super.setMap(map);
    
    markers.clear();

    for (RechargeStation station : RechargeStation.rechargeStations) {
      MarkerOptions markerOptions = new MarkerOptions()
              .position(station.location)
              .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ev_parking));

      Marker marker = map.addMarker(markerOptions);
      marker.setVisible(isDisplayed);
      markers.add(marker);
      //siwa.bindMarkerToStation(marker, station);
    }
  }
}
