package com.example.tomek.shoutbox.adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.tomek.shoutbox.fragments.FragmentEmotki;
import com.example.tomek.shoutbox.fragments.FragmentTagi;
import com.example.tomek.shoutbox.activities.IMainActivity;

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
            return new FragmentTagi(imain.getFragmentSb());
        } else {
            return new FragmentEmotki(imain.getFragmentSb());
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "";
        } else {
            return "";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}