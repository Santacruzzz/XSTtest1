package com.example.tomek.shoutbox.utils;

import android.content.Context;
import android.text.Spannable;
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
        addPattern(":-)", R.drawable.smile1);
        addPattern(":-D", R.drawable.comedy);
        addPattern(":D", R.drawable.happy);
        addPattern(";D", R.drawable.happy);
        addPattern(";)", R.drawable.oczko);
        addPattern(";-)", R.drawable.oczko);
        addPattern(":(", R.drawable.smutas);
        addPattern(":-(", R.drawable.smutas);
        addPattern(":o", R.drawable.szok);
        addPattern(":O", R.drawable.szok);
        addPattern(":beer:", R.drawable.lajkbeer);
        addPattern(":like:", R.drawable.like);
        addPattern(":P", R.drawable.jezyk);
        addPattern(";p", R.drawable.jezyk);
        addPattern(";P", R.drawable.jezyk);
        addPattern(":p", R.drawable.jezyk);
        addPattern("8)", R.drawable.oksy);
        addPattern("xD", R.drawable.xd);
        addPattern(";(", R.drawable.cry);
        addPattern(";-(", R.drawable.cry);
        addPattern("->", R.drawable.strzalka);
        addPattern(":serce:", R.drawable.serce);
        addPattern(":>", R.drawable.diabel);
        addPattern(";>", R.drawable.diabel);
        addPattern(":lol:", R.drawable.lol);
        addPattern(":yhy:", R.drawable.yhy);
        addPattern(":facepalm:", R.drawable.facepalm);
        addPattern("-,-", R.drawable.omg);
        addPattern("-.-", R.drawable.omg);
        addPattern(":|", R.drawable.zmieszany);
        addPattern(":troll:", R.drawable.troll);
        addPattern("^^", R.drawable.vhappy);
        addPattern(":shy:", R.drawable.shy);
        addPattern(":3", R.drawable.kociryj);
        addPattern(":ave:", R.drawable.ave);
        addPattern(":*", R.drawable.kiss);
        addPattern(":small:", R.drawable.small);
        addPattern(":wat:", R.drawable.wat);
        addPattern(":noeye:", R.drawable.noeye);
        addPattern(":fuck:", R.drawable.fuck);
    }

    public Spannable getSmiledText(CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(spannable);
        return spannable;
    }

    private void addPattern(String smile, int resource) {
        emoticons.put(Pattern.compile(Pattern.quote(smile)), resource);
        stringEmoticons.put(smile, resource);
    }

    private boolean addSmiles(Spannable spannable) {
        boolean hasChanges = false;
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
                    hasChanges = true;
                    spannable.setSpan(new ImageSpan(p_context, entry.getValue()),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChanges;
    }

    public HashMap<String, Integer> getEmoticonsMap() {
        return stringEmoticons;
    }
}
