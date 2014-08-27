package com.lemoulinstudio.bikefriend;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.j256.ormlite.dao.Dao;
import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;
import com.lemoulinstudio.bikefriend.db.MyDatabaseHelper;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@EBean(scope = EBean.Scope.Singleton)
public class BikeStationProviderRepository {

    private Collection<BikeStationProvider> bikeStationProviders;

    @OrmLiteDao(helper = MyDatabaseHelper.class, model = BikeStation.class)
    Dao<BikeStation, String> bikeStationDao;

    @AfterInject
    public void afterInject() {
        bikeStationProviders = Arrays.<BikeStationProvider>asList(
                new BikeStationProviderImpl(DataSourceEnum.YouBike_Taipei, bikeStationDao, new Date(0)),
                new BikeStationProviderImpl(DataSourceEnum.YouBike_Taichung, bikeStationDao, new Date(0)),
                new BikeStationProviderImpl(DataSourceEnum.YouBike_Changhua, bikeStationDao, new Date(0)),
                new BikeStationProviderImpl(DataSourceEnum.CityBike_Kaohsiung, bikeStationDao, new Date(0))
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
