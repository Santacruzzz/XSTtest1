package pl.xsteam.santacruz.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import pl.xsteam.santacruz.R;
import pl.xsteam.santacruz.UstawieniaFragment;
import pl.xsteam.santacruz.utils.Typy;

public class SettingsActivity extends PreferenceActivity {
    private SharedPreferences mSharedPrefs;
    private String mTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        wczytajStyl();
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new UstawieniaFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void wczytajStyl() {
        mSharedPrefs = getSharedPreferences(Typy.PREFS_NAME, 0);
        mTheme = mSharedPrefs.getString(Typy.PREFS_THEME, "dark");
        if (mTheme.equals("light")) {
            setTheme(R.style.xstThemeLight);
            getApplicationContext().setTheme(R.style.xstThemeLight);
        } else {
            setTheme(R.style.xstThemeDark);
            getApplicationContext().setTheme(R.style.xstThemeDark);
        }
    }
}