package com.example.tomek.shoutbox;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by Tomek on 2018-02-26.
 */

public class BazaDanych {
    private Activity activity;
    private ArrayList<Wiadomosc> listaWiadomosci;
    private ArrayList<User> listaOnline;
    private int lastDate = 0;
    private int iloscPobranych = 0;

    public BazaDanych() {
        setLastDate(getLastDate() + 1);
    }

    public void odswiezWiadomosci() {

    }

    public void pobierzStarsze() {

    }

    public void initialize(XstApplication xstApplication) {

    }

    public ArrayList<Wiadomosc> getListaWiadomosci() {
        return listaWiadomosci;
    }

    public void setListaWiadomosci(ArrayList<Wiadomosc> listaWiadomosci) {
        this.listaWiadomosci = listaWiadomosci;
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
}
