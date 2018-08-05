package com.example.tomek.shoutbox;

import com.example.tomek.shoutbox.utils.Typy;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tomek on 2017-11-09.
 */

public class OnlineItem {
    private String nick;
    private String avatar;
    private String os;
    private String platform;
    private String ua;
    private String timeString;
    private boolean online;
    private int laston;
    private int alive;
    private long userid;

    public OnlineItem(JSONObject item) {
        try {
            this.nick = item.getString("nickname");
            this.avatar = item.getString("avatar");
            this.os = item.getString("os_name");
            this.platform = item.getString("platforma");
            this.laston = item.getInt("laston");
            this.alive = item.getInt("alive");
            this.online = item.getBoolean("isOnline");
            this.userid = item.getLong("userID");
            this.ua = item.getString("ua_name");
            this.timeString = item.getString("timeString");

            if (this.avatar.equals("")) {
                avatar = "noavatar.gif";
            }
        } catch (JSONException ignored) {

        }
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getAvatarUrl() {
        return Typy.URL_AVATAR + getAvatar();
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getLaston() {
        return laston;
    }

    public void setLaston(int laston) {
        this.laston = laston;
    }

    public int getAlive() {
        return alive;
    }

    public void setAlive(int alive) {
        this.alive = alive;
    }

    public long getUserid() {
        return userid;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }
}
