package com.example.tomek.shoutbox;

import android.text.format.DateUtils;

import com.example.tomek.shoutbox.utils.Typy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tomek on 2017-10-19.
 */

public class Wiadomosc {
    private String autor, wiadomosc, data, avatar, source, sex, birthday;
    private int id, autorid, lajki = 0;
    private Typy.TypWiadomosci typWiadomosci;
    ArrayList<String> obrazki, linki;

    private SimpleDateFormat format_data;
    private SimpleDateFormat format_godziny;
    private SimpleDateFormat format_daty;

    public Wiadomosc(JSONObject obj) {
        obrazki = new ArrayList<>();
        linki = new ArrayList<>();
        format_data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format_godziny = new SimpleDateFormat("HH:mm");
        format_daty = new SimpleDateFormat("dd.MM HH:mm");
        try {
            typWiadomosci = Typy.TypWiadomosci.wiadomosc;
            setAutor(obj.getString("nickname"));
            setAutorId(obj.getInt("userID"));
            setAvatar(obj.getString("avatar"));
            setData(obj.getString("time"));
            setWiadomosc(obj.getString("content"));
            setLajki(obj.getInt("likes"));
            setId(obj.getInt("messageID"));

            JSONArray addons = obj.getJSONArray("addons");
            for (int i = 0; i < addons.length(); ++i) {
                JSONObject addon = addons.getJSONObject(i);
                if (addon.getString("type").equals("image")) {
                    obrazki.add(addon.getString("content"));
                } else if (addon.getString("type").equals("link")) {
                    linki.add(addon.getString("content"));
                }
            }
        } catch (JSONException ex) {

        }
    }

    public Wiadomosc(Typy.TypWiadomosci typ) {
        typWiadomosci = typ;
    }

    private void setObrazki(ArrayList<String> obrazki) {
        this.obrazki = obrazki;
    }

    private void setLinki(ArrayList<String> linki) {
        this.linki = linki;
    }

    public void addLike() {
        this.lajki ++;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getWiadomosc() {
        return wiadomosc;
    }

    public void setWiadomosc(String wiadomosc) {
        this.wiadomosc = wiadomosc;
    }

    public String getRawDate() {
        return data;
    }

    public String getDate() {
        Date date;
        String ret = data;

        try {
            date = format_data.parse(data);

            if (DateUtils.isToday(date.getTime())) {
                // dzisiaj
                ret = format_godziny.format(date);
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DATE, 1);
                if (DateUtils.isToday(cal.getTime().getTime())) {
                    // wczoraj
                    ret = "wczoraj " + format_godziny.format(date);
                } else {
                    ret = format_daty.format(date);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLajki() {
        return lajki;
    }

    public void setLajki(int lajki) {
        this.lajki = lajki;
    }

    public ArrayList<String> getObrazki() {
        return obrazki;
    }

    public ArrayList<String> getLinki() {
        return linki;
    }

    public int getAutorId() {
        return autorid;
    }

    public void setAutorId(int autorid) {
        this.autorid = autorid;
    }

    public String getAvatar() {
        return Typy.URL_AVATAR + avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Typy.TypWiadomosci getTypWiadomosci() {
        return typWiadomosci;
    }
}
