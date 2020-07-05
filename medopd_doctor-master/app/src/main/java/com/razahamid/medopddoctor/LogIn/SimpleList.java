package com.razahamid.medopddoctor.LogIn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.R;

import java.util.ArrayList;
import java.util.Objects;

public class SimpleList extends AppCompatActivity {
private FirebaseRef ref=new FirebaseRef();
private Bundle bundle;
private ArrayList<ModelList> modelLists=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);
     ArrayList<String>  data=  (ArrayList<String>) getIntent().getExtras().getSerializable(ref.List);
        assert data != null;
        for (int i = 0; i<data.size(); i++){
         modelLists.add(new ModelList(data.get(i)));
        }
        bundle=getIntent().getExtras();
        uploadedRecycledViewSetUp();
        initToolbar();
    }

    private void uploadedRecycledViewSetUp(){

        RecyclerView recyclerView = findViewById(R.id.mainRecycle);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        final ListAdopter listAdopter = new ListAdopter(this,modelLists);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(listAdopter);
        listAdopter.setClickListener(new ListAdopter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ModelList modelList=listAdopter.getModelList(position);
                Intent returnIntent = new Intent();
                Bundle bundle=new Bundle();
                bundle.putString(ref.CityName,modelList.Name);
                returnIntent.putExtras(bundle);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Chose One");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
