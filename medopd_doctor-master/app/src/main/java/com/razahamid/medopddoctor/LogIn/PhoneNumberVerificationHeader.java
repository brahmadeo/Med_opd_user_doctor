package com.razahamid.medopddoctor.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.ExtraFiles.GenericTextWatcher;
import com.razahamid.medopddoctor.ExtraFiles.LoadingDialog;
import com.razahamid.medopddoctor.Home.MainActivity;
import com.razahamid.medopddoctor.R;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;


public class PhoneNumberVerificationHeader extends Fragment {
    private View view;
    private Bundle bundle;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private LoadingDialog loadingDialog;
    private FirebaseAuth mAuth;
    private FirebaseRef ref=new FirebaseRef();
    private TextView tv_coundown;
    private CountDownTimer countDownTimer;
    private String codeValue=null;
    private EditText e1,e2,e3,e4,e5,e6;
    public PhoneNumberVerificationHeader() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_phone_number_verification_header, container, false);
        loadingDialog = new LoadingDialog(getContext(), R.style.DialogLoadingTheme);
        mAuth= FirebaseAuth.getInstance();
        bundle=getArguments();
        TextInputEditText phoneNumber=view.findViewById(R.id.phoneNumber);
        phoneNumber.setText(bundle.getString(ref.PhoneNumber).substring(3));
        tv_coundown = view.findViewById(R.id.tv_coundown);
        countDownTimer();
        initToolbar();
        view.findViewById(R.id.continueButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Varrified()){
                        if (codeValue!=null){
                            verifyPhoneNumberWithCode(mVerificationId,codeValue);
                        }
                    }else {
                        Toast.makeText(getContext(), "Double Check Code", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        setUpOTP();
         e1=view.findViewById(R.id.et1);
         e2=view.findViewById(R.id.et2);
         e3=view.findViewById(R.id.et3);
         e4=view.findViewById(R.id.et4);
         e5=view.findViewById(R.id.et5);
         e6=view.findViewById(R.id.et6);
        e1.addTextChangedListener(new GenericTextWatcher(e2, e1));
        e2.addTextChangedListener(new GenericTextWatcher(e3, e1));
        e3.addTextChangedListener(new GenericTextWatcher(e4, e2));
        e4.addTextChangedListener(new GenericTextWatcher(e5, e3));
        e5.addTextChangedListener(new GenericTextWatcher(e6, e4));
        e6.addTextChangedListener(new GenericTextWatcher(e6, e5));
        return view;
    }

    private boolean Varrified() {
        String es1=e1.getText().toString();
        String es2=e2.getText().toString();
        String es3=e3.getText().toString();
        String es4=e4.getText().toString();
        String es5=e5.getText().toString();
        String es6=e6.getText().toString();
        if (TextUtils.isEmpty(es1)||TextUtils.isEmpty(es2)||TextUtils.isEmpty(es3)||TextUtils.isEmpty(es4)||TextUtils.isEmpty(es5)||TextUtils.isEmpty(es6)){
            return false;
        }
        codeValue=es1+es2+es3+es4+es5+es6;
        return codeValue.length()==6;
    }

    private void setUpOTP() {
        loadingDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                bundle.getString(ref.PhoneNumber),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);



    }
    private void countDownTimer() {
        view.findViewById(R.id.resendButton).setVisibility(View.GONE);
        countDownTimer = new CountDownTimer(1000 * 60 * 2, 1000) {
            @Override
            public void onTick(long l) {
                try {
                    String text = String.format(Locale.getDefault(), "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(l) % 60,
                            TimeUnit.MILLISECONDS.toSeconds(l) % 60);
                    tv_coundown.setText(text);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFinish() {
                try {
                    tv_coundown.setText("00:00");
                    view.findViewById(R.id.resendButton).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.resendButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            resendVerificationCode(bundle.getString(ref.PhoneNumber), mResendToken);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };
        countDownTimer.start();
    }
    private void initToolbar() {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks   mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            loadingDialog.dismiss();
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.i("sms", "onVerificationFailed" +e.getMessage());
            loadingDialog.dismiss();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.i("sms", "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;
            loadingDialog.dismiss();
            countDownTimer.start();

        }
    };
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        loadingDialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity()
                        , new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.i("user", "verified");

                                    FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();

                                    if (user!=null)
                                    {
                                        FirebaseFirestore.getInstance().collection(ref.Users).document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

                                                    }else {
                                                        loadFragment(new SignUpNumber());
                                                    }
                                                }else {
                                                    Toast.makeText(getContext(), "Try again later", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }else {
                                    loadingDialog.dismiss();
                                    Toast.makeText(getContext(), "not Verified", Toast.LENGTH_SHORT).show();
                                    Log.i("user", "notVerified");
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("user", "notVerified");
                loadingDialog.dismiss();
            }
        });
    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }
    private void loadFragment(Fragment fragment){
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment, "SignIn");
            ft.addToBackStack(null);
            ft.commit();
        }
    }

}
