package com.razahamid.medopddoctor.Home.ui.Messages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.razahamid.medopddoctor.Adopters.SelectedSymptoms;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.R;

import java.util.ArrayList;

public class PatientDetails extends AppCompatActivity {
private String name,age,details,otherHealthProblem,numberOfDays;
private boolean Gender=true;
private FirebaseRef ref=new FirebaseRef();
private Bundle bundle;
    private ArrayList<String> AllSelected=new ArrayList<>();
    private SelectedSymptoms selectedSymptoms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
        bundle=getIntent().getExtras();
        initToolbar();
        findViewById(R.id.submite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (varified()){
                    Intent returnIntent = new Intent();
                    Bundle bundle=new Bundle();
                    bundle.putString(ref.PName,name);
                    bundle.putString(ref.PAge,age);
                    bundle.putString(ref.PDetails,details);
                    bundle.putString(ref.OtherHealthProblem,otherHealthProblem);
                    bundle.putString(ref.NumberOfDays,numberOfDays);
                    bundle.putBoolean(ref.Gender,Gender);
                    Log.i("data",bundle.toString());
                    returnIntent.putExtras(bundle);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        });
        FirebaseFirestore.getInstance().collection(ref.PatientDetails).document(bundle.getString(ref.DetailID)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
           try {
               TextInputEditText name=findViewById(R.id.et_name);
               name.setText(documentSnapshot.getString(ref.PName));
               TextInputEditText et_age=findViewById(R.id.et_age);
               et_age.setText(documentSnapshot.getString(ref.PAge));
               TextInputEditText et_details=findViewById(R.id.et_details);
               et_details.setText(documentSnapshot.getString(ref.PDetails));
               TextInputEditText et_otherHealth=findViewById(R.id.et_otherHealth);
               et_otherHealth.setText(documentSnapshot.getString(ref.OtherHealthProblem));
               TextInputEditText et_numberOfDays=findViewById(R.id.et_numberOfDays);
               et_numberOfDays.setText(documentSnapshot.getString(ref.NumberOfDays));
               if (documentSnapshot.getBoolean(ref.Gender)){
                   RadioButton male=findViewById(R.id.male);
                   male.setSelected(true);
                   RadioButton female=findViewById(R.id.female);
                   female.setSelected(false);
               }else {
                   RadioButton male=findViewById(R.id.male);
                   male.setSelected(false);
                   RadioButton female=findViewById(R.id.female);
                   female.setSelected(true);
               }
               if (documentSnapshot.contains(ref.Symptoms)){
                   AllSelected= (ArrayList<String>) documentSnapshot.get(ref.Symptoms);
                   selectedSymptoms.UpdateList(AllSelected);
               }
           }catch (Exception e1){
               e1.printStackTrace();
           }
            }
        });
        RecycleViewSetUp();
    }

    private boolean varified() {
        RadioGroup radioGroup=findViewById(R.id.gender);
        int id=radioGroup.getCheckedRadioButtonId();
        if (id==R.id.male){
            Gender=true;
        }else {
            Gender=false;
        }
        name=getTextValue(R.id.et_name);
        age=getTextValue(R.id.et_age);
        details=getTextValue(R.id.et_details);
        otherHealthProblem=getTextValue(R.id.et_otherHealth);
        numberOfDays=getTextValue(R.id.et_numberOfDays);
        if (TextUtils.isEmpty(name)){
            TextInputEditText name=findViewById(R.id.et_name);
            name.setError("Required");
            name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(age)){
            TextInputEditText et_age=findViewById(R.id.et_age);
            et_age.setError("Required");
            et_age.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(details)){
            TextInputEditText et_details=findViewById(R.id.et_details);
            et_details.setError("Required");
            et_details.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(otherHealthProblem)){
            TextInputEditText et_otherHealth=findViewById(R.id.et_otherHealth);
            et_otherHealth.setError("Required");
            et_otherHealth.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(numberOfDays)){
            TextInputEditText et_numberOfDays=findViewById(R.id.et_numberOfDays);
            et_numberOfDays.setError("Required");
            et_numberOfDays.requestFocus();
            return false;
        }

        return true;
    }

    private String getTextValue(int id) {
        TextInputEditText text=findViewById(id);
        return text.getText().toString();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Patient details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void RecycleViewSetUp(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.paymentRecycle);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(PatientDetails.this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setAlignItems(AlignItems.CENTER);
        recyclerView.setLayoutManager(layoutManager);
        selectedSymptoms=new SelectedSymptoms(PatientDetails.this,new ArrayList<String>());
        recyclerView.setAdapter(selectedSymptoms);
    }
}
