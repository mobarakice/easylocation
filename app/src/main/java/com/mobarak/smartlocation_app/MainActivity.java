package com.mobarak.smartlocation_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mobarak.smartlocation.listener.ILocationUpdatedListener;
import com.mobarak.smartlocation.location.SmartLocation;
import com.mobarak.smartlocation.utility.LocationManager;

public class MainActivity extends AppCompatActivity implements ILocationUpdatedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    TextView tvLocation;
    private Location location;
    private SmartLocation smartLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvLocation = findViewById(R.id.tv_location);
    }


    @Override
    public void onLocationUpdated(Location location) {
        this.location = location;
        Log.i(TAG, "onLocationUpdated-> LAT: " + location.getLatitude()
                + ", LONG: " + location.getLongitude()
                + ", Acc: " + location.getAccuracy());
        tvLocation.setText(String.format("%s%s\n%s%s\n%s%s",
                "Latitude->", location.getLatitude(),
                "Longitude->", location.getLongitude(),
                "Accuracy->", location.getAccuracy()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        smartLocation = LocationManager.getContinuousLocationWithDefaultInterval(this,
                this::onLocationUpdated);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (smartLocation != null) {
            smartLocation.stop();
        }
    }
}
