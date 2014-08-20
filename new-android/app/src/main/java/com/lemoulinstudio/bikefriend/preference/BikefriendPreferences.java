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

    @DefaultFloat(25.030142f) float cameraTargetLat();
    @DefaultFloat(121.53549f) float cameraTargetLng();
    @DefaultFloat(13.0f) float cameraZoom();
    @DefaultFloat(0.0f) float cameraTilt();
    @DefaultFloat(0.0f) float cameraBearing();

    /* Data exposed to the user. */

    @DefaultString("GoogleMap") String mapProvider();
    @DefaultString("DefinedBySystem") String locationLanguage();

}
