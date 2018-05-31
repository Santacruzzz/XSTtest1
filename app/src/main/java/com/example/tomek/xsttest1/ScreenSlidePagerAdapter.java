package com.example.tomek.xsttest1;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private IMainActivity imain;

    public ScreenSlidePagerAdapter(FragmentManager fm, Activity mAct) {
        super(fm);
        imain = (IMainActivity) mAct;
    }

    @Override
    public Fragment getItem(int position) {
        // Log.i("xst", "++++ getItem: " + position);
        if (position == 0) {
            return imain.getmFragmentSb();
        } else {
            return imain.getmFragmentOnline();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Shoutbox";
        } else {
            return "Online";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}