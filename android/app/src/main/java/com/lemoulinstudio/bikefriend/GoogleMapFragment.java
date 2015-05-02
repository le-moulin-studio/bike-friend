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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.sql.SQLException;
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
    private Map<Integer, BitmapDescriptor> numberToMarkerBitmapDescriptor;
    private Map<Object, List<Marker>> dataSourceWcToMarkers;
    private Map<Object, List<Marker>> dataSourceDrinkableWaterToMarkers;
    private TextPaint textPaint;

//    private boolean displayMapStationLayer;
//    private boolean displayMapWCLayer;
//    private boolean displayMapDrinkableWaterLayer;

    public GoogleMapFragment() {
        dataSourceToMarkers = new EnumMap<DataSourceEnum, List<Marker>>(DataSourceEnum.class);
        for (DataSourceEnum dataSource : DataSourceEnum.values()) {
            dataSourceToMarkers.put(dataSource, new ArrayList<Marker>());
        }

        numberToMarkerBitmapDescriptor = new HashMap<Integer, BitmapDescriptor>();
        dataSourceWcToMarkers = new HashMap();
        dataSourceWcToMarkers.put(new Object(), new ArrayList());
        dataSourceDrinkableWaterToMarkers = new HashMap();
        dataSourceDrinkableWaterToMarkers.put(new Object(), new ArrayList());
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

    @StringRes(R.string.map_popup_layer_stations)
    protected String messageLayerStations;

    @StringRes(R.string.map_popup_layer_toilets)
    protected String messageLayerWC;

    @StringRes(R.string.map_popup_layer_drinkable_water)
    protected String messageLayerDrinkableWater;

    @AfterViews
    protected void setupViews() {
        final GoogleMap map = getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(siwa);

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                BikeStation bikeStation = siwa.getBikeStation(marker);
                bikeStation.isPreferred = !bikeStation.isPreferred;
                try {bikeStationProviderRepository.updateDbFromMem(bikeStation);}
                catch (SQLException e) {}
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
        });

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

    private BitmapDescriptor getMarkerBitmapDescriptor(int number) {
        // We put a hard maximum of 99 on the number, flash mobs are not our typical users.
        number = Math.min(number, 99);

        if (!numberToMarkerBitmapDescriptor.containsKey(number)) {
            numberToMarkerBitmapDescriptor.put(number,
                    BitmapDescriptorFactory.fromBitmap(createMarkerBitmap(number)));
        }

        return numberToMarkerBitmapDescriptor.get(number);
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
    public void onActivityCreated(Bundle savedInstanceState) {
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
                if (Utils.isNetworkAvailable(getActivity())) {
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
//                displayMapStationLayer = true;
                recreateAllTheMarkers();

                return true;
            }
            case R.id.menu_layers: {
//                // Select layers to display on map
//                LayoutInflater inflater = LayoutInflater.from(this.getActivity());
//                View popup = inflater.inflate(R.layout.fragment_layer, null, false);
//
//                final CheckBox checkBoxStationsUi = (CheckBox)popup.findViewById(R.id.checkBoxStationLayer);
//                checkBoxStationsUi.setChecked(displayMapStationLayer);
//                checkBoxStationsUi.setText(messageLayerStations);
//                checkBoxStationsUi.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
//                    public void onCheckedChanged(android.widget.CompoundButton compoundButton, boolean checked) {
//                        displayMapStationLayer = checked;
//                        if (checked) {
//                            GoogleMapFragment.this.recreateAllTheMarkers();
//                        }
//                        else {
//                            GoogleMapFragment.this.removeAllTheBikeStationMarkers();
//                        }
//                    }}
//                );
//
//                final CheckBox checkBoxWCUi = (CheckBox)popup.findViewById(R.id.checkBoxWCLayer);
//                checkBoxWCUi.setChecked(displayMapWCLayer);
//                checkBoxWCUi.setText(messageLayerWC);
//                checkBoxWCUi.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
//                    public void onCheckedChanged(android.widget.CompoundButton compoundButton, boolean checked) {
//                        displayMapWCLayer = checked;
//                        if (checked) {
//                            GoogleMapFragment.this.recreateAllTheWCMarkers();
//                        }
//                        else {
//                            GoogleMapFragment.this.removeAllTheWCMarkers();
//                        }
//                    }}
//                );
//
//                final CheckBox checkBoxDrinkableWaterUi = (CheckBox)popup.findViewById(R.id.checkBoxDrinkableWater);
//                checkBoxDrinkableWaterUi.setChecked(displayMapDrinkableWaterLayer);
//                checkBoxDrinkableWaterUi.setText(messageLayerDrinkableWater);
//                checkBoxDrinkableWaterUi.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
//                    public void onCheckedChanged(android.widget.CompoundButton compoundButton, boolean checked) {
//                        displayMapDrinkableWaterLayer = checked;
//                        if (checked) {
//                            GoogleMapFragment.this.recreateAllTheDrinkableWaterMarkers();
//                        } else {
//                            GoogleMapFragment.this.removeAllTheDrinkableWaterMarkers();
//                        }
//                    }}
//                );
//
//                View parentView = this.getView();
//                final PopupWindow popupUi =  new PopupWindow(getActivity());
//                popup.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
//                popupUi.setContentView(popup);
//                popupUi.setWidth(popup.getMeasuredWidth());
//                popupUi.setHeight(popup.getMeasuredHeight());
//                popupUi.setOutsideTouchable(true);
//                popupUi.setFocusable(true);
//                popupUi.showAtLocation(parentView, Gravity.CENTER, 0, 0);
//                popupUi.update();
//                popupUi.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                    @Override
//                    public void onDismiss() {
//                        popupUi.dismiss();
//                    }
//                });
//                return true;
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
            case R.id.menu_place_pingtung: {
                animateCameraToBoundingBox(bikeStationProviderRepository
                        .getBikeStationProvider(DataSourceEnum.PingtungBike_Pingtung).getBounds());
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
//        if (!displayMapStationLayer) {
//            // Prevents displaying markers due to update triggered implicitly from onStart's registerForBikeStationUpdates
//            return;
//        }
        List<Marker> markers = dataSourceToMarkers.get(bikeStationProvider.getDataSourceEnum());
        removeAllMarkers(markers);

        GoogleMap map = getMap();
        Collection<BikeStation> bikeStations = bikeStationProvider.getBikeStations();
        synchronized (bikeStations) {
            for (BikeStation station : bikeStations) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(station.latitude, station.longitude))
                        .icon(getMarkerBitmapDescriptor(displayBicyclesOnMarkers ?
                                station.nbBicycles : station.nbEmptySlots));

                Marker marker = map.addMarker(markerOptions);
                markers.add(marker);
                siwa.bindMarkerToStation(marker, station);
            }
        }
    }

    public void onWCUpdated(Object provider) {
        List<Marker> markers = dataSourceWcToMarkers.values().iterator().next(); //FIXME: No proper datasource
        removeAllMarkers(markers);

        GoogleMap map = getMap();
        Collection<float[] /* latitude/longitude*/> stations = new ArrayList<float[]>();
        // FIXME Dummy data
        float coordinate1[] = {25.028662f, 121.56612f};
        float coordinate2[] = {25.034937f, 121.557662f};
        stations.add(coordinate1);
        stations.add(coordinate2);
        // end
        synchronized (stations) {
            for (float[] station : stations) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(station[0], station[1]))
                        .icon(getMarkerBitmapDescriptor(5));

                Marker marker = map.addMarker(markerOptions);
                markers.add(marker);
            }
        }
    }

    public void onDrinkableWaterUpdated(Object provider) {
        List<Marker> markers = dataSourceDrinkableWaterToMarkers.values().iterator().next(); //FIXME: No proper datasource
        removeAllMarkers(markers);

        GoogleMap map = getMap();
        Collection<float[] /* latitude/longitude*/> stations = new ArrayList<float[]>();
        // FIXME Dummy data
        float coordinate1[] = {25.128662f, 121.56612f};
        float coordinate2[] = {25.234937f, 121.557662f};
        stations.add(coordinate1);
        stations.add(coordinate2);
        // end
        synchronized (stations) {
            for (float[] station : stations) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(station[0], station[1]))
                        .icon(getMarkerBitmapDescriptor(2));

                Marker marker = map.addMarker(markerOptions);
                markers.add(marker);
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

        // Restore Map Layers state
//        displayMapStationLayer = preferences.mapStationsLayer().get();
//        displayMapWCLayer = preferences.mapWCLayer().get();
//        displayMapDrinkableWaterLayer = preferences.mapDrinkableWaterLayer().get();

        displayBicyclesOnMarkers = preferences.displayBicyclesOnMarkers().get();

        recreateAllTheMarkers();

//        // Display layers as per user preference at first
//        if (displayMapStationLayer) {
//            recreateAllTheMarkers();
//        }
//        if (displayMapWCLayer) {
//            recreateAllTheWCMarkers();
//        }
//        if (displayMapDrinkableWaterLayer) {
//            recreateAllTheDrinkableWaterMarkers();
//        }
    }

//    private void removeAllBikeStationsMarkers(List<Marker> markers) {
//        // Remove the old markers.
//        for (Marker marker : markers) {
//            marker.remove();
//            siwa.unbindMarker(marker);
//        }
//        markers.clear();
//    }

    private void removeAllMarkers(List<Marker> markers) {
        // Remove the old markers.
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }

    private void recreateAllTheMarkers() {
        // Initializes the markers on the map, according to the bike stations already available (in memory or db).
        for (BikeStationProvider bikeStationProvider : bikeStationProviderRepository.getBikeStationProviders()) {
            onBikeStationUpdated(bikeStationProvider);
        }
    }

//    private void removeAllTheBikeStationMarkers() {
//        for (List<Marker> markers : dataSourceToMarkers.values()) {
//            removeAllMarkers(markers);
//        }
//    }

//    private void recreateAllTheWCMarkers() {
//        // Initializes the markers on the map, according to the WCs already available (in memory or db).
//        onWCUpdated(null);
//    }

//    private void removeAllTheWCMarkers() {
//        List<Marker> markers = dataSourceWcToMarkers.values().iterator().next();
//        removeAllMarkers(markers);
//    }

//    private void recreateAllTheDrinkableWaterMarkers() {
//        // Initializes the markers on the map, according to the water drinkable source already available (in memory or db).
//        onDrinkableWaterUpdated(null);
//    }

//    private void removeAllTheDrinkableWaterMarkers() {
//        List<Marker> markers = dataSourceDrinkableWaterToMarkers.values().iterator().next();
//        removeAllMarkers(markers);
//    }

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
//                .mapStationsLayer().put(displayMapStationLayer)
//                .mapWCLayer().put(displayMapWCLayer)
//                .mapDrinkableWaterLayer().put(displayMapDrinkableWaterLayer)
                .apply();
    }

    @Override
    public void onStart() {
        super.onStart();
        bikeStationProviderRepository.registerForBikeStationUpdates(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        bikeStationProviderRepository.unregisterForBikeStationUpdates(this);
        siwa.unbindAllMarkers();
    }

    @UiThread
    public void showStation(BikeStation bikeStation) {
        // Scrolls the camera to show the station.
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(bikeStation.latitude, bikeStation.longitude),
                14));

        // Opens the info windows.
        Marker marker = siwa.getMarker(bikeStation);
        if (marker != null) {
            marker.showInfoWindow();
        }
    }
}
