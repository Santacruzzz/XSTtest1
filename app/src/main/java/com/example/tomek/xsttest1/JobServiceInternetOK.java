package com.example.tomek.xsttest1;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Tomek on 2017-10-20.
 */

public class JobServiceInternetOK extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i("xst", "INTERNET OK, rozsyłam info");
        Intent intent = new Intent();
        intent.setAction(Typy.BROADCAST_INTERNET_OK);
        sendBroadcast(intent);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
