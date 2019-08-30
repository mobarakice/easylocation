package com.mobarak.easylocation.location;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

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
import com.mobarak.easylocation.listener.ILocationUpdatedListener;
import com.mobarak.easylocation.utility.LocationUtil;

/**
 * Created by Mobarak on 25 August, 2019
 *
 * @author Mobarak Hosen
 */
public class EasyLocation {

    public static final int LOCATION_ENABLER_CODE = 101;
    private static final String TAG = EasyLocation.class.getSimpleName();
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private ILocationUpdatedListener listener;
    private Context context;
    private LocationSettingsRequest.Builder settingBuilder;
    private LocationParams locationParams;

    private EasyLocation(Builder builder) {
        this.context = builder.context;
        this.locationParams = builder.params;
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
            if (!LocationUtil.isLocationPermitted(context)) {
                LocationUtil.checkLocationPermission((Activity) context);
            }
            if (!LocationUtil.isLocationEnabled(context)) {
                turnOnLocationService();
            }
        }
        this.mFusedLocationClient.requestLocationUpdates(this.locationRequest,
                this.locationCallback, Looper.myLooper()); //Looper.myLooper()
    }

    public void onFixLocation() {
        if (context instanceof Activity) {
            if (!LocationUtil.isLocationPermitted(context)) {
                LocationUtil.checkLocationPermission((Activity) context);
            }
            if (!LocationUtil.isLocationEnabled(context)) {
                turnOnLocationService();
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


    public void turnOnLocationService() {
        try {
            enableLocationSettings();
        } catch (Exception e) {
            e.printStackTrace();
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
        ICallback listener(ILocationUpdatedListener listener);
    }

    public interface IFix {
        ICallback listener(ILocationUpdatedListener listener);
    }

    public interface ICallback {
        EasyLocation start();
    }

    public static class Builder implements LocationBuilder, IContext, IConfig, IContinuous, IFix, ICallback {

        private Context context;
        private LocationParams params;
        private ILocationUpdatedListener listener;
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
        public ICallback listener(ILocationUpdatedListener listener) {
            this.listener = listener;
            return this;
        }

        @Override
        public EasyLocation start() {
            EasyLocation easyLocationProvider = new EasyLocation(this);
            easyLocationProvider.init();
            if (isContinuous) {
                easyLocationProvider.onContinuousLocation();
            } else {
                easyLocationProvider.onFixLocation();
            }
            return easyLocationProvider;
        }
    }
}
