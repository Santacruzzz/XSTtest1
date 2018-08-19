package com.example.tomek.shoutbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.tomek.shoutbox.activities.IMainActivity;
import com.example.tomek.shoutbox.activities.MainActivity;

/**
 * Created by Tomek on 2017-10-20.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    private IMainActivity mAct;

    public MyBroadcastReceiver(MainActivity mainActivity) {
        mAct = (IMainActivity) mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mAct != null) {
            mAct.broadcastReceived(intent.getAction());
        }
    }
}
