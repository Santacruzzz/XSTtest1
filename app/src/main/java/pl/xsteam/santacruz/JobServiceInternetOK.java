package pl.xsteam.santacruz;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import pl.xsteam.santacruz.utils.Typy;

/**
 * Created by Tomek on 2017-10-20.
 */

public class JobServiceInternetOK extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        Intent intent = new Intent();
        intent.setAction(Typy.BROADCAST_INTERNET_WROCIL);
        sendBroadcast(intent);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
