package com.lemoulinstudio.bikefriend;

import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_emergency)
public class EmergencyFragment extends Fragment {

    @ViewById(R.id.hello_world)
    TextView myView;

    @AfterViews
    protected void setupViews() {
        myView.setText("Emergency");
    }

}
