package com.example.tomek.shoutbox;

import android.content.SharedPreferences;

import com.example.tomek.shoutbox.activities.OnlineListener;
import com.example.tomek.shoutbox.activities.SbListener;
import com.example.tomek.shoutbox.utils.EmoticonsParser;
import com.example.tomek.shoutbox.utils.Typy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Tomek on 2018-02-26.
 */

public class XstDb {
    private ArrayList<Wiadomosc> listaWiadomosci;
    private ArrayList<User> listaOnline;
    private ArrayList<MojObrazek> listaObrazkow;
    private ArrayList<String> listaObrazkowZdysku;
    private JSONArray jsonListaWiadomosci;
    private JSONArray jsonListaOnline;
    private JSONArray jsonListaObrazkow;
    private int lastDate = 0;
    private int iloscPobranych = 0;
    private SharedPreferences sharedPreferences;
    private XstApplication xstApp;
    private SbListener listenerSb;
    private OnlineListener listenerOnline;
    private JSONArray starszeJsonListaWiadomosci;
    private DbListener dbListener;
    private EmoticonsParser emoticonsParser;

    public XstDb() {
        listaWiadomosci = new ArrayList<>();
        listaOnline = new ArrayList<>();
        listaObrazkow = new ArrayList<>();
        listaObrazkowZdysku = new ArrayList<>();
        jsonListaWiadomosci = new JSONArray();
        jsonListaOnline = new JSONArray();
        jsonListaObrazkow = new JSONArray();
        xstApp = null;
    }

    public void setDbListener(DbListener dbListener) {
        this.dbListener = dbListener;
    }

    public void initialize(XstApplication xstApplication) {
        xstApp = xstApplication;
        emoticonsParser = new EmoticonsParser(xstApplication);
        sharedPreferences = xstApplication.getSharedPreferences(Typy.PREFS_NAME, 0);
        wczytajListeWiadomosci();

    }

    private void wczytajListeWiadomosci() {
        String items = sharedPreferences.getString(Typy.PREFS_MSGS, "0");
        try {
            jsonListaWiadomosci = new JSONArray(items);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        wypelnijListeWiadomosci();
    }

    private void wypelnijListeWiadomosci() {
        try {
            listaWiadomosci.clear();
            for (int i = 0; i < jsonListaWiadomosci.length(); i++) {
                JSONObject item = jsonListaWiadomosci.getJSONObject(i);
                listaWiadomosci.add(new Wiadomosc(xstApp, emoticonsParser, item));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (dbListener != null) {
            dbListener.wypelnionoListeWiadomosci();
        }
    }

    public ArrayList<Wiadomosc> getListaWiadomosci() {
        return listaWiadomosci;
    }


    public ArrayList<User> getListaOnline() {
        return listaOnline;
    }

    public void setListaOnline(ArrayList<User> listaOnline) {
        this.listaOnline = listaOnline;
    }

    public int getLastDate() {
        return lastDate;
    }

    public void setLastDate(int lastDate) {
        this.lastDate = lastDate;
    }

    public int getIloscPobranych() {
        return iloscPobranych;
    }

    public void setIloscPobranych(int iloscPobranych) {
        this.iloscPobranych = iloscPobranych;
    }

    public void dodajObrazekZdysku(String url) {
        if (listaObrazkowZdysku.contains(url)) {
            return;
        }
        listaObrazkowZdysku.add(url);
    }

    public void polajkowanoWiadomosc(int likedMsgPosition) {
        try {
            if (jsonListaWiadomosci.length() > likedMsgPosition) {
                JSONObject item = jsonListaWiadomosci.getJSONObject(likedMsgPosition);
                item.put("likes", item.getInt("likes") + 1);
                jsonListaWiadomosci.put(likedMsgPosition, item);
                listaWiadomosci.get(likedMsgPosition).addLike();
                zapiszJsonListeWiadomosci();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void zapiszJsonListeWiadomosci() {
        xstApp.zapiszUstawienie(Typy.PREFS_MSGS, jsonListaWiadomosci.toString());
        if (listenerSb != null) {
            listenerSb.odswiezWiadomosci();
        }
    }

    private void zapiszJsonListeOnline() {
        xstApp.zapiszUstawienie(Typy.PREFS_ONLINE, jsonListaOnline.toString());
        if (listenerOnline != null) {
            listenerOnline.odswiezOnline();
        }

    }

    public JSONArray getJsonListaWiadomosci() {
        return jsonListaWiadomosci;
    }

    public void setJsonListaWiadomosci(JSONArray lista) {
        jsonListaWiadomosci = lista;
        wypelnijListeWiadomosci();
        zapiszJsonListeWiadomosci();
    }

    public void clearAll() {
        listaWiadomosci.clear();
        listaObrazkowZdysku.clear();
        listaObrazkow.clear();
        listaOnline.clear();
        jsonListaWiadomosci = new JSONArray();
        jsonListaOnline = new JSONArray();
        jsonListaObrazkow = new JSONArray();
    }

    public void setJsonListaOnline(JSONArray jsonListaOnline) {
        this.jsonListaOnline = jsonListaOnline;
        wypelnijListeOnline();
        zapiszJsonListeOnline();
    }

    private void wypelnijListeOnline() {
        try {
            listaOnline.clear();
            for (int i = 0; i < jsonListaOnline.length(); i++) {
                JSONObject item = jsonListaOnline.getJSONObject(i);
                listaOnline.add(new User(item));
            }
        } catch (JSONException ignored) {

        }
    }

    public JSONArray getJsonListaOnline() {
        return jsonListaOnline;
    }

    public void setSbListener(SbListener listener) {
        listenerSb = listener;
    }

    public void setOnlineListener(OnlineListener listener) {
        listenerOnline = listener;
    }

    public String getOlderDate() {
        return listaWiadomosci.get(listaWiadomosci.size() - 2).getRawDate();
    }

    public void setStarszeJsonListaWiadomosci(JSONArray starszeJsonListaWiadomosci) {
        this.starszeJsonListaWiadomosci = starszeJsonListaWiadomosci;
        dodajPobraneStarsze();
    }

    private void dodajPobraneStarsze() {
        for(int i = 0; i < starszeJsonListaWiadomosci.length(); i++) {
            try {
                jsonListaWiadomosci.put(starszeJsonListaWiadomosci.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        wypelnijListeWiadomosci();
        if (dbListener != null) {
            dbListener.pobranoStarsze();
        }
    }

}
