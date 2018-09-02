package com.example.tomek.shoutbox;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tomek.shoutbox.activities.MainActivity;
import com.example.tomek.shoutbox.utils.Typy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private String mKey;
    private JSONArray mJsonWiadomosci;
    private boolean mServiceReady;
    private SharedPreferences mSharedPref;
    private int mDelayMillis;
    private int mAppVersion;
    JobScheduler mJobScheduler;
    private boolean settings_sprawdzajAktualizacje;
    private boolean settings_pokazujPowiadomienia;
    private String mAppVersionName;
    private String currentOnlineList;
    private XstApplication xstApp;
    private NotificationManagerCompat notificationManager;
    private Typy.ServiceState state;
    private Typy.ServiceRequest request;
    private ArrayList<Integer> activeNotificationsIds;
    private Context context;
    private ConnectivityManager connectivityManager;

    public XstService() {
        mDelayMillis = 30000;
        mServiceReady = false;
        mHandler = new Handler();
        mLastDate = -1;
        mAppVersion = 1;
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

        mAppVersion = 0;
        mAppVersionName = "";
        xstApp = (XstApplication) getApplicationContext();
        notificationManager = NotificationManagerCompat.from(this);
        state = Typy.ServiceState.state_wylogowano;
        request = Typy.ServiceRequest.request_none;
        activeNotificationsIds = new ArrayList<>();
        context = getApplicationContext();
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        PackageInfo appPackageInfo = getAppPackageInfo();
        if (appPackageInfo != null) {
            mAppVersion = appPackageInfo.versionCode;
            mAppVersionName = appPackageInfo.versionName;
        }

        mServiceReady = true;
        wczytajUstawienia();

        if (mHandler == null) {
            mHandler = new Handler();
        }
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(xstApp);
        }

        Log.e("xst", "Service: restarted");

        mRunnableWiadomosci = new Runnable() {
            @Override
            public void run() {
                try {
                    if (mServiceReady) {
                        pobierz_wiadomosc();
                        Log.i("xst", "Service state: " + state);
                        Log.i("xst", "Service request: " + request);
                    } else {
                        Log.e("xst", "Service not ready");
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

        if (mKey.length() > 0) {
            zacznijPobieracWiadomosci();
        }
    }

    private boolean isConnected() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle extra = intent.getExtras();
            if (extra != null) {
                String msg = extra.getString("msg");
                Log.i("xst", "Service start command: " + msg);
                if (msg == null) {
                    msg = "onPause";
                }
                switch (msg) {
                    case "zalogowano":
                        wczytajUstawienia();
                        zacznijPobieracWiadomosci();
                        break;

                    case "onResume":
                        state = Typy.ServiceState.state_onResume;
                        cancelAllNotifications();
                        mNewItems = 0;
                        mDelayMillis = 5000;
                        zacznijPobieracWiadomosci();
                        break;

                    case "onPause":
                        state = Typy.ServiceState.state_onPause;
                        mDelayMillis = 1000 * 30; // 30s
                        zacznijPobieracWiadomosci();
                        break;

                    case "wylogowano":
                        state = Typy.ServiceState.state_wylogowano;
                        cancelRefresh();
                        break;

                    case "wymusOdswiezenie":
                        mLastDate = 0;
                        request = Typy.ServiceRequest.request_wymusOdswiezanie;
                        zacznijPobieracWiadomosci();
                        break;

                    case "odswiez":
                        zacznijPobieracWiadomosci();
                        break;

                    case "like":
                        int msgId = extra.getInt("value");
                        lajkujWiadomosc(msgId);
                        break;

                    case "testPowiadomienia":
                        showNotification("test", "msg");
                        break;

                    case "internetWrocil":

                        break;
                }
            }
        } else {
            cancelRefresh();
            if (mKey.length() == 32) {
                zacznijPobieracWiadomosci();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void cancelAllNotifications() {
        activeNotificationsIds.clear();
        notificationManager.cancelAll();
    }

    private void zacznijPobieracWiadomosci() {
        cancelRefresh();
        mRunnableWiadomosci.run();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnableWiadomosci);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    private void pobierz_wiadomosc() {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", mKey);
        if (xstApp.getMsgJsonString().length() == 1) {
            mLastDate = 0;
        }
        if (mLastDate > 0) {
            params.put("last_date", Integer.valueOf(mLastDate).toString());
        }
        if (state == Typy.ServiceState.state_onResume) {
            params.put("is_online", "1");
            params.put("get_online", "1");
        } else {
            params.put("is_online", "0");
            params.put("get_online", "0");
        }

        params.put("android_version", android.os.Build.VERSION.RELEASE);
        params.put("app_version", mAppVersionName);

        Log.i("xst", "Service: wysylam getMsg: " + params.toString());
        final JSONObject requestJson = new JSONObject(params);
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Typy.API_MSG_GET, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (request == Typy.ServiceRequest.request_wymusOdswiezanie) {
                    request = Typy.ServiceRequest.request_none;
                    broadcastMessage(Typy.BROADCAST_KONIEC_ODSWIEZANIA);
                }
                if (state == Typy.ServiceState.state_blad_polaczenia) {
                    state = Typy.ServiceState.state_onPause;
                    broadcastMessage(Typy.BROADCAST_INTERNET_WROCIL);
                }
                try {
                    if (response.getInt("success") == 0) {
                        return;
                    }

                    mLastDate = response.getInt("last_date");
                    int receivedAppVersion = response.getInt("version");

                    if ((receivedAppVersion > mAppVersion) && settings_sprawdzajAktualizacje) {
                        showNotification("Jest nowa wersja aplikacji!", "update");
                    }

                    JSONArray items = response.getJSONArray("items");
                    int new_items = items.length();
                    if (new_items > 0) {
                        if (state == Typy.ServiceState.state_onPause && settings_pokazujPowiadomienia) {
                            showNotification("Nowe wiadomości", "msg");
                        }
                        xstApp.zapiszUstawienie(Typy.PREFS_MSGS, items.toString());
                        xstApp.zapiszUstawienie(Typy.PREFS_LAST_DATE, mLastDate);

                        Intent i = new Intent();
                        i.putExtra("items", items.toString());
                        i.setAction(Typy.BROADCAST_NEW_MSG);
                        sendBroadcast(i);
                    }

                    JSONArray online = response.getJSONArray("online");
                    if (online.length() > 0) {
                        if (!currentOnlineList.equals(online.toString())) {
                            Log.i("xst", "Service: nowa lista online! wysylam broadcast");
                            currentOnlineList = online.toString();
                            xstApp.zapiszUstawienie(Typy.PREFS_ONLINE, currentOnlineList);

                            Intent i = new Intent();
                            i.putExtra("online", online.toString());
                            i.setAction(Typy.BROADCAST_ONLINE);
                            sendBroadcast(i);
                        }
                    }
                } catch (JSONException exception) {
                    Log.e("xst", exception.getMessage());
                }
            }
        }, new VolleyErrorListener());
        req.setTag(Typy.TAG_GET_MSG);
        getRequestQueue().add(req);
    }

    private void broadcastMessage(String msg) {
        Intent i = new Intent();
        i.setAction(msg);
        Log.i("xst", "Service is broadcasting msg: " + msg);
        sendBroadcast(i);
    }

    private void broadcastConnectionError() {
        broadcastMessage(Typy.BROADCAST_KONIEC_ODSWIEZANIA);
        broadcastMessage(Typy.BROADCAST_INTERNET_LOST);
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
                    Log.e("xst", "Service: Blad lajkowania");
                }
            }
        }, new VolleyErrorListener());
        req.setTag(Typy.TAG_LIKE_MSG);
        getRequestQueue().add(req);
    }

    private void cancelRefresh() {
        getRequestQueue().cancelAll(Typy.TAG_GET_MSG);
        mHandler.removeCallbacks(mRunnableWiadomosci);
    }

    private void showNotification(String txt, String typ) {
        Log.i("xst", "Service: pokazuje notification: " + typ);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        String channel = Typy.NOTIF_CHANNEL_MSG_ID;
        int notifId = 0;
        if (typ.equals("msg")) {
             intent = new Intent(this, MainActivity.class);
             channel = Typy.NOTIF_CHANNEL_MSG_ID;
             notifId = Typy.MSG_NOTIFICATION_ID;
        } else if (typ.equals("update")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://xs-team.pl/android/XST.apk"));
            channel = Typy.NOTIF_CHANNEL_UPDT_ID;
            notifId = Typy.UPDT_NOTIFICATION_ID;
            if (notificationAlreadyShown(notifId)) {
                Log.i("xst", "Service: powiadomienie już pokazane, olewam");
                return;
            } else {
                activeNotificationsIds.add(notifId);
            }
        }
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.xst_text)
                .setContentTitle("XST Shoutbox")
                .setContentText(txt)
                .setContentIntent(pIntent)
                .setLights(Color.RED, 500, 900)
                .setAutoCancel(true);

        notificationManager.notify(notifId, mBuilder.build());
    }

    private boolean notificationAlreadyShown(int notifId) {
        return activeNotificationsIds.contains(notifId);
    }

    private void startJobSchedulerInternetOK() {
        mJobScheduler.schedule(
                new JobInfo.Builder(
                        1, new ComponentName(
                                this, JobServiceInternetOK.class)).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build());
    }

    private PackageInfo getAppPackageInfo() {
        try {
            return this.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void wczytajUstawienia() {
        settings_sprawdzajAktualizacje = xstApp.isAutomatyczneAktualizacje();
        settings_pokazujPowiadomienia = xstApp.isPokazujPowiadomienia();

        mLastDate = xstApp.getLastDate();
        mKey = xstApp.getApiKey();

        currentOnlineList = xstApp.getOnline();

        Log.i("xst", "Service: pobralem ustawienia: " + mKey);
    }

    private class VolleyErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            request = Typy.ServiceRequest.request_none;
            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                cancelRefresh();
                broadcastConnectionError();
                if (!isConnected()) {
                    startJobSchedulerInternetOK();
                }
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
    }
}
