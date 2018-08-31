package com.example.tomek.shoutbox.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tomek.shoutbox.DialogDodatki;
import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.adapters.AdapterTagi;

public class FragmentTagi extends Fragment implements AdapterView.OnItemClickListener {
    private DialogDodatki.AddonSelectedListener listener;
    private AdapterTagi mAdapter;

    public FragmentTagi() {}

    public void setListener(DialogDodatki.AddonSelectedListener listener) {
        this.listener = listener;
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
        if (listener != null) {
            listener.tagSelected(mAdapter.getItem(position).toString());
        }
    }
}
