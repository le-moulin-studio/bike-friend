package com.lemoulinstudio.bikefriend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lemoulinstudio.bikefriend.db.BikeStation;
import com.lemoulinstudio.bikefriend.db.DataSourceEnum;
import com.lemoulinstudio.bikefriend.preference.BikefriendPreferences_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EFragment
public class GoogleMapFragment extends SupportMapFragment implements BikeStationListener {

    private boolean displayBicyclesOnMarkers;
    private Map<DataSourceEnum, List<Marker>> dataSourceToMarkers;
    private Map<Integer, Bitmap> numberToMarkerBitmap;
    private TextPaint textPaint;

    public GoogleMapFragment() {
        dataSourceToMarkers = new EnumMap<DataSourceEnum, List<Marker>>(DataSourceEnum.class);
        for (DataSourceEnum dataSource : DataSourceEnum.values()) {
            dataSourceToMarkers.put(dataSource, new ArrayList<Marker>());
        }

        numberToMarkerBitmap = new HashMap<Integer, Bitmap>();
    }

    @Pref
    protected BikefriendPreferences_ preferences;

    @Bean
    protected BikeStationProviderRepository bikeStationProviderRepository;

    @Bean
    protected StationInfoWindowAdapter siwa;

    @DrawableRes(R.drawable.map_marker_green)
    protected Drawable markerDrawableGreen;

    @DrawableRes(R.drawable.map_marker_yellow)
    protected Drawable markerDrawableYellow;

    @DrawableRes(R.drawable.map_marker_red)
    protected Drawable markerDrawableRed;

    @StringRes(R.string.message_network_not_available)
    protected String messageNetworkNotAvailable;

    @StringRes(R.string.message_server_not_reachable_format)
    protected String messageServerNotReachable;

    @StringRes(R.string.message_parse_error_format)
    protected String messageDataParseError;

    @AfterViews
    protected void setupViews() {
        final GoogleMap map = getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(siwa);

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition cp) {
                LatLngBounds visibleRegion = map.getProjection().getVisibleRegion().latLngBounds;
                for (BikeStationProvider bikeStationProvider : bikeStationProviderRepository.getBikeStationProviders()) {
                    LatLngBounds providerBounds = bikeStationProvider.getBounds();
                    if (Utils.intersects(visibleRegion, providerBounds)) {
                        bikeStationProvider.notifyStationsAreWatched();
                    }
                }
            }
        });

        textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(markerDrawableGreen.getIntrinsicHeight() * 0.5f);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private Bitmap getMarkerBitmap(int number) {
        // We put a hard maximum of 99 on the number, flash mobs are not our typical users.
        number = Math.min(number, 99);

        if (!numberToMarkerBitmap.containsKey(number)) {
            numberToMarkerBitmap.put(number, createMarkerBitmap(number));
        }

        return numberToMarkerBitmap.get(number);
    }

    private Bitmap createMarkerBitmap(int number) {
        Drawable markerDrawable = number >= 5 ? markerDrawableGreen :
                number > 0 ? markerDrawableYellow : markerDrawableRed;

        int width = markerDrawable.getIntrinsicWidth();
        int height = markerDrawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        String text = "" + number;

        markerDrawable.setBounds(0, 0, width, height);
        markerDrawable.draw(canvas);

        Rect textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        canvas.drawText(text,
                width / 2,
                (height + textBounds.height()) / 2,
                textPaint);

        return bitmap;
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_bicycle).setVisible(displayBicyclesOnMarkers);
        menu.findItem(R.id.menu_parking).setVisible(!displayBicyclesOnMarkers);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh: {
                if (isNetworkAvailable()) {
                    LatLngBounds visibleRegion = getMap().getProjection().getVisibleRegion().latLngBounds;
                    for (BikeStationProvider bikeStationProvider : bikeStationProviderRepository.getBikeStationProviders()) {
                        LatLngBounds providerBounds = bikeStationProvider.getBounds();
                        if (Utils.intersects(visibleRegion, providerBounds)) {
                            bikeStationProvider.updateData();
                        }
                    }
                }
                else {
                    Toast.makeText(getActivity(), messageNetworkNotAvailable, Toast.LENGTH_LONG).show();
                }
                return true;
            }
            case R.id.menu_bicycle:
            case R.id.menu_parking: {
                // The user wants to switch between displaying available bicycles and available parkings.
                displayBicyclesOnMarkers = !displayBicyclesOnMarkers;
                getActivity().supportInvalidateOptionsMenu();
                recreateAllTheMarkers();
                return true;
            }
            case R.id.menu_layers: {
                return true;
            }
            case R.id.menu_place_taipei: {
                animateCameraToBoundingBox(bikeStationProviderRepository
                        .getBikeStationProvider(DataSourceEnum.YouBike_Taipei).getBounds());
                return true;
            }
            case R.id.menu_place_taichung: {
                animateCameraToBoundingBox(bikeStationProviderRepository
                        .getBikeStationProvider(DataSourceEnum.YouBike_Taichung).getBounds());
                return true;
            }
            case R.id.menu_place_changhua: {
                animateCameraToBoundingBox(bikeStationProviderRepository
                        .getBikeStationProvider(DataSourceEnum.YouBike_Changhua).getBounds());
                return true;
            }
            case R.id.menu_place_kaohsiung: {
                animateCameraToBoundingBox(bikeStationProviderRepository
                        .getBikeStationProvider(DataSourceEnum.CityBike_Kaohsiung).getBounds());
                return true;
            }
            case R.id.menu_place_taiwan: {
                animateCameraToBoundingBox(new LatLngBounds(
                        new LatLng(21.885012f, 119.877213f),
                        new LatLng(25.210294f, 122.180730f)));
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void animateCameraToBoundingBox(LatLngBounds bounds) {
//        Log.i("bikefriend", String.format("bounds = [%ff, %ff, %ff, %ff]",
//                bounds.southwest.latitude, bounds.southwest.longitude,
//                bounds.northeast.latitude, bounds.northeast.longitude));
//        LatLngBounds cameraBounds = getMap().getProjection().getVisibleRegion().latLngBounds;
//        Log.i("bikefriend", String.format("cameraBounds = [%ff, %ff, %ff, %ff]",
//                cameraBounds.southwest.latitude, cameraBounds.southwest.longitude,
//                cameraBounds.northeast.latitude, cameraBounds.northeast.longitude));
        getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }

    @Override
    public void onServerNotReachable(BikeStationProvider bikeStationProvider) {
        String placeName = getActivity().getString(bikeStationProvider.getDataSourceEnum().placeNameRes);
        String message = String.format(messageServerNotReachable, placeName);
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onParseError(BikeStationProvider bikeStationProvider) {
        String placeName = getActivity().getString(bikeStationProvider.getDataSourceEnum().placeNameRes);
        String message = String.format(messageDataParseError, placeName);
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBikeStationUpdated(BikeStationProvider bikeStationProvider) {
        List<Marker> markers = dataSourceToMarkers.get(bikeStationProvider.getDataSourceEnum());

        // Remove the old markers.
        for (Marker marker : markers) {
            marker.remove();
            siwa.unbindMarker(marker);
        }
        markers.clear();

        GoogleMap map = getMap();
        Collection<BikeStation> bikeStations = bikeStationProvider.getBikeStations();
        synchronized (bikeStations) {
            for (BikeStation station : bikeStations) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(station.latitude, station.longitude))
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(
                                displayBicyclesOnMarkers ? station.nbBicycles : station.nbEmptySlots)));

                Marker marker = map.addMarker(markerOptions);
                markers.add(marker);
                siwa.bindMarkerToStation(marker, station);
            }
        }
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

        displayBicyclesOnMarkers = preferences.displayBicyclesOnMarkers().get();

        recreateAllTheMarkers();
    }

    private void recreateAllTheMarkers() {
        // Initializes the markers on the map, according to the bike stations already available (in memory or db).
        for (BikeStationProvider bikeStationProvider : bikeStationProviderRepository.getBikeStationProviders()) {
            onBikeStationUpdated(bikeStationProvider);
        }
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
                .displayBicyclesOnMarkers().put(displayBicyclesOnMarkers)
                .apply();
    }

    @Override
    public void onStart() {
        super.onStart();

        for (BikeStationProvider bikeStationProvider : bikeStationProviderRepository.getBikeStationProviders()) {
            bikeStationProvider.addListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        for (BikeStationProvider bikeStationProvider : bikeStationProviderRepository.getBikeStationProviders()) {
            bikeStationProvider.removeListener(this);
        }

        siwa.unbindAllMarkers();
    }
}
