package com.example.tomek.xsttest1;

import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Tomek on 2017-10-19.
 */

interface IMainActivity {
    ImageLoader getImageLoader();
    RequestQueue getRequestQueue();
    void broadcastReceived(String intent);
    void nowa_wiadomosc(Intent i);
    void zalogowano(boolean b);
    FragmentSb getmFragmentSb();
    FragmentOnline getmFragmentOnline();
    ArrayList<Wiadomosc> getWiadomosci();
    ArrayList<OnlineItem> getOnline();
    int getThemeColor(int[] arr);
    int getThemeRecourceId(int[] arr);
    void nowe_online(Intent intent);
    void wyslij_wiadomosc(String wiadomosc);
}
