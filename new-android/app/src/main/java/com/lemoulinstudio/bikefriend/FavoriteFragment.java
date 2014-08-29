package com.lemoulinstudio.bikefriend;

import android.support.v4.app.ListFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_favorite)
public class FavoriteFragment extends ListFragment {

    @Bean
    FavoriteStationsAdapter adapter;

    @AfterViews
    protected void setupViews() {
        this.setListAdapter(adapter);
    }
}
