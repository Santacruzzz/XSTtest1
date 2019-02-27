package com.example.tomek.shoutbox.activities;

import android.content.Intent;

/**
 * Created by Tomek on 2017-10-19.
 */

public interface IMainActivity {
    void broadcastReceived(String intent);
    void nowa_wiadomosc(Intent i);
    void zalogowano();
    void odswiezWiadomosci();
    int getThemeColor(int[] arr);
    int getThemeRecourceId(int[] arr);
    void sendMessage(String wiadomosc);
    void polajkowanoWiadomosc(int msgid);
    void lajkujWiadomosc(int id, int position);
    Integer getKeyboardSize();
    int getState();
}
