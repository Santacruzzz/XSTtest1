package com.example.tomek.xsttest1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Tomek on 2017-10-25.
 */

public class FragmentSb extends Fragment implements View.OnClickListener, ListView.OnItemLongClickListener, ListView.OnItemClickListener {
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

        registerForContextMenu(listViewWiadomosci);

        mBtnSend.setOnClickListener(this);
        mBtnCamera.setOnClickListener(this);
        listViewWiadomosci.setOnItemClickListener(this);
        //listViewWiadomosci.setOnItemLongClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Wiadomosc item = arrayWiadomosci.get(position);
        ArrayList<String> linki = item.getLinki();
        if (linki.isEmpty()) {
            //Toast.makeText(mAct,"Brak linkow", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(mAct, "Są linki: " + linki.size(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(mAct,"Jeszcze nie działa", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listaWiadomosci) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Wiadomosc obj = arrayWiadomosci.get(acmi.position);

            menu.setHeaderTitle("Wybierz akcję");

            menu.add(0, 99, 0, "Lubię to!"); //groupId, itemId, order, title

            int id_linka = 0;
            for (String link : obj.getLinki()) {
                if (link.length() > 30) {
                    link = link.substring(0, 25) + "...";
                }
                menu.add(1, id_linka, id_linka, "Link " + (id_linka + 1) + ": " + link);
                id_linka++;
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Wiadomosc obj = arrayWiadomosci.get(info.position);
        int l_id = item.getItemId();
        if (l_id == 99) {
            mImain.lajkujWiadomosc(obj.getId());
        } else {
            ArrayList<String> l_linki = obj.getLinki();
            if (l_linki.size() > 0) {
                if (l_id <= l_linki.size()) {
                    String l_url = l_linki.get(l_id);
                    if (!l_url.startsWith("http")) {
                        l_url = "http://" + l_url;
                    }
                    Intent l_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(l_url));
                    startActivity(l_intent);
                }
            }
        }
        return true;
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

    public void polajkowanoWiadomosc(int msgid) {
        for (Wiadomosc w : arrayWiadomosci) {
            if (w.getId() == msgid) {
                w.addLike();
                adapterWiadomosci.notifyDataSetChanged();
                return;
            }
        }
    }
}
