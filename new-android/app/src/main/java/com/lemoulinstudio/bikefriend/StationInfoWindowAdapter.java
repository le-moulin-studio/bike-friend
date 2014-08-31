package com.lemoulinstudio.bikefriend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.BikeSystem;

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
        this.windowView = LayoutInflater.from(context).inflate(R.layout.station_info_content, null);
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

//        data_source_image
        ImageView dataSourceImageView = (ImageView) windowView.findViewById(R.id.data_source_image);
        dataSourceImageView.setImageResource(
                station.dataSource.bikeSystem == BikeSystem.YouBike ?
                        R.drawable.map_marker_youbike : R.drawable.map_marker_citybike);

        TextView chineseNameView = (TextView) windowView.findViewById(R.id.chinese_name);
        chineseNameView.setText(station.chineseName);

        TextView englishNameView = (TextView) windowView.findViewById(R.id.english_name);
        englishNameView.setText(station.englishName);

        TextView chineseAddressView = (TextView) windowView.findViewById(R.id.chinese_address);
        chineseAddressView.setText(station.chineseAddress);

        TextView englishAddressView = (TextView) windowView.findViewById(R.id.english_address);
        englishAddressView.setText(station.englishAddress);

        TextView nbBicycleView = (TextView) windowView.findViewById(R.id.nb_bicycles);
        nbBicycleView.setText("" + station.nbBicycles);

        TextView nbParkingView = (TextView) windowView.findViewById(R.id.nb_parkings);
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

}
