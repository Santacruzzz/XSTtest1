package pl.xsteam.santacruz.utils;

/**
 * Created by Tomek on 2017-10-19.
 */

@SuppressWarnings("ALL")
public class Typy
{
    public static final String URL_PROTOCOL = "http://";
    public static final String URL_BASE = "xs-team.pl";
    public static final String URL_API = URL_PROTOCOL + URL_BASE + "/api/";
    public static final String URL_AVATAR = URL_PROTOCOL + URL_BASE + "/images/avatars/";
    public static final String URL_OBRAZKI = URL_PROTOCOL + URL_BASE + "/uploads/";
    public static final String URL_UPLOAD = URL_PROTOCOL + URL_BASE + "/api/upload";

    public static final String API_ZALOGUJ = URL_API + "user/login";
    public static final String API_MSG_GET = URL_API + "msg/get";
    public static final String API_MSG_SEND = URL_API + "msg/send";
    public static final String API_MSG_LIKE = URL_API + "msg/like";
    public static final String API_GET_OBRAZKI = URL_API + "msg/getObrazki";
    public static final String API_DEL_OBRAZKI = URL_API + "msg/delObrazki";
    public static final String API_MSG_GET_MORE = URL_API + "msg/getOlder";

    public static final String TAG_GET_MSG = "getmsg";
    public static final String TAG_SEND_MSG = "sendmsg";
    public static final String TAG_LIKE_MSG = "likemsg";
    public static final Object TAG_ZALOGUJ = "zaloguj";

    public static final String BROADCAST_INTERNET_WROCIL = "wrocil_net";
    public static final String BROADCAST_INTERNET_OK = "jest_net";
    public static final String BROADCAST_ONLINE = "online_change";
    public static final String BROADCAST_NEW_MSG = "nowa_wiadomosc";
    public static final String BROADCAST_NEW_MONEY = "nowa_kasa";
    public static final String BROADCAST_LIKE_MSG = "nowy_like";
    public static final String BROADCAST_INTERNET_LOST = "error";
    public static final String BROADCAST_KONIEC_ODSWIEZANIA = "odswiezono";
    public static final String BROADCAST_UPDATE_AVAILABLE = "aktualizacja";

    public static final String PREFS_NAME = "xstprefs";
    public static final String PREFS_API_KEY = "pak";
    public static final String PREFS_LOGIN = "pl";
    public static final String PREFS_NICNKAME = "pn";
    public static final String PREFS_AVATAR = "avatar";
    public static final String PREFS_LAST_DATE = "lastdate";
    public static final String PREFS_MSGS = "msgs";
    public static final String PREFS_MONEY = "kasa";
    public static final String PREFS_ONLINE = "onlineitems";
    public static final String PREFS_OBRAZKI = "obrazki";
    public static final String PREFS_KB_SIZE = "kbsize";
    public static final String PREFS_USE_KB_SIZE = "useKbsize";
    public static final String PREFS_HIDE_KB_AFTER_SEND = "hideKbAfterSend";
    public static final String PREFS_OBRAZKI_LAST_DATE = "obrazki_last_date";
    public static final String PREFS_THEME = "theme";
    public static final String PREFS_OBRAZKI_Z_DYSKU = "obrazki_z_dysku";

    public static final String FRAGMENT_SHOUTBOX = "shoutbox";
    public static final String FRAGMENT_USTAWIENIA = "ustawienia";
    public static final String FRAGMENT_TS = "teamspeak";
    public static final String FRAGMENT_ZALOGUJ = "Zaloguj";
    public static final String FRAGMENT_MOJE_OBRAZKI = "obrazki";

    public static final int MSG_NOTIFICATION_ID = 69;
    public static final int UPDT_NOTIFICATION_ID = 70;
    public static final int REQUEST_ZALOGUJ = 100;
    public static final int REQUEST_PICK_IMAGE = 80;
    public static final int REQUEST_UPLOAD_IMAGE = 81;
    public static final int REQUEST_USTAWIENIA = 82;
    public static final int REQUEST_MOJE_OBRAZKI = 83;
    public static final int PERMISSION_REQUEST_CODE = 555;
    public static final int MAX_IMAGE_DIMENSION = 1200; // px

    public static final String POBIERZ_STARSZE = "pobierz_starsze";

    public static final String STATE_MSG = "state_msg";
    public static final int STATE_ONLINE = 1;
    public static final int STATE_OFFLINE = 2;
    public static final String NOTIF_CHANNEL_MSG_ID = "xstmsgchannel";
    public static final String NOTIF_CHANNEL_UPDT_ID = "xstupdtchannel";
    public static final String APP_VERSION_ON_SERVER = "appVersionOnServer";
    public static final String BROADCAST_NEW_MSG_OLDER = "pobranoStarsze";

    public static final String responseGetMessages = "getMessages";
    public static final String responseOlderMessages = "olderMessages";
    public static final String responseLikeMessage = "likeMessage";
    public static final String responseSendMessage = "sendMessage";
    public static final String responseGetAppVersion = "getAppVersion";
    public static final String responseGetImages = "getImages";
    public static final String responseDeleteImage = "deleteImage";

    public enum ServiceState
    {
        state_inactive,
        state_onResume,
        state_onPause,
        state_wylogowano,
        state_wymusOdswiezenie,
        state_odswiez,
        state_blad_polaczenia;
    }

    public enum ServiceRequest
    {
        request_wymusOdswiezanie,
        request_lajkuj,
        request_wyslijWiadomosc,
        request_none;

    }

    public enum TypWiadomosci
    {
        wiadomosc,
        przycisk_pokaz_starsze;
    }
}
