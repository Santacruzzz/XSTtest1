package com.example.tomek.shoutbox.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
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
import com.example.tomek.shoutbox.utils.Typy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LayoutGlownyActivity extends XstActivity
        implements ViewPager.OnPageChangeListener,
                   IMainActivity,
                   View.OnClickListener,
                   CompoundButton.OnCheckedChangeListener {
    ArrayList<Wiadomosc> arrayListWiadomosci;
    ArrayList<OnlineItem> arrayListOnline;
    MyBroadcastReceiver broadcastReceiver;
    NowaWiadomoscReceiver nowaWiadomoscReceiver;

    private DrawerLayout drawerLayout;
    private RelativeLayout relativeLayoutProfileBox;
    private NetworkImageView userAvatar;
    private TextView userNick;
    private ImageView presenceImage;
    private TextView textOnline;
    private TextView textConnectionError;

    private Switch checkBoxDarkTheme;
    private Button buttonWyloguj;
    private Button buttonZaloguj;

    private boolean czyJestInternet = true;
    private boolean wyswietlilemBladInternetu = false;
    private boolean pozwolNaZmianeStylu = true;

    private FragmentSb fragmentSb;
    private FragmentOnline fragmentOnline;
    private ArrayList<NavItem> navItemsList;
    private int likedMsgPosition;
    private ListView drawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zaladujWidokPodstawowy();

        if (czyZalogowany) {
            zalogowano();
        } else {
            zaladujWidokNiezalogowany();
        }

        checkBoxDarkTheme.setOnCheckedChangeListener(this);
    }

    private void zaladujWidokPodstawowy() {
        setContentView(R.layout.layout_glowny);

        getSupportActionBar().hide();

        relativeLayoutProfileBox = findViewById(R.id.profileBox);
        userAvatar = findViewById(R.id.userAvatar);
        userNick = findViewById(R.id.userName);
        arrayListWiadomosci = new ArrayList<>();
        arrayListOnline = new ArrayList<>();
        navItemsList = new ArrayList<>();
        navItemsList.add(new NavItem(Typy.FRAGMENT_USTAWIENIA, android.R.drawable.ic_menu_preferences));
        navItemsList.add(new NavItem(Typy.FRAGMENT_MOJE_OBRAZKI, android.R.drawable.ic_menu_gallery));
        drawerLayout = findViewById(R.id.drawer_layout);
        presenceImage = findViewById(R.id.preseceImage);
        textOnline = findViewById(R.id.textOnline);
        textConnectionError = findViewById(R.id.textConnectionError);
        drawerList = findViewById(R.id.left_drawer);
        drawerList.setAdapter(new DrawerListAdapter(this, navItemsList));
        drawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        ViewPager mPager = findViewById(R.id.sb_pager);
        mPager.addOnPageChangeListener(this);
        ScreenSlidePagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);

        checkBoxDarkTheme = findViewById(R.id.checkBoxTheme);
        buttonWyloguj = findViewById(R.id.buttonWyloguj);
        buttonZaloguj = findViewById(R.id.buttonZaloguj);
        buttonWyloguj.setOnClickListener(this);
        buttonZaloguj.setOnClickListener(this);
        likedMsgPosition = 0;
        relativeLayoutProfileBox.setVisibility(View.VISIBLE);
        pozwolNaZmianeStylu = false;
        if (themeName.equals("dark")) {
            checkBoxDarkTheme.setChecked(true);
        } else {
            checkBoxDarkTheme.setChecked(false);
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
        mStartService("wymusOdswiezenie");
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
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    obsluzBrakInternetu();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), "AuthFailureError", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), "ServerError", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(), "NetworkError", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), "ParseError", Toast.LENGTH_SHORT).show();
                }
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
    public Integer getKeyboardSize() {
        return keyboradSize;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("xst", "====main ON SAVE");
    }

    @Override
    protected void onPause() {
        Log.i("xst", "====main ON PAUSE");
        super.onPause();
        wyrejestrujReceivery();
        if (czyZalogowany) {
            mStartService("onPause");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (czyJestInternet) {
            obsluzPowrotInternetu();
        } else {
            obsluzBrakInternetu();
        }
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
            case Typy.BROADCAST_INTERNET_WROCIL:
                mStartService("odswiez");
                obsluzPowrotInternetu();
                break;

            case Typy.BROADCAST_INTERNET_LOST:
                obsluzBrakInternetu();
                fragmentSb.bladOdswiezania();
                break;

            case Typy.BROADCAST_INTERNET_OK:
                obsluzPowrotInternetu();
                break;
        }
    }

    private void obsluzPowrotInternetu() {
        czyJestInternet = true;
        if (wyswietlilemBladInternetu) {
            wyswietlilemBladInternetu = false;
            Toast.makeText(this, "Połączenie przywrócone", Toast.LENGTH_SHORT).show();
        }
        ustawWidokStanuPolaczenia("Online", Color.GREEN, android.R.drawable.presence_online, View.GONE);
    }

    private void ustawWidokStanuPolaczenia(String statusName, int statusTextColor, int presenceIcon, int infoTextViewVisibility) {
        textOnline.setText(statusName);
        textOnline.setTextColor(statusTextColor);
        presenceImage.setImageResource(presenceIcon);
        textConnectionError.setVisibility(infoTextViewVisibility);
    }

    private void obsluzBrakInternetu() {
        czyJestInternet = false;
        if (! wyswietlilemBladInternetu) {
            wyswietlilemBladInternetu = true;
            Toast.makeText(this, "Brak połączenia", Toast.LENGTH_SHORT).show();
        }
        ustawWidokStanuPolaczenia("Offline", Color.RED, android.R.drawable.presence_offline, View.VISIBLE);
    }

    @Override
    public void nowa_wiadomosc(Intent i) {
        wczytajWiadomosci(i);
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
        String selectedActivity = navItemsList.get(i).mTitle.toLowerCase();
        if (selectedActivity.equals(Typy.FRAGMENT_USTAWIENIA)) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (selectedActivity.equals(Typy.FRAGMENT_MOJE_OBRAZKI)) {
            Intent intent = new Intent(this, MojeObrazki.class);
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
        registerReceiver(broadcastReceiver, new IntentFilter(Typy.BROADCAST_INTERNET_WROCIL));
        registerReceiver(broadcastReceiver, new IntentFilter(Typy.BROADCAST_INTERNET_LOST));
        registerReceiver(broadcastReceiver, new IntentFilter(Typy.BROADCAST_INTERNET_OK));
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
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Typy.REQUEST_ZALOGUJ:
                if (resultCode == RESULT_OK) {
                    wczytajUstawienia();
                    zalogowano();
                    String msg = data.getStringExtra("msg");
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                break;
            case Typy.REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    Intent uploadIntent = new Intent(this, UploadActivity.class);
                    uploadIntent.putExtra("imageUri", uri.toString());
                    startActivity(uploadIntent);
                }
                break;
        }
    }
}
