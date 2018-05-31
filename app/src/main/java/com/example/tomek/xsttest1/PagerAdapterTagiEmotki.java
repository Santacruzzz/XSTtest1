package com.example.tomek.xsttest1;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapterTagiEmotki extends FragmentStatePagerAdapter {

    private IMainActivity imain;

    public PagerAdapterTagiEmotki(FragmentManager fm, Activity mAct) {
        super(fm);
        imain = (IMainActivity) mAct;
    }

    @Override
    public Fragment getItem(int position) {
        // Log.i("xst", "++++ getItem: " + position);
        if (position == 0) {
            return new FragmentTagi();
        } else {
            return new FragmentEmotki();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Tagi";
        } else {
            return "Emotki";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}