package com.example.tomek.shoutbox.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.adapters.AdapterTagi;
import com.example.tomek.shoutbox.fragments.FragmentSb;

@SuppressLint("ValidFragment")
public class FragmentTagi extends Fragment implements AdapterView.OnItemClickListener {

    AdapterTagi mAdapter;
    FragmentSb mSb;

    public FragmentTagi(FragmentSb fragmentSb) {
        mSb = fragmentSb;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tagi, container, false);
        ListView list = rootView.findViewById(R.id.listTagi);
        list.setOnItemClickListener(this);
        list.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAdapter = new AdapterTagi(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSb.kliknietoTag(mAdapter.getItem(position).toString());
    }
}
