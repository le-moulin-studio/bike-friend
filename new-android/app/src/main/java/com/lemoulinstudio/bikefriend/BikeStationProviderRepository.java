package com.lemoulinstudio.bikefriend;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;

import org.androidannotations.annotations.EBean;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@EBean(scope = EBean.Scope.Singleton)
public class BikeStationProviderRepository {

    private final Collection<BikeStationProvider> bikeStationProviders;

    public BikeStationProviderRepository() {
        bikeStationProviders = Arrays.<BikeStationProvider>asList(
                new BikeStationProviderImpl(DataSourceEnum.YouBike_Taipei, new Date(0)),
                new BikeStationProviderImpl(DataSourceEnum.YouBike_Taichung, new Date(0)),
                new BikeStationProviderImpl(DataSourceEnum.YouBike_Changhua, new Date(0)),
                new BikeStationProviderImpl(DataSourceEnum.CityBike_Kaohsiung, new Date(0))
                );
    }

    public Collection<BikeStationProvider> getBikeStationProviders() {
        return bikeStationProviders;
    }

    public BikeStationProvider getBikeStationProvider(DataSourceEnum dataSource) {
        for (BikeStationProvider bikeStationProvider : bikeStationProviders) {
            if (bikeStationProvider.getDataSourceEnum() == dataSource) {
                return bikeStationProvider;
            }
        }

        // Not found.
        return null;
    }
}
