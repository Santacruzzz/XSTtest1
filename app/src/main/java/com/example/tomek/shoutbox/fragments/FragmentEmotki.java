package com.example.tomek.shoutbox.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.adapters.EmotkiAdapter;

public class FragmentEmotki extends Fragment implements AdapterView.OnItemClickListener {

    EmotkiAdapter mAdapter;
    FragmentSb mSb;

    public FragmentEmotki(FragmentSb sb) {
        mSb = sb;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emotki, container, false);
        GridView grid = rootView.findViewById(R.id.gridEmotki);
        grid.setOnItemClickListener(this);
        grid.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAdapter = new EmotkiAdapter(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAdapter != null && mSb != null) {
            if (mAdapter.getCount() >= position) {
                mSb.kliknietoEmotke(mAdapter.getItem(position).toString());
            }
        }
    }
}
