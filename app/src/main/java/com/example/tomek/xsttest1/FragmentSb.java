package com.example.tomek.xsttest1;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Tomek on 2017-10-25.
 */

public class FragmentSb extends Fragment implements View.OnClickListener {
    private View mView;
    private ListView listViewWiadomosci;
    private ArrayList<Wiadomosc> arrayWiadomosci;
    private AdapterWiadomosci adapterWiadomosci;
    private ImageButton mBtnSend;
    private ImageButton mBtnCamera;
    private EditText mWiadomosc;

    private Activity mAct;
    private IMainActivity mImain;

    public FragmentSb() {
        arrayWiadomosci = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAct = (LayoutGlownyActivity) context;
        mImain = (IMainActivity) mAct;
        adapterWiadomosci = new AdapterWiadomosci(mAct, arrayWiadomosci);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layout_sb, container, false);
        listViewWiadomosci = mView.findViewById(R.id.listaWiadomosci);
        listViewWiadomosci.setAdapter(adapterWiadomosci);
        adapterWiadomosci.notifyDataSetChanged();
        mBtnSend = mView.findViewById(R.id.buttonWyslij);
        mBtnCamera = mView.findViewById(R.id.buttonCamera);
        mWiadomosc = mView.findViewById(R.id.editWyslij);

        mBtnSend.setOnClickListener(this);
        mBtnCamera.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void odswiezWiadomosci(ArrayList<Wiadomosc> nowe) {
            arrayWiadomosci.clear();
            arrayWiadomosci.addAll(nowe);
        if (adapterWiadomosci != null) {
            this.adapterWiadomosci.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (arrayWiadomosci.size() == 0) {
            odswiezWiadomosci(mImain.getWiadomosci());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonWyslij:
                wyslij_wiadomosc();
                break;

            case R.id.buttonCamera:
                break;
        }
    }

    public void wyslano_wiadomosc(boolean success) {
        mBtnSend.setEnabled(true);
        if (success) {
            mWiadomosc.setText("");
        }
    }

    private void wyslij_wiadomosc() {
        String wiadomosc = mWiadomosc.getText().toString();
        if (wiadomosc.length() > 0) {
            mBtnSend.setEnabled(false);
            mImain.wyslij_wiadomosc(wiadomosc);
        }
    }
}
