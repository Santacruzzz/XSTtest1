package pl.xsteam.santacruz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pl.xsteam.santacruz.activities.IMainActivity;
import pl.xsteam.santacruz.utils.Typy;

/**
 * Created by Tomek on 2017-10-24.
 */

public class NowaWiadomoscReceiver extends BroadcastReceiver {
    private IMainActivity mIact;

    public NowaWiadomoscReceiver(IMainActivity mIact) {
        this.mIact = mIact;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Typy.BROADCAST_NEW_MSG)) {
            mIact.nowa_wiadomosc(intent);
        } else if (intent.getAction().equals(Typy.BROADCAST_LIKE_MSG)) {
            mIact.polajkowanoWiadomosc(intent.getExtras().getInt("msgid"));
        }
    }
}
