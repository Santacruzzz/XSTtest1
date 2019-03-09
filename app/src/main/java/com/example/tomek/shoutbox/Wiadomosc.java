package com.example.tomek.shoutbox;

import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.text.style.QuoteSpan;
import android.view.View;

import com.example.tomek.shoutbox.activities.PokazObrazekActivity;
import com.example.tomek.shoutbox.activities.XstActivity;
import com.example.tomek.shoutbox.adapters.AdapterWiadomosci;
import com.example.tomek.shoutbox.utils.CustomQuoteSpan;
import com.example.tomek.shoutbox.utils.EmoticonsParser;
import com.example.tomek.shoutbox.utils.Typy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomek on 2017-10-19.
 */

public class Wiadomosc {
    private String autor, wiadomosc, data, avatar, source, sex, birthday;
    private int id, autorid, lajki = 0;
    private Typy.TypWiadomosci typWiadomosci;
    private Spannable spanMessage;
    ArrayList<String> obrazki, linki;
    XstApplication xstApp;
    private SimpleDateFormat format_data;
    private SimpleDateFormat format_godziny;
    private SimpleDateFormat format_daty;

    public Wiadomosc(XstApplication xstApplication, EmoticonsParser parser, JSONObject obj) {
        obrazki = new ArrayList<>();
        linki = new ArrayList<>();
        format_data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format_godziny = new SimpleDateFormat("HH:mm");
        format_daty = new SimpleDateFormat("dd.MM HH:mm");
        xstApp = xstApplication;
        try {
            typWiadomosci = Typy.TypWiadomosci.wiadomosc;
            setAutor(obj.getString("nickname"));
            setAutorId(obj.getInt("userID"));
            setAvatar(obj.getString("avatar"));
            setData(obj.getString("time"));
            setWiadomosc(obj.getString("content"));
            setLajki(obj.getInt("likes"));
            setId(obj.getInt("messageID"));

            JSONArray addons = obj.getJSONArray("addons");
            for (int i = 0; i < addons.length(); ++i) {
                JSONObject addon = addons.getJSONObject(i);
                if (addon.getString("type").equals("image")) {
                    obrazki.add(addon.getString("content"));
                } else if (addon.getString("type").equals("link")) {
                    linki.add(addon.getString("content"));
                }
            }
        } catch (JSONException ex) {

        }

        SpannableString spannableString = new SpannableString(Html.fromHtml(wiadomosc));
        int txtViewHeight = 42;
        spanMessage = parser.getSmiledText(spannableString, txtViewHeight);
        replaceQuoteSpans(spanMessage);
        if (obrazki.size() > 0) {
            int numer_obrazka = 1;
            for (String obrazek : obrazki) {
                spanMessage = setSpannableImgLink(spanMessage, obrazek, numer_obrazka, autor);
                numer_obrazka ++;
            }
        }
    }

    public Spannable getSpanMessage()
    {
        return spanMessage;
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
        txt.setSpan(new SpanOnClickListener(xstApp, obrazek, p_author), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return txt;
    }

    private class SpanOnClickListener extends ClickableSpan {
        String url;
        String author;
        XstApplication xstApp;

        SpanOnClickListener(XstApplication p_xstApp, String p_url, String p_author) {
            url = p_url;
            author = p_author;
            xstApp = p_xstApp;
        }

        @Override
        public void onClick(View v) {
            Intent l_intent = new Intent(xstApp, PokazObrazekActivity.class);
            l_intent.putExtra("image_url", url);
            l_intent.putExtra("author", author);
            xstApp.startActivity(l_intent);

        }
    }

    private void replaceQuoteSpans(Spannable spannable) {
        QuoteSpan[] quoteSpans = spannable.getSpans(0, spannable.length(), QuoteSpan.class);
        for (QuoteSpan quoteSpan : quoteSpans) {
            int start = spannable.getSpanStart(quoteSpan);
            int end = spannable.getSpanEnd(quoteSpan);
            int flags = spannable.getSpanFlags(quoteSpan);
            spannable.removeSpan(quoteSpan);
            spannable.setSpan(
                    new CustomQuoteSpan(
                            Color.parseColor("#22000000"),
                            Color.RED,
                            4,
                            15), start, end, flags);
        }
    }


    public Wiadomosc(Typy.TypWiadomosci typ) {
        typWiadomosci = typ;
    }

    private void setObrazki(ArrayList<String> obrazki) {
        this.obrazki = obrazki;
    }

    private void setLinki(ArrayList<String> linki) {
        this.linki = linki;
    }

    public void addLike() {
        this.lajki ++;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getWiadomosc() {
        return wiadomosc;
    }

    public void setWiadomosc(String wiadomosc) {
        this.wiadomosc = wiadomosc;
    }

    public String getRawDate() {
        return data;
    }

    public String getDate() {
        Date date;
        String ret = data;

        try {
            date = format_data.parse(data);

            if (DateUtils.isToday(date.getTime())) {
                // dzisiaj
                ret = format_godziny.format(date);
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DATE, 1);
                if (DateUtils.isToday(cal.getTime().getTime())) {
                    // wczoraj
                    ret = "wczoraj " + format_godziny.format(date);
                } else {
                    ret = format_daty.format(date);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLajki() {
        return lajki;
    }

    public void setLajki(int lajki) {
        this.lajki = lajki;
    }

    public ArrayList<String> getObrazki() {
        return obrazki;
    }

    public ArrayList<String> getLinki() {
        return linki;
    }

    public int getAutorId() {
        return autorid;
    }

    public void setAutorId(int autorid) {
        this.autorid = autorid;
    }

    public String getAvatar() {
        return Typy.URL_AVATAR + avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Typy.TypWiadomosci getTypWiadomosci() {
        return typWiadomosci;
    }
}
