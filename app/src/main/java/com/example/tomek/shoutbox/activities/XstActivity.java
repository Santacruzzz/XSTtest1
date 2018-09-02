package com.example.tomek.shoutbox.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.XstApplication;
import com.example.tomek.shoutbox.XstDb;
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
    protected int keyboardSize;
    protected long obrazkiLastDate;
    protected XstApplication xstApp;
    protected XstDb bazaDanych;
    private ActionBar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        xstApp = (XstApplication) getApplicationContext();
        super.onCreate(savedInstanceState);
        sharedPrefs = getSharedPreferences(Typy.PREFS_NAME, 0);
        bazaDanych = xstApp.getBazaDanych();
        wczytajUstawienia();
        wczytajStyl();
        setKeyboardSizeListener();
    }

    protected void ustawToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setDisplayShowHomeEnabled(true);
        }

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
        if (themeName.equals("light")) {
            setTheme(R.style.xstThemeLight);
            xstApp.setTheme(R.style.xstThemeLight);
        } else {
            setTheme(R.style.xstThemeDark);
            xstApp.setTheme(R.style.xstThemeDark);
        }
    }

    protected void wczytajUstawienia() {
        apiKey = xstApp.getApiKey();
        login = xstApp.getLogin();
        nickname = xstApp.getNickname();
        avatarFileName = xstApp.getAvatarFileName();
        themeName = xstApp.getThemeName();
        keyboardSize = xstApp.getKeyboardSize();
        obrazkiLastDate = xstApp.getObrazkiLastDate();

        Log.i("xst", String.format("wczytajUstawienia(): apiKey=%s, login=%s, nickname=%s, avatar=%s, theme=%s kbsize=%d",
                apiKey, login, nickname, avatarFileName, themeName, keyboardSize) );

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

    private void setKeyboardSizeListener() {


        final View rootview = this.getWindow().getDecorView();
        rootview.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {

                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    return;
                }

                Rect r = new Rect();
                rootview.getWindowVisibleDisplayFrame(r);

                int screenHeight = rootview.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top);
                int resourceId = getResources().getIdentifier("status_bar_height","dimen", "android");
                if (resourceId > 0) {
                    heightDifference -= getResources().getDimensionPixelSize(resourceId);
                }

                if (heightDifference > 250 && heightDifference != keyboardSize) {
                    xstApp.zapiszUstawienie(Typy.PREFS_KB_SIZE, heightDifference);
                    keyboardSize = heightDifference;
                }

            }
        });
    }

    public SharedPreferences getSharedPrefs() {
        return sharedPrefs;
    }

    protected void setHomeMenuIcon() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (themeName.equals("dark")) {
                actionBar.setHomeAsUpIndicator(R.drawable.hamb_small);
            } else {
                actionBar.setHomeAsUpIndicator(R.drawable.hamb_small_black);
            }
        }
    }
}
