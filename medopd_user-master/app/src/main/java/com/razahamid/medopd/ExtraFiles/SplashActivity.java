package com.razahamid.medopd.ExtraFiles;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import com.razahamid.medopd.LogIn.LogInActivity;
import com.razahamid.medopd.LogIn.LoginPinActivity;
import com.razahamid.medopd.R;


public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_TIME = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashactivity);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this,
                        LogInActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
                handler.removeCallbacks(this);
            }
        }, SPLASH_DISPLAY_TIME);
    }
}