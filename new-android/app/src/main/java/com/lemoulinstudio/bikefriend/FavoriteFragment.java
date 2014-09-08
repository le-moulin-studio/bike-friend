package com.lemoulinstudio.bikefriend;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.lemoulinstudio.bikefriend.preference.BikefriendPreferences_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EFragment(R.layout.fragment_favorite)
public class FavoriteFragment extends ListFragment implements BikeStationListener {

    @Pref
    protected BikefriendPreferences_ preferences;

    @Bean
    protected BikeStationProviderRepository bikeStationProviderRepository;

    @Bean
    protected FavoriteStationsAdapter adapter;

    @StringRes(R.string.favorite_list_empty_list_text)
    protected String emptyListText;

    @StringRes(R.string.message_network_not_available)
    protected String messageNetworkNotAvailable;

    @StringRes(R.string.message_server_not_reachable_format)
    protected String messageServerNotReachable;

    @StringRes(R.string.message_parse_error_format)
    protected String messageDataParseError;

    @AfterViews
    protected void setupViews() {
        setListAdapter(adapter);
        setEmptyText(emptyListText);
    }

    // This is for the UI update every second.
    private final Handler handler = new Handler();
    private final Runnable secondTickRunnable = new Runnable() {
        @Override
        public void run() {
            if (isVisible()) {
                adapter.refreshData();

                if (Utils.isNetworkAvailable(getActivity())) {
                    for (BikeStationProvider bikeStationProvider : adapter.getListedBikeStationProviders()) {
                        bikeStationProvider.notifyStationsAreWatched();
                    }
                }

                // We re-schedule this task for 15 seconds later.
                handler.postDelayed(this, 15 * 1000);
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.favorite, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh: {
                if (Utils.isNetworkAvailable(getActivity())) {
                    for (BikeStationProvider bikeStationProvider : adapter.getListedBikeStationProviders()) {
                        bikeStationProvider.updateData();
                    }
                }
                else {
                    Toast.makeText(getActivity(), messageNetworkNotAvailable, Toast.LENGTH_LONG).show();
                }
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Registers for receiving updates.
        bikeStationProviderRepository.registerForBikeStationUpdates(this);

        // Gets the UI's data refreshed.
        adapter.refreshData();

        // Launches the auto-update task.
        handler.post(secondTickRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregisters for receiving updates.
        bikeStationProviderRepository.unregisterForBikeStationUpdates(this);

        // Stops the auto-update task.
        handler.removeCallbacks(secondTickRunnable);
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
        adapter.refreshData();
    }

}
