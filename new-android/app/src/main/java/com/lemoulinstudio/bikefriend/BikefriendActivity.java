package com.lemoulinstudio.bikefriend;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lemoulinstudio.bikefriend.drawer.DrawerListAdapter;
import com.lemoulinstudio.bikefriend.preference.BikefriendPreferenceActivity;
import com.lemoulinstudio.bikefriend.preference.BikefriendPreferences_;
import com.lemoulinstudio.bikefriend.preference.MapProvider;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.activity_bikefriend)
public class BikefriendActivity extends ActionBarActivity {

    @Pref
    protected BikefriendPreferences_ preferences;

    @ViewById(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;

    @ViewById(R.id.drawer_list_view)
    protected ListView drawerListView;

    protected ActionBarDrawerToggle actionBarDrawerToggle;

    @AfterViews
    protected void initViews() {
        drawerListView.setAdapter(new DrawerListAdapter(this));

        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDrawerItemClicked((int) id);
            }
        });

        setClickedItem(mapItemId);

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // Helper between the navigation drawer and the action bar app icon.
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                             /* host Activity */
                drawerLayout,                     /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                // Warning: Could be a performance issue, consider caching this value.
                if (!preferences.userLearnedDrawer().get()) {
                    preferences.userLearnedDrawer().put(true);
                }

                if (currentFragment != null) {
                    currentFragment.setHasOptionsMenu(false);
                }

                invalidateOptionsMenu(); // triggers a call of onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if (currentFragment != null) {
                    currentFragment.setHasOptionsMenu(true);
                }

                invalidateOptionsMenu(); // triggers a call of onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        if (!preferences.userLearnedDrawer().get()) {
            drawerLayout.openDrawer(drawerListView);
        }

        // Defer code dependent on restoration of previous instance state.
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    private void onDrawerItemClicked(int itemId) {
        setClickedItem(itemId);
        drawerLayout.closeDrawers();
    }

    private int currentItemId = -1;
    private final int mapItemId = 0;

    private Fragment currentFragment;
    private GoogleMapFragment_ googleMapFragment;
    private MapsForgeFragment_ mapsForgeFragment;

    private void setClickedItem(int itemId) {
        // If the user clicked on an item which is already displayed, we do nothing.
        if (itemId != currentItemId) {
            switch (itemId) {
                case mapItemId: {
                    MapProvider mapProvider = MapProvider.valueOf(preferences.mapProvider().get());
                    switch (mapProvider) {
                        case GoogleMap: {
                            if (googleMapFragment == null) {
                                googleMapFragment = new GoogleMapFragment_();
                            }
                            currentFragment = googleMapFragment;
                            break;
                        }
                        case OpenStreetMap: {
                            if (mapsForgeFragment == null) {
                                mapsForgeFragment = new MapsForgeFragment_();
                            }
                            currentFragment = mapsForgeFragment;
                            break;
                        }
                    }
                    break;
                }
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, currentFragment)
                    .commit();

            currentItemId = itemId;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle events on the home button and the drawer slider icon.
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Any other buttons are handled here.
        switch (item.getItemId()) {
            case R.id.action_settings: {
                drawerLayout.closeDrawers();

                // Launch the preference activity
                Intent i = new Intent(this, BikefriendPreferenceActivity.class);
                startActivity(i);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
