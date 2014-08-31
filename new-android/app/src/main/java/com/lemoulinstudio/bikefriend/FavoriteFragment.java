package com.lemoulinstudio.bikefriend;

import android.support.v4.app.ListFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.res.StringRes;

@EFragment(R.layout.fragment_favorite)
public class FavoriteFragment extends ListFragment {

    @Bean
    protected FavoriteStationsAdapter adapter;

    @StringRes(R.string.favorite_list_empty_list_text)
    protected String emptyListText;

    @AfterViews
    protected void setupViews() {
        setListAdapter(adapter);
        setEmptyText(emptyListText);
    }

}
