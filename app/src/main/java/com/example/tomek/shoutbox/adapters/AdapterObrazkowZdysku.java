package com.example.tomek.shoutbox.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.SquareImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterObrazkowZdysku extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<String> listaObrazkow;
    private Context cntx;

    public AdapterObrazkowZdysku(Context p_cntx, ArrayList<String> lista) {
        cntx = p_cntx;
        inflater = LayoutInflater.from(cntx);
        listaObrazkow = lista;
        if (lista == null) {
            Log.e("xst", "LISTA JEST PUSTA!");
            listaObrazkow = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return listaObrazkow.size();
    }

    @Override
    public Object getItem(int position) {
        return listaObrazkow.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) cntx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        View row;
        try {
            if (inflater != null) {
                row = inflater.inflate(R.layout.obrazek_z_dysku, parent, false);
            } else return null;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }

        SquareImageView obrazek = row.findViewById(R.id.imageObrazekZdysku);
        Picasso.with(cntx).load("file://" + listaObrazkow.get(position)).fit().centerCrop().into(obrazek);

        return row;
    }

    public void setLista(ArrayList<String> lista) {
        this.listaObrazkow = lista;
        notifyDataSetChanged();
    }
}