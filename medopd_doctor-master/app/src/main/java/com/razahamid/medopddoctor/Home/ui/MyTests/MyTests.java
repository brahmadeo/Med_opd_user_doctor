package com.razahamid.medopddoctor.Home.ui.MyTests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.razahamid.medopddoctor.Adopters.AllRequestTest;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.Models.ModelAllRequestTest;
import com.razahamid.medopddoctor.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyTests extends Fragment {
    private View view;
    private AllRequestTest allTestLocations;
    private FirebaseRef ref=new FirebaseRef();
    private FirebaseUser firebaseUser;
    public MyTests() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_my_tests, container, false);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        yourRecycledViewSetUp();
        return view;
    }
    private void yourRecycledViewSetUp() {
        final RecyclerView recyclerView = view.findViewById(R.id.allLications);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        allTestLocations =new AllRequestTest(getContext(),new ArrayList<ModelAllRequestTest>());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(allTestLocations);
        allTestLocations.setClickListener(new AllRequestTest.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final DocumentSnapshot documentSnapshot= allTestLocations.getDocument(position);
                Toast.makeText(getContext(), documentSnapshot.getId() , Toast.LENGTH_SHORT).show();
            }
        });
        FirebaseFirestore.getInstance().collection(ref.TestRequests).whereEqualTo(ref.User,firebaseUser.getUid()).orderBy(ref.Time, Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                allTestLocations.deleteAllItems();
                assert queryDocumentSnapshots != null;
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    allTestLocations.insertItems(new ModelAllRequestTest(documentSnapshot));
                }
            }
        });
    }
}
