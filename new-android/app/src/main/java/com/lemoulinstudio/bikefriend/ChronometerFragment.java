package com.lemoulinstudio.bikefriend;

import android.support.v4.app.Fragment;
import android.widget.Chronometer;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Date;

@EFragment(R.layout.fragment_chronometer)
public class ChronometerFragment extends Fragment {

    @ViewById(R.id.hello_world)
    TextView myView;

    @AfterViews
    protected void setupViews() {
        myView.setText("Chronometer");
    }

}
