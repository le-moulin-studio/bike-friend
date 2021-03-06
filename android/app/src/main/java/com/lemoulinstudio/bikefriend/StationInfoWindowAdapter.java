package com.lemoulinstudio.bikefriend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.lemoulinstudio.bikefriend.db.BikeStation;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
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

    public BikeStation getBikeStation(Marker marker) {
        return markerToStation.get(marker);
    }

    public void unbindMarker(Marker marker) {
    markerToStation.remove(marker);
    }

    public void unbindAllMarkers() {
        markerToStation.clear();
    }

    public View getInfoWindow(Marker marker) {
        BikeStation station = markerToStation.get(marker);

        ImageView dataSourceImageView = (ImageView) windowView.findViewById(R.id.data_source_image);
        dataSourceImageView.setImageResource(station.dataSource.bikeSystem.mapMarkerResource);

        TextView chineseNameView = (TextView) windowView.findViewById(R.id.chinese_name);
        chineseNameView.setVisibility(station.chineseName != null ? View.VISIBLE : View.GONE);
        chineseNameView.setText(station.chineseName);

        TextView englishNameView = (TextView) windowView.findViewById(R.id.english_name);
        englishNameView.setVisibility(station.englishName != null ? View.VISIBLE : View.GONE);
        englishNameView.setText(station.englishName);

        TextView chineseAddressView = (TextView) windowView.findViewById(R.id.chinese_address);
        chineseAddressView.setVisibility(station.chineseAddress != null ? View.VISIBLE : View.GONE);
        chineseAddressView.setText(station.chineseAddress);

        TextView englishAddressView = (TextView) windowView.findViewById(R.id.english_address);
        englishAddressView.setVisibility(station.englishAddress != null ? View.VISIBLE : View.GONE);
        englishAddressView.setText(station.englishAddress);

        ImageView favoriteImageView = (ImageView) windowView.findViewById(R.id.favorite_image_view);
        favoriteImageView.setImageResource(station.isPreferred ?
                R.drawable.ic_action_star_yellow : R.drawable.ic_action_star_grey);

        TextView nbBicycleView = (TextView) windowView.findViewById(R.id.nb_bicycle);
        nbBicycleView.setText("" + station.nbBicycles);

        TextView nbParkingView = (TextView) windowView.findViewById(R.id.nb_parking);
        nbParkingView.setText("" + station.nbEmptySlots);

        TextView stationDataAgeView = (TextView) windowView.findViewById(R.id.station_data_age);

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
        stationDataAgeView.setText(ageString);

        return windowView;
    }

    public View getInfoContents(Marker marker) {
    return null;
    }

    /* This function is not called often, it is ok if it is slow. */
    public Marker getMarker(BikeStation bikeStation) {
        for (Map.Entry<Marker, BikeStation> entry : markerToStation.entrySet()) {
            if (entry.getValue() == bikeStation) {
                return entry.getKey();
            }
        }

        return null;
    }
}
