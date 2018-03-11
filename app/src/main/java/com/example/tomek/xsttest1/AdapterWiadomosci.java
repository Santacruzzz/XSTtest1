package com.example.tomek.xsttest1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/**
 * Created by Tomek on 2017-10-19.
 */

public class AdapterWiadomosci extends BaseAdapter {
    private ArrayList<Wiadomosc> lista;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;
    private IMainActivity imain;
    private Activity mAct;
    private EmoticonsParser m_parserEmotek;

    private int mBgResourceID_even = 0;
    private int mBgResourceID_odd = 0;
    private boolean isReused = true;

    public AdapterWiadomosci(Activity act, ArrayList<Wiadomosc> _list) {
        lista = _list;
        inflater = LayoutInflater.from(act.getApplicationContext());
        imain = (IMainActivity) act;
        imageLoader = imain.getImageLoader();
        mAct = act;
        m_parserEmotek = new EmoticonsParser(mAct);
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

        try {
            if (inflater != null) {
                row = inflater.inflate(R.layout.wiadomosc_layout, arg2, false);
            } else return null;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }

        Wiadomosc mWiadomosc;
        try {
            mWiadomosc = lista.get(pozycja);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        final RelativeLayout layWiadomosc = row.findViewById(R.id.layoutWiadomosc);
        if (pozycja % 2 == 0) {
            layWiadomosc.setBackgroundResource(mBgResourceID_even);
        } else {
            layWiadomosc.setBackgroundResource(mBgResourceID_odd);
        }

        row.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layWiadomosc.getBackground().setHotspot(event.getX(), event.getY());
                layWiadomosc.performClick();
                return(false);
            }
        });


        FlowLayout layoutImages = row.findViewById(R.id.layoutImages);
        TextView autor = row.findViewById(R.id.v_nick);
        TextView wiadomosc = row.findViewById(R.id.v_wiadomosc);
        TextView data = row.findViewById(R.id.v_data);
        TextView lajki = row.findViewById(R.id.v_lajki);
        TextView naglowekObrazkow = row.findViewById(R.id.naglowekObrazkow);
        ImageView img_like = row.findViewById(R.id.v_lajk_ikona);


        if (imageLoader == null) {
            imageLoader = imain.getImageLoader();
        }
        NetworkImageView avatar = row.findViewById(R.id.v_avatar);
        avatar.setImageUrl(mWiadomosc.getAvatar(), imageLoader);

//        ImageView avatar = row.findViewById(R.id.v_avatar);
//        Picasso.with(mAct).load(mWiadomosc.getAvatar()).into(avatar);
        LayoutInflater l_inflater = (LayoutInflater)mAct.getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        autor.setText(mWiadomosc.getAutor());
        int numer_obrazka = 1;
        if (mWiadomosc.getObrazki().size() > 0) {
            naglowekObrazkow.setVisibility(View.VISIBLE);
            for (String obrazek : mWiadomosc.getObrazki()) {
                View l_inflatedView = l_inflater.inflate(R.layout.layout_miniatura_obrazka, layoutImages, false);

                ImageView v_obrazek = l_inflatedView.findViewById(R.id.miniatura_obrazek);
                TextView txt_obrazek = l_inflatedView.findViewById(R.id.miniatura_tekst);
                txt_obrazek.setText("#" + numer_obrazka);

                Picasso.with(mAct).load(obrazek).into(v_obrazek);
//                v_obrazek.setImageUrl(obrazek, imageLoader);

                layoutImages.addView(l_inflatedView);
                v_obrazek.setOnClickListener(new ObrazekOnClickListener(obrazek));
                numer_obrazka ++;
            }
        } else {
            naglowekObrazkow.setVisibility(View.GONE);
        }

        SpannableString spannableString = new SpannableString(Html.fromHtml((mWiadomosc.getWiadomosc())));
        wiadomosc.setText(m_parserEmotek.getSmiledText(spannableString));

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

    private class ObrazekOnClickListener implements View.OnClickListener {
        String m_url;

        ObrazekOnClickListener(String p_url) {
            m_url = p_url;
        }

        @Override
        public void onClick(View v) {
            Intent l_intent = new Intent(mAct, PokazObrazekActivity.class);
            l_intent.putExtra("image_url", m_url);
            mAct.startActivity(l_intent);

        }
    }
}
