package com.example.tomek.xsttest1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by Tomek on 2018-02-01.
 */

public class UstawieniaFragment extends PreferenceFragmentCompat {

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
