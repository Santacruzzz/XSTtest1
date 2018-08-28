package com.example.tomek.shoutbox;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import com.example.tomek.shoutbox.utils.Typy;

public class XstApplication extends Application {

    protected XstDb bazaDanych;
    protected SharedPreferences sharedPrefs;
    protected String themeName;
    protected String apiKey;
    protected String login;
    protected String nickname;
    protected String avatarFileName;
    protected String onlineJsonString;
    protected String msgJsonString;
    protected Long obrazkiLastDate;
    protected int keyboardSize;
    protected boolean pokazujPowiadomienia;
    protected boolean automatyczneAktualizacje;
    protected int lastDate;

    public XstApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPrefs = getSharedPreferences(Typy.PREFS_NAME, 0);

        wczytajUstawienia();
        bazaDanych = new XstDb();
        bazaDanych.initialize(this);

        createNotificationMsgChannel();
        createNotificationUpdtChannel();
    }

    private void wczytajUstawienia() {
        apiKey = sharedPrefs.getString(Typy.PREFS_API_KEY, "");
        login = sharedPrefs.getString(Typy.PREFS_LOGIN, "");
        nickname = sharedPrefs.getString(Typy.PREFS_NICNKAME, "");
        avatarFileName = sharedPrefs.getString(Typy.PREFS_AVATAR, "");
        themeName = sharedPrefs.getString(Typy.PREFS_THEME, "dark");
        keyboardSize = sharedPrefs.getInt(Typy.PREFS_KB_SIZE, 90);
        obrazkiLastDate = sharedPrefs.getLong(Typy.PREFS_OBRAZKI_LAST_DATE, 0);
        automatyczneAktualizacje = sharedPrefs.getBoolean("automatyczne_aktualizacje", true);
        pokazujPowiadomienia = sharedPrefs.getBoolean("pokazuj_powiadomienia", true);
        lastDate = sharedPrefs.getInt(Typy.PREFS_LAST_DATE, 0);
        onlineJsonString = sharedPrefs.getString(Typy.PREFS_ONLINE, "");
        msgJsonString = sharedPrefs.getString(Typy.PREFS_MSGS, "");
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
        editor.apply();
        wczytajUstawienia();
    }

    public void zapiszUstawienie(String key, int val)
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(key, val);
        editor.apply();
        wczytajUstawienia();
    }

    public void zapiszUstawienie(String key, long val)
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putLong(key, val);
        editor.apply();
        wczytajUstawienia();
    }

    public String getOnline() {
        return onlineJsonString;
    }

    public long getObrazkiLastDate() {
        return obrazkiLastDate;
    }

    public XstDb getBazaDanych() {
        return bazaDanych;
    }

    protected void createNotificationMsgChannel() {
        createNotificationChannel(Typy.NOTIF_CHANNEL_MSG_ID,
                                  getString(R.string.channel_name),
                                  getString(R.string.channel_description));
    }

    protected void createNotificationUpdtChannel() {
        createNotificationChannel(Typy.NOTIF_CHANNEL_UPDT_ID,
                getString(R.string.channel_updt_name),
                getString(R.string.channel_updt_description));
    }

    private void createNotificationChannel(final String channelId, String channelName, String channelDesc) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDesc);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public boolean isPokazujPowiadomienia() {
        return pokazujPowiadomienia;
    }

    public boolean isAutomatyczneAktualizacje() {
        return automatyczneAktualizacje;
    }

    public int getLastDate() {
        return lastDate;
    }

    public String getMsgJsonString() {
        return msgJsonString;
    }
}
