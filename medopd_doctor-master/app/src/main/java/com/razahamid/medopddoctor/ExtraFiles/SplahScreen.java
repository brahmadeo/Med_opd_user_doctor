package com.razahamid.medopddoctor.ExtraFiles;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.razahamid.medopddoctor.Home.ui.Messages.AddPrescriptionActivity;
import com.razahamid.medopddoctor.LogIn.LogInActivity;
import com.razahamid.medopddoctor.R;

public class SplahScreen extends AppCompatActivity
{private final int SPLASH_DISPLAY_TIME = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashactivity);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplahScreen.this,
                        LogInActivity.class);
                SplahScreen.this.startActivity(mainIntent);
                SplahScreen.this.finish();
                handler.removeCallbacks(this);
            }
        }, SPLASH_DISPLAY_TIME);
    }
}
