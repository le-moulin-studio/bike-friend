package com.lemoulinstudio.bikefriend;

import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_chrono)
public class ChronoFragment extends Fragment {

    @ViewById(R.id.hello_world)
    TextView myView;

    @ViewById(R.id.my_button)
    Button myButton;

    @AfterViews
    protected void setupOfMyViews_foobar() {
        myView.setText("Bonjour le monde !");
    }

}
