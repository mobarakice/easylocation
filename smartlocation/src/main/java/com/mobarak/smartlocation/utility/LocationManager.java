package com.mobarak.smartlocation.utility;


import android.content.Context;
import android.location.Location;

import com.mobarak.smartlocation.location.SmartLocation;
import com.mobarak.smartlocation.location.LocationAccuracy;
import com.mobarak.smartlocation.location.LocationParams;
import com.mobarak.smartlocation.listener.ILocationUpdatedListener;
import com.mobarak.smartlocation.models.LocationInfo;


/**
 * Created by Mobarak on 25 August, 2019
 *
 * @author Mobarak Hosen
 */

public class LocationManager {

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

    public static SmartLocation getFixLocation(Context context, ILocationUpdatedListener listener) {
        return SmartLocation.builder()
                .with(context)
                .config(getLocationParams(TRACKING_INTERVAL, TRACKING_DISTANCE))
                .fix()
                .listener(listener)
                .start();
    }

    public static SmartLocation getContinuousLocationWithDefaultInterval(
            Context context,
            ILocationUpdatedListener listener) {
        return SmartLocation.builder()
                .with(context)
                .config(getLocationParams(TRACKING_INTERVAL, TRACKING_DISTANCE))
                .continuous()
                .listener(listener)
                .start();
    }

    public static SmartLocation getContinuousLocationWithCustomInterval(
            Context context,
            long intervalInSecond,
            long distanceInMeter,
            ILocationUpdatedListener listener) {
        return SmartLocation.builder()
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
