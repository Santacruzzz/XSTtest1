package com.example.tomek.shoutbox.utils;

/**
 * Created by Tomek on 2017-10-19.
 */

@SuppressWarnings("ALL")
public class Typy {
    public static final String URL_PROTOCOL = "http";
    public static final String URL_API = URL_PROTOCOL + "://xs-team.pl/api/";
    public static final String URL_AVATAR = URL_PROTOCOL + "://xs-team.pl/images/avatars/";
    public static final String API_MSG_GET = URL_API + "msg/get";
    public static final String API_MSG_SEND = URL_API + "msg/send";
    public static final String API_MSG_LIKE = URL_API + "msg/like";
    public static final String TAG_GET_MSG = "getmsg";
    public static final String TAG_SEND_MSG = "sendmsg";
    public static final String TAG_LIKE_MSG = "likemsg";
    public static final String BROADCAST_INTERNET_OK = "jest_net";
    public static final String BROADCAST_ONLINE = "online_change";
    public static final String BROADCAST_NEW_MSG = "nowa_wiadomosc";
    public static final String BROADCAST_LIKE_MSG = "nowy_like";
    public static final String BROADCAST_ERROR = "error";
    public static final String PREFS_NAME = "xstprefs";
    public static final String PREFS_API_KEY = "pak";
    public static final String PREFS_LOGIN = "pl";
    public static final String PREFS_NICNKAME = "pn";
    public static final String API_ZALOGUJ = URL_API + "user/login";
    public static final Object TAG_ZALOGUJ = "zaloguj";
    public static final String PREFS_AVATAR = "avatar";
    public static final String PREFS_LAST_DATE = "lastdate";
    public static final String PREFS_MSGS = "msgs";
    public static final String PREFS_ONLINE = "onlineitems";

    public static final String PREFS_THEME = "theme";
    public static final String FRAGMENT_SHOUTBOX = "shoutbox";
    public static final String FRAGMENT_USTAWIENIA = "ustawienia";
    public static final String FRAGMENT_ZALOGUJ = "Zaloguj";

    public static final String STATE_MSG = "state_msg";
    public static final int REQUEST_ZALOGUJ = 100;
}
