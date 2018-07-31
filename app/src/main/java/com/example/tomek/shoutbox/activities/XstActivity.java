package com.example.tomek.shoutbox.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.utils.LruBitmapCache;
import com.example.tomek.shoutbox.utils.Typy;

public class XstActivity extends AppCompatActivity implements IVolley {

    protected RequestQueue requestQueue;
    protected ImageLoader imageLoader;
    protected SharedPreferences sharedPrefs;
    protected String themeName;
    protected String apiKey;
    protected String login;
    protected String nickname;
    protected String avatarFileName;
    protected boolean czyZalogowany;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = getSharedPreferences(Typy.PREFS_NAME, 0);
        wczytajUstawienia();
        wczytajStyl();
    }

    @Override
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    @Override
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (imageLoader == null) {
            imageLoader = new ImageLoader(this.requestQueue, new LruBitmapCache());
        }
        return this.imageLoader;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    protected void wczytajStyl() {
        Log.i("xst", "Wczytuje styl: " + themeName);
        if (themeName.equals("light")) {
            setTheme(R.style.xstThemeLight);
            getApplicationContext().setTheme(R.style.xstThemeLight);
        } else {
            setTheme(R.style.xstThemeDark);
            getApplicationContext().setTheme(R.style.xstThemeDark);
        }
    }

    protected void wczytajUstawienia() {
        apiKey = sharedPrefs.getString(Typy.PREFS_API_KEY, "");
        login = sharedPrefs.getString(Typy.PREFS_LOGIN, "");
        nickname = sharedPrefs.getString(Typy.PREFS_NICNKAME, "");
        avatarFileName = sharedPrefs.getString(Typy.PREFS_AVATAR, "");
        themeName = sharedPrefs.getString(Typy.PREFS_THEME, "dark");

        Log.i("xst", String.format("wczytajUstawienia(): apiKey=%s, login=%s, nickname=%s, avatar=%s, theme=%s",
                apiKey, login, nickname, avatarFileName, themeName) );

        czyZalogowany = apiKey.length() == 32;
        if (login.isEmpty()) {
            czyZalogowany = false;
        }
        if (nickname.isEmpty()) {
            czyZalogowany = false;
        }
    }

    protected void enableBackButtonInActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);

        }
    }
}
