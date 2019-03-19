package pl.xsteam.santacruz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Tomek on 2017-10-21.
 */

public class PowerUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, XstService.class));
    }
}
