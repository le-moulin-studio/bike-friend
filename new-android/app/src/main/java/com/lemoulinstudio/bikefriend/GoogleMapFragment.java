package com.lemoulinstudio.bikefriend;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import com.lemoulinstudio.bikefriend.preference.BikefriendPreferences_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EFragment
public class GoogleMapFragment extends SupportMapFragment {

    public static interface FragmentListener {
    }

    private FragmentListener fragmentListener;

    public GoogleMapFragment() {
    }

    @Pref
    protected BikefriendPreferences_ preferences;

    @AfterViews
    protected void setupViews() {
        GoogleMap map = getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Restores the state of the camera on the map.
        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(preferences.cameraTargetLat().get(),
                           preferences.cameraTargetLng().get()),
                preferences.cameraZoom().get(),
                preferences.cameraTilt().get(),
                preferences.cameraBearing().get());

        getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void onPause() {
        super.onPause();

        // Saves the state of the camera on the map.
        CameraPosition cameraPosition = getMap().getCameraPosition();

        preferences.edit()
                .cameraTargetLat().put((float) cameraPosition.target.latitude)
                .cameraTargetLng().put((float) cameraPosition.target.longitude)
                .cameraZoom().put(cameraPosition.zoom)
                .cameraTilt().put(cameraPosition.tilt)
                .cameraBearing().put(cameraPosition.bearing)
                .apply();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //fragmentListener = (FragmentListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //fragmentListener = null;
    }

}
