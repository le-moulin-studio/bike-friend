package com.lemoulinstudio.bikefriend;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Vincent Cantin
 */
public class StationInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
  
  private Map<Marker, Station> markerToStation = new HashMap<Marker, Station>();
  private View windowView;

  public StationInfoWindowAdapter(LayoutInflater layoutInflater) {
    windowView = layoutInflater.inflate(R.layout.station_info_window, null);
  }
  
  public void bindMarkerToStation(Marker marker, Station station) {
    markerToStation.put(marker, station);
  }

  public void unbindAllMarkers() {
    markerToStation.clear();
  }
  
  public View getInfoWindow(Marker marker) {
    Station station = markerToStation.get(marker);
    String name = station.englishName;
    
    TextView titleUi = ((TextView) windowView.findViewById(R.id.title));
    titleUi.setText(name);
    
    TextView nbBicycleUi = ((TextView) windowView.findViewById(R.id.nb_bicycles));
    nbBicycleUi.setText("Bicycles: " + station.nbBikes);
    
    TextView nbEmptySlotUi = ((TextView) windowView.findViewById(R.id.nb_empty_slots));
    nbEmptySlotUi.setText("Empty Slots: " + station.nbEmptySlots);
    
    TextView stationDataAgeUi = ((TextView) windowView.findViewById(R.id.station_data_age));
    
    String ageString;
    long age = new Date().getTime() - station.date.getTime();
    if (age < 60 * 1000) {
      ageString = String.format("%d sec(s)", age / 1000);
    }
    else if (age < 60 * 60 * 1000) {
      ageString = String.format("%d min(s)", age / (60 * 1000));
    }
    else if (age < 24 * 60 * 60 * 1000) {
      ageString = String.format("%d hour(s)", age / (60 * 60 * 1000));
    }
    else {
      ageString = String.format("%d day(s)", age / (24 * 60 * 60 * 1000));
    }
    stationDataAgeUi.setText(String.format("Station data is %s old.", ageString));
    
    return windowView;
  }

  public View getInfoContents(Marker marker) {
    return null;
  }

}
