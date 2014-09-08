package com.lemoulinstudio.bikefriend;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.BikeSystem;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    protected Set<BikeStationProvider> listedProviders;
    protected List<Class> itemClasses;

    @AfterInject
    protected void setupViews() {
        inflater = LayoutInflater.from(context);
        items = new ArrayList<Object>();
        listedProviders = new HashSet<BikeStationProvider>();
        itemClasses = Arrays.<Class>asList(BikeStation.class, BikeStationProvider.class);

        selectItemsToList();
    }

    private void selectItemsToList() {
        // Lists all the favorite items and their provider.
        items.clear();
        listedProviders.clear();

        for (BikeStationProvider provider : bikeStationProviderRepository.getBikeStationProviders()) {
            boolean providerAlreadyAdded = false;
            for (BikeStation station : provider.getBikeStations()) {
                if (station.isPreferred) {
                    if (!providerAlreadyAdded) {
                        items.add(provider);
                        listedProviders.add(provider);
                        providerAlreadyAdded = true;
                    }
                    items.add(station);
                }
            }
        }
    }

    public Collection<BikeStationProvider> getListedBikeStationProviders() {
        return listedProviders;
    }

    public void refreshData() {
        // We do not reset our selection of stations to show in the list.
        // Instead, we just let the observers know that their value (and their view)
        // has changed and that they need to fetch it from this adapter.
        notifyDataSetChanged();
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

            ImageView dataSourceImageView = (ImageView) convertView.findViewById(R.id.data_source_image);
            dataSourceImageView.setImageResource(
                    station.dataSource.bikeSystem == BikeSystem.YouBike ?
                            R.drawable.map_marker_youbike : R.drawable.map_marker_citybike);

            TextView chineseNameView = (TextView) convertView.findViewById(R.id.chinese_name);
            chineseNameView.setVisibility(station.chineseName != null ? View.VISIBLE : View.GONE);
            chineseNameView.setText(station.chineseName);

            TextView englishNameView = (TextView) convertView.findViewById(R.id.english_name);
            englishNameView.setVisibility(station.englishName != null ? View.VISIBLE : View.GONE);
            englishNameView.setText(station.englishName);

            TextView chineseAddressView = (TextView) convertView.findViewById(R.id.chinese_address);
            chineseAddressView.setVisibility(station.chineseAddress != null ? View.VISIBLE : View.GONE);
            chineseAddressView.setText(station.chineseAddress);

            TextView englishAddressView = (TextView) convertView.findViewById(R.id.english_address);
            englishAddressView.setVisibility(station.englishAddress != null ? View.VISIBLE : View.GONE);
            englishAddressView.setText(station.englishAddress);

            ImageView favoriteImageView = (ImageView) convertView.findViewById(R.id.favorite_image_view);
            favoriteImageView.setImageResource(station.isPreferred ? R.drawable.ic_action_important : R.drawable.ic_action_not_important);

            TextView nbBicycleView = (TextView) convertView.findViewById(R.id.nb_bicycle);
            nbBicycleView.setText("" + station.nbBicycles);

            TextView nbParkingView = (TextView) convertView.findViewById(R.id.nb_parking);
            nbParkingView.setText("" + station.nbEmptySlots);

            TextView stationDataAgeView = (TextView) convertView.findViewById(R.id.station_data_age);

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

            View.OnClickListener favoriteImageViewListener = new View.OnClickListener(){
                public void onClick(android.view.View view) {
                    station.isPreferred = !station.isPreferred;
                    try {
                        bikeStationProviderRepository.updateDbFromMem(station);
                        ((ImageView)view).setImageResource(station.isPreferred ? R.drawable.ic_action_important : R.drawable.ic_action_not_important);
                    } catch (SQLException e) {
                        Log.e(BikefriendApplication.TAG, "Error when deselecting favorite station" + station, e);
                    }
                }
            };

            favoriteImageView.setOnClickListener(favoriteImageViewListener);
        }
        else {
            // Provider
            BikeStationProvider provider = (BikeStationProvider) obj;
            convertView = inflater.inflate(R.layout.fragment_favorite_station_header, parent, false);
            TextView header = (TextView) convertView.findViewById(R.id.fragment_favorite_station_region_separator);
            header.setText(provider.getDataSourceEnum().placeNameRes);
        }
        return convertView;
    }

}
