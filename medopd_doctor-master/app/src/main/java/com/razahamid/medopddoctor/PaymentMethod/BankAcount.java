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

/**
 * A simple {@link Fragment} subclass.
 */
public class BankAcount extends Fragment {
    private View view;
    private Toolbar toolbar;
    private String accountHolderName,accountNumber,confirrm,ifscCode;
    private LoadingDialog loadingDialog;
    private FirebaseUser firebaseUser;
    private FirebaseRef ref=new FirebaseRef();
    public BankAcount(Toolbar toolbar) {
        this.toolbar=toolbar;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_bank_acount, container, false);
        loadingDialog=new LoadingDialog(getContext(),R.style.DialogLoadingTheme);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        TextView title=toolbar.findViewById(R.id.title);
        title.setText("Add Bank Details");
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
                    UserCard.put(ref.AccountNumber, accountNumber);
                    UserCard.put(ref.IfscCode,ifscCode);
                    UserCard.put(ref.CardType,"b");
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
                accountNumber=getstringvalue(R.id.accountNumber);
                confirrm=getstringvalue(R.id.confirrm);
                ifscCode=getstringvalue(R.id.ifscCode);
                if (TextUtils.isEmpty(accountHolderName)){
                    setErrorMessage(R.id.accountHolderName);
                    return false;
                }

                if (TextUtils.isEmpty(accountNumber)){
                    setErrorMessage(R.id.accountNumber);
                    return false;
                }
                if (TextUtils.isEmpty(confirrm)){
                    setErrorMessage(R.id.confirrm);
                    return false;
                }
                if (!accountNumber.equals(confirrm)){
                    setErrorMessage(R.id.confirrm,"Should be same");
                    return false;
                }
                if (TextUtils.isEmpty(ifscCode)){
                    setErrorMessage(R.id.ifscCode);
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
    private void setErrorMessage(int id,String message){
        TextInputEditText textInputEditText=view.findViewById(id);
        textInputEditText.setError(message);
        textInputEditText.requestFocus();
    }
}
