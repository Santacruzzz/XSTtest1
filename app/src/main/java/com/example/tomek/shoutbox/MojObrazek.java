package com.example.tomek.shoutbox;

import android.content.SharedPreferences;

import com.example.tomek.shoutbox.utils.Typy;

import java.util.Date;

public class MojObrazek {
    public int id;
    public Date data;
    public String file;
    private SharedPreferences sharedPrefs;

    public MojObrazek(int p_id, Date p_data, String p_file) {
        id = p_id;
        data = p_data;
        file = p_file;
    }

    public String getFullPath() {
        return Typy.URL_OBRAZKI + file;
    }
}