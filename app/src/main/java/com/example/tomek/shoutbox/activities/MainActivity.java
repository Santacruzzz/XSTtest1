package com.example.tomek.shoutbox.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.User;
import com.example.tomek.shoutbox.Wiadomosc;
import com.example.tomek.shoutbox.XstDb;
import com.example.tomek.shoutbox.XstService;
import com.example.tomek.shoutbox.adapters.DrawerListAdapter;
import com.example.tomek.shoutbox.adapters.ScreenSlidePagerAdapter;
import com.example.tomek.shoutbox.utils.Typy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends XstActivity
        implements ViewPager.OnPageChangeListener,
                   IMainActivity,
                   View.OnClickListener,
                   SwipeRefreshLayout.OnRefreshListener,
                   CompoundButton.OnCheckedChangeListener,
                   IPermission {
    ArrayList<Wiadomosc> arrayListWiadomosci;
    ArrayList<User> arrayListOnline;
    MyBroadcastReceiver broadcastReceiver;
    NowaWiadomoscReceiver nowaWiadomoscReceiver;

    private DrawerLayout drawerLayout;
    private NetworkImageView userAvatar;
    private TextView userNick;
    private ImageView presenceImage;
    private TextView textOnline;
    private TextView textConnectionError;

    private boolean czyJestPolaczenie = true;
    private boolean wyswietlilemBladInternetu = false;
    private boolean pozwolNaZmianeStylu = true;

    private ArrayList<NavItem> navItemsList;
    private int likedMsgPosition;
    private ViewPager mPager;
    private boolean isThreadConnectionErrorRun;
    private SbListener listenerSb;
    private OnlineListener listenerOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zaladujWidokPodstawowy();
        enableBackButtonInActionBar();
        setHomeMenuIcon();

        if (!czyZalogowany) {
            zaladujWidokNiezalogowany();
        } else {
            ustawWidokZalogowania();
        }
    }

    private void zaladujWidokPodstawowy() {
        setContentView(R.layout.layout_glowny);

        navItemsList = new ArrayList<>();
        navItemsList.add(new NavItem(Typy.FRAGMENT_USTAWIENIA, android.R.drawable.ic_menu_preferences));
        navItemsList.add(new NavItem(Typy.FRAGMENT_MOJE_OBRAZKI, android.R.drawable.ic_menu_gallery));

        RelativeLayout relativeLayoutProfileBox = findViewById(R.id.profileBox);
        relativeLayoutProfileBox.setVisibility(View.VISIBLE);

        userAvatar = findViewById(R.id.userAvatar);
        userNick = findViewById(R.id.userName);
        arrayListWiadomosci = new ArrayList<>();
        arrayListOnline = new ArrayList<>();

        presenceImage = findViewById(R.id.preseceImage);
        textOnline = findViewById(R.id.textOnline);
        textConnectionError = findViewById(R.id.textConnectionError);

        drawerLayout = findViewById(R.id.drawer_layout);
        ListView drawerList = findViewById(R.id.left_drawer);
        drawerList.setAdapter(new DrawerListAdapter(this, navItemsList));
        drawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        mPager = findViewById(R.id.sb_pager);
        mPager.addOnPageChangeListener(this);
        ScreenSlidePagerAdapter sbAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(sbAdapter);

        Button buttonWyloguj = findViewById(R.id.buttonWyloguj);
        buttonWyloguj.setOnClickListener(this);

        likedMsgPosition = 0;
        isThreadConnectionErrorRun = false;
        pozwolNaZmianeStylu = false;

        Switch checkBoxDarkTheme = findViewById(R.id.checkBoxTheme);
        checkBoxDarkTheme.setOnCheckedChangeListener(this);

        if (themeName.equals("dark")) {
            checkBoxDarkTheme.setChecked(true);
        } else {
            checkBoxDarkTheme.setChecked(false);
        }

        pozwolNaZmianeStylu = true;
        userNick.setText(nickname);

        ustawToolbar();
    }

    private void zaladujWidokNiezalogowany() {
        Log.i("xst", "MainActivity: zaladujWidokNiezalogowany");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showDrawerMenu();
                break;
            case R.id.menu_ustawienia:
                uruchomUstawienia();
                break;
            case R.id.menu_test:
                if (czyJestPolaczenie) {
                    obsluzBrakInternetu();
                } else {
                    obsluzPowrotInternetu();
                }
                break;
        }
        return true;
    }

    private void showDrawerMenu() {
        drawerLayout.openDrawer(Gravity.START);
    }

    @Override
    public void odswiezWiadomosci() {
        mStartService("wymusOdswiezenie");
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
                        if (listenerSb != null) {
                            listenerSb.wyslano_wiadomosc(true);
                        }
                        mStartService("odswiez");
                    } else {
                        if (listenerSb != null) {
                            listenerSb.wyslano_wiadomosc(false);
                        }
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
        bazaDanych.polajkowanoWiadomosc(likedMsgPosition);
        if (listenerSb != null) {
            listenerSb.polajkowanoWiadomosc(msgid, likedMsgPosition);
        }
    }

    @Override
    public void lajkujWiadomosc(int id, int position) {
        likedMsgPosition = position;
        mStartService("like", id);
    }

    @Override
    public Integer getKeyboardSize() {
        return keyboardSize;
    }

    @Override
    public int getState() {
        if (czyJestPolaczenie) {
            return Typy.STATE_ONLINE;
        }
        return Typy.STATE_OFFLINE;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (czyZalogowany) {
            wyrejestrujReceivery();
            mStartService("onPause");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (czyJestPolaczenie) {
            obsluzPowrotInternetu();
        } else {
            obsluzBrakInternetu();
        }
        if (czyZalogowany) {
            zarejestrujReceivery();
            mStartService("onResume");
            wczytajWiadomosci(null);
            odswiezTytul();
        }
        Log.i("xst", "MainActivity: onResume, zalogowany: " + czyZalogowany);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (listenerSb != null) {
            listenerSb.dismissDialog();
        }
    }

    private void odswiezTytul() {
        setTitle(determineCurrentTitle());
    }

    private String determineCurrentTitle() {
        if (mPager == null) return "";
        int currentPageId = mPager.getCurrentItem();
        switch (currentPageId) {
            case 0: {
                return "Shoutbox";
            }
            case 1: {
                return "Online";
            }
        }
        return "XST Shoutbox";
    }

    @Override
    public void broadcastReceived(String intent) {
        Log.i("xst", "MainActivity received msg: " + intent);
        switch (intent) {
            case Typy.BROADCAST_INTERNET_WROCIL:
                mStartService("onResume");
                obsluzPowrotInternetu();
                break;

            case Typy.BROADCAST_INTERNET_LOST:
                obsluzBrakInternetu();
                break;

            case Typy.BROADCAST_INTERNET_OK:
                obsluzPowrotInternetu();
                break;

            case Typy.BROADCAST_ONLINE:
                if (listenerOnline != null) {
                    listenerOnline.odswiezOnline();
                }
                break;
            case Typy.BROADCAST_KONIEC_ODSWIEZANIA:
                if (listenerSb != null) {
                    listenerSb.anulujOdswiezanie();
                }
                break;
        }
    }

    private void obsluzBrakInternetu() {
        if (!czyJestPolaczenie || isThreadConnectionErrorRun) {
            return;
        }
        czyJestPolaczenie = false;

        if (! wyswietlilemBladInternetu) {
            wyswietlilemBladInternetu = true;
            if (listenerOnline != null) {
                listenerOnline.odswiezOnline();
            }
            pokazTextConnectionError();
            ustawWidokStanuPolaczenia("Offline", Color.RED, android.R.drawable.presence_offline);
        }
    }

    private void pokazTextConnectionError() {
        if (isThreadConnectionErrorRun) {
            return;
        }
        textConnectionError.setVisibility(View.VISIBLE);
        textConnectionError.setBackgroundColor(Color.RED);
        textConnectionError.setText("Brak połączenia");
    }

    private void obsluzPowrotInternetu() {
        if (czyJestPolaczenie) {
            return;
        }
        czyJestPolaczenie = true;

        if (wyswietlilemBladInternetu && !isThreadConnectionErrorRun) {
            if (listenerOnline != null) {
                listenerOnline.odswiezOnline();
            }
            textConnectionError.setBackgroundColor(Color.rgb(0, 200, 0));
            textConnectionError.setText("Połączenie przywrócone");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        isThreadConnectionErrorRun = true;
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isThreadConnectionErrorRun = false;
                            textConnectionError.setVisibility(View.GONE);
                            wyswietlilemBladInternetu = false;
                        }
                    });
                }
            };
            thread.start(); //start the thread
            ustawWidokStanuPolaczenia("Online", Color.GREEN, android.R.drawable.presence_online);
        }
    }

    private void ustawWidokStanuPolaczenia(String statusName, int statusTextColor, int presenceIcon) {
        textOnline.setText(statusName);
        textOnline.setTextColor(statusTextColor);
        presenceImage.setImageResource(presenceIcon);
    }

    @Override
    public void nowa_wiadomosc(Intent i) {
        wczytajWiadomosci(i);
    }

    public void zalogowano() {
        czyZalogowany = true;
        ustawWidokZalogowania();
        mStartService("zalogowano");
        wczytajWiadomosci(null);
    }

    private void ustawWidokZalogowania() {
        userAvatar.setImageUrl(Typy.URL_AVATAR + avatarFileName, getImageLoader());
        userNick.setText(nickname);
        drawerLayout.closeDrawers();
    }

    private void wczytajWiadomosci(Intent intent) {
        if (listenerSb != null) {
            listenerSb.odswiezWiadomosci();
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
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            themeName = "dark";
        } else {
            themeName = "light";
        }
        xstApp.zapiszUstawienie(Typy.PREFS_THEME, themeName);
        if (pozwolNaZmianeStylu) {
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public boolean haveWritePermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestPermission() {
        Log.i("xst", "request permission");
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            pokazDialog("Aby wysyłać obrazki przyznaj uprawnienia dostępu do pamięci.\nOtworzyć ustawienia?", "Dostęp do plików");
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                                              new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                              Typy.PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Typy.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (listenerSb != null) {
                        listenerSb.pokazDialogDodatki();
                    }
                } else {
                    pokazDialog("Aby wysyłać obrazki przyznaj uprawnienia dostępu do pamięci.\nOtworzyć ustawienia?", "Dostęp do plików");
                }
                break;
        }
    }

    private void pokazDialog(String message, String title) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(MainActivity.this);
        if (title.length() > 0) {
            builder.setTitle(title);
        }
        builder.setMessage(message);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        builder.create();
        builder.show();
    }

    public void imageSelectedToUpload(String path) {
        startActivityUploadImage(Uri.fromFile(new File(path)));
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
            uruchomUstawienia();
        } else if (selectedActivity.equals(Typy.FRAGMENT_MOJE_OBRAZKI)) {
            uruchomMojeObrazki();
        }

        //Bundle args = new Bundle();
        //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        //fragment.setArguments(args);

        //drawerLayout.closeDrawers();
    }

    private void uruchomMojeObrazki() {
        Intent intent = new Intent(this, MojeObrazki.class);
        startActivityForResult(intent, Typy.REQUEST_MOJE_OBRAZKI);
    }

    private void uruchomUstawienia() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, Typy.REQUEST_USTAWIENIA);
    }

    private void wyloguj() {
        SharedPreferences.Editor editor = getSharedPreferences(Typy.PREFS_NAME, 0).edit();
        editor.clear();
        editor.apply();
        czyZalogowany = false;
        bazaDanych.clearAll();
        wyrejestrujReceivery();

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
        broadcastReceiver = new MyBroadcastReceiver(this);
        nowaWiadomoscReceiver = new NowaWiadomoscReceiver(this);

        registerReceiver(broadcastReceiver, new IntentFilter(Typy.BROADCAST_INTERNET_WROCIL));
        registerReceiver(broadcastReceiver, new IntentFilter(Typy.BROADCAST_INTERNET_LOST));
        registerReceiver(broadcastReceiver, new IntentFilter(Typy.BROADCAST_INTERNET_OK));
        registerReceiver(broadcastReceiver, new IntentFilter(Typy.BROADCAST_KONIEC_ODSWIEZANIA));
        registerReceiver(broadcastReceiver, new IntentFilter(Typy.BROADCAST_ONLINE));
        registerReceiver(nowaWiadomoscReceiver, new IntentFilter(Typy.BROADCAST_NEW_MSG));
        registerReceiver(nowaWiadomoscReceiver, new IntentFilter(Typy.BROADCAST_LIKE_MSG));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        odswiezTytul();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("xst", "MainActivity: onActivityResult, code: " + requestCode + " resultCode:" + resultCode);
        switch (requestCode) {
            case Typy.REQUEST_ZALOGUJ:
                if (resultCode == RESULT_OK) {
                    if (data.getBooleanExtra("success", false)) {
                        wczytajUstawienia();
                        zalogowano();
                        String msg = data.getStringExtra("msg");
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        finish();
                    }
                } else {
                    zaladujWidokNiezalogowany();
                }
                break;

            case Typy.REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    startActivityUploadImage(data.getData());
                }
                break;

            case Typy.REQUEST_UPLOAD_IMAGE:
                if (resultCode == RESULT_OK) {
                    String returnedResult = data.getDataString();
                    if (returnedResult != null) {
                        String link = returnedResult.split("\\|")[0];
                        String ifInsertToMessage = returnedResult.split("\\|")[1];
                        if (Boolean.parseBoolean(ifInsertToMessage)) {
                            if (listenerSb != null) {
                                listenerSb.wstawLinkObrazka(link);
                            }
                        }
                    }
                }
                break;

            case Typy.REQUEST_MOJE_OBRAZKI:
            case Typy.REQUEST_USTAWIENIA:
                    drawerLayout.closeDrawers();
                break;
        }
    }

    private void startActivityUploadImage(Uri uri) {
        Intent uploadIntent = new Intent(this, UploadActivity.class);
        uploadIntent.putExtra("imageUri", uri.toString());
        startActivityForResult(uploadIntent, Typy.REQUEST_UPLOAD_IMAGE);
    }

    public XstDb getXstDatabase() {
        return xstApp.getBazaDanych();
    }

    public void setSbListener(SbListener listener) {
        listenerSb = listener;
    }

    public void setOnlineListener(OnlineListener listener) {
        listenerOnline = listener;
    }
}
