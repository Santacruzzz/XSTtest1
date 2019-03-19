package pl.xsteam.santacruz.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import pl.xsteam.santacruz.DialogDodatki;
import pl.xsteam.santacruz.fragments.FragmentEmotki;
import pl.xsteam.santacruz.fragments.FragmentObrazkiZdysku;
import pl.xsteam.santacruz.fragments.FragmentTagi;

import java.util.ArrayList;

public class PagerAdapterTagiEmotki extends FragmentStatePagerAdapter {
    private DialogDodatki.AddonSelectedListener listener;
    private ArrayList<String> listaObrazkowZdysku;

    public PagerAdapterTagiEmotki(FragmentManager childFragmentManager, DialogDodatki.AddonSelectedListener listener, ArrayList<String> lista) {
        super(childFragmentManager);
        this.listener = listener;
        listaObrazkowZdysku = lista;
    }

    @Override
    public Fragment getItem(int position) {
        // Log.i("xst", "++++ getItem: " + position);

        switch (position) {
            case 0:
                FragmentEmotki fragmentEmotki = new FragmentEmotki();
                fragmentEmotki.setListener(listener);
                return fragmentEmotki;
            case 1:
                FragmentTagi fragmentTagi = new FragmentTagi();
                fragmentTagi.setListener(listener);
                return fragmentTagi;
            case 2:
                FragmentObrazkiZdysku fragmentObrazkiZdysku = new FragmentObrazkiZdysku();
                fragmentObrazkiZdysku.setListener(listener);
                fragmentObrazkiZdysku.setListaObrazkowZdysku(listaObrazkowZdysku);
                return fragmentObrazkiZdysku;
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Emotki";
            case 1:
                return "Tagi";
            case 2:
                return "Obrazki";
        }
        return "";
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void setLista(ArrayList<String> lista) {
        this.listaObrazkowZdysku = lista;
        notifyDataSetChanged();
    }
}