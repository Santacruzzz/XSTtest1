package com.example.tomek.xsttest1;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tomek on 2017-11-05.
 */

public class PagerFragmentSB extends Fragment implements ViewPager.OnPageChangeListener {
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private IMainActivity imain;
    private Activity mAct;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Log.i("xst", "++++ onAttach");
        imain = (IMainActivity) context;
        mAct = (LayoutGlownyActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Log.i("xst", "++++ onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Log.i("xst", "++++ onCreateView");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.sb_viewpager_layout, container, false);

        mPager = rootView.findViewById(R.id.sb_pager);
        mPager.addOnPageChangeListener(this);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        return rootView;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 1) {
            imain.getmFragmentOnline().odswiezOnline(imain.getOnline());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
            // Log.i("xst", "++++ screenAdapter constructor");
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

}
