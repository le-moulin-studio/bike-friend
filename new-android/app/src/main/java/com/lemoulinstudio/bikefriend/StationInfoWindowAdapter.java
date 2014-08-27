package com.lemoulinstudio.bikefriend;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.lemoulinstudio.bikefriend.db.BikeStation;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Vincent Cantin
 */
@EBean
public class StationInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Map<Marker, BikeStation> markerToStation;
    private final String language;

    public StationInfoWindowAdapter() {
        this.markerToStation = new HashMap<Marker, BikeStation>();
        this.language = Locale.getDefault().getLanguage();
    }

    @RootContext
    protected Context context;

    @StringRes(R.string.map_popup_station_nb_bike_format)
    protected String nbBikeFormat;

    @StringRes(R.string.map_popup_station_nb_empty_slot_format)
    protected String nbEmptySlotFormat;

    @StringRes(R.string.map_popup_station_data_age_sec_format)
    protected String dataAgeSecondFormat;

    @StringRes(R.string.map_popup_station_data_age_min_format)
    protected String dataAgeMinuteFormat;

    @StringRes(R.string.map_popup_station_data_age_hour_format)
    protected String dataAgeHourFormat;

    @StringRes(R.string.map_popup_station_data_age_day_format)
    protected String dataAgeDayFormat;

    protected View windowView;

    @AfterInject
    protected void setWindowView() {
        this.windowView = LayoutInflater.from(context).inflate(R.layout.station_info_window, null);
    }

    public void bindMarkerToStation(Marker marker, BikeStation station) {
        markerToStation.put(marker, station);
    }

    public void unbindMarker(Marker marker) {
    markerToStation.remove(marker);
    }

    public void unbindAllMarkers() {
        markerToStation.clear();
    }

    public View getInfoWindow(Marker marker) {
        BikeStation station = markerToStation.get(marker);

        TextView titleUi = ((TextView) windowView.findViewById(R.id.title));
        titleUi.setText(station.englishName);

        TextView nbBicycleUi = ((TextView) windowView.findViewById(R.id.nb_bicycles));
        nbBicycleUi.setText(String.format(nbBikeFormat, station.nbBicycles));

        TextView nbEmptySlotUi = ((TextView) windowView.findViewById(R.id.nb_empty_slots));
        nbEmptySlotUi.setText(String.format(nbEmptySlotFormat, station.nbEmptySlots));

        TextView stationDataAgeUi = ((TextView) windowView.findViewById(R.id.station_data_age));

        String ageString;
        long age = new Date().getTime() - station.lastUpdate.getTime();
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