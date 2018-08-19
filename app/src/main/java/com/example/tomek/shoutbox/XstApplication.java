package com.example.tomek.shoutbox;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.example.tomek.shoutbox.utils.Typy;

public class XstApplication extends Application {

    private BazaDanych bazaDanych;
    protected SharedPreferences sharedPrefs;
    protected String themeName;
    protected String apiKey;
    protected String login;
    protected String nickname;
    protected String avatarFileName;
    protected String onlineJsonString;
    protected int keyboardSize;

    public XstApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPrefs = getSharedPreferences(Typy.PREFS_NAME, 0);

        wczytajUstawienia();
        bazaDanych = new BazaDanych();
        bazaDanych.initialize(this);
    }

    private void wczytajUstawienia() {
        apiKey = sharedPrefs.getString(Typy.PREFS_API_KEY, "");
        login = sharedPrefs.getString(Typy.PREFS_LOGIN, "");
        nickname = sharedPrefs.getString(Typy.PREFS_NICNKAME, "");
        avatarFileName = sharedPrefs.getString(Typy.PREFS_AVATAR, "");
        themeName = sharedPrefs.getString(Typy.PREFS_THEME, "dark");
        keyboardSize = sharedPrefs.getInt(Typy.PREFS_KB_SIZE, 90);
    }

    // Called by the system when the device configuration changes while your component is running.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getAvatarFileName() {
        return avatarFileName;
    }

    public String getLogin() {
        return login;
    }

    public String getNickname() {
        return nickname;
    }

    public String getThemeName() {
        return themeName;
    }

    public int getKeyboardSize() {
        return keyboardSize;
    }

    public void zapiszUstawienie(String key, String val)
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(key, val);
        editor.commit();
        wczytajUstawienia();
    }

    public void zapiszUstawienie(String key, int val)
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(key, val);
        editor.commit();
        wczytajUstawienia();
    }

    public String getOnline() {
        onlineJsonString = sharedPrefs.getString(Typy.PREFS_ONLINE, "");
        return onlineJsonString;
    }
}
