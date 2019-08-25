package com.mobarak.smartlocation.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class PreferenceUtility {
    Context mContext;
    SharedPreferences preferences;

    public PreferenceUtility(Context mContext) {
        this.mContext = mContext;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(mContext);;
    }

    public static void saveObjectToSharedPreference(Context context, String preferenceFileName, String serializedObjectKey, Object object) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            final Gson gson = new Gson();
            String serializedObject = gson.toJson(object);
            sharedPreferencesEditor.putString(serializedObjectKey, serializedObject);
            sharedPreferencesEditor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <GenericClass> GenericClass getSavedObjectFromPreference(Context context, String preferenceFileName, String preferenceKey, Class<GenericClass> classType) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
            if (sharedPreferences!=null && sharedPreferences.contains(preferenceKey)) {
                final Gson gson = new Gson();
                return gson.fromJson(sharedPreferences.getString(preferenceKey, ""), classType);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
