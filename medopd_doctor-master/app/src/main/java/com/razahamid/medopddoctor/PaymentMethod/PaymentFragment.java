package com.razahamid.medopddoctor.PaymentMethod;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.ExtraFiles.LoadingDialog;
import com.razahamid.medopddoctor.Home.MainActivity;
import com.razahamid.medopddoctor.Models.CardsModel;
import com.razahamid.medopddoctor.PaymentMethod.Adopter.UserCards;
import com.razahamid.medopddoctor.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class PaymentFragment extends Fragment {
private FirebaseRef ref=new FirebaseRef();
private View view;
private FirebaseUser firebaseUser;
private LoadingDialog loadingDialog;
private UserCards userCards;
private DocumentSnapshot userDetails=null;
private DocumentSnapshot ratesDetails=null;
private TextInputEditText withdrawlBox;
private boolean deleteIcon =false;
private long finalValue;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment, container, false);
        loadingDialog = new LoadingDialog(getContext(), R.style.DialogLoadingTheme);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        loadingDialog.show();
        withdrawlBox=view.findViewById(R.id.withdrawlBox);
        view.findViewById(R.id.AddNewCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(getContext(),AddNewPayment.class));
            }
        });
        view.findViewById(R.id.history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),AddNewPayment.class).putExtra("history",true));
            }
        });
        AllUserCardsSetUp();
        loadValues();
        view.findViewById(R.id.withdrawButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Varified()){

                    loadingDialog.show();
                    try {
                        long deductedPoints=Long.parseLong(Objects.requireNonNull(withdrawlBox.getText()).toString())*ratesDetails.getLong(ref.PointsPerOneRs);
                        final long remainingPoints=userDetails.getLong(ref.Points)-deductedPoints;
                        Log.i("Value",String.valueOf(remainingPoints));
                        Log.i("Value",String.valueOf(userDetails.getLong(ref.Points)));
                        Log.i("Value",String.valueOf(Long.parseLong(Objects.requireNonNull(withdrawlBox.getText()).toString())));
                        if (remainingPoints>=0){
                            Map<String,Object> paymentRequest=new HashMap<>();
                            paymentRequest.put(ref.Amount, Objects.requireNonNull(withdrawlBox.getText()).toString());
                            paymentRequest.put(ref.User,firebaseUser.getUid());
                            paymentRequest.put(ref.Time, FieldValue.serverTimestamp());
                            paymentRequest.put(ref.PointsPerOneRs,ratesDetails.getLong(ref.PointsPerOneRs));
                            paymentRequest.put(ref.UserTotalBalance,finalValue);
                            paymentRequest.put(ref.Status,"Pending");
                            paymentRequest.put(ref.remainingPoints,remainingPoints);
                            paymentRequest.put(ref.UserDeductedPoints,deductedPoints);
                            String id=userCards.getSelectedCardValue();
                            if (id==null){
                                loadingDialog.dismiss();
                                if (userCards.getItemCount()>0){
                                    Toast.makeText(getContext(), "Select payment method", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getContext(), "Add  payment method", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                            paymentRequest.put(ref.UserUPI,id);
                            if (userDetails.contains(ref.PhoneNumber)){
                                paymentRequest.put(ref.PhoneNumber,userDetails.getString(ref.PhoneNumber));
                            }

                            FirebaseFirestore.getInstance().collection(ref.PaymentRequest).add(paymentRequest).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()){
                                        Map<String,Object> updatedPoints=new HashMap<>();
                                        updatedPoints.put(ref.Points,remainingPoints);
                                        FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).update(updatedPoints).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                loadingDialog.dismiss();
                                                if (task.isSuccessful()){
                                                    loadValues();
                                                    Toast.makeText(getContext(), "Request Submitted", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                loadingDialog.dismiss();
                                                Toast.makeText(getContext(), "Something Went Wrong try again", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else {
                                        loadingDialog.dismiss();
                                        Toast.makeText(getContext(), "Something Went Wrong try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    loadingDialog.dismiss();
                                    Toast.makeText(getContext(), "Something Went Wrong try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            loadingDialog.dismiss();
                            Toast.makeText(getContext(), "Something Went Wrong try again", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        loadingDialog.dismiss();
                        Toast.makeText(getContext(), "Something Went Wrong try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            private boolean Varified() {
                try {
                    long withdrawAmount=Long.parseLong(Objects.requireNonNull(withdrawlBox.getText()).toString());
                    if (withdrawAmount>=ratesDetails.getLong(ref.MinimumWithdraw)){
                        if (withdrawAmount <= finalValue){
                            return true;
                        }else {
                            withdrawlBox.setError("Withdraw amount should be less than balance");
                            return false;
                        }
                    }else {
                        withdrawlBox.setError("Minimum withdraw amount is INR "+String.valueOf(ratesDetails.getLong(ref.MinimumWithdraw)));
                        return  false;
                    }
                }catch (Exception e){
                    if (withdrawlBox.getText().toString().equals("")){
                        withdrawlBox.setError("Minimum withdraw amount is INR "+String.valueOf(ratesDetails.getLong(ref.MinimumWithdraw)));
                    }
                    e.printStackTrace();
                    return false;
                }
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((MainActivity) getActivity()).getSupportActionBar()).setTitle("Payments");

    }
    private void loadFragment(Fragment fragment, String tag){
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, fragment,tag);
            ft.commit();
        }
    }
    private void AllUserCardsSetUp(){
        RecyclerView recyclerView = view.findViewById(R.id.paymentRecycle);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        userCards = new UserCards(getContext(),new ArrayList<CardsModel>());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userCards);
        userCards.setClickListener(new UserCards.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String id=userCards.getSelectedCardValue();
                if (id==null){
                    deleteIcon=false;
                }else {
                    deleteIcon=true;
                }
                getActivity().invalidateOptionsMenu();
            }
        });
        FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).collection(ref.Accounts).orderBy(ref.Time, Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
             try {
                 assert queryDocumentSnapshots != null;
                 userCards.deleteAllItems();
                 for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                     userCards.insertItems(new CardsModel(documentSnapshot));
                 }
             }catch (Exception e1){
                 e1.printStackTrace();
             }
            }
        });
    }
    private void SetUserBalance(DocumentSnapshot userDetails, DocumentSnapshot ratesDetails){
        TextView Totalammount=view.findViewById(R.id.ammount);
        finalValue=0;
        if (userDetails!=null && ratesDetails!=null){
            if (ratesDetails.contains(ref.PointsPerOneRs) && userDetails.contains(ref.Points)){
                try {
                    finalValue=userDetails.getLong(ref.Points)/ratesDetails.getLong(ref.PointsPerOneRs);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        Totalammount.setText("INR  "+String.valueOf(finalValue));
    }
    private void loadValues() {
        loadingDialog.show();
        FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
             try {
                 if (documentSnapshot!=null){
                     userDetails=documentSnapshot;
                     FirebaseFirestore.getInstance().collection(ref.ShareDataDetails).document(ref.RatesDetails).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                             if(task.isSuccessful()){
                                 ratesDetails=task.getResult();
                                 SetUserBalance(userDetails,ratesDetails);
                                 loadingDialog.dismiss();
                             }else {
                                 loadingDialog.dismiss();
                                 Toast.makeText(getContext(), "Something Went Wrong try again", Toast.LENGTH_SHORT).show();
                             }
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             loadingDialog.dismiss();
                             Toast.makeText(getContext(), "Something Went Wrong try again", Toast.LENGTH_SHORT).show();
                         }
                     });
                 }else {
                     loadingDialog.dismiss();
                     Toast.makeText(getContext(), "Something Went Wrong try again", Toast.LENGTH_SHORT).show();
                 }
             }catch (Exception e1){
                 e1.printStackTrace();
             }
            }
        });
        FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.dismiss();
                Toast.makeText(getContext(), "Something Went Wrong try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_delete, menu);
        menu.findItem(R.id.action_delete).setVisible(deleteIcon);

        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            resetDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void resetDialog() {
        loadingDialog.show();
        String id=userCards.getSelectedCardValue();
        if (id==null){
            deleteIcon=false;
            getActivity().invalidateOptionsMenu();
            return;
        }
        FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).collection(ref.Accounts).document(userCards.getSelectedCardValue()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
                String id=userCards.getSelectedCardValue();
                if (id==null){
                    deleteIcon=false;
                   getActivity().invalidateOptionsMenu();
                }
            }
        });
    }
}