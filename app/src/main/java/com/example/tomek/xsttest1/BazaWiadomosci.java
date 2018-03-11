package com.example.tomek.xsttest1;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by Tomek on 2018-02-26.
 */

public class BazaWiadomosci {
    private Activity m_activity;
    private ArrayList<Wiadomosc> m_listaWiadomosci;
    private int m_lastDate;

    public BazaWiadomosci(Activity p_activity) {
        m_activity = p_activity;
    }
}
