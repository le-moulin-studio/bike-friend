package com.lemoulinstudio.bikefriend.preference;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultFloat;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(SharedPref.Scope.UNIQUE)
public interface BikefriendPreferences {

    /* Data not exposed to the user. */

    @DefaultBoolean(false) boolean userLearnedDrawer();

    @DefaultFloat((24.979649f + 25.137976f) / 2) float cameraTargetLat();
    @DefaultFloat((121.493065f + 121.662750f) / 2) float cameraTargetLng();
    @DefaultFloat(11.0f) float cameraZoom();
    @DefaultFloat(0.0f) float cameraTilt();
    @DefaultFloat(0.0f) float cameraBearing();

    @DefaultBoolean(true) boolean displayBicyclesOnMarkers();

    @DefaultBoolean(false) boolean chronometerIsStarted();
    @DefaultLong(0) long chronometerStartTime();

//    @DefaultBoolean(true) boolean mapStationsLayer();
//    @DefaultBoolean(false) boolean mapWCLayer();
//    @DefaultBoolean(false) boolean mapDrinkableWaterLayer();

    /* Data exposed to the user. */

    @DefaultString("120000") String autoRefreshMinPeriod();
    @DefaultString("DefinedBySystem") String locationLanguage();
    @DefaultString("GoogleMap") String mapProvider();

}
