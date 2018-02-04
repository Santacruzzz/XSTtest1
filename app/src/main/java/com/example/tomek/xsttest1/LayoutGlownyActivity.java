package com.example.tomek.xsttest1;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LayoutGlownyActivity extends AppCompatActivity implements IMainActivity, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    ListView listViewWiadomosci;
    ArrayList<Wiadomosc> arrayWiadomosci;
    ArrayList<OnlineItem> arrayOnline;
    AdapterWiadomosci adapterWiadomosci;
    AdapterOnline adapterOnline;
    RequestQueue mRequestQueue;
    ImageLoader mImageLoader;
    MyBroadcastReceiver mReceiver;
    NowaWiadomoscReceiver mNowaWiadomoscReceiver;

    SharedPreferences mSharedPrefs;

    Button mBtnZaloguj;
    EditText mTextLogin;
    EditText mTextPassword;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private RelativeLayout mRelativeLayoutProfileBox;
    NetworkImageView userAvatar;
    TextView userNick;
    private Switch mCheckBoxDarkTheme;
    private Button mButtonWyloguj;

    private boolean mZalogowany;
    private String mApiKey;
    private String mLogin;
    private String mNickname;
    private String mAvatar;
    private String mTheme;
    private String mAktualnyWidok;

    private boolean mPozwolNaZmianeStylu;

    private FragmentSb mFragmentSb;
    private PagerFragmentSB mPagerFragmentSb;
    private FragmentLogowanie mFragmentLogowanie;

    private FragmentOnline mFragmentOnline;
    ArrayList<NavItem> mNavItems;
    private FragmentManager fragmentManager;
    private ArrayAdapter<String> mMenuAdapter;
    private View mPrevView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAktualnyWidok = "shoutbox";
        if (savedInstanceState != null) {
            String l_aktualnyWidok = savedInstanceState.getString("mAktualnyWidok");
            if (l_aktualnyWidok != null) {
                if (l_aktualnyWidok.length() > 0) {
                    mAktualnyWidok = l_aktualnyWidok;
                }
            }
        }

        mSharedPrefs = getSharedPreferences(Typy.PREFS_NAME, 0);
        mTheme = mSharedPrefs.getString(Typy.PREFS_THEME, "dark");

        if (mTheme.equals("light")) {
            setTheme(R.style.xstThemeLight);
            getApplicationContext().setTheme(R.style.xstThemeLight);
        } else {
            setTheme(R.style.xstThemeDark);
            getApplicationContext().setTheme(R.style.xstThemeDark);
        }

        setContentView(R.layout.layout_glowny);
        fragmentManager = getSupportFragmentManager();
        mRelativeLayoutProfileBox = findViewById(R.id.profileBox);
        userAvatar = findViewById(R.id.userAvatar);
        userNick = findViewById(R.id.userName);
        arrayWiadomosci = new ArrayList<>();
        arrayOnline = new ArrayList<>();
        mNavItems = new ArrayList<>();
        mNavItems.add(new NavItem("Shoutbox", R.drawable.xst));
        mNavItems.add(new NavItem("Ustawienia", R.drawable.xst));

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new DrawerListAdapter(this, mNavItems));
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mCheckBoxDarkTheme = findViewById(R.id.checkBoxTheme);
        mButtonWyloguj = findViewById(R.id.buttonWyloguj);

        mPozwolNaZmianeStylu = true;
        wczytaj_ustawienia();

        if (mZalogowany) {
            zalogowano(true);
        } else {
            wyloguj();
        }

        mButtonWyloguj.setOnClickListener(this);
        mCheckBoxDarkTheme.setOnCheckedChangeListener(this);

    }

    private void pokazFragmentLogowanie() {
        fragmentManager.beginTransaction().replace(R.id.content_frame, getmFragmentLogowanie()).commit();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void wczytaj_ustawienia() {
        mApiKey = mSharedPrefs.getString(Typy.PREFS_API_KEY, "");
        mLogin = mSharedPrefs.getString(Typy.PREFS_LOGIN, "");
        mNickname = mSharedPrefs.getString(Typy.PREFS_NICNKAME, "");
        mAvatar = mSharedPrefs.getString(Typy.PREFS_AVATAR, "");
        mTheme = mSharedPrefs.getString(Typy.PREFS_THEME, "dark");

        mPozwolNaZmianeStylu = false;
        if (mTheme.equals("dark")) {
            mCheckBoxDarkTheme.setChecked(true);
        } else {
            mCheckBoxDarkTheme.setChecked(false);
        }
        mPozwolNaZmianeStylu = true;

        Log.i("xst", mApiKey + ", " + mLogin + ", " + mNickname + ", " + mAvatar);

        mZalogowany = mApiKey.length() == 32;
        if (mLogin.isEmpty()) {
            mZalogowany = false;
        }
        if (mNickname.isEmpty()) {
            mZalogowany = false;
        }

        if (mZalogowany) {
            mRelativeLayoutProfileBox.setVisibility(View.VISIBLE);
            if (mAvatar.length() > 0) {
                userAvatar.setImageUrl(Typy.URL_AVATAR + mAvatar, getImageLoader());
            }
            userNick.setText(mNickname);
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

    @Override
    public ArrayList<Wiadomosc> getWiadomosci() {
        return arrayWiadomosci;
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
        params.put("msg-base64", Utils.zakodujWiadomosc(wiadomosc));
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
                // TODO sprawdzi czy blad to odpowiedz 500 czy brak internetu
                // TODO dodać job internetu
            }
        });
        req.setTag(Typy.TAG_SEND_MSG);
        getRequestQueue().add(req);
    }

    private FragmentLogowanie getmFragmentLogowanie() {
        if (mFragmentLogowanie == null) {
            mFragmentLogowanie = new FragmentLogowanie();
        }
        return mFragmentLogowanie;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("xst", "___onResume");
        zarejestrujReceivery();
        if (mZalogowany) {
            mStartService("onResume");
        } else {
            mStartService("wylogowano");
        }
        pobierz_wiadomosc(null);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    public PagerFragmentSB getmPagerFragmentSb() {
        if (mPagerFragmentSb == null) {
            mPagerFragmentSb = new PagerFragmentSB();
        }
        return this.mPagerFragmentSb;
    }

    @Override
    public void broadcastReceived(String intent) {
        if (Objects.equals(intent, Typy.BROADCAST_INTERNET_OK)) {
            pobierz_wiadomosc(null);
        }
    }

    @Override
    public void nowa_wiadomosc(Intent i) {
        pobierz_wiadomosc(i);
    }

    public void zalogowano(boolean wczytano_ustawienia) {
        if ( !wczytano_ustawienia) {
            wczytaj_ustawienia();
        }
        mRelativeLayoutProfileBox.setVisibility(View.VISIBLE);
        mButtonWyloguj.setVisibility(View.VISIBLE);
        mReceiver = new MyBroadcastReceiver(this);
        mNowaWiadomoscReceiver = new NowaWiadomoscReceiver(this);

        zarejestrujReceivery();

        updateDrawerList(true);
        selectDrawerItem(0);
        mStartService("zalogowano");
        pobierz_wiadomosc(null);
    }

    private void updateDrawerList(boolean zalogowano) {

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonWyloguj:
                wyloguj();
                pokazFragmentLogowanie();
                mDrawerLayout.closeDrawers();
                break;
        }
    }

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
            if (!mZalogowany) {
                Toast.makeText(getApplicationContext(), "Najpierw się zaloguj.", Toast.LENGTH_SHORT).show();
                return;
            }
            selectDrawerItem(i);
        }
    }

    private void selectDrawerItem(int i) {
        if (mNavItems.get(i).mTitle.equals("Shoutbox")) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new PagerFragmentSB()).commit();
        } else if (mNavItems.get(i).mTitle.equals("Ustawienia")) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new UstawieniaFragment()).commit();
        }

        //Bundle args = new Bundle();
        //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        //fragment.setArguments(args);

        setTitle(mNavItems.get(i).mTitle);

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(i, true);
        mDrawerLayout.closeDrawers();
    }

    private void wyloguj() {
        SharedPreferences.Editor editor = getSharedPreferences(Typy.PREFS_NAME, 0).edit();
        editor.remove(Typy.PREFS_NICNKAME);
        editor.remove(Typy.PREFS_LOGIN);
        editor.remove(Typy.PREFS_API_KEY);
        editor.remove(Typy.PREFS_AVATAR);
        editor.remove(Typy.PREFS_MSGS);
        editor.remove(Typy.PREFS_LAST_DATE);
        editor.commit();
        mZalogowany = false;

        updateDrawerList(false);
        wyrejestrujReceivery();

        mRelativeLayoutProfileBox.setVisibility(View.GONE);
        mButtonWyloguj.setVisibility(View.GONE);
        mStartService("wylogowano");
        pokazFragmentLogowanie();
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
        registerReceiver(mNowaWiadomoscReceiver, new IntentFilter(Typy.BROADCAST_NEW_MSG));
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }
}
