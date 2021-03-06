package com.lemoulinstudio.bikefriend;

import android.support.v4.app.Fragment;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_lost_found)
public class LostFoundFragment extends Fragment {

    @ViewById(R.id.hello_world)
    TextView myView;

    @AfterViews
    protected void setupViews() {
        myView.setText("Lost and found");
    }

}
