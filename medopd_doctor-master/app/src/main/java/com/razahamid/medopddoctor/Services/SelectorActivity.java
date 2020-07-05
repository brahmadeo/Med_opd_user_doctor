package com.razahamid.medopddoctor.Services;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.R;

import java.util.Objects;

public class SelectorActivity extends AppCompatActivity {
private Bundle bundle;
private FirebaseRef ref=new FirebaseRef();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        bundle=getIntent().getExtras();
        assert bundle != null;
        Log.i("taskId", bundle.toString());
        Log.i("taskId", Objects.requireNonNull(bundle.getString("taskId")));
    }
}
