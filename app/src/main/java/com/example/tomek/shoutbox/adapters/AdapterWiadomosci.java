package com.example.tomek.shoutbox.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.QuoteSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.Wiadomosc;
import com.example.tomek.shoutbox.activities.MainActivity;
import com.example.tomek.shoutbox.activities.PokazObrazekActivity;
import com.example.tomek.shoutbox.utils.CustomQuoteSpan;
import com.example.tomek.shoutbox.utils.EmoticonsParser;
import com.example.tomek.shoutbox.utils.Typy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;

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
        mAct = act;
        mBgResourceID_even = act.getThemeRecourceId(new int[]{R.attr.msgBackground});
        mBgResourceID_odd = act.getThemeRecourceId(new int[]{R.attr.msgBackground_odd});
        lista = mAct.getXstDatabase().getListaWiadomosci();
    }

    @Override
    public int getCount() {
        if (lista.size() == 0) return 0;
//        if (lista.get(lista.size() - 1).getTypWiadomosci() == Typy.TypWiadomosci.wiadomosc) {
//            lista.add(new Wiadomosc(Typy.TypWiadomosci.przycisk_pokaz_starsze));
//        }
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

        ViewHolder holder;

        if (row == null) {
            row = inflater.inflate(R.layout.wiadomosc_layout, null);
            holder = new ViewHolder();

            holder.layWiadomosc = row.findViewById(R.id.layoutWiadomosc);
            holder.wiadomosc = row.findViewById(R.id.v_wiadomosc);
            holder.autor = row.findViewById(R.id.v_nick);
            holder.date = row.findViewById(R.id.v_data);
            holder.lajki = row.findViewById(R.id.v_lajki);
            holder.img_like = row.findViewById(R.id.v_lajk_ikona);
            holder.avatar = row.findViewById(R.id.v_avatarOnline);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        setMessageBackground(pozycja, holder.layWiadomosc);
        Picasso.with(mAct.getApplicationContext()).load(mWiadomosc.getAvatar()).into(holder.avatar);
        holder.autor.setText(mWiadomosc.getAutor());
        setNasalizationFont(holder.autor);

        BetterLinkMovementMethod method =
                BetterLinkMovementMethod.linkify(Linkify.WEB_URLS, (ViewGroup) row);
        method.setOnLinkClickListener(new LinkClickListener(mWiadomosc));
        holder.wiadomosc.setText(mWiadomosc.getSpanMessage());

        holder.date.setText(mWiadomosc.getDate());
        setViewForLikes(mWiadomosc, holder.lajki, holder.img_like);
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

    private void setNasalizationFont(TextView autor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
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

    private static class ViewHolder {
        public ConstraintLayout layWiadomosc;
        public TextView wiadomosc;
        public TextView autor;
        public TextView date;
        public TextView lajki;
        public ImageView img_like;
        public ImageView avatar;
    }

    private class LinkClickListener implements BetterLinkMovementMethod.OnLinkClickListener {
        Wiadomosc wiadomosc;

        LinkClickListener(Wiadomosc wiad) {
            wiadomosc = wiad;
        }

        @Override
        public boolean onClick(TextView textView, String url) {
            Log.i("xst", "Klikniety link: " + url);
            if (isImgLink(url)) {
                int id = getImgId(url);
                ArrayList<String> obrazki = wiadomosc.getObrazki();
                Log.i("xst", "Klikniety id: " + id + ", obrazki: " + obrazki.toString());
                Intent l_intent = new Intent(mAct, PokazObrazekActivity.class);
                l_intent.putExtra("image_url", obrazki.get(id));
                l_intent.putExtra("author", wiadomosc.getAutor());
                mAct.startActivity(l_intent);
            } else {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setData(Uri.parse(url));
                mAct.startActivity(i);
            }


            return true;
        }

        private int getImgId(String url) {
            int id = 0;
            Pattern patt = Pattern.compile("^img://(\\d+)");
            Matcher matcher = patt.matcher(url);
            matcher.find();
            if (matcher.groupCount() == 1) {
                id = Integer.valueOf(matcher.group(1));
            }
            return id;
        }

        private boolean isImgLink(String url) {
            return IsMatch(url, "^img://.*");
        }

        private boolean IsMatch(String s, String pattern) {
            try {
                Pattern patt = Pattern.compile(pattern);
                Matcher matcher = patt.matcher(s);
                return matcher.matches();
            } catch (RuntimeException e) {
                return false;
            }
        }

    }
}
