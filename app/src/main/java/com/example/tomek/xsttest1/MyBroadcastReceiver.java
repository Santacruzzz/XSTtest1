package com.example.tomek.xsttest1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Tomek on 2017-10-20.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    private IMainActivity mAct;

    public MyBroadcastReceiver(LayoutGlownyActivity layoutGlownyActivity) {
        mAct = (IMainActivity) layoutGlownyActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mAct != null) {
            mAct.broadcastReceived(intent.getAction());
        }
    }
}
