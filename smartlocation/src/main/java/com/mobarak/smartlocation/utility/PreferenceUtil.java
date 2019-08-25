package com.mobarak.smartlocation.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class PreferenceUtil {
    public static final String SMART_LOCATION_PREFERENCE ="smartLocationPreference";
    public static final String CACHE_LOCATION_KEY ="cached-latest-location";

    public static void saveObjectToSharedPreference(Context context, Object object) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SMART_LOCATION_PREFERENCE, Context.MODE_PRIVATE);
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            final Gson gson = new Gson();
            String serializedObject = gson.toJson(object);
            sharedPreferencesEditor.putString(CACHE_LOCATION_KEY, serializedObject);
            sharedPreferencesEditor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <GenericClass> GenericClass getSavedObjectFromPreference(Context context, Class<GenericClass> classType) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SMART_LOCATION_PREFERENCE, Context.MODE_PRIVATE);
            if (sharedPreferences!=null && sharedPreferences.contains(CACHE_LOCATION_KEY)) {
                final Gson gson = new Gson();
                return gson.fromJson(sharedPreferences.getString(CACHE_LOCATION_KEY, ""), classType);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
