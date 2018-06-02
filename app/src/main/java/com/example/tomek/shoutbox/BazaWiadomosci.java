package com.example.tomek.shoutbox;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by Tomek on 2018-02-26.
 */

public class BazaWiadomosci {
    private Activity m_activity;
    private ArrayList<Wiadomosc> m_listaWiadomosci;
    private int m_lastDate = 0;
    private int m_iloscPobranych = 0;

    public BazaWiadomosci(Activity p_activity) {
        m_activity = p_activity;
    }

    public void setListaWiadomosci(ArrayList<Wiadomosc> p_lista) {
        m_listaWiadomosci = p_lista;
    }

    public void odswiezWiadomosci() {

    }

    public void pobierzStarsze() {

    }
}
