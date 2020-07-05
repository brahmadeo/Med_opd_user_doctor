package com.razahamid.medopd.ExtraFiles;

import android.app.Application;
import android.os.SystemClock;

import java.util.concurrent.TimeUnit;


/**
 * Created by Rajesh Dabhi on 22/6/2017.
 */

public class AppController extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        SystemClock.sleep(TimeUnit.SECONDS.toMillis(2000));

    }



}
