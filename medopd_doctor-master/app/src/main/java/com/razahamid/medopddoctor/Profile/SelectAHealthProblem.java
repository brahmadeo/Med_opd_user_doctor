package com.razahamid.medopddoctor.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.razahamid.medopddoctor.Adopters.AllProblems;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.ExtraFiles.LoadingDialog;
import com.razahamid.medopddoctor.Models.ModelAllProblems;
import com.razahamid.medopddoctor.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectAHealthProblem extends AppCompatActivity {
    private AllProblems allProblems;
    private FirebaseRef ref=new FirebaseRef();
    private LoadingDialog loadingDialog;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog=new LoadingDialog(SelectAHealthProblem.this,R.style.DialogLoadingTheme);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
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
                loadingDialog.show();
                // Create a new user with a first and last name
                DocumentSnapshot documentSnapshot=allProblems.getDocument(position);
                Map<String, Object> user = new HashMap<>();
                user.put(ref.Speciality, documentSnapshot.getString(ref.ProblemName));
                user.put(ref.status,0);
                user.put(ref.SpecialityId,documentSnapshot.getId());
                user.put(ref.Time, FieldValue.serverTimestamp());
              FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).collection(ref.Speciality).add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                  @Override
                  public void onComplete(@NonNull Task<DocumentReference> task) {
                      try {loadingDialog.dismiss();
                          if (task.isSuccessful()){
                              Toast.makeText(SelectAHealthProblem.this,  "Added", Toast.LENGTH_SHORT).show();
                              finish();
                          }else {
                              Toast.makeText(SelectAHealthProblem.this,  "Error try again", Toast.LENGTH_SHORT).show();
                          }
                      }catch (Exception e){
                          e.printStackTrace();
                      }
                  }
              });
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
