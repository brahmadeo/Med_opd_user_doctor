package com.razahamid.medopd.Home.ui.Account.ConsultMe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razahamid.medopd.Adopters.AllSymptoms;
import com.razahamid.medopd.Adopters.SelectedSymptoms;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.Models.AllSymptomsModel;
import com.razahamid.medopd.R;

import java.util.ArrayList;
import java.util.Objects;

public class PatientDetails extends AppCompatActivity {
private String name,age,details,otherHealthProblem,numberOfDays, alreadyTakenMedicine;
private boolean Gender=true;
private FirebaseRef ref=new FirebaseRef();
private ArrayList<String> AllSelected=new ArrayList<>();
private SelectedSymptoms selectedSymptoms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
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
                    bundle.putString(ref.AlreadyTakenMedicine, alreadyTakenMedicine);
                    bundle.putBoolean(ref.Gender,Gender);
                    bundle.putSerializable(ref.Symptoms,AllSelected);
                    returnIntent.putExtras(bundle);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        });
        findViewById(R.id.AddNewCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog= ProgressDialog.show(PatientDetails.this, "Downloading List", "Please Wait...", true);
                FirebaseFirestore.getInstance().collection(ref.AppData).document(ref.PatientDetails).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                      progressDialog.dismiss();
                      if (task.isSuccessful()){
                          DocumentSnapshot documentSnapshot=task.getResult();
                          if (documentSnapshot.contains(ref.Symptoms)){
                              ArrayList<String> allSymptoms= (ArrayList<String>) documentSnapshot.get(ref.Symptoms);
                              showDialogSymptoms(allSymptoms);
                          }else {
                              Toast.makeText(PatientDetails.this,  "Try Again network error", Toast.LENGTH_SHORT).show();
                          }
                      }else {
                          Toast.makeText(PatientDetails.this,  "Try Again network error", Toast.LENGTH_SHORT).show();
                      }
                    }
                });
            }
        });
        RecycleViewSetUp();
    }
    private void showDialogSymptoms(ArrayList<String> allSymptoms) {
        ArrayList<AllSymptomsModel> allSymptomsModels=new ArrayList<>();
        for (int i=0;i<allSymptoms.size();i++){
            allSymptomsModels.add(new AllSymptomsModel(allSymptoms.get(i)));
        }
        final AllSymptoms allSymptoms1=new AllSymptoms(PatientDetails.this,allSymptomsModels,AllSelected);
        final Dialog dialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_new_symptoms);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllSelected=allSymptoms1.getAllSelected();
                selectedSymptoms.UpdateList(AllSelected);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recycleView);
        GridLayoutManager layoutManager = new GridLayoutManager(PatientDetails.this,2);
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.setAdapter(allSymptoms1);
        dialog.show();
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
        alreadyTakenMedicine = getTextValue(R.id.et_already_taken_medicine);
        if (TextUtils.isEmpty(name)){
            TextInputEditText name=findViewById(R.id.et_name);
            name.setError("Required");
            name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(age)){
            TextInputEditText et_details=findViewById(R.id.et_age);
            et_details.setError("Required");
            et_details.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(details)){
            TextInputEditText name=findViewById(R.id.et_details);
            name.setError("Required");
            name.requestFocus();
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
        if (TextUtils.isEmpty(alreadyTakenMedicine)){
            TextInputEditText et_already_taken_medicine=findViewById(R.id.et_already_taken_medicine);
            et_already_taken_medicine.setError("Required");
            et_already_taken_medicine.requestFocus();
            return false;
        }

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
    private String getTextValue(int id) {
        TextInputEditText text=findViewById(id);
        return text.getText().toString();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fill Patient details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
