package com.razahamid.medopddoctor.LogIn;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.ExtraFiles.LoadingDialog;
import com.razahamid.medopddoctor.Home.MainActivity;
import com.razahamid.medopddoctor.Home.ui.Messages.UserChat;
import com.razahamid.medopddoctor.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpNumber extends Fragment implements IOnBackPressed{
    private View view;
    private FirebaseRef ref=new FirebaseRef();
    private TextInputEditText et_name,et_cityName,et_speciality,et_education,et_registration,et_experience;
    private int specialityInt=102;
    private int educationInt=103;
    private int registrationInt=104;
    private String s_name,s_cityName,s_speciality,s_education,s_registration,s_experience;
    private boolean Gender=true;
    private DocumentSnapshot documentSnapshot=null;
    private LoadingDialog loadingDialog;
    private Bundle registrationBundle=null;
    private FirebaseUser firebaseUser;
    private String totalEarning = "100";
    public SignUpNumber() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.login_sign_up_number, container, false);
        loadingDialog=new LoadingDialog(getContext(),R.style.DialogLoadingTheme);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser==null){
            loadFragment(new SignIn());
        }
        et_name=view.findViewById(R.id.et_name);
        et_cityName=view.findViewById(R.id.et_cityName);
        et_speciality=view.findViewById(R.id.et_speciality);
        et_education=view.findViewById(R.id.et_education);
        et_registration=view.findViewById(R.id.et_registration);
        et_experience=view.findViewById(R.id.et_experience);
        et_speciality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (documentSnapshot==null){
                    LoadData();
                    return;
                }
                Bundle bundle=new Bundle();
                bundle.putSerializable(ref.List,(ArrayList<String>) documentSnapshot.get(ref.Specialities));
                startActivityForResult(new Intent(getContext(),SimpleList.class).putExtras(bundle),specialityInt);
            }
        });
        et_education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (documentSnapshot==null){
                    LoadData();
                    return;
                }
                Bundle bundle=new Bundle();
                bundle.putSerializable(ref.List,(ArrayList<String>) documentSnapshot.get(ref.Educations));
                startActivityForResult(new Intent(getContext(),SimpleList.class).putExtras(bundle),educationInt);
            }
        });
        et_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (documentSnapshot==null){
                    LoadData();
                    return;
                }
                Bundle bundle=new Bundle();
                bundle.putSerializable(ref.List,(ArrayList<String>) documentSnapshot.get(ref.RegistrationCouncil));
                startActivityForResult(new Intent(getContext(),RegistrationDetails.class).putExtras(bundle),registrationInt);
            }
        });
        LoadData();
        view.findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Varified()){
                  loadingDialog.show();
                    Map<String, Object> user = new HashMap<>();
                    user.put(ref.FirstName, s_name);
                    user.put(ref.CityName, s_cityName);
                    user.put(ref.Speciality, s_speciality);
                    user.put(ref.Gender,Gender);
                    user.put(ref.Education,s_education);
                    user.put(ref.Experience,s_experience);
                    user.put(ref.RegistrationNumber,registrationBundle.getString(ref.RegistrationNumber));
                    user.put(ref.RegistrationCouncil,registrationBundle.getString(ref.RegistrationCouncil));
                    user.put(ref.RegistrationYear,registrationBundle.getString(ref.RegistrationYear));
                    user.put(ref.status,0);
                    user.put(ref.LogInType,ref.Doctor);
                    FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loadingDialog.dismiss();
                        if (task.isSuccessful()){
                            SaveEarning(totalEarning);
                            SharedPreferences sharedPref = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
                            sharedPref.edit().putBoolean("Data",true).apply();
                            startActivity(new Intent(getContext(), MainActivity.class));
                            getActivity().finish();
                        }else {
                            Toast.makeText(getContext(), "Try Again", Toast.LENGTH_SHORT).show();
                        }
                        }
                    });
                }
            }
        });
        loadingDialog.show();
        FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                loadingDialog.dismiss();
                if (task.isSuccessful()){
                    if (Objects.requireNonNull(task.getResult()).exists()){
                        DocumentSnapshot documentSnapshot=task.getResult();
                        if (documentSnapshot.contains(ref.LogInType)){
                            if (documentSnapshot.getString(ref.LogInType).equals(ref.Doctor)){
                                startActivity(new Intent(getContext(), MainActivity.class));
                                getActivity().finish();
                            }else {
                                Toast.makeText(getContext(), "This Number is not Registered As a Doctor", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }else {
                    Toast.makeText(getContext(), "Try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
    private void SaveEarning(String totalEarning){
        Map<String, Object> user = new HashMap<>();
        user.put(ref.Earning, totalEarning);
        FirebaseFirestore.getInstance().collection(ref.EarningReport).document(firebaseUser.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), " Earning saved", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getContext(), "Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean Varified() {
        RadioGroup radioGroup=view.findViewById(R.id.gender);
        int id=radioGroup.getCheckedRadioButtonId();
        if (id==R.id.male){
            Gender=true;
        }else {
            Gender=false;
        }
        s_name=et_name.getText().toString();
        s_cityName=et_cityName.getText().toString();
        s_speciality=et_speciality.getText().toString();
        s_education=et_education.getText().toString();
        s_registration=et_registration.getText().toString();
        s_experience=et_experience.getText().toString();
        if (TextUtils.isEmpty(s_name)){
            et_name.setError("Required");
            et_name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(s_cityName)){
            et_cityName.setError("Required");
            et_cityName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(s_speciality)){
            et_speciality.setError("Required");
            et_speciality.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(s_education)){
            et_education.setError("Required");
            et_education.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(s_registration)){
            et_registration.setError("Required");
            et_registration.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(s_experience)){
            et_experience.setError("Required");
            et_experience.requestFocus();
            return false;
        }
        return true;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==specialityInt){
            if (resultCode== Activity.RESULT_OK){
                assert data != null;
                et_speciality.setText(Objects.requireNonNull(data.getExtras()).getString(ref.CityName));
            }
        }else if (requestCode==educationInt){
            if (resultCode== Activity.RESULT_OK){
                assert data != null;
                et_education.setText(Objects.requireNonNull(data.getExtras()).getString(ref.CityName));
            }
        }else if (requestCode==registrationInt){
            if (resultCode== Activity.RESULT_OK){
                assert data != null;
                et_registration.setText("Filled");
                registrationBundle=data.getExtras();
            }
        }
    }
    private void LoadData(){
        loadingDialog.show();
        FirebaseFirestore.getInstance().collection(ref.AppData).document(ref.DoctorSignUp).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if (task.isSuccessful()){
                   loadingDialog.dismiss();
                   documentSnapshot=task.getResult();
               }
            }
        });
    }
    public void loadFragment(Fragment fragment){
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
    }
    @Override
    public boolean onBackPressed(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(),LogInActivity.class));
        return true;
    }
}
