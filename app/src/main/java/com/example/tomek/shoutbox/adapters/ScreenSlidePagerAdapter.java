package com.example.tomek.shoutbox.adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.tomek.shoutbox.activities.IMainActivity;
import com.example.tomek.shoutbox.fragments.FragmentOnline;
import com.example.tomek.shoutbox.fragments.FragmentSb;

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
//            return imain.getFragmentSb();
            return new FragmentSb();
        } else {
//            return imain.getFragmentOnline();
            return new FragmentOnline();
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