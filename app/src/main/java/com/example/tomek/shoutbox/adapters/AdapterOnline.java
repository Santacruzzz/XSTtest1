package com.example.tomek.shoutbox.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tomek.shoutbox.activities.IMainActivity;
import com.example.tomek.shoutbox.OnlineItem;
import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.utils.Typy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Tomek on 2017-11-05.
 */

public class AdapterOnline extends BaseAdapter {

    private ArrayList<OnlineItem> lista;
    private LayoutInflater inflater;
    private IMainActivity imain;
    private Activity mAct;

    public AdapterOnline(Activity act, ArrayList<OnlineItem> _list) {
        lista = _list;
        Context context = act.getApplicationContext();
        inflater = LayoutInflater.from(context);
        imain = (IMainActivity) act;
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
            assert inflater != null;
            row = inflater.inflate(R.layout.online_item, viewGroup, false);
        }
        TextView nick = row.findViewById(R.id.v_nick);
        TextView status = row.findViewById(R.id.statusOnline);
        TextView txtPlatforma = row.findViewById(R.id.v_txtPlatforma);
        TextView txtUa = row.findViewById(R.id.v_txtUa);
        ImageView platformaImage = row.findViewById(R.id.v_platforma);
        OnlineItem mOnlineItem;

        try {
            mOnlineItem = lista.get(i);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        String str_av = Typy.URL_AVATAR + mOnlineItem.getAvatar();

        ImageView avatar = row.findViewById(R.id.v_avatarOnline);
        Picasso.with(mAct).load(mOnlineItem.getAvatarUrl()).into(avatar);

        nick.setText(mOnlineItem.getNick());
        final RelativeLayout layOnline = row.findViewById(R.id.layOnline);
        status.setText(mOnlineItem.getTimeString());

        row.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layOnline.getBackground().setHotspot(event.getX(), event.getY());
                layOnline.performClick();
                return(false);
            }
        });


        switch (mOnlineItem.getPlatform()) {
            case "Windows":
                platformaImage.setImageResource(R.drawable.windows);
                break;
            case "Android":
                platformaImage.setImageResource(R.drawable.android);
                break;
            case "Linux":
                platformaImage.setImageResource(R.drawable.linux);
                break;
            default:
                platformaImage.setImageResource(R.drawable.xst);
                break;
        }
        txtPlatforma.setText(mOnlineItem.getOs());
        txtUa.setText(mOnlineItem.getUa());

        if (mOnlineItem.isOnline()) {
            int[] attr_color_nick = {R.attr.nickColor};
            nick.setTextColor(imain.getThemeColor(attr_color_nick));
            int[] attrs = {R.attr.onlineTextColor};
            status.setTextColor(imain.getThemeColor(attrs));
            avatar.setAlpha(1f);
            platformaImage.setAlpha(1f);
        } else {
            int[] attr_color_nick = {R.attr.offlineNickColor};
            nick.setTextColor(imain.getThemeColor(attr_color_nick));
            int[] attrs = {R.attr.offlineTextColor};
            status.setTextColor(imain.getThemeColor(attrs));
            avatar.setAlpha(0.4f);
            platformaImage.setAlpha(0.4f);
        }

        return row;
    }
}
