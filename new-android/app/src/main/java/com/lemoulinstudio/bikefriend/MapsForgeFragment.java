package com.lemoulinstudio.bikefriend;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lemoulinstudio.bikefriend.db.DataSourceEnum;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;

@EFragment(R.layout.fragment_maps_forge)
public class MapsForgeFragment extends Fragment {

    public MapsForgeFragment() {
    }

    @ViewById(R.id.mapView)
    protected MapView mapView;

    protected TileCache tileCache;
    protected TileRendererLayer tileRendererLayer;

    @AfterViews
    protected void setupViews() {
        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(true);
        mapView.setBuiltInZoomControls(true);
        mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
        mapView.getMapZoomControls().setZoomLevelMax((byte) 25);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set the part of the map to show on the screen.
        mapView.getModel().mapViewPosition.setCenter(new LatLong(24.987210, 121.501474));
        mapView.getModel().mapViewPosition.setZoomLevel((byte) 15);

        // Initializes the tile cache.
        tileCache = AndroidUtil.createTileCache(getActivity(), "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                this.mapView.getModel().frameBufferModel.getOverdrawFactor());

        // Initializes the tile renderer layer.
        tileRendererLayer = new TileRendererLayer(tileCache,
                mapView.getModel().mapViewPosition, false, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setMapFile(getMapFile());
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

        // Associate the map view with the tile renderer.
        mapView.getLayerManager().getLayers().add(tileRendererLayer);
    }

    protected File getMapFile() {
        File file = new File(Environment.getExternalStorageDirectory(), "mapsforge/taiwan.map");
        Log.i(BikefriendApplication.TAG, "Map file is " + file.getAbsolutePath());
        return file;
    }

    @Override
    public void onStop() {
        super.onStop();

        mapView.getLayerManager().getLayers().remove(tileRendererLayer);
        tileRendererLayer.onDestroy();
        tileCache.destroy();
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
                animateCameraToBoundingBox(DataSourceEnum.YouBike_Taipei);
                return true;
            }
            case R.id.menu_place_changhua: {
                animateCameraToBoundingBox(DataSourceEnum.YouBike_Changhua);
                return true;
            }
            case R.id.menu_place_taichung: {
                animateCameraToBoundingBox(DataSourceEnum.YouBike_Taichung);
                return true;
            }
            case R.id.menu_place_kaohsiung: {
                animateCameraToBoundingBox(DataSourceEnum.CityBike_Kaohsiung);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void animateCameraToBoundingBox(DataSourceEnum dataSource) {
        LatLong center = new LatLong(
                (dataSource.bounds.southwest.latitude + dataSource.bounds.northeast.latitude) / 2,
                (dataSource.bounds.southwest.longitude + dataSource.bounds.northeast.longitude) / 2);
        mapView.getModel().mapViewPosition.animateTo(center);
    }

}
