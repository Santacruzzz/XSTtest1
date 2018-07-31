package com.example.tomek.shoutbox.activities;

import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.example.tomek.shoutbox.fragments.FragmentOnline;
import com.example.tomek.shoutbox.fragments.FragmentSb;
import com.example.tomek.shoutbox.OnlineItem;
import com.example.tomek.shoutbox.Wiadomosc;

import java.util.ArrayList;

/**
 * Created by Tomek on 2017-10-19.
 */

public interface IMainActivity {
    void broadcastReceived(String intent);
    void nowa_wiadomosc(Intent i);
    void zalogowano();
    FragmentSb getFragmentSb();
    FragmentOnline getFragmentOnline();
    ArrayList<Wiadomosc> getWiadomosci();
    void odswiezWiadomosci();
    ArrayList<OnlineItem> getOnline();
    int getThemeColor(int[] arr);
    int getThemeRecourceId(int[] arr);
    void nowe_online(Intent intent);
    void wyslij_wiadomosc(String wiadomosc);
    void polajkowanoWiadomosc(int msgid);
    void lajkujWiadomosc(int id, int position);
}
