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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.example.tomek.shoutbox.MyBroadcastReceiver;
import com.example.tomek.shoutbox.NavItem;
import com.example.tomek.shoutbox.NowaWiadomoscReceiver;
import com.example.tomek.shoutbox.OnlineItem;
import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.Wiadomosc;
import com.example.tomek.shoutbox.XstService;
import com.example.tomek.shoutbox.adapters.DrawerListAdapter;
import com.example.tomek.shoutbox.adapters.ScreenSlidePagerAdapter;
import com.example.tomek.shoutbox.fragments.FragmentOnline;
import com.example.tomek.shoutbox.fragments.FragmentSb;
import com.example.tomek.shoutbox.utils.LruBitmapCache;
import com.example.tomek.shoutbox.utils.Typy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LayoutGlownyActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener,
                   IMainActivity,
                   View.OnClickListener,
                   CompoundButton.OnCheckedChangeListener {
    ArrayList<Wiadomosc> arrayListWiadomosci;
    ArrayList<OnlineItem> arrayListOnline;
    RequestQueue requestQueue;
    ImageLoader imageLoader;
    MyBroadcastReceiver broadcastReceiver;
    NowaWiadomoscReceiver nowaWiadomoscReceiver;

    SharedPreferences sharedPrefs;

    private DrawerLayout drawerLayout;
    private RelativeLayout relativeLayoutProfileBox;
    NetworkImageView userAvatar;
    TextView userNick;
    ImageView presenceImage;
    TextView textOnline;
    private Switch mCheckBoxDarkTheme;
    private Button buttonWyloguj;
    private Button buttonZaloguj;

    private boolean czyZalogowany = false;
    private boolean czyJestInternet = true;
    private boolean wyswietlilemBladInternetu = false;
    private boolean pozwolNaZmianeStylu = true;
    private String apiKey;
    private String login;
    private String nickname;
    private String avatarFileName;
    private String themeName;
    private FragmentSb fragmentSb;
    private FragmentOnline fragmentOnline;
    ArrayList<NavItem> navItemsList;
    private int likedMsgPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wczytajStyl();
        wczytaj_ustawienia();
        zaladujWidokPodstawowy();

        if (czyZalogowany) {
            zalogowano();
        } else {
            zaladujWidokNiezalogowany();
        }

        mCheckBoxDarkTheme.setOnCheckedChangeListener(this);
    }

    private void wczytajStyl() {
        sharedPrefs = getSharedPreferences(Typy.PREFS_NAME, 0);
        themeName = sharedPrefs.getString(Typy.PREFS_THEME, "dark");
        Log.i("xst", "Wczytuje styl: " + themeName);
        if (themeName.equals("light")) {
            setTheme(R.style.xstThemeLight);
            getApplicationContext().setTheme(R.style.xstThemeLight);
        } else {
            setTheme(R.style.xstThemeDark);
            getApplicationContext().setTheme(R.style.xstThemeDark);
        }
    }

    private void zaladujWidokPodstawowy() {
        setContentView(R.layout.layout_glowny);

        relativeLayoutProfileBox = findViewById(R.id.profileBox);
        userAvatar = findViewById(R.id.userAvatar);
        userNick = findViewById(R.id.userName);
        arrayListWiadomosci = new ArrayList<>();
        arrayListOnline = new ArrayList<>();
        navItemsList = new ArrayList<>();
        navItemsList.add(new NavItem(Typy.FRAGMENT_USTAWIENIA, R.drawable.xst));
        drawerLayout = findViewById(R.id.drawer_layout);
        presenceImage = findViewById(R.id.preseceImage);
        textOnline = findViewById(R.id.textOnline);
        ListView mDrawerList = findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new DrawerListAdapter(this, navItemsList));
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        ViewPager mPager = findViewById(R.id.sb_pager);
        mPager.addOnPageChangeListener(this);
        ScreenSlidePagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);

        mCheckBoxDarkTheme = findViewById(R.id.checkBoxTheme);
        buttonWyloguj = findViewById(R.id.buttonWyloguj);
        buttonZaloguj = findViewById(R.id.buttonZaloguj);
        buttonWyloguj.setOnClickListener(this);
        buttonZaloguj.setOnClickListener(this);
        likedMsgPosition = 0;
        relativeLayoutProfileBox.setVisibility(View.VISIBLE);
        pozwolNaZmianeStylu = false;
        if (themeName.equals("dark")) {
            mCheckBoxDarkTheme.setChecked(true);
        } else {
            mCheckBoxDarkTheme.setChecked(false);
        }
        pozwolNaZmianeStylu = true;
        userNick.setText(nickname);
    }

    private void zaladujWidokNiezalogowany() {
        startActivityForResult(new Intent(this, LoginActivity.class), Typy.REQUEST_ZALOGUJ);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public ArrayList<Wiadomosc> getWiadomosci() {
        return arrayListWiadomosci;
    }

    @Override
    public void odswiezWiadomosci() {
        mStartService("odswiez");
    }

    @Override
    public ArrayList<OnlineItem> getOnline() {
        try {
            String sitems = sharedPrefs.getString(Typy.PREFS_ONLINE, "0");
            JSONArray items = new JSONArray(sitems);
            arrayListOnline.clear();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                arrayListOnline.add(new OnlineItem(item));
            }
        } catch (JSONException ignored) {

        }
        return arrayListOnline;
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
        getFragmentOnline().odswiezOnline(getOnline());
    }

    @Override
    public void wyslij_wiadomosc(String wiadomosc) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", apiKey);
        params.put("msg", wiadomosc);
//        params.put("msg-base64", Utils.zakodujWiadomosc(wiadomosc));
        JSONObject request = new JSONObject(params);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Typy.API_MSG_SEND, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("success") == 1) {
                        getFragmentSb().wyslano_wiadomosc(true);
                        mStartService("odswiez");
                    } else {
                        getFragmentSb().wyslano_wiadomosc(false);
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
        getFragmentSb().polajkowanoWiadomosc(msgid, likedMsgPosition);
    }

    @Override
    public void lajkujWiadomosc(int id, int position) {
        likedMsgPosition = position;
        mStartService("like", id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wyrejestrujReceivery();
        if (czyZalogowany) {
            mStartService("onPause");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        zarejestrujReceivery();
        if (czyZalogowany) {
            mStartService("onResume");
        } else {
            mStartService("wylogowano");
        }
        wczytajWiadomosci(null);
    }

    @Override
    public void broadcastReceived(String intent) {
        switch (intent) {
            case Typy.BROADCAST_INTERNET_OK:
                mStartService("odswiez");
                obsluzPowrotInternetu();
                break;

            case Typy.BROADCAST_INTERNET_LOST:
                obsluzBrakInternetu();
                fragmentSb.bladOdswiezania();
                break;
        }
    }

    private void obsluzPowrotInternetu() {
        czyJestInternet = true;
        if (wyswietlilemBladInternetu) {
            wyswietlilemBladInternetu = false;
            Toast.makeText(this, "Połączenie przywrócone", Toast.LENGTH_SHORT).show();
            textOnline.setText("Online");
            textOnline.setTextColor(Color.GREEN);
            presenceImage.setImageResource(android.R.drawable.presence_online);
        }
    }

    private void obsluzBrakInternetu() {
        czyJestInternet = false;
        if (! wyswietlilemBladInternetu) {
            wyswietlilemBladInternetu = true;
            Toast.makeText(this, "Brak połączenia", Toast.LENGTH_SHORT).show();
            textOnline.setText("Offline");
            textOnline.setTextColor(Color.RED);
            presenceImage.setImageResource(android.R.drawable.presence_offline);
        }
    }

    @Override
    public void nowa_wiadomosc(Intent i) {
        wczytajWiadomosci(i);
    }

    private void wczytaj_ustawienia() {
        apiKey = sharedPrefs.getString(Typy.PREFS_API_KEY, "");
        login = sharedPrefs.getString(Typy.PREFS_LOGIN, "");
        nickname = sharedPrefs.getString(Typy.PREFS_NICNKAME, "");
        avatarFileName = sharedPrefs.getString(Typy.PREFS_AVATAR, "");
        themeName = sharedPrefs.getString(Typy.PREFS_THEME, "dark");

        czyZalogowany = apiKey.length() == 32;
        if (login.isEmpty()) {
            czyZalogowany = false;
        }
        if (nickname.isEmpty()) {
            czyZalogowany = false;
        }
    }

    public FragmentSb getFragmentSb() {
        if (fragmentSb == null) {
            fragmentSb = new FragmentSb();
        }
        return fragmentSb;
    }

    public FragmentOnline getFragmentOnline() {
        if (fragmentOnline == null) {
            fragmentOnline = new FragmentOnline();
        }
        return fragmentOnline;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (imageLoader == null) {
            imageLoader = new ImageLoader(this.requestQueue, new LruBitmapCache());
        }
        return this.imageLoader;
    }

    public void zalogowano() {
        czyZalogowany = true;
        ustawWidokZalogowania();

        broadcastReceiver = new MyBroadcastReceiver(this);
        nowaWiadomoscReceiver = new NowaWiadomoscReceiver(this);

        zarejestrujReceivery();

        mStartService("zalogowano");
        wczytajWiadomosci(null);
    }

    private void ustawWidokZalogowania() {
        userAvatar.setImageUrl(Typy.URL_AVATAR + avatarFileName, getImageLoader());
        userNick.setText(nickname);
        relativeLayoutProfileBox.setVisibility(View.VISIBLE);
        buttonWyloguj.setVisibility(View.VISIBLE);
        buttonZaloguj.setVisibility(View.INVISIBLE);
        drawerLayout.closeDrawers();
    }

    private void ustawWidokWylogowania() {
        relativeLayoutProfileBox.setVisibility(View.GONE);
        buttonZaloguj.setVisibility(View.VISIBLE);
        buttonWyloguj.setVisibility(View.INVISIBLE);
        drawerLayout.openDrawer(Gravity.START);
    }

    private void wczytajWiadomosci(Intent intent) {
        try {
            String sitems = sharedPrefs.getString(Typy.PREFS_MSGS, "0");
            JSONArray items = new JSONArray(sitems);
            arrayListWiadomosci.clear();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                arrayListWiadomosci.add(new Wiadomosc(item));
            }
            if (fragmentSb != null) {
                fragmentSb.odswiezWiadomosci(this.arrayListWiadomosci);
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
                drawerLayout.closeDrawers();
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
            themeName = "dark";
        } else {
            themeName = "light";
        }
        sharedPrefs.edit().putString(Typy.PREFS_THEME, themeName).commit();
        if (pozwolNaZmianeStylu) {
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
            if (czyZalogowany) {
                selectDrawerItem(i);
            }
        }
    }
    private void selectDrawerItem(int i) {
        if (navItemsList.get(i).mTitle.toLowerCase().equals(Typy.FRAGMENT_USTAWIENIA)) {
            //TODO START ACTIVITY USTAWIENIA
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        //Bundle args = new Bundle();
        //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        //fragment.setArguments(args);

        drawerLayout.closeDrawers();
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
        czyZalogowany = false;

        wyrejestrujReceivery();

        buttonWyloguj.setVisibility(View.GONE);
        mStartService("wylogowano");
        zaladujWidokNiezalogowany();
    }

    private void wyrejestrujReceivery() {
        try {
            if (broadcastReceiver != null && nowaWiadomoscReceiver != null) {
                unregisterReceiver(broadcastReceiver);
                unregisterReceiver(nowaWiadomoscReceiver);
            }
        } catch (IllegalArgumentException ignored) {

        }
    }

    private void zarejestrujReceivery() {
        registerReceiver(broadcastReceiver, new IntentFilter(Typy.BROADCAST_INTERNET_OK));
        registerReceiver(broadcastReceiver, new IntentFilter(Typy.BROADCAST_INTERNET_LOST));
        registerReceiver(nowaWiadomoscReceiver, new IntentFilter(Typy.BROADCAST_NEW_MSG));
        registerReceiver(nowaWiadomoscReceiver, new IntentFilter(Typy.BROADCAST_LIKE_MSG));
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
                getFragmentOnline().odswiezOnline(getOnline());
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
                wczytaj_ustawienia();
                zalogowano();
                String msg = data.getStringExtra("msg");
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        }
    }

}
