package com.example.tomek.shoutbox;

import android.content.SharedPreferences;

import com.example.tomek.shoutbox.utils.Typy;

import java.util.Date;

public class MojObrazek {
    public int id;
    public Date data;
    public String file;
    private SharedPreferences sharedPrefs;
    private boolean checked;

    public MojObrazek(int p_id, Date p_data, String p_file) {
        id = p_id;
        data = p_data;
        file = p_file;
        checked = false;
    }

    public String getObrazekUrl() {
        return Typy.URL_OBRAZKI + file;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean check) {
        checked = check;
    }

    public boolean getChecked() {
        return checked;
    }
}