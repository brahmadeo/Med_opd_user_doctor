package com.razahamid.medopd.LogIn;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.ExtraFiles.LoadingDialog;
import com.razahamid.medopd.ExtraFiles.NoInternet;
import com.razahamid.medopd.Home.MainActivity;
import com.razahamid.medopd.Intro.IntroImageWizard;
import com.razahamid.medopd.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignIn extends Fragment {
    private View view;
    private String email,password;
    private LoadingDialog loadingDialog;
    private FirebaseAuth mAuth;
    private FirebaseRef ref=new FirebaseRef();
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private RequestQueue mRequestQueue;
    public SignIn() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view= inflater.inflate(R.layout.login_sign_in_fragment, container, false);
        loadingDialog = new LoadingDialog(requireContext(), R.style.DialogLoadingTheme);
        mAuth = FirebaseAuth.getInstance();
        view.findViewById(R.id.sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SignUp(),"SignUp");
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        view.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        view.findViewById(R.id.resetPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ForgetPassword(),"forgetPassword");
            }
        });
        final TextView phoneNumber=view.findViewById(R.id.phoneLogin);
        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber.setClickable(false);
                if (Verified()){
                    TextInputEditText phoneNumberText=view.findViewById(R.id.phoneNumber);
                    Bundle bundle=new Bundle();
                    String number=phoneNumberText.getText().toString();
                    if (number.startsWith("0")){
                        bundle.putString(ref.PhoneNumber,"+91"+number.substring(1));
                    }else {
                        bundle.putString(ref.PhoneNumber,"+91"+number);
                    }
                    Fragment fragment=new PhoneNumberVerificationHeader();
                    fragment.setArguments(bundle);
                    loadFragment(fragment,"phoneNumber");
                }else {
                    phoneNumber.setClickable(true);
                }


            }

            private boolean Verified() {
                TextInputEditText phoneNumber=view.findViewById(R.id.phoneNumber);
                if (TextUtils.isEmpty(phoneNumber.getText().toString())){
                    phoneNumber.setError("Phone Number Required");
                    phoneNumber.requestFocus();
                    return false;
                }
                if (phoneNumber.getText().toString().length()<10){
                    phoneNumber.setError("Too short number");
                    phoneNumber.requestFocus();
                    return false;
                }
                return true;
            }
        });


       ((TextView) view.findViewById(R.id.tv_content)).setMovementMethod(LinkMovementMethod.getInstance());
        return view;

    }

    private void loadFragment(Fragment fragment, String tag){
        if (fragment != null) {
            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.LogInFrame, fragment, tag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (isOnline()){
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }else {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
            //loadFragment(new NoInternet(),"NoInternet");
        }
    }
    private void updateUI(FirebaseUser currentUser) {
        loadingDialog.show();
        if (currentUser!=null){
            loadingDialog.dismiss();
            SharedPreferences sharedPref = requireActivity().getSharedPreferences("data",Context.MODE_PRIVATE);
            if (sharedPref.contains(ref.NewUser)){
                if (sharedPref.getBoolean(ref.NewUser,false)){
                    loadFragment(new SignUpNumber(),"number");
                }else {
                    if (sharedPref.contains("firstTime")){
                        startActivity(new Intent(getContext(), LoginPinActivity.class));
                    }else {
                        startActivity(new Intent(getContext(), IntroImageWizard.class));
                    }
                    requireActivity().finish();
                }
            }else {
                if (sharedPref.contains("firstTime")){
                    startActivity(new Intent(getContext(), LoginPinActivity.class));
                }else {
                    startActivity(new Intent(getContext(), IntroImageWizard.class));
                }
                requireActivity().finish();
            }

        }else {
            loadingDialog.dismiss();
        }
    }
    private void SignInWithEmailAndPassword(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("results", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
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
    private boolean isOnline() {
        ConnectivityManager con_manager = (ConnectivityManager) requireActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert con_manager != null;
        return (con_manager.getActiveNetworkInfo() != null && con_manager.getActiveNetworkInfo().isAvailable() && con_manager.getActiveNetworkInfo().isConnected());
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("data", "firebaseAuthWithGoogle:" + acct.getId());
        loadingDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("data", "signInWithCredential:success");
                           final FirebaseUser user = mAuth.getCurrentUser();
                           Log.i("value",user.getUid());
                            FirebaseFirestore.getInstance().collection(ref.Users).document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        DocumentSnapshot documentSnapshot=task.getResult();
                                        if (documentSnapshot!=null && documentSnapshot.contains(ref.FirstName)){
                                            Log.i("status","not null");
                                            mGoogleSignInClient.signOut();
                                            updateUI(user);
                                        }else {
                                            Log.i("status","null");
                                            UploadUserDetails(user,acct);
                                        }
                                    }else {
                                        loadingDialog.dismiss();
                                        Toast.makeText(getContext(), "Failed to logIn try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                 loadingDialog.dismiss();
                                    Toast.makeText(getContext(), "Failed to logIn try again", Toast.LENGTH_SHORT).show();
                                }
                            }) ;
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("data", "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.i("data",account.getDisplayName());
                Log.i("data",account.getEmail());
                Log.i("data",account.getPhotoUrl().toString());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("data", "Google sign in failed", e);
                // ...
            }
        }
    }


    private void UploadUserDetails(final FirebaseUser currentUser , GoogleSignInAccount acct) {
        Log.i("uploadvalues",String.valueOf(true));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put(ref.FirstName, acct.getDisplayName());
        user.put(ref.Email, acct.getEmail());
        user.put(ref.PhoneVerification,"no");
        user.put(ref.TermsCondition,"not");
        try {
            user.put(ref.ImageUrl,acct.getPhotoUrl().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        mGoogleSignInClient.signOut();
        Log.i("uploadValues",String.valueOf(user));
        db.collection(ref.Users).document(currentUser.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingDialog.dismiss();
                Log.i("uploadvalues",String.valueOf(task.isSuccessful()));
                if (task.isSuccessful()){
                    updateUI(currentUser);
                }else {
                    updateUI(null);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            loadingDialog.dismiss();
             updateUI(null);
            }
        });
    }
}
