package com.example.tomek.shoutbox.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tomek.shoutbox.R;
import com.example.tomek.shoutbox.Wiadomosc;
import com.example.tomek.shoutbox.activities.IMainActivity;
import com.example.tomek.shoutbox.activities.LayoutGlownyActivity;
import com.example.tomek.shoutbox.adapters.AdapterWiadomosci;
import com.example.tomek.shoutbox.adapters.PagerAdapterTagiEmotki;
import com.example.tomek.shoutbox.utils.Typy;

import java.util.ArrayList;

/**
 * Created by Tomek on 2017-10-25.
 */

public class FragmentSb extends Fragment implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        ViewPager.OnPageChangeListener {

    private ListView listViewWiadomosci;
    private ArrayList<Wiadomosc> arrayWiadomosci;
    private AdapterWiadomosci adapterWiadomosci;
    private ImageButton mBtnSend;
    private ImageButton mBtnExpand;
    private EditText mWiadomosc;
    private SwipeRefreshLayout mRefreshLayout;
    private LinearLayout mLayoutPrzyciski;
    private ViewPager pagerTagiEmotki;
    private Integer keyboradSize;
    private boolean viewPagerVisible;

    private Activity mAct;

    private IMainActivity mImain;

    public FragmentSb() {
        viewPagerVisible = false;
        arrayWiadomosci = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAct = (LayoutGlownyActivity) context;
        mImain = (IMainActivity) mAct;
        adapterWiadomosci = new AdapterWiadomosci(mAct, arrayWiadomosci);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View mView = inflater.inflate(R.layout.layout_sb, container, false);
        listViewWiadomosci = mView.findViewById(R.id.listaWiadomosci);
        listViewWiadomosci.setAdapter(adapterWiadomosci);
        adapterWiadomosci.notifyDataSetChanged();
        mBtnSend = mView.findViewById(R.id.buttonWyslij);
        ImageButton mBtnAddImage = mView.findViewById(R.id.buttonAddImage);
        mBtnExpand = mView.findViewById(R.id.btnExpand);
        ImageButton mBtnTagi = mView.findViewById(R.id.buttonTags);
        ImageButton mBtnEmotki = mView.findViewById(R.id.buttonEmotki);
        mWiadomosc = mView.findViewById(R.id.editWyslij);
        mRefreshLayout = mView.findViewById(R.id.swiperefresh);
        mLayoutPrzyciski = mView.findViewById(R.id.layoutPrzyciski);

        pagerTagiEmotki = mView.findViewById(R.id.viewPagerTagiEmotki);
        TabLayout mTabsTagiEmotki = mView.findViewById(R.id.tabsTagiEmotki);
        PagerAdapterTagiEmotki pagerAdapterTagiEmotki = new PagerAdapterTagiEmotki(getChildFragmentManager(), mAct);
        mTabsTagiEmotki.setupWithViewPager(pagerTagiEmotki);
        pagerTagiEmotki.setAdapter(pagerAdapterTagiEmotki);
        mTabsTagiEmotki.getTabAt(0).setIcon(android.R.drawable.ic_menu_more);
        mTabsTagiEmotki.getTabAt(1).setIcon(R.drawable.smile1);

        registerForContextMenu(listViewWiadomosci);

        mBtnSend.setOnClickListener(this);
        mBtnAddImage.setOnClickListener(this);
        mBtnExpand.setOnClickListener(this);
        mBtnTagi.setOnClickListener(this);
        mBtnEmotki.setOnClickListener(this);

        mRefreshLayout.setOnRefreshListener(this);
        pagerTagiEmotki.addOnPageChangeListener(this);
        pagerTagiEmotki.setVisibility(View.GONE);

        mWiadomosc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (pagerTagiEmotki.getVisibility() == View.VISIBLE) {
                        hideViewPager();
                    }
                }
            }
        });

        mView.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                Rect r = new Rect(); mView.getWindowVisibleDisplayFrame(r);
                int screenHeight = mView.getRootView().getHeight();
                keyboradSize = screenHeight - (r.bottom - r.top);
                Log.i("xst", "FragmentSB: keyboardSize=" + keyboradSize);
            }
        });

        return mView;
    }

    public static Intent getPickImageIntent() {
        Intent chooserIntent = new Intent();
        chooserIntent.setType("image/*");
        chooserIntent.setAction(Intent.ACTION_GET_CONTENT);
        return chooserIntent;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Typy.STATE_MSG, mWiadomosc.getText().toString());
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
            mRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
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

            case R.id.buttonAddImage:
                Intent l_intent = getPickImageIntent();
                mAct.startActivityForResult(Intent.createChooser(l_intent, "Wybierz obraz"), Typy.REQUEST_PICK_IMAGE);
                break;

            case R.id.btnExpand:
                mWiadomosc.clearFocus();
                hideKeyboard(mAct);
                showViewPager();
                break;
        }
    }

    public boolean isViewPagerVisible() { return viewPagerVisible; }

    private void showViewPager() {
        viewPagerVisible = true;
        mBtnExpand.setVisibility(View.GONE);
        pagerTagiEmotki.setVisibility(View.VISIBLE);
        mLayoutPrzyciski.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listaWiadomosci) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Wiadomosc obj = arrayWiadomosci.get(acmi.position);

            menu.setHeaderTitle("Wybierz akcję");

            menu.add(0, 99, 0, "Lubię to!"); //groupId, itemId, order, title

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Wiadomosc obj = arrayWiadomosci.get(info.position);
        int l_id = item.getItemId();
        if (l_id == 99) {
            mImain.lajkujWiadomosc(obj.getId(), info.position);
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
            hideViewPager();
            hideKeyboard(mAct);
        }
    }

    private void wyslij_wiadomosc() {
        String wiadomosc = mWiadomosc.getText().toString();
        if (wiadomosc.length() > 0) {
            mBtnSend.setEnabled(false);
            mImain.wyslij_wiadomosc(wiadomosc);
        }
    }

    public void polajkowanoWiadomosc(int msgid, int position) {
        Log.i("xst", "--- Fragment sb: zwiekszam like dla wiadomosci: " + msgid);
        int firstPosition = listViewWiadomosci.getFirstVisiblePosition() - listViewWiadomosci.getHeaderViewsCount();
        Wiadomosc w = arrayWiadomosci.get(position);
        w.addLike();
        View row = listViewWiadomosci.getChildAt(position - firstPosition);
        if (row == null) {
            return;
        }
        if ((int)row.getTag() != msgid) {
            return;
        }
        Log.i("xst", "tag: " + row.getTag());
        TextView ilosc_lajkow = row.findViewById(R.id.v_lajki);
        ilosc_lajkow.setText(String.valueOf(w.getLajki()));
        ImageView ikona_lajkow = row.findViewById(R.id.v_lajk_ikona);
        if (ikona_lajkow.getVisibility() != View.VISIBLE) {
            ikona_lajkow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        mImain.odswiezWiadomosci();
        Log.i("xst", "--- Fragment SB: Odświeżam wiadomosci");
    }

    public void bladOdswiezania() {
        mRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            Log.i("xst", "Chowam klawe!!!");
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void kliknietoEmotke(String pattern) {
        mWiadomosc.append(pattern);
    }

    public void kliknietoTag(String item) {
        mWiadomosc.append(item);
    }

    public void hideViewPager() {
        viewPagerVisible = false;
        mBtnExpand.setVisibility(View.VISIBLE);
        pagerTagiEmotki.setVisibility(View.GONE);
        mLayoutPrzyciski.setVisibility(View.GONE);
    }
}


