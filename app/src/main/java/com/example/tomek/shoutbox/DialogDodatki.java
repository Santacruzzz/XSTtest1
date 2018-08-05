package com.example.tomek.shoutbox;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.tomek.shoutbox.adapters.PagerAdapterTagiEmotki;

public class DialogDodatki extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dodatki_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton mBtnAddImage = view.findViewById(R.id.buttonAddImage);

        ViewPager pagerTagiEmotki = view.findViewById(R.id.viewPagerTagiEmotki);
        TabLayout mTabsTagiEmotki = view.findViewById(R.id.tabsTagiEmotki);
        PagerAdapterTagiEmotki pagerAdapterTagiEmotki = new PagerAdapterTagiEmotki(getChildFragmentManager());
        mTabsTagiEmotki.setupWithViewPager(pagerTagiEmotki);
        pagerTagiEmotki.setAdapter(pagerAdapterTagiEmotki);
        mTabsTagiEmotki.getTabAt(0).setIcon(android.R.drawable.ic_menu_more);
        mTabsTagiEmotki.getTabAt(1).setIcon(R.drawable.smile1);

        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
