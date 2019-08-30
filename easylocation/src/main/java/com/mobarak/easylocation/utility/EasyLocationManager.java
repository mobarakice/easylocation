package com.mobarak.easylocation.utility;


import android.content.Context;
import android.location.Location;

import com.mobarak.easylocation.location.EasyLocation;
import com.mobarak.easylocation.location.LocationAccuracy;
import com.mobarak.easylocation.location.LocationParams;
import com.mobarak.easylocation.listener.ILocationUpdatedListener;
import com.mobarak.easylocation.models.LocationInfo;


/**
 * Created by Mobarak on 25 August, 2019
 *
 * @author Mobarak Hosen
 */

public class EasyLocationManager {

    public static int TRACKING_INTERVAL = 10;
    public static int TRACKING_DISTANCE = 3;


    private static LocationParams getLocationParams(long intervalInSecond, long distanceInMeter) {
        long mLocTrackingInterval = 1000 * (intervalInSecond > 0 ? intervalInSecond : TRACKING_INTERVAL);
        float trackingDistance = distanceInMeter > 0 ? distanceInMeter : TRACKING_DISTANCE;
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance)
                .setInterval(mLocTrackingInterval);
        return builder.build();
    }

    public static EasyLocation getFixLocation(Context context, ILocationUpdatedListener listener) {
        return EasyLocation.builder()
                .with(context)
                .config(getLocationParams(TRACKING_INTERVAL, TRACKING_DISTANCE))
                .fix()
                .listener(listener)
                .start();
    }

    public static EasyLocation getContinuousLocationWithDefaultInterval(
            Context context,
            ILocationUpdatedListener listener) {
        return EasyLocation.builder()
                .with(context)
                .config(getLocationParams(TRACKING_INTERVAL, TRACKING_DISTANCE))
                .continuous()
                .listener(listener)
                .start();
    }

    public static EasyLocation getContinuousLocationWithCustomInterval(
            Context context,
            long intervalInSecond,
            long distanceInMeter,
            ILocationUpdatedListener listener) {
        return EasyLocation.builder()
                .with(context)
                .config(getLocationParams(intervalInSecond, distanceInMeter))
                .continuous()
                .listener(listener)
                .start();
    }

    public static void saveLatestLocation(Context context, Location location) {
        if (context != null && location != null) {
            LocationInfo locationInfo = new LocationInfo.LocationInfoBuilder()
                    .setLatitude(location.getLatitude() + "")
                    .setLongitude(location.getLongitude() + "")
                    .setAltitude(location.getAltitude() + "")
                    .setAccuracy((int) location.getAccuracy())
                    .setQuery_time(location.getTime())
                    .build();
            PreferenceUtil.saveObjectToSharedPreference(context, locationInfo);
        }

    }

    public static LocationInfo getCachedLocation(Context context) {
        return PreferenceUtil.getSavedObjectFromPreference(context, LocationInfo.class);
    }
}
