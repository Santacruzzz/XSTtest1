package com.example.tomek.shoutbox.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.tomek.shoutbox.MojObrazek;
import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.activities.IMainActivity;
import com.example.tomek.shoutbox.activities.IVolley;
import com.example.tomek.shoutbox.utils.Typy;
import com.example.tomek.shoutbox.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class AdapterMojeObrazki extends BaseAdapter {
    private ArrayList<MojObrazek> obrazki;
    private ImageLoader imageLoader;
    private Activity activity;
    private IVolley iVolley;
    private LayoutInflater inflater;

    public AdapterMojeObrazki(Activity p_activity) {
        activity = p_activity;
        iVolley = (IVolley) activity;
        obrazki = new ArrayList<>();
    }

    public void setObrazki(JSONArray p_obrazki) {
        if (obrazki.size() > 0) {
            return;
        }
        try {
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

        if (imageLoader == null) {
            imageLoader = iVolley.getImageLoader();
        }

        MojObrazek obrazek = obrazki.get(position);

        NetworkImageView imageView = row.findViewById(R.id.imageObrazek);
        imageView.setImageUrl(Typy.URL_OBRAZKI + obrazek.file, imageLoader);

        return row;
    }
}
