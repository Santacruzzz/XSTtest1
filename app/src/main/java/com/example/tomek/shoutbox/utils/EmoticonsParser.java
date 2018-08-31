package com.example.tomek.shoutbox.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.example.tomek.shoutbox.R;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomek on 2018-02-21.
 */

public class EmoticonsParser {
    private final Spannable.Factory spannableFactory;
    private final HashMap<Pattern, Integer> emoticons;
    private final HashMap<String, Integer> stringEmoticons;
    private final Context p_context;

    public EmoticonsParser(Context p_activity) {
        p_context = p_activity;
        spannableFactory = Spannable.Factory.getInstance();
        emoticons = new HashMap<>();
        stringEmoticons = new HashMap<>();
        addPattern(":)", R.drawable.smile1);
        addPattern(":-)", R.drawable.smile2);
        addPattern(":-D", R.drawable.comedy);
        addPattern(":D", R.drawable.happy);
        addPattern(";D", R.drawable.happy2);
        addPattern(";)", R.drawable.oczko);
        addPattern(":(", R.drawable.smutas);
        addPattern(":-(", R.drawable.smutas2);
        addPattern(":o", R.drawable.szok);
        addPattern(":O", R.drawable.szok);
        addPattern(":beer:", R.drawable.lajkbeer);
        addPattern(":like:", R.drawable.like);
        addPattern(":P", R.drawable.jezyk);
        addPattern(";p", R.drawable.jezyk2);
        addPattern(";P", R.drawable.jezyk);
        addPattern(":p", R.drawable.jezyk2);
        addPattern("8)", R.drawable.oksy);
        addPattern("xD", R.drawable.xd);
        addPattern(";(", R.drawable.cry);
        addPattern(";-(", R.drawable.cry2);
        addPattern("->", R.drawable.strzalka);
        addPattern(":serce:", R.drawable.serce);
        addPattern(":>", R.drawable.diabel);
        addPattern(":lol:", R.drawable.lol);
        addPattern(":yhy:", R.drawable.yhy);
        addPattern(":facepalm:", R.drawable.facepalm);
        addPattern("-.-", R.drawable.omg);
        addPattern(":|", R.drawable.zmieszany);
        addPattern(":troll:", R.drawable.troll);
        addPattern(":DD", R.drawable.vhappy);
        addPattern(":shy:", R.drawable.shy);
        addPattern(":3", R.drawable.kociryj);
        addPattern(":ave:", R.drawable.ave);
        addPattern(":*", R.drawable.kiss);
        addPattern(":small:", R.drawable.small);
        addPattern(":wat:", R.drawable.wat);
        addPattern(":noeye:", R.drawable.noeye);
        addPattern(":fuck:", R.drawable.fuck);
        addPattern(":rare:", R.drawable.rare);
        addPattern(":pepe:", R.drawable.pepe);
        addPattern(":doge:", R.drawable.doge);
        addPattern(":yds:", R.drawable.yds);
        addPattern(":coold:", R.drawable.coold);
        addPattern(":cool:", R.drawable.cool);
        addPattern("::x:", R.drawable.x);
        addPattern(":dayz:", R.drawable.dayz);
        addPattern(":mumble:", R.drawable.mumble);
        addPattern(":cmok:", R.drawable.cmok);
        addPattern(":uff:", R.drawable.uff);
        addPattern(":mm:", R.drawable.mm);
        addPattern(":wkurw:", R.drawable.wkurw);
        addPattern(":zly:", R.drawable.zly);
        addPattern("^^", R.drawable.vv);
    }

    public Spannable getSmiledText(CharSequence text, int txtViewHeight) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(spannable, txtViewHeight);
        return spannable;
    }

    private void addPattern(String smile, int resource) {
        emoticons.put(Pattern.compile(Pattern.quote(smile)), resource);
        if (!stringEmoticons.containsValue(resource)) {
            stringEmoticons.put(smile, resource);
        }
    }

    private void addSmiles(Spannable spannable, int txtViewHeight) {
        for (java.util.Map.Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), ImageSpan.class))
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else {
                        set = false;
                        break;
                    }
                if (set) {
                    Drawable smiley = p_context.getResources().getDrawable(entry.getValue());
                    smiley.setBounds(0, 0, txtViewHeight, txtViewHeight);
                    spannable.setSpan(new ImageSpan(smiley, ImageSpan.ALIGN_BOTTOM), matcher.start(), matcher.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                }
            }
        }
    }

    public HashMap<String, Integer> getEmoticonsMap() {
        return stringEmoticons;
    }
}
