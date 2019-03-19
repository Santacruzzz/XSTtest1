package pl.xsteam.santacruz.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pl.xsteam.santacruz.XstService;

public class ScreenOnOffReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "BroadcastReceiver", Toast.LENGTH_SHORT).show();

        boolean screenOff = false;
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
        }

        // Toast.makeText(context, "BroadcastReceiver :"+screenOff, Toast.LENGTH_SHORT).show();

        // Send Current screen ON/OFF value to service
        Intent i = new Intent(context, XstService.class);
        if (screenOff) {
            i.putExtra("msg", "screenOff");
        } else {
            i.putExtra("msg", "screenOn");
        }
        context.startService(i);
    }
}
