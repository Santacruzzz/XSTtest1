package com.example.tomek.shoutbox.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.User;
import com.example.tomek.shoutbox.activities.IMainActivity;
import com.example.tomek.shoutbox.activities.MainActivity;
import com.example.tomek.shoutbox.activities.OnlineListener;
import com.example.tomek.shoutbox.adapters.AdapterOnline;

import java.util.ArrayList;

/**
 * Created by Tomek on 2017-11-05.
 */

public class FragmentOnline extends Fragment implements OnlineListener {

    private View mView;
    private ListView listViewOnline;
    private AdapterOnline adapterOnline;
    private ArrayList<User> arrayOnline;

    private MainActivity mAct;
    private IMainActivity mImain;

    public FragmentOnline() {
        arrayOnline = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAct = (MainActivity) context;
        mImain = mAct;
        adapterOnline = new AdapterOnline(mAct, mAct.getXstDatabase().getListaOnline());
        mAct.setOnlineListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_online, container, false);
        listViewOnline = mView.findViewById(R.id.listaOnline);
        listViewOnline.setAdapter(adapterOnline);
        adapterOnline.notifyDataSetChanged();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        odswiezOnline();
    }

    @Override
    public void odswiezOnline() {
        adapterOnline.odswiezOnline();
    }
}
