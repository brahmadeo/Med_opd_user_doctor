package com.razahamid.medopd.LogIn;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.ExtraFiles.LoadingDialog;
import com.razahamid.medopd.ExtraFiles.NoInternet;
import com.razahamid.medopd.Home.MainActivity;
import com.razahamid.medopd.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailVerifyPage extends Fragment {
private View view;
private FirebaseUser firebaseUser;
private LoadingDialog loadingDialog;
private FirebaseRef ref=new FirebaseRef();
private FirebaseAuth mAuth;
    public EmailVerifyPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_email_verify_page, container, false);
        loadingDialog = new LoadingDialog(Objects.requireNonNull(getContext()), R.style.DialogLoadingTheme);
        mAuth= FirebaseAuth.getInstance();
        firebaseUser= mAuth.getCurrentUser();
        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
               loadFragment(new SignIn());
            }
        });
        TextView Details=view.findViewById(R.id.Details);
        Details.setText("Please verify "+firebaseUser.getEmail()+" belongs to you,Press verify button then  goto you email.");
        view.findViewById(R.id.Verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot=task.getResult();
                            String email=documentSnapshot.getString(ref.Email);
                            String password=documentSnapshot.getString(ref.Password);
                            mAuth.signOut();
                            SignInWithEmailAndPassword(email,password);
                        }else {
                            loadingDialog.dismiss();
                            Toast.makeText(getContext(), "Something went wrong try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.dismiss();
                        Toast.makeText(getContext(), "Something went wrong try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return view;
    }
    private void loadFragment(Fragment fragment){
        if (fragment != null) {
            FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.LogInFrame, fragment, "SignIn");
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser!=null){
            if (currentUser.isEmailVerified()){
                startActivity(new Intent(getContext(), MainActivity.class));
                Objects.requireNonNull(getActivity()).finish();
            }
        }
    }
    private boolean isOnline() {
        ConnectivityManager con_manager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert con_manager != null;
        return (con_manager.getActiveNetworkInfo() != null && con_manager.getActiveNetworkInfo().isAvailable() && con_manager.getActiveNetworkInfo().isConnected());
    }
    private void setLink(){
        if (!isOnline()){
            loadFragment(new NoInternet(R.id.LogInFrame));
            return;
        }
        if (firebaseUser.isEmailVerified()){
            startActivity(new Intent(getContext(), MainActivity.class));
            Objects.requireNonNull(getActivity()).finish();
        }else {
            loadingDialog.show();
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), "Check your email for verification link", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Some thing went wrong Try again", Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Some thing went wrong Try again", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            });
        }
    }

    private void SignInWithEmailAndPassword(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("results", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user!=null){
                                if (user.isEmailVerified()){
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                    Objects.requireNonNull(getActivity()).finish();
                                }else {
                                    setLink();
                                }
                            }else {
                                mAuth.signOut();
                                loadFragment(new SignIn());
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("results", "signInWithEmail:failure"+ task.getException());
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
}
