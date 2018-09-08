package com.example.tomek.shoutbox;

import android.content.SharedPreferences;

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
    private int lastDate = 0;
    private int iloscPobranych = 0;
    private SharedPreferences sharedPreferences;
    private XstApplication xstApp;

    public XstDb() {
        listaWiadomosci = new ArrayList<>();
        listaOnline = new ArrayList<>();
        listaObrazkow = new ArrayList<>();
        listaObrazkowZdysku = new ArrayList<>();
        xstApp = null;
    }

    public void odswiezWiadomosci() {

    }

    public void initialize(XstApplication xstApplication) {
        xstApp = xstApplication;
        sharedPreferences = xstApplication.getSharedPreferences(Typy.PREFS_NAME, 0);
        wczytajListeWiadomosci();

    }

    private void wczytajListeWiadomosci() {
        String sitems = sharedPreferences.getString(Typy.PREFS_MSGS, "0");
        wypelnijListeWiadomosci(sitems);
    }

    private void wypelnijListeWiadomosci(String items) {
        try {
            jsonListaWiadomosci = new JSONArray(items);
            listaWiadomosci.clear();
            for (int i = 0; i < jsonListaWiadomosci.length(); i++) {
                JSONObject item = jsonListaWiadomosci.getJSONObject(i);
                listaWiadomosci.add(new Wiadomosc(item));
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        if (listaWiadomosci == null) {
            return;
        }
        try {
            if (jsonListaWiadomosci.length() > likedMsgPosition) {
                JSONObject item = jsonListaWiadomosci.getJSONObject(likedMsgPosition);
                item.put("likes", item.getInt("likes") + 1);
                jsonListaWiadomosci.put(likedMsgPosition, item);
                zapiszJsonListeWiadomosci();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void zapiszJsonListeWiadomosci() {
        xstApp.zapiszUstawienie(Typy.PREFS_MSGS, jsonListaWiadomosci.toString());
    }

    public JSONArray getJsonListaWiadomosci() {
        return jsonListaWiadomosci;
    }

    public void setJsonListaWiadomosci(JSONArray lista) {
        jsonListaWiadomosci = lista;
        wypelnijListeWiadomosci(lista.toString());
        zapiszJsonListeWiadomosci();
    }

    public void clearAll() {
        listaWiadomosci.clear();
        listaObrazkowZdysku.clear();
        listaObrazkow.clear();
        listaOnline.clear();
        jsonListaWiadomosci = new JSONArray();

    }
}
