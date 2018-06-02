package com.example.tomek.shoutbox.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tomek.shoutbox.NavItem;
import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Tomek on 2017-11-08.
 */

public class DrawerListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<NavItem> mNavItems;

    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
        mContext = context;
        mNavItems = navItems;
    }

    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        }
        else {
            view = convertView;
        }

        TextView titleView = view.findViewById(R.id.title);
        ImageView iconView = view.findViewById(R.id.icon);

        titleView.setText( Utils.capitalizeFirstLetter(mNavItems.get(position).mTitle) );
        iconView.setImageResource(mNavItems.get(position).mIcon);

        return view;
    }
}