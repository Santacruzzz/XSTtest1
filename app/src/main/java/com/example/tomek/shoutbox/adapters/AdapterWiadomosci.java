package com.example.tomek.shoutbox.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.tomek.shoutbox.utils.EmoticonsParser;
import com.example.tomek.shoutbox.activities.IMainActivity;
import com.example.tomek.shoutbox.activities.PokazObrazekActivity;
import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.Wiadomosc;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        m_parserEmotek = new EmoticonsParser(mAct.getApplicationContext());
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


        TextView autor = row.findViewById(R.id.v_nick);
        TextView wiadomosc = row.findViewById(R.id.v_wiadomosc);
        TextView data = row.findViewById(R.id.v_data);
        TextView lajki = row.findViewById(R.id.v_lajki);
        ImageView img_like = row.findViewById(R.id.v_lajk_ikona);
        
        if (imageLoader == null) {
            imageLoader = imain.getImageLoader();
        }

        NetworkImageView avatar = row.findViewById(R.id.v_avatar);
        avatar.setImageUrl(mWiadomosc.getAvatar(), imageLoader);

//        ImageView avatar = row.findViewById(R.id.v_avatar);
//        Picasso.with(mAct).load(mWiadomosc.getAvatar()).into(avatar);

        autor.setText(mWiadomosc.getAutor());
        SpannableString spannableString = new SpannableString(Html.fromHtml((mWiadomosc.getWiadomosc())));
        Spannable l_spanableWiadomosc = m_parserEmotek.getSmiledText(spannableString);

        if (mWiadomosc.getObrazki().size() > 0) {
            int numer_obrazka = 1;
            for (String obrazek : mWiadomosc.getObrazki()) {
                l_spanableWiadomosc = setSpannableImgLink(l_spanableWiadomosc, obrazek, numer_obrazka);
                numer_obrazka ++;
            }
        }

        wiadomosc.setText(l_spanableWiadomosc);
        wiadomosc.setMovementMethod(LinkMovementMethod.getInstance());

        data.setText(mWiadomosc.getData());
        lajki.setText(mWiadomosc.getLajki() != 0 ? Integer.valueOf(mWiadomosc.getLajki()).toString() : "");
        if (mWiadomosc.getLajki() < 1) {
            img_like.setVisibility(View.INVISIBLE);
        } else {
            img_like.setVisibility(View.VISIBLE);
        }
        row.setTag(mWiadomosc.getId());
        return row;
    }

    public void clear() {
        lista.clear();
    }

    private class SpanOnClickListener extends ClickableSpan  {
        String m_url;

        SpanOnClickListener(String p_url) {
            m_url = p_url;
        }

        @Override
        public void onClick(View v) {
            Intent l_intent = new Intent(mAct, PokazObrazekActivity.class);
            l_intent.putExtra("image_url", m_url);
            mAct.startActivity(l_intent);

        }
    }

    private Spannable setSpannableImgLink(Spannable txt, String obrazek, Integer id) {
        Pattern mImgsPattern = Pattern.compile("\\[Obrazek\\ #" + id + "\\]");
        int start = 0;
        int end = 0;
        Matcher l_matcher = mImgsPattern.matcher(txt);
        while(l_matcher.find()) {
            start = l_matcher.start();
            end = l_matcher.end();
        }
        txt.setSpan(new SpanOnClickListener(obrazek), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return txt;

        // v_obrazek.setOnClickListener(new ObrazekOnClickListener(obrazek));

        // All the rest will have the same spannable.
//        ClickableSpan cs = new ClickableSpan() {
//            @Override
//            public void onClick(View v) {
//                Log.d("main", "textview clicked");
//                Toast.makeText(mAct, "textview clicked", Toast.LENGTH_SHORT).show();
//            } };

        // set the "test " spannable.
        //txt.setSpan(cs, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // set the " span" spannable
        //txt.setSpan(cs, 6, txt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //tv.setText(txt);
        //tv.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
