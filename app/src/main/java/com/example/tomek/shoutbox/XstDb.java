package com.example.tomek.shoutbox;

import android.content.SharedPreferences;

import com.example.tomek.shoutbox.utils.Typy;

import java.util.ArrayList;

/**
 * Created by Tomek on 2018-02-26.
 */

public class XstDb {
    private ArrayList<Wiadomosc> listaWiadomosci;
    private ArrayList<User> listaOnline;
    private ArrayList<MojObrazek> listaObrazkow;
    private ArrayList<String> listaObrazkowZdysku;
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
}
