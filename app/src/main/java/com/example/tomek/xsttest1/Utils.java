package com.example.tomek.xsttest1;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by Tomek on 2018-02-04.
 */

public abstract class Utils {
    public static String zakodujWiadomosc(String tresc) {
        byte[] data = new byte[0];
        try {
            data = tresc.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
