package com.example.tomek.shoutbox.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.tomek.shoutbox.DialogDodatki;
import com.example.tomek.shoutbox.fragments.FragmentEmotki;
import com.example.tomek.shoutbox.fragments.FragmentTagi;

public class PagerAdapterTagiEmotki extends FragmentStatePagerAdapter {
    private DialogDodatki.AddonSelectedListener listener;

    public PagerAdapterTagiEmotki(FragmentManager childFragmentManager, DialogDodatki.AddonSelectedListener listener) {
        super(childFragmentManager);
        this.listener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        // Log.i("xst", "++++ getItem: " + position);
        if (position == 0) {
            return new FragmentTagi(listener);
        } else {
            return new FragmentEmotki(listener);
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