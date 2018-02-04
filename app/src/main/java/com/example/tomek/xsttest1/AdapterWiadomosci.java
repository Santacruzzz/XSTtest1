package com.example.tomek.xsttest1;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by Tomek on 2017-10-19.
 */

public class AdapterWiadomosci extends BaseAdapter {
    ArrayList<Wiadomosc> lista;
    Context context;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    IMainActivity imain;
    Activity mAct;

    private int mBgResourceID_even = 0;
    private int mBgResourceID_odd = 0;
    private boolean isReused = true;

    public AdapterWiadomosci(Activity act, ArrayList<Wiadomosc> _list) {
        lista = _list;
        this.context = act.getApplicationContext();
        inflater = LayoutInflater.from(context);
        imain = (IMainActivity) act;
        imageLoader = imain.getImageLoader();
        mAct = act;
        mBgResourceID_even = imain.getThemeRecourceId(new int[]{R.attr.msgBackground});
        mBgResourceID_odd = imain.getThemeRecourceId(new int[]{R.attr.msgBackground_odd});
    }

    public void setLista(ArrayList<Wiadomosc> list) {
        lista = list;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int pozycja) {
        if (pozycja >= 0 && pozycja < lista.size()) {
            return lista.get(pozycja);
        }
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int pozycja, View row, ViewGroup arg2) {
        if (inflater == null) {
            inflater = (LayoutInflater) mAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (row == null) {
            isReused = false;
            row = inflater.inflate(R.layout.wiadomosc_layout, null);
        }
        TextView autor = row.findViewById(R.id.v_nick);
        TextView wiadomosc = row.findViewById(R.id.v_wiadomosc);
        TextView data = row.findViewById(R.id.v_data);
        TextView lajki = row.findViewById(R.id.v_lajki);
        RelativeLayout layWiadomosc = row.findViewById(R.id.layoutWiadomosc);
        ImageView img_like = row.findViewById(R.id.v_lajk_ikona);
        Wiadomosc mWiadomosc;

        try {
            mWiadomosc = lista.get(pozycja);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        if (pozycja % 2 == 0) {
            layWiadomosc.setBackgroundResource(mBgResourceID_even);
//            layWiadomosc.setBackgroundColor(Color.RED);
        } else {
            layWiadomosc.setBackgroundResource(mBgResourceID_odd);
//            layWiadomosc.setBackgroundColor(Color.BLUE);
        }

        if (imageLoader == null) {
            imageLoader = imain.getImageLoader();
        }

        NetworkImageView avatar = row.findViewById(R.id.v_avatar);
        avatar.setImageUrl(mWiadomosc.getAvatar(), imageLoader);
        Log.i("xst", mWiadomosc.getAvatar());

        autor.setText(mWiadomosc.getAutor());
        String obr = "";
        String str_wiadomosc = mWiadomosc.getWiadomosc();

        int numer_obrazka = 1;
        if (mWiadomosc.getObrazki() != null) {
            for (String obrazek : mWiadomosc.getObrazki()) {
                //TODO OBRAZKI
                str_wiadomosc = str_wiadomosc.replace("[Obrazek #" + numer_obrazka + "]", "[Obrazek #1: " + obrazek + "]");
                ++numer_obrazka;
            }
        }


//        SpannableString spn = new SpannableString(Html.fromHtml((mWiadomosc.getWiadomosc() + " " + obr).toString()));
//        wiadomosc.setText(imain.getTekstEmotki(spn));


        wiadomosc.setText(str_wiadomosc);
//        wiadomosc.setLinksClickable(false);
        data.setText(mWiadomosc.getData());
        lajki.setText(mWiadomosc.getLajki() != 0 ? Integer.valueOf(mWiadomosc.getLajki()).toString() : "");
        if (mWiadomosc.getLajki() < 1) {
            img_like.setVisibility(View.INVISIBLE);
        } else {
            img_like.setVisibility(View.VISIBLE);
        }
        return row;
    }

    public void clear() {
        lista.clear();
    }
}
