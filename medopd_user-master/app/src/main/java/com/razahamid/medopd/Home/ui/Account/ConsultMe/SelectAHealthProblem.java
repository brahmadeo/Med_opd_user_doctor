package com.razahamid.medopd.Home.ui.Account.ConsultMe;

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
import com.razahamid.medopd.Adopters.AllProblems;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.Models.ModelAllProblems;
import com.razahamid.medopd.R;

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
                DocumentSnapshot documentSnapshot=allProblems.getDocument(position);
                Bundle bundle=new Bundle();
                bundle.putString(ref.ProblemName,documentSnapshot.getString(ref.ProblemName));
                bundle.putString(ref.IconUrl,documentSnapshot.getString(ref.IconUrl));
                bundle.putString(ref.ProblemId,documentSnapshot.getId());
                if (documentSnapshot.contains(ref.PreviousPrice)){
                    bundle.putLong(ref.PreviousPrice,documentSnapshot.getLong(ref.PreviousPrice));
                }
                bundle.putLong(ref.ProblemPrice,documentSnapshot.getLong(ref.ProblemPrice));
                startActivity(new Intent(SelectAHealthProblem.this,Summary.class).putExtras(bundle));
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
        Toolbar toolbar = findViewById(R.id.toolbar);
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
