package com.razahamid.medopd.LogIn;


import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.razahamid.medopd.ExtraFiles.LoadingDialog;
import com.razahamid.medopd.ExtraFiles.NoInternet;
import com.razahamid.medopd.R;

import java.util.Objects;

public class ForgetPassword extends Fragment {

    private View view;
    private String email;
    private LoadingDialog loadingDialog;
    public ForgetPassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_forget_password, container, false);
        loadingDialog = new LoadingDialog(Objects.requireNonNull(getContext()), R.style.DialogLoadingTheme);
        view.findViewById(R.id.SignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnline()){
                    loadFragment(new NoInternet(R.id.LogInFrame),"noInternet");
                    return;
                }
                loadingDialog.show();
                if (Verified()){
                    requestResetPassword(email);
                }else {
                    loadingDialog.dismiss();
                }
            }

            private void requestResetPassword(final String email) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingDialog.dismiss();
                                if (task.isSuccessful()) {
                                     Toast.makeText(getContext(), "Reset link send check your email : "+email, Toast.LENGTH_SHORT).show();
                                    loadFragment(new SignIn(),"Sign in");
                                }else {
                                    Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });
            }

            private boolean Verified() {
                TextInputEditText Email=view.findViewById(R.id.Email);
                email= Objects.requireNonNull(Email.getText()).toString();
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
                return true;
            }
        });
        view.findViewById(R.id.SignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SignUp(),"SignUp");
            }
        });

        return view;

    }
    private void loadFragment(Fragment fragment, String tag){
        if (fragment != null) {
            FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.LogInFrame, fragment, tag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    private boolean isOnline() {
        ConnectivityManager con_manager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert con_manager != null;
        return (con_manager.getActiveNetworkInfo() != null && con_manager.getActiveNetworkInfo().isAvailable() && con_manager.getActiveNetworkInfo().isConnected());
    }
}
