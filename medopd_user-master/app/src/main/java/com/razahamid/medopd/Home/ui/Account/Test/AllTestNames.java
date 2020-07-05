package com.razahamid.medopd.Home.ui.Account.Test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.Models.ModelAllTestNames;
import com.razahamid.medopd.R;

import java.time.Instant;
import java.util.ArrayList;

public class AllTestNames extends AppCompatActivity {
    private com.razahamid.medopd.Adopters.AllTestNames allTestNames;
    private FirebaseRef ref=new FirebaseRef();
    private ArrayList<String> Unique;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tests);
        yourRecycledViewSetUp();
        initToolbar();
    }

    private void yourRecycledViewSetUp() {
        final RecyclerView recyclerView = findViewById(R.id.testRecycle);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(AllTestNames.this,2);
        allTestNames =new com.razahamid.medopd.Adopters.AllTestNames(AllTestNames.this,new ArrayList<ModelAllTestNames>());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(allTestNames);
        allTestNames.setClickListener(new com.razahamid.medopd.Adopters.AllTestNames.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle=new Bundle();
                DocumentSnapshot documentSnapshot= allTestNames.getDocument(position);
                bundle.putString(ref.TestName,documentSnapshot.getString(ref.TestName));
                bundle.putString(ref.TestFee,String.valueOf(documentSnapshot.getLong(ref.TestFee)));
                bundle.putString(ref.TestId,documentSnapshot.getId());
                if (documentSnapshot.contains(ref.instructionsText)){
                    bundle.putString(ref.instructionsText,documentSnapshot.getString(ref.instructionsText));
                }
                startActivity(new Intent(AllTestNames.this,RegisterTest.class).putExtras(bundle));
            }
        });
        FirebaseFirestore.getInstance().collection(ref.AllTestLocations).orderBy(ref.Time, Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                allTestNames.deleteAllItems();
                Unique=new ArrayList<>();
                assert queryDocumentSnapshots != null;
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    if (!Unique.contains(documentSnapshot.getString(ref.TestName))){
                        allTestNames.insertItems(new ModelAllTestNames(documentSnapshot));
                        Unique.add(documentSnapshot.getString(ref.TestName));
                    }
                }
            }
        });
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select A Test");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
