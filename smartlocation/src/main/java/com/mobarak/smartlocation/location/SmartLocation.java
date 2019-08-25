package com.mobarak.smartlocation.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mobarak.smartlocation.listener.OnLocationUpdatedListener;
import com.mobarak.smartlocation.settings.LocationSettingsEnabler;

/**
 * Created by Mobarak on 25 August, 2019
 * @author Mobarak Hosen
 */
public class SmartLocation {

    public static final int LOCATION_ENABLER_CODE = 101;
    public static final int PERMISSIONS_REQUEST_LOCATION = 22;
    private static final String TAG = SmartLocation.class.getSimpleName();
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private OnLocationUpdatedListener listener;
    private Context context;
    private LocationSettingsRequest.Builder settingBuilder;
    private boolean isContinues;
    private LocationParams locationParams;

    private SmartLocation(Builder builder) {
        this.context = builder.context;
        this.locationParams = builder.params;
        this.isContinues = builder.isContinuous;
        this.listener = builder.listener;

    }

    public static LocationBuilder builder() {
        return new Builder();
    }

    public void init() {
        this.locationRequest = createRequest(locationParams);
        this.settingBuilder = new LocationSettingsRequest.Builder();
        settingBuilder.addLocationRequest(this.locationRequest);
        this.locationSettingsRequest = settingBuilder.build();

        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult); // why? this. is. retarded. Android.
                if (listener != null) {
                    listener.onLocationUpdated(locationResult.getLastLocation());
                }
            }
        };

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

    }

    private LocationRequest createRequest(LocationParams params) {
        LocationRequest request = new LocationRequest();
        request.setInterval(params.getInterval() > 0 ? params.getInterval() : UPDATE_INTERVAL_IN_MILLISECONDS);
        request.setFastestInterval(params.getInterval() > 0 ? params.getInterval() : FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        switch (params.getAccuracy()) {
            case HIGH:
                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                break;
            case MEDIUM:
                request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                break;
            case LOW:
                request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                break;
            case LOWEST:
                request.setPriority(LocationRequest.PRIORITY_NO_POWER);
        }

        return request;
    }

    public void onContinuousLocation() {
        if (context instanceof Activity) {
            if (!isLocationPermitted(context)) {
                checkLocationPermission((Activity) context);
            }
            if (!isLocationEnabled(context)) {
                turnOnLocationService(context);
            }
        }
        this.mFusedLocationClient.requestLocationUpdates(this.locationRequest,
                this.locationCallback, Looper.myLooper()); //Looper.myLooper()
    }

    public void onFixLocation() {
        if (context instanceof Activity) {
            if (!isLocationPermitted(context)) {
                checkLocationPermission((Activity) context);
            }
            if(!isLocationEnabled(context)){
                turnOnLocationService(context);
            }
        }
        this.mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (listener != null) {
                            listener.onLocationUpdated(location);
                        }
                    }
                });


    }

    public LocationSettingsRequest getLocationSettingsRequest() {
        return this.locationSettingsRequest;
    }

    public void stop() {
        Log.i(TAG, "stop() Stopping location tracking");
        this.mFusedLocationClient.removeLocationUpdates(this.locationCallback);
    }

    public void enableLocationSettings() {
        if (context instanceof Activity) {

            if (settingBuilder == null) {
                this.settingBuilder = new LocationSettingsRequest.Builder();
                settingBuilder.addLocationRequest(this.locationRequest);
            }

            //**************************
            settingBuilder.setAlwaysShow(true); //this is the key ingredient
            //**************************
            Task<LocationSettingsResponse> result =
                    LocationServices.getSettingsClient(context).checkLocationSettings(settingBuilder.build());
            result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(Task<LocationSettingsResponse> task) {
                    try {
                        LocationSettingsResponse response = task.getResult(ApiException.class);
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                    } catch (ApiException exception) {
                        switch (exception.getStatusCode()) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the
                                // user a dialog.
                                try {
                                    // Cast to a resolvable exception.
                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    resolvable.startResolutionForResult(
                                            (Activity) context, LOCATION_ENABLER_CODE);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                } catch (ClassCastException e) {
                                    // Ignore, should be an impossible error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way to fix the
                                // settings so we won't show the dialog.
                                break;
                        }
                    }
                }
            });
        }
    }

    public boolean isLocationPermitted(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isLocationEnabled(Context context) {
        try {
            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void turnOnLocationService(Context context) {
        try {
            LocationSettingsEnabler enabler = new LocationSettingsEnabler(context);
            enabler.enableLocationSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkLocationPermission(final Activity context) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }

        } else {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);


        }
    }

    public interface LocationBuilder {
        IContext with(Context context);
    }

    public interface IContext {
        IConfig config(LocationParams params);
    }

    public interface IConfig {
        IContinuous continuous();

        IFix fix();
    }

    public interface IContinuous {
        ICallback listener(OnLocationUpdatedListener listener);
    }

    public interface IFix {
        ICallback listener(OnLocationUpdatedListener listener);
    }

    public interface ICallback {
        SmartLocation start();
    }

    public static class Builder implements LocationBuilder, IContext, IConfig, IContinuous, IFix, ICallback {

        private Context context;
        private LocationParams params;
        private OnLocationUpdatedListener listener;
        private boolean isContinuous;

        @Override
        public IContext with(Context context) {
            this.context = context;
            return this;
        }

        @Override
        public IConfig config(LocationParams params) {
            this.params = params;
            return this;
        }

        @Override
        public IContinuous continuous() {
            this.isContinuous = true;
            return this;
        }

        @Override
        public IFix fix() {
            this.isContinuous = false;
            return this;
        }

        @Override
        public ICallback listener(OnLocationUpdatedListener listener) {
            this.listener = listener;
            return this;
        }

        @Override
        public SmartLocation start() {
            SmartLocation smartLocationProvider = new SmartLocation(this);
            smartLocationProvider.init();
            if (isContinuous) {
                smartLocationProvider.onContinuousLocation();
            } else {
                smartLocationProvider.onFixLocation();
            }
            return smartLocationProvider;
        }
    }
}
