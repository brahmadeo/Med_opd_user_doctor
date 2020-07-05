package com.razahamid.medopddoctor.Home.ui.Account.ConsultMe;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.razahamid.medopddoctor.Adopters.AllProblems;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.Models.ModelAllProblems;
import com.razahamid.medopddoctor.R;

import java.util.ArrayList;

public class SelectAHealthProblem extends AppCompatActivity {
    private AllProblems allProblems;
    private FirebaseRef ref=new FirebaseRef();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_a_health_problem);
        yourRecycledViewSetUp();
        initToolbar();
    }

    private void yourRecycledViewSetUp() {
        final RecyclerView recyclerView = findViewById(R.id.ProblemsRecycle);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(SelectAHealthProblem.this,2);
        allProblems=new AllProblems(SelectAHealthProblem.this,new ArrayList<ModelAllProblems>());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(allProblems);
        allProblems.setClickListener(new AllProblems.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final DocumentSnapshot documentSnapshot= allProblems.getDocument(position);
                Toast.makeText(SelectAHealthProblem.this, documentSnapshot.getId() , Toast.LENGTH_SHORT).show();
            }
        });
        FirebaseFirestore.getInstance().collection(ref.AllHealthProblems).orderBy(ref.Time, Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                allProblems.deleteAllItems();
                assert queryDocumentSnapshots != null;
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    allProblems.insertItems(new ModelAllProblems(documentSnapshot));
                }
            }
        });
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select A Health Problem");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
