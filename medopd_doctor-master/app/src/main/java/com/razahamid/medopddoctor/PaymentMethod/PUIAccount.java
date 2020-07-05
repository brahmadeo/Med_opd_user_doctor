package com.razahamid.medopddoctor.PaymentMethod;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.ExtraFiles.LoadingDialog;
import com.razahamid.medopddoctor.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PUIAccount extends Fragment {
private View view;
private Toolbar toolbar;
    private String accountHolderName, UserUPID;
    private LoadingDialog loadingDialog;
    private FirebaseUser firebaseUser;
    private FirebaseRef ref=new FirebaseRef();
    public PUIAccount(Toolbar toolbar) {
      this.toolbar=toolbar;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_pui_account, container, false);
        loadingDialog=new LoadingDialog(getContext(),R.style.DialogLoadingTheme);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        TextView title=toolbar.findViewById(R.id.title);
        title.setText("Add UPI Details");
        toolbar.findViewById(R.id.exitButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        view.findViewById(R.id.submite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Varified()){
                    loadingDialog.show();
                    // Create a new user with a first and last name
                    Map<String, Object> UserCard = new HashMap<>();
                    UserCard.put(ref.AccountHolderName, accountHolderName);
                    UserCard.put(ref.UserUPI, UserUPID);
                    UserCard.put(ref.CardType,"u");
                    UserCard.put(ref.Time, FieldValue.serverTimestamp());
                    FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).collection(ref.Accounts).add(UserCard).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            loadingDialog.dismiss();
                            if (task.isSuccessful()){
                                Toast.makeText(getContext(), "Add", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }    else {
                                Toast.makeText(getContext(), "Try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                            Toast.makeText(getContext(), "Try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            private boolean Varified() {
                accountHolderName=getstringvalue(R.id.accountHolderName);
                UserUPID =getstringvalue(R.id.UserUPID);
                if (TextUtils.isEmpty(accountHolderName)){
                    setErrorMessage(R.id.accountHolderName);
                    return false;
                }
                if (TextUtils.isEmpty(UserUPID)){
                    setErrorMessage(R.id.UserUPID);
                    return false;
                }
                return true;
            }
        });
        return view;
    }
    private String getstringvalue(int id){
        TextInputEditText textInputEditText=view.findViewById(id);
        return textInputEditText.getText().toString();
    }
    private void setErrorMessage(int id){
        TextInputEditText textInputEditText=view.findViewById(id);
        textInputEditText.setError("required");
        textInputEditText.requestFocus();
    }
}
