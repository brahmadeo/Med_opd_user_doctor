package com.razahamid.medopd.LogIn;


import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.ExtraFiles.LoadingDialog;
import com.razahamid.medopd.ExtraFiles.NoInternet;
import com.razahamid.medopd.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUp extends Fragment {
    private View view;
    private String firstName,email,password,rePassword;
    private LoadingDialog loadingDialog;
    private FirebaseAuth mAuth;
    private FirebaseRef ref=new FirebaseRef();
    public SignUp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.login_sign_up_fragment, container, false);
        loadingDialog = new LoadingDialog(getContext(), R.style.DialogLoadingTheme);
        mAuth = FirebaseAuth.getInstance();
        view.findViewById(R.id.SignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SignIn());
            }
        });
        view.findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnline()){
                    loadFragment(new NoInternet(R.id.LogInFrame));
                    return;
                }
                loadingDialog.show();
                if (Verified()){
                   CreateUserWithEmailAndPassword(email,password);
                }else {
                    loadingDialog.dismiss();
                }
            }
            private boolean Verified() {
                TextInputEditText FirstName=view.findViewById(R.id.first_name);
                firstName= Objects.requireNonNull(FirstName.getText()).toString();
                TextInputEditText Email=view.findViewById(R.id.email);
                email= Objects.requireNonNull(Email.getText()).toString();
                TextInputEditText Password=view.findViewById(R.id.password);
                password= Objects.requireNonNull(Password.getText()).toString();
                TextInputEditText RePassword=view.findViewById(R.id.confirm_password);
                rePassword= Objects.requireNonNull(RePassword.getText()).toString();
                if (firstName.replace(" ","").isEmpty()){
                    FirstName.requestFocus();
                    FirstName.setError("required");
                    return false;
                }
                if (email.replace(" ","").isEmpty()){
                    Email.requestFocus();
                    Email.setError("required");
                    return false;
                }
                if (!email.contains("@")){
                    Email.requestFocus();
                    Email.setError("not valid");
                    return false;
                }
                if (password.replace(" ","").length()<6){
                    Password.requestFocus();
                    Password.setError("mini length 6");
                    return false;
                }
                if (!rePassword.equals(password)){
                    Password.requestFocus();
                    Password.setError("Not same");
                    return false;
                }
                return true;
            }
        });
        return view;
    }

    private void loadFragment(Fragment fragment){
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.LogInFrame, fragment, "signIn");
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void CreateUserWithEmailAndPassword(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("results", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                UploadUserDetails(user);
                            }else {
                                updateUI(null);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("results", "createUserWithEmail:failure"+task.getException());
                            Toast.makeText(getContext(), "Authentication failed."+task.getException(), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.dismiss();
            }
        });
    }

    private void UploadUserDetails(final FirebaseUser currentUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put(ref.FirstName, firstName);
        user.put(ref.Email, email);
        user.put(ref.Password,password);
        user.put(ref.PhoneVerification,"no");
        user.put(ref.TermsCondition,"not");
        db.collection(ref.Users).document(currentUser.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
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
          loadFragment(new EmailVerifyPage());
        }
    }

    private boolean isOnline() {
        ConnectivityManager con_manager = (ConnectivityManager) requireActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert con_manager != null;
        return (con_manager.getActiveNetworkInfo() != null && con_manager.getActiveNetworkInfo().isAvailable() && con_manager.getActiveNetworkInfo().isConnected());
    }
}
