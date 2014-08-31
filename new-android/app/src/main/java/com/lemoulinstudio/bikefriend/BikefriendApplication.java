package com.lemoulinstudio.bikefriend;

import android.app.Application;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EApplication;
//import org.mapsforge.map.android.graphics.AndroidGraphicFactory;

@EApplication
public class BikefriendApplication extends Application {

    public static final String TAG = "bikefriend";

    @Override
    public void onCreate() {
        super.onCreate();

//        AndroidGraphicFactory.createInstance(this);

        //initStuffsInBackground();
    }

//    @Background
//    private void initStuffsInBackground() {
//    }

}
