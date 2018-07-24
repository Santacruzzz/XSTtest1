package com.example.tomek.shoutbox;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tomek.shoutbox.activities.LayoutGlownyActivity;
import com.example.tomek.shoutbox.utils.Typy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Tomek on 2017-10-21.
 */

public class XstService extends Service {

    private int mLastDate;
    private int mNewItems;
    private Handler mHandler;
    private RequestQueue mRequestQueue;
    private Runnable mRunnableWiadomosci;
    private boolean mShowNotifications;
    private String mKey;
    private JSONArray mJsonWiadomosci;
    private boolean mServiceReady;
    private SharedPreferences mSharedPref;
    private int mDelayMillis;
    private boolean mJestemOnline;
    private int mAppVersion;
    JobScheduler mJobScheduler;
    private boolean mSprawdzajAktualizacje;
    private boolean mPokazujPowiadomienia;

    public XstService() {
        mJestemOnline = false;
        mDelayMillis = 30000;
        mServiceReady = false;
        mHandler = new Handler();
        mLastDate = -1;
        mAppVersion = 1;

        mShowNotifications = true;
        mKey = "";
        mJsonWiadomosci = new JSONArray();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        mSharedPref = getSharedPreferences(Typy.PREFS_NAME, 0);
        mLastDate = mSharedPref.getInt(Typy.PREFS_LAST_DATE, 0);
        mKey = mSharedPref.getString(Typy.PREFS_API_KEY, "");
        mAppVersion = getAppVersion();

        mServiceReady = true;

        if (mHandler == null) {
            mHandler = new Handler();
        }
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this.getApplicationContext());
        }

        mRunnableWiadomosci = new Runnable() {

            @Override
            public void run() {
                try {
                    if (mServiceReady) {
                        wczytajUstawienia();
                        pobierz_wiadomosc();
                    }
                }
                catch (Exception e) {
                    // TODO: handle exception
                }
                finally {
                    //also call the same runnable to call it at regular interval
                    mHandler.postDelayed(this, mDelayMillis);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle extra = intent.getExtras();
            if (extra != null) {
                String msg = extra.getString("msg");
                Log.i("xst", "----- SERVICE: " + msg);
                assert msg != null;

                switch (msg) {
                    case "zalogowano":
                        cancelRefresh();
                        mKey = mSharedPref.getString(Typy.PREFS_API_KEY, "");
                        mJestemOnline = true;
                        mRunnableWiadomosci.run();

                        break;
                    case "onResume":
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        assert notificationManager != null;
                        notificationManager.cancelAll();
                        mShowNotifications = false;
                        mNewItems = 0;
                        cancelRefresh();
                        mDelayMillis = 5000;
                        mJestemOnline = true;
                        mRunnableWiadomosci.run();

                        break;
                    case "onPause":
                        mShowNotifications = true;
                        cancelRefresh();
                        mJestemOnline = false;
                        mDelayMillis = 1000 * 30; // 30s

                        mRunnableWiadomosci.run();

                        break;
                    case "wylogowano":
                        cancelRefresh();
                        mShowNotifications = false;
                        mJestemOnline = false;
                        break;
                    case "odswiez":
                        mLastDate = 0;
                        cancelRefresh();
                        mRunnableWiadomosci.run();
                        break;
                    case "like":
                        int msgId = extra.getInt("value");
                        lajkujWiadomosc(msgId);
                        break;
                }
            }
        } else {
            cancelRefresh();
            if (mKey.length() == 32) {
                mRunnableWiadomosci.run();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("xst", "SERVICE: onDestroy ");
        mHandler.removeCallbacks(mRunnableWiadomosci);
        super.onDestroy();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    private void pobierz_wiadomosc() {
        Log.i("xst", "Sprawdzam wiadomosci, data: " + mLastDate);

        HashMap<String, String> params = new HashMap<>();
        params.put("key", mKey);
        if (mSharedPref.getString(Typy.PREFS_MSGS, "0").length() == 1) {
            mLastDate = 0;
        }
        if (mLastDate > 0) {
            params.put("last_date", Integer.valueOf(mLastDate).toString());
        }
        if (mJestemOnline) {
            params.put("is_online", "1");
        }
        params.put("android_version", android.os.Build.VERSION.RELEASE);
        Log.i("xst", "--SERVICE: wysylam params: " + params.toString());
        JSONObject request = new JSONObject(params);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Typy.API_MSG_GET, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    mLastDate = response.getInt("last_date");
                    mAppVersion = response.getInt("version");

                    if (mAppVersion > getAppVersion() && mSprawdzajAktualizacje) {
                        show_notification("Jest nowa wersja aplikacji!", "update");
                    }

                    JSONArray items = response.getJSONArray("items");
                    int new_items = items.length();
                    if (new_items > 0) {
                        if (mShowNotifications && mPokazujPowiadomienia) {
                            show_notification("Nowe wiadomości", "msg");
                        }
                        SharedPreferences.Editor editor = mSharedPref.edit();
                        editor.putString(Typy.PREFS_MSGS, items.toString());
                        editor.putInt(Typy.PREFS_LAST_DATE, mLastDate);
                        editor.apply();

                        Intent i = new Intent();
                        i.putExtra("items", items.toString());
                        i.setAction(Typy.BROADCAST_NEW_MSG);
                        Log.i("xst", "--- SERVICE: wysyłam broadcast nowe wiadomosci!!!");
                        sendBroadcast(i);
                    }

                    JSONArray online = response.getJSONArray("online");
                    if (online.length() > 0) {
                        SharedPreferences.Editor editor = mSharedPref.edit();
                        editor.putString(Typy.PREFS_ONLINE, online.toString());
                        editor.apply();

                        if ( ! mShowNotifications) {
                            // aplikacja włączona
                            Intent i = new Intent();
                            i.putExtra("online", online.toString());
                            i.setAction(Typy.BROADCAST_ONLINE);
                            sendBroadcast(i);
                        }
                    }
                } catch (JSONException ignored) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                cancelRefresh();
                if (volleyError != null && volleyError.getMessage() != null) {
                    Log.e("xst", volleyError.getMessage());
                }
                startScheduleService();
                broadcastError();
                // TODO sprawdzi czy blad to odpowiedz 500 czy brak internetu
                // TODO dodać job internetu
            }
        });
        req.setTag(Typy.TAG_GET_MSG);
        getRequestQueue().add(req);
    }

    private void broadcastError() {
        Intent i = new Intent();
        i.setAction(Typy.BROADCAST_INTERNET_LOST);
        sendBroadcast(i);
    }

    private void lajkujWiadomosc(final int msgId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", mKey);
        params.put("msgid", String.valueOf(msgId));
        JSONObject request = new JSONObject(params);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Typy.API_MSG_LIKE, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int success = response.getInt("success");
                    if (success == 1) {
                        Intent i = new Intent();
                        i.putExtra("msgid", msgId);
                        i.setAction(Typy.BROADCAST_LIKE_MSG);
                        sendBroadcast(i);
                    } else {
                        String msg = response.getString("message");
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException ignored) {
                    Log.e("xst", "service: Blad lajkowania");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Problem z połączeniem", Toast.LENGTH_SHORT).show();
                startScheduleService();
            }
        });
        req.setTag(Typy.TAG_LIKE_MSG);
        getRequestQueue().add(req);
    }

    private void cancelRefresh() {
        getRequestQueue().cancelAll(Typy.TAG_GET_MSG);
        mHandler.removeCallbacks(mRunnableWiadomosci);
    }

    private void show_notification(String txt, String typ) {
        Log.i("xst", "--SERVICE, pokazuje notification: " + typ);
        Intent intent = new Intent();
        if (typ.equals("msg")) {
             intent = new Intent(this, LayoutGlownyActivity.class);
        } else if (typ.equals("update")) {
             intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://xs-team.pl/android/XST.apk"));
        }
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(this)
                .setContentTitle("XST Shoutbox")
                .setContentText(txt)
                .setSmallIcon(R.drawable.xst_text)
                .setContentIntent(pIntent)
                .setLights(Color.WHITE, 500, 2000)
                .setAutoCancel(true).build();


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(1, n);
    }

    private void startScheduleService() {
        //TODO dodać do usługi listenera jeśli wróci internet
        mJobScheduler.schedule(new JobInfo.Builder(1, new ComponentName(this, JobServiceInternetOK.class)).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build());
    }

    private int getAppVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void wczytajUstawienia() {
        mSprawdzajAktualizacje = mSharedPref.getBoolean("automatyczne_aktualizacje", true);
        mPokazujPowiadomienia = mSharedPref.getBoolean("pokazuj_powiadomienia", true);
    }
}
