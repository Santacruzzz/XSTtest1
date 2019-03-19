package pl.xsteam.santacruz.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import pl.xsteam.santacruz.DialogDodatki;
import pl.xsteam.santacruz.R;
import pl.xsteam.santacruz.adapters.AdapterObrazkowZdysku;

import java.util.ArrayList;

public class FragmentObrazkiZdysku extends Fragment implements AdapterView.OnItemClickListener {
    private DialogDodatki.AddonSelectedListener listener;
    private AdapterObrazkowZdysku mAdapter;
    private ArrayList<String> listaObrazkowZdysku;

    public FragmentObrazkiZdysku() {
    }

    public void setListener(DialogDodatki.AddonSelectedListener listener) {
        this.listener = listener;
    }

    public void setListaObrazkowZdysku(ArrayList<String> lista) {
        listaObrazkowZdysku = lista;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_obrazki_z_dysku, container, false);
        GridView grid = rootView.findViewById(R.id.gridObrazkiZdysku);
        grid.setOnItemClickListener(this);
        grid.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAdapter = new AdapterObrazkowZdysku(context, listaObrazkowZdysku);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAdapter.getCount() >= position) {
            if (listener != null) {
                listener.imageSelected((String) mAdapter.getItem(position));
            }
        }
    }
}