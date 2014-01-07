package com.lemoulinstudio.bikefriend;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Vincent Cantin
 */
public class StationInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
  
  private final View windowView;
  private final Map<Marker, Station> markerToStation;
  
  private final String language;
  
  private final String nbBikeFormat;
  private final String nbEmptySlotFormat;
  private final String dataAgeSecondFormat;
  private final String dataAgeMinuteFormat;
  private final String dataAgeHourFormat;
  private final String dataAgeDayFormat;

  public StationInfoWindowAdapter(Activity activity) {
    this.windowView = activity.getLayoutInflater().inflate(R.layout.station_info_window, null);
    this.markerToStation = new HashMap<Marker, Station>();
    
    this.language = Locale.getDefault().getLanguage();
    
    Resources resources = activity.getResources();
    this.nbBikeFormat = resources.getString(R.string.map_popup_station_nb_bike_format);
    this.nbEmptySlotFormat = resources.getString(R.string.map_popup_station_nb_empty_slot_format);
    this.dataAgeSecondFormat = resources.getString(R.string.map_popup_station_data_age_sec_format);
    this.dataAgeMinuteFormat = resources.getString(R.string.map_popup_station_data_age_min_format);
    this.dataAgeHourFormat = resources.getString(R.string.map_popup_station_data_age_hour_format);
    this.dataAgeDayFormat = resources.getString(R.string.map_popup_station_data_age_day_format);
  }
  
  public void bindMarkerToStation(Marker marker, Station station) {
    markerToStation.put(marker, station);
  }

  public void unbindMarker(Marker marker) {
    markerToStation.remove(marker);
  }

  public void unbindAllMarkers() {
    markerToStation.clear();
  }
  
  public View getInfoWindow(Marker marker) {
    Station station = markerToStation.get(marker);
    
    TextView titleUi = ((TextView) windowView.findViewById(R.id.title));
    titleUi.setText(station.getName(language));
    
    TextView nbBicycleUi = ((TextView) windowView.findViewById(R.id.nb_bicycles));
    nbBicycleUi.setText(String.format(nbBikeFormat, station.getNbBikes()));
    
    TextView nbEmptySlotUi = ((TextView) windowView.findViewById(R.id.nb_empty_slots));
    nbEmptySlotUi.setText(String.format(nbEmptySlotFormat, station.getNbEmptySlots()));
    
    TextView stationDataAgeUi = ((TextView) windowView.findViewById(R.id.station_data_age));
    
    String ageString;
    long age = new Date().getTime() - station.getDate().getTime();
    if (age < 60 * 1000) {
      ageString = String.format(dataAgeSecondFormat, age / 1000);
    }
    else if (age < 60 * 60 * 1000) {
      ageString = String.format(dataAgeMinuteFormat, age / (60 * 1000));
    }
    else if (age < 24 * 60 * 60 * 1000) {
      ageString = String.format(dataAgeHourFormat, age / (60 * 60 * 1000));
    }
    else {
      ageString = String.format(dataAgeDayFormat, age / (24 * 60 * 60 * 1000));
    }
    stationDataAgeUi.setText(ageString);
    
    return windowView;
  }

  public View getInfoContents(Marker marker) {
    return null;
  }

}
