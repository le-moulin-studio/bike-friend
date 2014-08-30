package com.lemoulinstudio.bikefriend;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lemoulinstudio.bikefriend.db.BikeStation;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.res.StringRes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by davidandreoletti on 28/08/2014.
 */
@EBean
public class FavoriteStationsAdapter extends BaseAdapter {

    @Bean
    protected BikeStationProviderRepository bikeStationProviderRepository;

    protected LayoutInflater inflater;

    @RootContext
    protected Context context;

    // Duplicate with StationInfoWindowAdapter
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
    // end

    protected List<Object> items;
    protected List<Class> itemClasses;

    protected int total;

    @AfterInject
    protected void setupViews() {
        inflater = LayoutInflater.from(context);
        items = new ArrayList();
        itemClasses = Arrays.<Class>asList(BikeStation.class, BikeStationProvider.class);

        updateItems();
    }

    private void updateItems() {
        // Build List of favorite items
        items.clear();
        Collection<BikeStationProvider> providers = bikeStationProviderRepository.getBikeStationProviders();
        for (BikeStationProvider provider : providers) {
            boolean providerAlreadyAdded = false;
            for (BikeStation station : provider.getBikeStations()) {
                if (station.isPreferred) {
                    if (!providerAlreadyAdded) {
                        items.add(provider);
                        providerAlreadyAdded = true;
                    }
                    if (i18nStationName(station) != null) {
                        items.add(station);
                    }
                }
            }
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public int getViewTypeCount () {
        return itemClasses.size();
    }

    @Override
    public int getItemViewType (int position) {
        return itemClasses.indexOf(items.get(position).getClass());
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        // Fetch data at position
        Object obj = getItem(i);
        // Create view
        if (obj instanceof BikeStation) {
            // Station
            final BikeStation station = (BikeStation) obj;
            convertView = inflater.inflate(R.layout.fragment_favorite_station, parent, false);

            // Duplicate with StationinfoWindowAdapter
            TextView titleUi = ((TextView) convertView.findViewById(R.id.title));
            String res = i18nStationName(station).trim();
            titleUi.setText(res);

            TextView nbBicycleUi = ((TextView) convertView.findViewById(R.id.nb_bicycles));
            nbBicycleUi.setText(String.format(nbBikeFormat, station.nbBicycles));

            TextView nbEmptySlotUi = ((TextView) convertView.findViewById(R.id.nb_empty_slots));
            nbEmptySlotUi.setText(String.format(nbEmptySlotFormat, station.nbEmptySlots));

            TextView stationDataAgeUi = ((TextView) convertView.findViewById(R.id.station_data_age));

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
            // end

            ImageButton starButton = (ImageButton) convertView.findViewById(R.id.fragment_favorite_station_starbutton);
            starButton.setImageResource(R.drawable.ic_action_important);

            View.OnClickListener saveAsfavorite = new View.OnClickListener(){
                public void onClick(android.view.View view) {
                    station.isPreferred = !station.isPreferred;
                    try {
                        bikeStationProviderRepository.updateDbFromMem(station);
                        ((ImageButton)view).setImageResource(station.isPreferred ? R.drawable.ic_action_important : R.drawable.ic_action_not_important);
                    } catch (SQLException e) {
                        Log.e(BikefriendApplication.TAG, "Error when deselecting favorite station" + station, e);
                    }
                }
            };

            starButton.setOnClickListener(saveAsfavorite);
        }
        else {
            // Provider
            BikeStationProvider provider = (BikeStationProvider) obj;
            convertView = inflater.inflate(R.layout.fragment_favorite_station_header, parent, false);
            TextView header = (TextView) convertView.findViewById(R.id.fragment_favorite_station_region_separator);
            header.setText(provider.getDataSourceEnum().name());
        }
        return convertView;
    }

    public static String i18nStationName(BikeStation station) {
        String languageCode = Locale.getDefault().getLanguage();
        return languageCode.equals("zh") || languageCode.equals("ja") ?
             station.chineseName : station.englishName;
    }
}
