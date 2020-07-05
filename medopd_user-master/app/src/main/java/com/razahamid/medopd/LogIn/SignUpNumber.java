package com.razahamid.medopd.LogIn;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.ExtraFiles.LoadingDialog;
import com.razahamid.medopd.ExtraFiles.NoInternet;
import com.razahamid.medopd.Home.MainActivity;
import com.razahamid.medopd.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpNumber extends Fragment implements  IOnBackPressed{
    private View view;
    private String firstName,email;
    private LoadingDialog loadingDialog;
    private FirebaseAuth mAuth;
    private FirebaseRef ref=new FirebaseRef();
    private SharedPreferences sharedPref;
    public SignUpNumber() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.login_sign_up_number, container, false);
        sharedPref = requireActivity().getSharedPreferences("data",Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(ref.NewUser,true).apply();
        loadingDialog = new LoadingDialog(requireContext(), R.style.DialogLoadingTheme);
        mAuth = FirebaseAuth.getInstance();
        view.findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnline()){
                    loadFragment(new NoInternet(R.id.LogInFrame));
                    return;
                }
                loadingDialog.show();
                if (Verified()){
                  UploadUserDetails(Objects.requireNonNull(mAuth.getCurrentUser()));
                }else {
                    loadingDialog.dismiss();
                }
            }
            private boolean Verified() {
                EditText FirstName=view.findViewById(R.id.first_name);
                firstName= Objects.requireNonNull(FirstName.getText()).toString();
                EditText Email=view.findViewById(R.id.email);
                email= Objects.requireNonNull(Email.getText()).toString();
                if (firstName.replace(" ","").isEmpty()){
                    FirstName.requestFocus();
                    FirstName.setError("required");
                    return false;
                }
                return true;
            }
        });
        return view;
    }


    private void loadFragment(Fragment fragment){
        if (fragment != null) {
            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.LogInFrame, fragment, "signIn");
            ft.addToBackStack(null);
            ft.commit();
        }
    }


    private void UploadUserDetails(final FirebaseUser currentUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put(ref.FirstName, firstName);
        user.put(ref.Email, email);
        user.put(ref.PhoneVerification,"no");
        user.put(ref.TermsCondition,"not");
        user.put(ref.LogInType,ref.Patient);
        db.collection(ref.Users).document(currentUser.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sharedPref.edit().putBoolean(ref.NewUser,false).apply();
                updateUI(currentUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateUI(null);
            }
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        loadingDialog.dismiss();
        if (currentUser!=null){
        startActivity(new Intent(getContext(), MainActivity.class));
            requireActivity().finish();
        }
    }

    private boolean isOnline() {
        ConnectivityManager con_manager = (ConnectivityManager) requireActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert con_manager != null;
        return (con_manager.getActiveNetworkInfo() != null && con_manager.getActiveNetworkInfo().isAvailable() && con_manager.getActiveNetworkInfo().isConnected());
    }

    @Override
    public boolean onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(),LogInActivity.class));
        return true;
    }
}
