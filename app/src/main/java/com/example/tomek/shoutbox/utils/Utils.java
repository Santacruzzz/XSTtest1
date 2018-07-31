package com.example.tomek.shoutbox.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;

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

    public static Date getDateFromString(String p_date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss"); // 2015-04-07 18:20:26
        Date date = new Date();
        try {
            return dateFormat.parse(p_date);
        } catch (ParseException ex) {
            return new Date();
        }
    }
}
