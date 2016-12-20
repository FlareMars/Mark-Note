package com.flaremars.markandnote.util;

import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by FlareMars on 2016/11/4.
 */
public class SharedPrefHelper {

    private SharedPreferences preferences;

    public SharedPrefHelper(SharedPreferences sharedPreferences) {
        this.preferences = sharedPreferences;
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key ,String defaultStr) {
        return preferences.getString(key, defaultStr);
    }

    public void saveString(String key, String content) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, content);
        editor.apply();
    }

    public void saveStrings(Map<String, String> data) {
        SharedPreferences.Editor editor = preferences.edit();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

    public boolean getBool(String key) {
        return preferences.getBoolean(key, false);
    }

    public void saveBool(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Long getLong(String key) {
        Long value = preferences.getLong(key, -1);
        return value == -1 ? null : value;
    }

    public void saveLong(String key, Long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key,value);
        editor.apply();
    }

    public Integer getInt(String key) {
        Integer value = preferences.getInt(key, -1);
        return value == -1 ? null : value;
    }

    public void saveInt(String key, Integer value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
