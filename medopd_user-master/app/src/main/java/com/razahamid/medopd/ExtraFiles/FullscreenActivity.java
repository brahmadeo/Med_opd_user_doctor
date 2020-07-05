package com.razahamid.medopd.ExtraFiles;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.razahamid.medopd.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
private FirebaseRef ref=new FirebaseRef();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        Bundle bundle=getIntent().getExtras();
        Glide.with(this).load(Uri.fromFile(new File(bundle.getString(ref.ImageUrl)))).into((ImageView) findViewById(R.id.fullScreenImage));
        initToolbar();
    }
    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
