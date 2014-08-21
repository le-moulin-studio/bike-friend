package com.lemoulinstudio.bikefriend;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh: {
//                if (getMap() != null) {
//                    for (StationProvider stationProvider : stationProviders) {
//                        stationProvider.refreshData();
//                    }
//                }
                return true;
            }
            case R.id.menu_place_taipei: {
//                LatLngBounds bounds = getMap().getProjection().getVisibleRegion().latLngBounds;
//                Log.i("bikefriend", String.format("bounds = [%f, %f, %f, %f]",
//                        bounds.southwest.latitude, bounds.southwest.longitude,
//                        bounds.northeast.latitude, bounds.northeast.longitude));
//                bounds = new LatLngBounds(new LatLng(24.987210, 121.501474), new LatLng(25.085955, 121.570671));
                animateCameraToBoundingBox(R.array.latlngBound_taipei);
                return true;
            }
            case R.id.menu_place_changhua: {
                animateCameraToBoundingBox(R.array.latlngBound_changhua);
                return true;
            }
            case R.id.menu_place_taichung: {
                animateCameraToBoundingBox(R.array.latlngBound_taichung);
                return true;
            }
            case R.id.menu_place_kaohsiung: {
                animateCameraToBoundingBox(R.array.latlngBound_kaohsiung);
                return true;
            }
            case R.id.menu_place_tainan: {
                animateCameraToBoundingBox(R.array.latlngBound_tainan);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void animateCameraToBoundingBox(int latlngResource) {
        String[] boundStrings = getActivity().getResources().getStringArray(latlngResource);
        LatLngBounds bounds = new LatLngBounds(
                new LatLng(Double.parseDouble(boundStrings[0]), Double.parseDouble(boundStrings[1])),
                new LatLng(Double.parseDouble(boundStrings[2]), Double.parseDouble(boundStrings[3])));
        getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
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
