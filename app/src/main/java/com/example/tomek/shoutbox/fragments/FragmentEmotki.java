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

import com.example.tomek.shoutbox.DialogDodatki;
import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.adapters.EmotkiAdapter;

@SuppressLint("ValidFragment")
public class FragmentEmotki extends Fragment implements AdapterView.OnItemClickListener {
    private DialogDodatki.AddonSelectedListener listener;
    private EmotkiAdapter mAdapter;

    public FragmentEmotki() {}

    public FragmentEmotki(DialogDodatki.AddonSelectedListener listener) {
        this.listener = listener;
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
        if (mAdapter.getCount() >= position) {
            if (listener != null) {
                listener.smileySelected(mAdapter.getItem(position).toString());
            }
        }
    }
}
