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
import pl.xsteam.santacruz.adapters.EmotkiAdapter;

public class FragmentEmotki extends Fragment implements AdapterView.OnItemClickListener {
    private DialogDodatki.AddonSelectedListener listener;
    private EmotkiAdapter mAdapter;

    public FragmentEmotki() {}

    public void setListener(DialogDodatki.AddonSelectedListener listener) {
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
