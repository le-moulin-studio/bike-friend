package com.lemoulinstudio.bikefriend.preference;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultFloat;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
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

    /* Data exposed to the user. */

    @DefaultString("2min") String dataValidityDuration();
    @DefaultString("DefinedBySystem") String locationLanguage();
    @DefaultString("GoogleMap") String mapProvider();

}
