package com.example.tomek.shoutbox.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.tomek.shoutbox.fragments.FragmentOnline;
import com.example.tomek.shoutbox.fragments.FragmentSb;
import com.example.tomek.shoutbox.MyBroadcastReceiver;
import com.example.tomek.shoutbox.NavItem;
import com.example.tomek.shoutbox.NowaWiadomoscReceiver;
import com.example.tomek.shoutbox.OnlineItem;
import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.adapters.ScreenSlidePagerAdapter;
import com.example.tomek.shoutbox.Wiadomosc;
import com.example.tomek.shoutbox.XstService;
import com.example.tomek.shoutbox.adapters.DrawerListAdapter;
import com.example.tomek.shoutbox.utils.LruBitmapCache;
import com.example.tomek.shoutbox.utils.Typy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LayoutGlownyActivity
        extends AppCompatActivity
        implements ViewPager.OnPageChangeListener,
        IMainActivity,
                   View.OnClickListener,
                   CompoundButton.OnCheckedChangeListener {
    ArrayList<Wiadomosc> arrayWiadomosci;
    ArrayList<OnlineItem> arrayOnline;
    RequestQueue mRequestQueue;
    ImageLoader mImageLoader;
    MyBroadcastReceiver mReceiver;
    NowaWiadomoscReceiver mNowaWiadomoscReceiver;

    SharedPreferences mSharedPrefs;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private RelativeLayout mRelativeLayoutProfileBox;
    NetworkImageView userAvatar;
    TextView userNick;
    private Switch mCheckBoxDarkTheme;
    private Button mButtonWyloguj;
    private Button mButtonZaloguj;

    private boolean mZalogowany;
    private String mApiKey;
    private String mLogin;
    private String mNickname;
    private String mAvatar;
    private String mTheme;
    private boolean mPozwolNaZmianeStylu;
    private FragmentSb mFragmentSb;
    private FragmentOnline mFragmentOnline;
    ArrayList<NavItem> mNavItems;
    private int mLikedMsgPosition;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private Toolbar toolbar;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wczytajStyl();
        wczytaj_ustawienia();
        zaladujWidokPodstawowy();

        if (mZalogowany) {
            zalogowano();
        } else {
            zaladujWidokNiezalogowany();
        }

        mCheckBoxDarkTheme.setOnCheckedChangeListener(this);
    }

    private void wczytajStyl() {
        mSharedPrefs = getSharedPreferences(Typy.PREFS_NAME, 0);
        mTheme = mSharedPrefs.getString(Typy.PREFS_THEME, "dark");
        Log.i("xst", "Wczytuje styl: " + mTheme);
        if (mTheme.equals("light")) {
            setTheme(R.style.xstThemeLight);
            getApplicationContext().setTheme(R.style.xstThemeLight);
        } else {
            setTheme(R.style.xstThemeDark);
            getApplicationContext().setTheme(R.style.xstThemeDark);
        }
    }

    private void zaladujWidokPodstawowy() {
        setContentView(R.layout.layout_glowny);

        mRelativeLayoutProfileBox = findViewById(R.id.profileBox);
        userAvatar = findViewById(R.id.userAvatar);
        userNick = findViewById(R.id.userName);
        arrayWiadomosci = new ArrayList<>();
        arrayOnline = new ArrayList<>();
        mNavItems = new ArrayList<>();
        mNavItems.add(new NavItem(Typy.FRAGMENT_USTAWIENIA, R.drawable.xst));
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new DrawerListAdapter(this, mNavItems));
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        ViewPager mPager = findViewById(R.id.sb_pager);
        mPager.addOnPageChangeListener(this);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(mPagerAdapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);

        mCheckBoxDarkTheme = findViewById(R.id.checkBoxTheme);
        mButtonWyloguj = findViewById(R.id.buttonWyloguj);
        mButtonZaloguj = findViewById(R.id.buttonZaloguj);
        mButtonWyloguj.setOnClickListener(this);
        mButtonZaloguj.setOnClickListener(this);
        mLikedMsgPosition = 0;
        mRelativeLayoutProfileBox.setVisibility(View.VISIBLE);
        mPozwolNaZmianeStylu = false;
        if (mTheme.equals("dark")) {
            mCheckBoxDarkTheme.setChecked(true);
        } else {
            mCheckBoxDarkTheme.setChecked(false);
        }
        mPozwolNaZmianeStylu = true;
        userNick.setText(mNickname);
    }

    private void zaladujWidokNiezalogowany() {
        startActivityForResult(new Intent(this, LoginActivity.class), Typy.REQUEST_ZALOGUJ);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public ArrayList<Wiadomosc> getWiadomosci() {
        return arrayWiadomosci;
    }

    @Override
    public void odswiezWiadomosci() {
        mStartService("odswiez");
    }

    @Override
    public ArrayList<OnlineItem> getOnline() {
        try {
            String sitems = mSharedPrefs.getString(Typy.PREFS_ONLINE, "0");
            JSONArray items = new JSONArray(sitems);
            arrayOnline.clear();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                arrayOnline.add(new OnlineItem(item));
            }
        } catch (JSONException ignored) {

        }
        return arrayOnline;
    }

    @Override
    public int getThemeRecourceId(int[] attrs) {
        int themeId;
        int attributeResourceId = 0;
        try {
            themeId = getPackageManager().getActivityInfo(getComponentName(), 0).theme;
            TypedArray a = getTheme().obtainStyledAttributes(themeId, attrs);
            attributeResourceId = a.getResourceId(0, 0);
            a.recycle();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return attributeResourceId;
    }

    @Override
    public int getThemeColor(int[] attrs) {
        int themeId;
        int color = 0;
        try {
            themeId = getPackageManager().getActivityInfo(getComponentName(), 0).theme;
            TypedArray ta = obtainStyledAttributes(themeId, attrs);
            color = ta.getColor(0, Color.BLACK); //I set Black as the default color
            ta.recycle();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return color;
    }

    @Override
    public void nowe_online(Intent intent) {
        getmFragmentOnline().odswiezOnline(getOnline());
    }

    @Override
    public void wyslij_wiadomosc(String wiadomosc) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", mApiKey);
        params.put("msg", wiadomosc);
//        params.put("msg-base64", Utils.zakodujWiadomosc(wiadomosc));
        JSONObject request = new JSONObject(params);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Typy.API_MSG_SEND, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("success") == 1) {
                        getmFragmentSb().wyslano_wiadomosc(true);
                        mStartService("odswiez");
                    } else {
                        getmFragmentSb().wyslano_wiadomosc(false);
                    }
                } catch (JSONException ignored) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                obsluzBrakInternetu();
                // TODO sprawdzi czy blad to odpowiedz 500 czy brak internetu
                // TODO dodać job internetu
            }
        });
        req.setTag(Typy.TAG_SEND_MSG);
        getRequestQueue().add(req);
    }

    @Override
    public void polajkowanoWiadomosc(int msgid) {
        getmFragmentSb().polajkowanoWiadomosc(msgid, mLikedMsgPosition);
    }

    @Override
    public void lajkujWiadomosc(int id, int position) {
        mLikedMsgPosition = position;
        mStartService("like", id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wyrejestrujReceivery();
        if (mZalogowany) {
            mStartService("onPause");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        zarejestrujReceivery();
        if (mZalogowany) {
            mStartService("onResume");
        } else {
            mStartService("wylogowano");
        }
        pobierz_wiadomosc(null);
    }

    @Override
    public void broadcastReceived(String intent) {
        switch (intent) {
            case Typy.BROADCAST_INTERNET_OK: {
                pobierz_wiadomosc(null);
                Log.i("xst", "WRÓCIŁ INTERNEEET");
                mStartService("odswiez");
                break;
            }
            case Typy.BROADCAST_ERROR: {
                obsluzBrakInternetu();
                mFragmentSb.bladOdswiezania();
                break;
            }
        }

    }

    private void obsluzBrakInternetu() {
        Toast.makeText(this, "Brak internetu", Toast.LENGTH_LONG).show();
    }

    @Override
    public void nowa_wiadomosc(Intent i) {
        pobierz_wiadomosc(i);
    }

    private void wczytaj_ustawienia() {
        mApiKey = mSharedPrefs.getString(Typy.PREFS_API_KEY, "");
        mLogin = mSharedPrefs.getString(Typy.PREFS_LOGIN, "");
        mNickname = mSharedPrefs.getString(Typy.PREFS_NICNKAME, "");
        mAvatar = mSharedPrefs.getString(Typy.PREFS_AVATAR, "");
        mTheme = mSharedPrefs.getString(Typy.PREFS_THEME, "dark");

        mZalogowany = mApiKey.length() == 32;
        if (mLogin.isEmpty()) {
            mZalogowany = false;
        }
        if (mNickname.isEmpty()) {
            mZalogowany = false;
        }
    }

    public FragmentSb getmFragmentSb() {
        if (mFragmentSb == null) {
            mFragmentSb = new FragmentSb();
        }
        return mFragmentSb;
    }

    public FragmentOnline getmFragmentOnline() {
        if (mFragmentOnline == null) {
            mFragmentOnline = new FragmentOnline();
        }
        return mFragmentOnline;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public void zalogowano() {
        mZalogowany = true;
        ustawWidokZalogowania();

        mReceiver = new MyBroadcastReceiver(this);
        mNowaWiadomoscReceiver = new NowaWiadomoscReceiver(this);

        zarejestrujReceivery();

        mStartService("zalogowano");
        pobierz_wiadomosc(null);
    }

    private void ustawWidokZalogowania() {
        userAvatar.setImageUrl(Typy.URL_AVATAR + mAvatar, getImageLoader());
        userNick.setText(mNickname);
        mRelativeLayoutProfileBox.setVisibility(View.VISIBLE);
        mButtonWyloguj.setVisibility(View.VISIBLE);
        mButtonZaloguj.setVisibility(View.INVISIBLE);
        mDrawerLayout.closeDrawers();
    }

    private void ustawWidokWylogowania() {
        mRelativeLayoutProfileBox.setVisibility(View.GONE);
        mButtonZaloguj.setVisibility(View.VISIBLE);
        mButtonWyloguj.setVisibility(View.INVISIBLE);
        mDrawerLayout.openDrawer(Gravity.START);
    }

    private void pobierz_wiadomosc(Intent intent) {
        try {
            String sitems = mSharedPrefs.getString(Typy.PREFS_MSGS, "0");
            JSONArray items = new JSONArray(sitems);
            arrayWiadomosci.clear();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                arrayWiadomosci.add(new Wiadomosc(item));
            }
            if (mFragmentSb != null) {
                mFragmentSb.odswiezWiadomosci(this.arrayWiadomosci);
            }
        } catch (JSONException ignored) {

        }
    }

    private void mStartService(String msg) {
        Intent intentStartService = new Intent(this, XstService.class);
        intentStartService.putExtra("msg", msg);
        startService(intentStartService);
    }

    private void mStartService(String msg, int value) {
        Intent intentStartService = new Intent(this, XstService.class);
        intentStartService.putExtra("msg", msg);
        intentStartService.putExtra("value", value);
        startService(intentStartService);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonWyloguj:
                wyloguj();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.buttonZaloguj:
                zaladujWidokNiezalogowany();
                break;
        }
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            mTheme = "dark";
        } else {
            mTheme = "light";
        }
        mSharedPrefs.edit().putString(Typy.PREFS_THEME, mTheme).commit();
        if (mPozwolNaZmianeStylu) {
            finish();
            Intent intent = new Intent(this, LayoutGlownyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (mZalogowany) {
                selectDrawerItem(i);
            }
        }
    }
    private void selectDrawerItem(int i) {
        if (mNavItems.get(i).mTitle.toLowerCase().equals(Typy.FRAGMENT_USTAWIENIA)) {
            //TODO START ACTIVITY USTAWIENIA
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        //Bundle args = new Bundle();
        //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        //fragment.setArguments(args);

        mDrawerLayout.closeDrawers();
    }

    private void wyloguj() {
        SharedPreferences.Editor editor = getSharedPreferences(Typy.PREFS_NAME, 0).edit();
        editor.remove(Typy.PREFS_NICNKAME);
        editor.remove(Typy.PREFS_LOGIN);
        editor.remove(Typy.PREFS_API_KEY);
        editor.remove(Typy.PREFS_AVATAR);
        editor.remove(Typy.PREFS_MSGS);
        editor.remove(Typy.PREFS_ONLINE);
        editor.remove(Typy.PREFS_LAST_DATE);
        editor.commit();
        mZalogowany = false;

        wyrejestrujReceivery();

        mButtonWyloguj.setVisibility(View.GONE);
        mStartService("wylogowano");
        zaladujWidokNiezalogowany();
    }

    private void wyrejestrujReceivery() {
        try {
            if (mReceiver != null && mNowaWiadomoscReceiver != null) {
                unregisterReceiver(mReceiver);
                unregisterReceiver(mNowaWiadomoscReceiver);
            }
        } catch (IllegalArgumentException ignored) {

        }
    }

    private void zarejestrujReceivery() {
        registerReceiver(mReceiver, new IntentFilter(Typy.BROADCAST_INTERNET_OK));
        registerReceiver(mReceiver, new IntentFilter(Typy.BROADCAST_ERROR));
        registerReceiver(mNowaWiadomoscReceiver, new IntentFilter(Typy.BROADCAST_NEW_MSG));
        registerReceiver(mNowaWiadomoscReceiver, new IntentFilter(Typy.BROADCAST_LIKE_MSG));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        switch (position) {
            case 0: {
                setTitle("Shoutbox");
                break;
            }
            case 1: {
                setTitle("Online");
                getmFragmentOnline().odswiezOnline(getOnline());
                break;
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Typy.REQUEST_ZALOGUJ) {
            if (resultCode == RESULT_OK) {
                zalogowano();
                String msg = data.getStringExtra("msg");
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        }
    }

}
