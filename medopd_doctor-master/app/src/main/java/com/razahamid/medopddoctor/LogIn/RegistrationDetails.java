package com.razahamid.medopddoctor.LogIn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.R;

import java.util.ArrayList;
import java.util.Objects;

public class RegistrationDetails extends AppCompatActivity {
    private FirebaseRef ref=new FirebaseRef();
    private TextInputEditText et_registrationNumber,et_registrationCouncil,et_registrationYear;
    private String s_registrationNumber,s_registrationCouncil,s_registrationYear;
    private int registrationCouncilInt=105;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_details);
        bundle=getIntent().getExtras();
        initToolbar();
        et_registrationCouncil=findViewById(R.id.et_registrationCouncil);
        et_registrationNumber=findViewById(R.id.et_registrationNumber);
        et_registrationYear=findViewById(R.id.et_registrationYear);
        et_registrationCouncil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(RegistrationDetails.this,SimpleList.class).putExtras(bundle),registrationCouncilInt);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==registrationCouncilInt){
            if (resultCode== Activity.RESULT_OK){
                assert data != null;
                et_registrationCouncil.setText(Objects.requireNonNull(data.getExtras()).getString(ref.CityName));
            }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Registration Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_save:
                saveAndExit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void saveAndExit() {
        if (Varified()){
            Intent returnIntent = new Intent();
            Bundle bundle=new Bundle();
            bundle.putString(ref.RegistrationCouncil,s_registrationCouncil);
            bundle.putString(ref.RegistrationNumber,s_registrationNumber);
            bundle.putString(ref.RegistrationYear,s_registrationYear);
            returnIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }
    }

    private boolean Varified() {
        s_registrationNumber=et_registrationNumber.getText().toString();
        s_registrationCouncil=et_registrationCouncil.getText().toString();
        s_registrationYear=et_registrationYear.getText().toString();
        if (TextUtils.isEmpty(s_registrationNumber)){
            et_registrationNumber.setError("Required");
            et_registrationNumber.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(s_registrationCouncil)){
            et_registrationCouncil.setError("Required");
            et_registrationCouncil.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(s_registrationYear)){
            et_registrationYear.setError("Required");
            et_registrationYear.requestFocus();
            return false;
        }
        return true;
    }
}
