package com.example.tomek.shoutbox.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tomek.shoutbox.MojObrazek;
import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterMojeObrazki extends BaseAdapter {
    private ArrayList<MojObrazek> obrazki;
    private Activity activity;
    private LayoutInflater inflater;
    private SimpleDateFormat dateFormat;

    public AdapterMojeObrazki(Activity p_activity) {
        activity = p_activity;
        obrazki = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void setObrazki(JSONArray p_obrazki, boolean czyOdswiezyc) {
        if (!czyOdswiezyc) {
            if (obrazki.size() > 0) {
                return;
            }
        }
        Log.i("xst", "ODSWIEZAM OBRAZKI");
        try {
            obrazki.clear();
            for (int i = 0; i < p_obrazki.length(); i++) {
                JSONObject obrazek = p_obrazki.getJSONObject(i);
                obrazki.add(new MojObrazek(obrazek.getInt("id"),
                                           Utils.getDateFromString(obrazek.getString("data")),
                                           obrazek.getString("file")));
            }
        } catch (JSONException ignored) {

        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return obrazki.size();
    }

    @Override
    public Object getItem(int position) {
        return obrazki.get(position);
    }

    @Override
    public long getItemId(int position) {
        return obrazki.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        View row;
        try {
            if (inflater != null) {
                row = inflater.inflate(R.layout.obrazek_layout, parent, false);
            } else return null;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }

        MojObrazek obrazek = obrazki.get(position);
        ImageView imageView = row.findViewById(R.id.imageObrazek);
        ImageView imageOverlay = row.findViewById(R.id.imgOverlay);
        TextView textData = row.findViewById(R.id.textDataObrazka);
        textData.setText(dateFormat.format(obrazek.data));
        if (obrazek.isChecked()) {
            imageOverlay.setVisibility(View.VISIBLE);
        } else {
            imageOverlay.setVisibility(View.INVISIBLE);
        }
        Picasso.with(activity).load(obrazek.getObrazekUrl()).into(imageView);
        row.setTag(obrazek.id);
        return row;
    }

    public void setItemCheckedState(int i, boolean checked) {
        obrazki.get(i).setChecked(checked);
    }

    public void clearChoices() {
        for (int i = 0; i < obrazki.size(); i++) {
            obrazki.get(i).setChecked(false);
        }
    }

    public void deleteItem(int position) {
        if (position < obrazki.size()) {
            obrazki.remove(position);
        }
    }

    public void dodajObrazki(JSONArray p_obrazki) {
        for (int i = 0; i < p_obrazki.length(); i++) {
            try {
                JSONObject obrazek = p_obrazki.getJSONObject(i);
                obrazki.add(new MojObrazek(obrazek.getInt("id"),
                        Utils.getDateFromString(obrazek.getString("data")),
                        obrazek.getString("file")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (p_obrazki.length() > 0) {
            notifyDataSetChanged();
        }
    }
}
