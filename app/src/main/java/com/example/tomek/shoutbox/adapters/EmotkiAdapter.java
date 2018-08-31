package com.example.tomek.shoutbox.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.tomek.shoutbox.utils.EmoticonsParser;
import com.example.tomek.shoutbox.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class EmotkiAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private EmoticonsParser mEmotki;
    private ArrayList<String> mListaEmotekPattern;
    private ArrayList<Integer> mListaEmotekResId;
    Context cntx;

    public EmotkiAdapter(Context p_cntx) {
        cntx = p_cntx;
        inflater = LayoutInflater.from(cntx);
        mEmotki = new EmoticonsParser(cntx);
        mListaEmotekPattern = new ArrayList<>();
        mListaEmotekResId = new ArrayList<>();
        for (Map.Entry<String, Integer> entry: mEmotki.getEmoticonsMap().entrySet()) {
            mListaEmotekPattern.add(entry.getKey());
            mListaEmotekResId.add(entry.getValue());
        }
    }

    @Override
    public int getCount() {
        return mListaEmotekPattern.size();
    }

    @Override
    public Object getItem(int position) {
        return mListaEmotekPattern.get(position);
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
                row = inflater.inflate(R.layout.emotka_layout, parent, false);
            } else return null;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }

        ImageView emotka = row.findViewById(R.id.imageEmotka);
        Picasso.with(cntx).load(mListaEmotekResId.get(position)).into(emotka);

        return row;
    }
}
