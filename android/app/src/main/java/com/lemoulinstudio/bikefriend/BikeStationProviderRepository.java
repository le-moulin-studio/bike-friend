package com.lemoulinstudio.bikefriend;

import com.j256.ormlite.dao.Dao;
import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;
import com.lemoulinstudio.bikefriend.db.MyDatabaseHelper;
import com.lemoulinstudio.bikefriend.preference.BikefriendPreferences_;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.SQLException;


@EBean(scope = EBean.Scope.Singleton)
public class BikeStationProviderRepository {

    private Collection<BikeStationProvider> bikeStationProviders;

    @Pref
    protected BikefriendPreferences_ preferences;

    @OrmLiteDao(helper = MyDatabaseHelper.class, model = BikeStation.class)
    Dao<BikeStation, String> bikeStationDao;

    @AfterInject
    public void afterInject() {
        bikeStationProviders = new ArrayList<BikeStationProvider>();
        for (DataSourceEnum dataSource : DataSourceEnum.values()) {
            bikeStationProviders.add(new BikeStationProviderImpl(dataSource, preferences, bikeStationDao));
        }
    }

    public Collection<BikeStationProvider> getBikeStationProviders() {
        return bikeStationProviders;
    }

    public void registerForBikeStationUpdates(BikeStationListener listener) {
        for (BikeStationProvider bikeStationProvider : bikeStationProviders) {
            bikeStationProvider.addListener(listener);
        }
    }

    public void unregisterForBikeStationUpdates(BikeStationListener listener) {
        for (BikeStationProvider bikeStationProvider : bikeStationProviders) {
            bikeStationProvider.removeListener(listener);
        }
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

    // This can be called from the favoriteFragment.
    public void updateDbFromMem(BikeStation bikeStation) throws SQLException {
        bikeStationDao.update(bikeStation);
    }
}
