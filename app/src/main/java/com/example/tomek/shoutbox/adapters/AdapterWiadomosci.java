package com.example.tomek.shoutbox.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.Wiadomosc;
import com.example.tomek.shoutbox.activities.IMainActivity;
import com.example.tomek.shoutbox.activities.MainActivity;
import com.example.tomek.shoutbox.activities.PokazObrazekActivity;
import com.example.tomek.shoutbox.utils.EmoticonsParser;
import com.example.tomek.shoutbox.utils.Typy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomek on 2017-10-19.
 */

public class AdapterWiadomosci extends BaseAdapter {
    private ArrayList<Wiadomosc> lista;
    private LayoutInflater inflater;
    private MainActivity mAct;
    private EmoticonsParser m_parserEmotek;

    private int mBgResourceID_even = 0;
    private int mBgResourceID_odd = 0;
    private boolean isReused = true;

    public AdapterWiadomosci(MainActivity act) {
        inflater = LayoutInflater.from(act.getApplicationContext());
        IMainActivity imain = act;
        mAct = act;
        m_parserEmotek = new EmoticonsParser(mAct.getApplicationContext());
        mBgResourceID_even = imain.getThemeRecourceId(new int[]{R.attr.msgBackground});
        mBgResourceID_odd = imain.getThemeRecourceId(new int[]{R.attr.msgBackground_odd});
        lista = mAct.getXstDatabase().getListaWiadomosci();
    }

    @Override
    public int getCount() {
        if (lista.size() == 0) return 0;
        if (lista.get(lista.size() - 1).getTypWiadomosci() == Typy.TypWiadomosci.wiadomosc) {
            lista.add(new Wiadomosc(Typy.TypWiadomosci.przycisk_pokaz_starsze));
        }
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
        if (lista.size() <= pozycja) {
            return null;
        }
        Wiadomosc mWiadomosc;
        mWiadomosc = lista.get(pozycja);

        if (inflater != null) {
            if (mWiadomosc.getTypWiadomosci() == Typy.TypWiadomosci.przycisk_pokaz_starsze) {
                row = inflater.inflate(R.layout.wiadomosc_layout_pokaz_wiecej, arg2, false);
                return row;
            }
            row = inflater.inflate(R.layout.wiadomosc_layout, arg2, false);
        }

        final ConstraintLayout layWiadomosc = row.findViewById(R.id.layoutWiadomosc);
        TextView wiadomosc = row.findViewById(R.id.v_wiadomosc);
        TextView autor = row.findViewById(R.id.v_nick);
        TextView date = row.findViewById(R.id.v_data);
        TextView lajki = row.findViewById(R.id.v_lajki);
        ImageView img_like = row.findViewById(R.id.v_lajk_ikona);
        ImageView avatar = row.findViewById(R.id.v_avatarOnline);

        setMessageBackground(pozycja, layWiadomosc);
        Picasso.with(mAct).load(mWiadomosc.getAvatar()).into(avatar);
        autor.setText(mWiadomosc.getAutor());
        setNasalizationFont(autor);
        fillMessageWithImagesAndEmoticons(mWiadomosc, wiadomosc);
        date.setText(mWiadomosc.getDate());
        setViewForLikes(mWiadomosc, lajki, img_like);
        row.setTag(mWiadomosc.getId());
        return row;
    }

    private void setViewForLikes(Wiadomosc mWiadomosc, TextView lajki, ImageView img_like) {
        lajki.setText(mWiadomosc.getLajki() != 0 ? Integer.valueOf(mWiadomosc.getLajki()).toString() : "");
        if (mWiadomosc.getLajki() < 1) {
            img_like.setVisibility(View.INVISIBLE);
        } else {
            img_like.setVisibility(View.VISIBLE);
        }
    }

    private void fillMessageWithImagesAndEmoticons(Wiadomosc mWiadomosc, TextView wiadomosc) {
        SpannableString spannableString = new SpannableString(Html.fromHtml((mWiadomosc.getWiadomosc())));
        int txtViewHeight = wiadomosc.getLineHeight();
        Spannable l_spanableWiadomosc = m_parserEmotek.getSmiledText(spannableString, txtViewHeight);

        if (mWiadomosc.getObrazki().size() > 0) {
            int numer_obrazka = 1;
            for (String obrazek : mWiadomosc.getObrazki()) {
                l_spanableWiadomosc = setSpannableImgLink(l_spanableWiadomosc,
                                                          obrazek,
                                                          numer_obrazka,
                                                          mWiadomosc.getAutor());
                numer_obrazka ++;
            }
        }

        wiadomosc.setMovementMethod(LinkMovementMethod.getInstance());
        wiadomosc.setText(l_spanableWiadomosc);
    }

    private void setNasalizationFont(TextView autor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            Typeface face = Typeface.createFromAsset(mAct.getAssets(),
                    "fonts/nasalization.ttf");
            autor.setTypeface(face);
        }
    }

    private void setMessageBackground(int pozycja, ConstraintLayout layWiadomosc) {
        if (pozycja % 2 == 0) {
            layWiadomosc.setBackgroundResource(mBgResourceID_even);
        } else {
            layWiadomosc.setBackgroundResource(mBgResourceID_odd);
        }
    }

    public void clear() {
        lista.clear();
    }

    public ArrayList<Wiadomosc> getWiadomosci() {
        return lista;
    }

    private class SpanOnClickListener extends ClickableSpan  {
        String url;
        String author;

        SpanOnClickListener(String p_url, String p_author) {
            url = p_url;
            author = p_author;
        }

        @Override
        public void onClick(View v) {
            Intent l_intent = new Intent(mAct, PokazObrazekActivity.class);
            l_intent.putExtra("image_url", url);
            l_intent.putExtra("author", author);
            mAct.startActivity(l_intent);

        }
    }

    private Spannable setSpannableImgLink(Spannable txt, String obrazek, Integer id, String p_author) {
        Pattern mImgsPattern = Pattern.compile("\\[Obrazek\\ #" + id + "\\]");
        int start = 0;
        int end = 0;
        Matcher l_matcher = mImgsPattern.matcher(txt);
        while(l_matcher.find()) {
            start = l_matcher.start();
            end = l_matcher.end();
        }
        txt.setSpan(new SpanOnClickListener(obrazek, p_author), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return txt;
    }
}
