package com.lemoulinstudio.bikefriend;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.TextView;

import com.lemoulinstudio.bikefriend.preference.BikefriendPreferences_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Calendar;

@EFragment(R.layout.fragment_chronometer)
public class ChronometerFragment extends Fragment {

    @ViewById(R.id.chronometer_time_text)
    protected TextView timeTextView;

    @ViewById(R.id.chronometer_info_text)
    protected TextView infoTextView;

    @ViewById(R.id.chronometer_start_button)
    protected Button startButton;

    @ViewById(R.id.chronometer_stop_button)
    protected Button stopButton;

    @Pref
    protected BikefriendPreferences_ preferences;

    // Those 2 fields need to be persisted on disk, we use the preferences.
    protected boolean isStarted;
    protected long startTime;

    // This is for the UI update every second.
    private final Handler handler = new Handler();
    private final Runnable secondTickRunnable = new Runnable() {
        @Override
        public void run() {
            if (isStarted) {
                updateTimeTextView();
                handler.postDelayed(this, 1000);
            }
        }
    };

    @AfterViews
    protected void setupViews() {
    }

    @Override
    public void onResume() {
        super.onResume();

        // Read the preferences into local variables.
        isStarted = preferences.chronometerIsStarted().get();
        startTime = preferences.chronometerStartTime().get();

        updateButtonState();
        updateInfoTextView();
        updateTimeTextView();

        if (isStarted) {
            // Starts the UI refresh.
            handler.post(secondTickRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Save the state in the preferences.
        preferences.edit()
                .chronometerIsStarted().put(isStarted)
                .chronometerStartTime().put(startTime)
                .apply();

        // Makes sure that the UI refresh stops.
        isStarted = false;
        handler.removeCallbacks(secondTickRunnable);
    }

    @Click(R.id.chronometer_start_button)
    protected void onStartButtonClicked() {
        if (!isStarted) {
            isStarted = true;
            startTime = System.currentTimeMillis();
            updateButtonState();
            updateInfoTextView();

            // Starts the UI refresh.
            handler.post(secondTickRunnable);
        }
    }

    @Click(R.id.chronometer_stop_button)
    protected void onStopButtonClicked() {
        if (isStarted) {
            isStarted = false;
            updateButtonState();
            updateInfoTextView();
            updateTimeTextView();

            // Stops the UI refresh.
            handler.removeCallbacks(secondTickRunnable);
        }
    }

    private void updateButtonState() {
        startButton.setEnabled(!isStarted);
        stopButton.setEnabled(isStarted);
    }

    private void updateInfoTextView() {
        if (isStarted) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startTime);
            int seconds = calendar.get(Calendar.SECOND);
            int minutes = calendar.get(Calendar.MINUTE);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            infoTextView.setText(String.format("(Started at %02d:%02d:%02d)", hours, minutes, seconds));
        }
        else {
            infoTextView.setText("");
        }
    }

    private void updateTimeTextView() {
        if (isStarted) {
            long duration = System.currentTimeMillis() - startTime;

            duration /= 1000; // milliseconds -> seconds
            long seconds = duration % 60;

            duration /= 60; // seconds -> minutes
            long minutes = duration % 60;

            duration /= 60; // minutes -> hours
            long hours = duration % 24;

            duration /= 24; // hours -> days
            long days = duration;

            timeTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        }
        else {
            timeTextView.setText("00:00:00");
        }
    }

}
