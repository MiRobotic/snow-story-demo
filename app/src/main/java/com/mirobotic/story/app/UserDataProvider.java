package com.mirobotic.story.app;

import android.content.Context;
import android.content.SharedPreferences;

import static com.mirobotic.story.app.Config.LANG_ENGLISH;

public class UserDataProvider {


    private static UserDataProvider dataProvider = null;
    private SharedPreferences preferences;

    private UserDataProvider(Context context) {
        preferences = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
    }

    public static UserDataProvider getInstance(Context context) {
        if (dataProvider == null) {
            dataProvider = new UserDataProvider(context);
        }
        return dataProvider;
    }

    public String getLanguageCode() {
        return preferences.getString("lang", "en");
    }

    public void setLanguageCode(String language) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("code", language);
        editor.apply();
    }

    public String getLanguage() {
        return preferences.getString("lang", LANG_ENGLISH);
    }

    public void setLanguage(String language) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lang", language);
        editor.apply();
    }

    public boolean isPermissionAsked() {
        return preferences.getBoolean("permissionsAsked", false);
    }

    public void dontAskPermission() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("permissionsAsked", true);
        editor.apply();
    }
}
