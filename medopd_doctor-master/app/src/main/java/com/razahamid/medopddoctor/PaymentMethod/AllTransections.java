package com.razahamid.medopddoctor.PaymentMethod;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.PaymentMethod.Adopter.WithdrawRequests;
import com.razahamid.medopddoctor.R;

import java.util.ArrayList;
import java.util.Objects;

public class AllTransections extends Fragment {
private View view;
private Toolbar toolbar;
private WithdrawRequests withdrawRequests;
private FirebaseRef ref=new FirebaseRef();
private FirebaseUser firebaseUser;
    public AllTransections(Toolbar toolbar) {
      this.toolbar=toolbar;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_all_transections, container, false);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        TextView title=toolbar.findViewById(R.id.title);
        title.setText("Transactions");
        toolbar.findViewById(R.id.exitButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        AllUserHistorySetUp();
        return view;
    }
    private void AllUserHistorySetUp(){
        RecyclerView recyclerView = view.findViewById(R.id.history);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        withdrawRequests = new WithdrawRequests(getContext(),new ArrayList<DocumentSnapshot>());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(withdrawRequests);
        FirebaseFirestore.getInstance().collection(ref.PaymentRequest).whereEqualTo(ref.User,firebaseUser.getUid()).orderBy(ref.Time, Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                try {
                    assert queryDocumentSnapshots != null;
                    withdrawRequests.deleteAllItems();
                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                        withdrawRequests.insertItems(documentSnapshot);
                    }
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });
    }
}
