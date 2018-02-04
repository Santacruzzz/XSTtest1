package com.example.tomek.xsttest1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * Created by Tomek on 2017-11-05.
 */

class AdapterOnline extends BaseAdapter {

    ArrayList<OnlineItem> lista;
    Context context;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    IMainActivity imain;
    Activity mAct;

    public AdapterOnline(Activity act, ArrayList<OnlineItem> _list) {
        lista = _list;
        this.context = act.getApplicationContext();
        inflater = LayoutInflater.from(context);
        imain = (IMainActivity) act;
        imageLoader = imain.getImageLoader();
        mAct = act;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int i) {
        if (i >= 0 && i < lista.size()) {
            return lista.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        if (i >= 0 && i < lista.size()) {
            return lista.get(i).getUserid();
        }
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (inflater == null) {
            inflater = (LayoutInflater) mAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        View row = convertView;
        if (row == null) {
            row = inflater.inflate(R.layout.online_item, null);
        }
        TextView nick = row.findViewById(R.id.v_nick);
        TextView status = row.findViewById(R.id.statusOnline);
        OnlineItem mOnlineItem;

        try {
            mOnlineItem = lista.get(i);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        String str_av = Typy.URL_AVATAR + mOnlineItem.getAvatar();

        if (imageLoader == null) {
            imageLoader = imain.getImageLoader();
        }

        NetworkImageView avatar = row.findViewById(R.id.v_avatar);
        avatar.setImageUrl(str_av, imageLoader);
        nick.setText(mOnlineItem.getNick());

        status.setText(mOnlineItem.getTimeString());
        if (mOnlineItem.isOnline()) {
            int[] attr_color_nick = {R.attr.nickColor};
            nick.setTextColor(imain.getThemeColor(attr_color_nick));
            int[] attrs = {R.attr.onlineTextColor};
            status.setTextColor(imain.getThemeColor(attrs));
        } else {
            int[] attr_color_nick = {R.attr.offlineNickColor};
            nick.setTextColor(imain.getThemeColor(attr_color_nick));
            int[] attrs = {R.attr.offlineTextColor};
            status.setTextColor(imain.getThemeColor(attrs));
        }

        return row;
    }
}
