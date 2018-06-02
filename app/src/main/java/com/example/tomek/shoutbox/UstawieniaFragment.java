package com.example.tomek.shoutbox;

import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;

import com.example.tomek.shoutbox.utils.Typy;

/**
 * Created by Tomek on 2018-02-01.
 */

public class UstawieniaFragment extends PreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        getPreferenceManager().setSharedPreferencesName(Typy.PREFS_NAME);
        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
