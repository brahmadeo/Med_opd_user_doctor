package com.razahamid.medopd.Home.ui.Account.ConsultMe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.ExtraFiles.LoadingDialog;
import com.razahamid.medopd.Home.ui.Account.MyAccountFragment;
import com.razahamid.medopd.R;

import java.util.ArrayList;
import java.util.Objects;

public class SelectADoctor extends AppCompatActivity {
private LoadingDialog loadingDialog;
private FirebaseRef ref=new FirebaseRef();
private Bundle bundle;
private ListAdopter listAdopter;
private ViewPager viewPager;
private int MAX_STEP = 0;
private MyViewPagerAdapter myViewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_a_doctor);
        bundle=getIntent().getExtras();
        viewPager =  findViewById(R.id.view_pager);
        loadingDialog=new LoadingDialog(SelectADoctor.this,R.style.DialogLoadingTheme);
        loadingDialog.show();
        initToolbar();
        setUpTopBanners();
        LoadValues();

        uploadedRecycledViewSetUp();
    }

    private void LoadValues() {
        FirebaseFirestore.getInstance().collection(ref.Users).whereArrayContains(ref.Specialities,bundle.getString(ref.ProblemId)).whereEqualTo(ref.status,1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
              try {
                  loadingDialog.dismiss();
                  listAdopter.deleteAllItems();
                 for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                     listAdopter.insertItems(new ModelList(documentSnapshot));
                 }
              }catch (Exception e1){
                  e1.printStackTrace();
              }
            }
        });
    }
    private void setUpTopBanners() {
        FirebaseFirestore.getInstance().collection(ref.AppData).document(ref.HomePage).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot=task.getResult();
                        assert documentSnapshot != null;
                        if (documentSnapshot.exists()){
                            ArrayList<String> arrayList= (ArrayList<String>) documentSnapshot.get(ref.SelectDoctor);
                            MAX_STEP=arrayList.size();
                            myViewPagerAdapter = new MyViewPagerAdapter(SelectADoctor.this,arrayList);
                            viewPager.setAdapter(myViewPagerAdapter);
                            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
                            viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin_overlap_payment));
                            viewPager.setOffscreenPageLimit(MAX_STEP);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void uploadedRecycledViewSetUp(){
        final RecyclerView recyclerView = findViewById(R.id.doctorRecycleView);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        listAdopter = new ListAdopter(this,new ArrayList<ModelList>());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(listAdopter);
        listAdopter.setClickListener(new ListAdopter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ModelList modelList=listAdopter.getModelList(position);
                Intent returnIntent = new Intent();
                Bundle bundle=new Bundle();
                bundle.putString(ref.DoctorName,modelList.documentSnapshot.getString(ref.FirstName));
                bundle.putString(ref.DoctorId,modelList.documentSnapshot.getId());
                bundle.putString(ref.ImageUrl,modelList.documentSnapshot.getString(ref.ImageUrl));
                Log.i("data",bundle.toString());
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
    private ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            bottomProgressDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private void bottomProgressDots(int current_index) {
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        ImageView[] dots = new ImageView[MAX_STEP];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current_index].setImageResource(R.drawable.shape_circle);
            dots[current_index].setColorFilter(getResources().getColor(R.color.light_green_600), PorterDuff.Mode.SRC_IN);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private Context context;
        private ArrayList<String> arrayListImages;
        public MyViewPagerAdapter(Context context,ArrayList<String> arrayListImages) {
            this.context=context;
            this.arrayListImages = arrayListImages;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.item_card_payment1, container, false);
            if (arrayListImages.size()>0){
                Glide.with(context).load(arrayListImages.get(position)).into((ImageView) view.findViewById(R.id.mainImage));
            }
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return MAX_STEP;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
        public void AddArrayList(ArrayList arrayList){
            this.arrayListImages=arrayList;
            notifyDataSetChanged();
        }
    }
}
