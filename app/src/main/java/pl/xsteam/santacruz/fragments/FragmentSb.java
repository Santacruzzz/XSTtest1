package pl.xsteam.santacruz.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import pl.xsteam.santacruz.DialogDodatki;
import pl.xsteam.santacruz.R;
import pl.xsteam.santacruz.Wiadomosc;
import pl.xsteam.santacruz.activities.IMainActivity;
import pl.xsteam.santacruz.activities.IPermission;
import pl.xsteam.santacruz.activities.MainActivity;
import pl.xsteam.santacruz.activities.SbListener;
import pl.xsteam.santacruz.adapters.AdapterWiadomosci;
import pl.xsteam.santacruz.utils.ImagesFromGallery;
import pl.xsteam.santacruz.utils.Typy;
import pl.xsteam.santacruz.utils.Utils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Tomek on 2017-10-25.
 */

public class FragmentSb extends Fragment implements
        View.OnClickListener,
        ListView.OnScrollListener,
        SwipeRefreshLayout.OnRefreshListener,
        DialogDodatki.AddonSelectedListener,
        ImagesFromGallery.ImageFoundListener,
        DialogDodatki.DialogListener,
        SbListener,
        ListView.OnItemClickListener {

    private ListView listViewWiadomosci;
    private AdapterWiadomosci adapterWiadomosci;
    private ImageButton mBtnSend;
    private EditText mWiadomosc;
    private TextView textMessageCount;
    private SwipeRefreshLayout mRefreshLayout;
    private MainActivity mAct;
    private IMainActivity mImain;
    private DialogDodatki dodatkiDialog;
    private IPermission iPermission;
    private boolean isDialogShown;
    private boolean pobieramStarsze;
    private FloatingActionButton fabLoadMore;
    private int preLast;

    public FragmentSb() {
        dodatkiDialog = null;
        isDialogShown = false;
        pobieramStarsze = false;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mAct = (MainActivity) context;
        mImain = mAct;
        iPermission = mAct;
        adapterWiadomosci = new AdapterWiadomosci(mAct);
        isDialogShown = false;
        pobieramStarsze = false;
        mAct.setSbListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.w("xst", "FragmentSb: onCreateView");
        final View mView = inflater.inflate(R.layout.layout_sb, container, false);
        listViewWiadomosci = mView.findViewById(R.id.listaWiadomosci);
        listViewWiadomosci.setAdapter(adapterWiadomosci);
        listViewWiadomosci.setOnScrollListener(this);
        adapterWiadomosci.notifyDataSetChanged();
        mBtnSend = mView.findViewById(R.id.buttonWyslij);
        ImageButton btnDodatki = mView.findViewById(R.id.btnDodatki);
        mWiadomosc = mView.findViewById(R.id.editWyslij);
        mRefreshLayout = mView.findViewById(R.id.swiperefresh);
        textMessageCount = mView.findViewById(R.id.textMessageCount);
        listViewWiadomosci.setOnItemClickListener(this);
        fabLoadMore = mView.findViewById(R.id.fabLoadMore);
        fabLoadMore.setOnClickListener(this);
        fabLoadMore.hide();

        mWiadomosc.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(mWiadomosc.getLineCount() > 1) {
                    textMessageCount.setVisibility(View.VISIBLE);
                    textMessageCount.setText(String.format(Locale.ENGLISH, "%d/500", s.length()));
                } else {
                    textMessageCount.setVisibility(View.INVISIBLE);
                }
            }
        });

        registerForContextMenu(listViewWiadomosci);

        mBtnSend.setOnClickListener(this);
        btnDodatki.setOnClickListener(this);

        mRefreshLayout.setOnRefreshListener(this);
        return mView;
    }

    public static Intent getPickImageIntent()
    {
        Intent chooserIntent = new Intent();
        chooserIntent.setType("image/*");
        chooserIntent.setAction(Intent.ACTION_GET_CONTENT);
        return chooserIntent;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(Typy.STATE_MSG, mWiadomosc.getText().toString());
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.buttonWyslij:
                wyslij_wiadomosc();
                break;

            case R.id.btnDodatki:
                pokazDialogDodatki();
                break;

            case R.id.fabLoadMore:
                getMoreMessages();
                break;
        }
    }

    @Override
    public void dismissDialog()
    {
        if (dodatkiDialog != null) {
            dodatkiDialog.dismiss();
        }
    }

    @Override
    public void pokazDialogDodatki()
    {
        if (isDialogShown) {
            return;
        }

        if (mAct.haveWritePermission()) {
            Log.i("xst", "FragmentSb: Mam prawa do plikow");
            wczytajObrazkiZdysku();
        } else {
            mAct.requestPermission();
            return;
        }

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        dodatkiDialog = DialogDodatki.newInstance(mAct);
        dodatkiDialog.setAddonSelectedListener(this);
        dodatkiDialog.setDialogListeer(this);
        dodatkiDialog.show(ft, "dialog");
        isDialogShown = true;
    }

    @Override
    public void pobranoStarsze()
    {
        fabLoadMore.hide();
        pobieramStarsze = false;
    }

    private void wczytajObrazkiZdysku()
    {
        ImagesFromGallery imagesFromGallery = new ImagesFromGallery(this);
        imagesFromGallery.execute(mAct.getApplicationContext());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listaWiadomosci) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            ArrayList<Wiadomosc> arrayWiadomosci = adapterWiadomosci.getWiadomosci();
            Log.i("xst", "FragmentSb: onCreateContextMenu - acmi.position: " + acmi.position + "  arrayWiadomosci.size():" +  arrayWiadomosci.size());
            if (acmi.position >= adapterWiadomosci.getWiadomosci().size() - 1) {
                return;
            }
            Wiadomosc obj = adapterWiadomosci.getWiadomosci().get(acmi.position);

            menu.setHeaderTitle("Wybierz akcję");
            menu.add(0, 99, 0, "Lubię to!"); //groupId, itemId, order, title
            menu.add(0, 100, 1, "Skopiuj treść"); //groupId, itemId, order, title
            menu.add(0, 101, 2, "Cytuj"); //groupId, itemId, order, title
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ArrayList<Wiadomosc> arrayWiadomosci = adapterWiadomosci.getWiadomosci();
        Wiadomosc wiadomosc = arrayWiadomosci.get(info.position);
        View view = getMessageViewFromPosition(info.position);
        if (view == null)
        {
            return false;
        }
        TextView msgContentView = view.findViewById(R.id.v_wiadomosc);
        TextView msgNickView = view.findViewById(R.id.v_nick);

        switch (item.getItemId())
        {
            case 99:
                mImain.lajkujWiadomosc(wiadomosc.getId(), info.position);
                break;
            case 100:
                Utils.copyToClipboard(mAct, msgContentView.getText().toString(), "Skopiowano treść");
                break;
            case 101:
                wstawCytat(msgContentView.getText().toString(), msgNickView.getText().toString());
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void wyslano_wiadomosc(boolean success) {
        mBtnSend.setEnabled(true);
        if (success) {
            mWiadomosc.setText("");
            if (mAct.getSharedPrefs().getBoolean(Typy.PREFS_HIDE_KB_AFTER_SEND, true)) {
                hideKeyboard(mAct);
            }
        }
    }

    private void wyslij_wiadomosc() {
        String wiadomosc = mWiadomosc.getText().toString();
        if (wiadomosc.length() > 0) {
            mBtnSend.setEnabled(false);
            mImain.sendMessage(wiadomosc);
        }
    }

    @Override
    public void polajkowanoWiadomosc(int msgid, int position) {
        View row = getMessageViewFromPosition(position);
        ArrayList<Wiadomosc> arrayWiadomosci = adapterWiadomosci.getWiadomosci();
        Wiadomosc w = arrayWiadomosci.get(position);
        if (row == null) {
            return;
        }

        TextView ilosc_lajkow = row.findViewById(R.id.v_lajki);
        ilosc_lajkow.setText(String.valueOf(w.getLajki()));
        ImageView ikona_lajkow = row.findViewById(R.id.v_lajk_ikona);
        if (ikona_lajkow.getVisibility() != View.VISIBLE) {
            ikona_lajkow.setVisibility(View.VISIBLE);
        }
    }

    private View getMessageViewFromPosition(int position) {
        int firstPosition = listViewWiadomosci.getFirstVisiblePosition() - listViewWiadomosci.getHeaderViewsCount();
        return listViewWiadomosci.getChildAt(position - firstPosition);
    }

    @Override
    public void onRefresh() {
        mImain.odswiezWiadomosci();
        Log.i("xst", "ragmentSb: Odświeżam wiadomosci");
    }

    @Override
    public void anulujOdswiezanie() {
        Log.i("xst", "FragmentSb: Anuluje odświeżanie");
        if (mRefreshLayout != null) {
            mRefreshLayout.setRefreshing(false);
        } else {
            Log.e("xst", "FragmentSb: refreszLajout jest pust!");
        }
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void smileySelected(String smiley) {
        mWiadomosc.append(smiley);
    }

    @Override
    public void tagSelected(String tag) {
        mWiadomosc.append(tag);
    }

    @Override
    public void pickImageSelected() {
        if (iPermission.haveWritePermission()) {
            Intent l_intent = getPickImageIntent();
            mAct.startActivityForResult(Intent.createChooser(l_intent, "Wybierz obraz"), Typy.REQUEST_PICK_IMAGE);
        } else {
            iPermission.requestPermission();
        }
    }

    @Override
    public void imageSelected(String path) {
        mAct.imageSelectedToUpload(path);
    }

    public void wstawLinkObrazka(String url) {
        mWiadomosc.setText(String.format("%s[img]%s[/img]", mWiadomosc.getText(), url));
    }

    public void wstawCytat(String txt, String author) {
        mWiadomosc.setText(String.format("%s[quote]%s[/quote=%s]", mWiadomosc.getText(), txt, author));
    }

    @Override
    public void imagesFound(ArrayList<String> lista) {
        Utils.saveArrayList(mAct.getSharedPrefs(), lista, Typy.PREFS_OBRAZKI_Z_DYSKU);
        dodatkiDialog.imagesFound(lista);
    }

    @Override
    public void noImages() {
        mAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mAct.getApplicationContext(), "Brak obrazków do załadowania", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void dialogDismissed() {
        isDialogShown = false;
    }

    @Override
    public void odswiezWiadomosci() {
        adapterWiadomosci.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        if(adapterWiadomosci.getWiadomosci().size() - 1 == i) {
//            if (pobieramStarsze) {
//                return;
//            }
//            pobieramStarsze = true;
//            mAct.setSbListener(this);
//            mAct.runServiceCommand(Typy.POBIERZ_STARSZE);
//        }
    }

    public void getMoreMessages()
    {
        if (pobieramStarsze) {
            return;
        }
        pobieramStarsze = true;
        mAct.setSbListener(this);
        mAct.runServiceCommand(Typy.POBIERZ_STARSZE);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (fabLoadMore == null) return;
        switch(view.getId())
        {
            case R.id.listaWiadomosci:

                // Make your calculation stuff here. You have all your
                // needed info from the parameters of this function.

                // Sample calculation to determine if the last
                // item is fully visible.
                final int lastItem = firstVisibleItem + visibleItemCount;

                if(lastItem == totalItemCount)
                {
                    if(preLast!=lastItem)
                    {
                        //to avoid multiple calls for last item
                        Log.d("Last", "Last");
                        preLast = lastItem;
                    }
                    showFabOnScroll();
                }
                else
                {
                    hideFabOnScroll();
                }
        }
    }

    private void hideFabOnScroll() {
        if (pobieramStarsze) return;
        fabLoadMore.hide();
    }

    private void showFabOnScroll() {
        if (pobieramStarsze) return;
        fabLoadMore.show();
    }
}


