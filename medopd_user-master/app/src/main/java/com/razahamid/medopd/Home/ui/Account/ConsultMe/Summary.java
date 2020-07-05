package com.razahamid.medopd.Home.ui.Account.ConsultMe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.ExtraFiles.LoadingDialog;
import com.razahamid.medopd.Home.ui.Account.Test.RegisterTest;
import com.razahamid.medopd.Home.ui.Messages.UserChat;
import com.razahamid.medopd.Models.ModelAllProblems;
import com.razahamid.medopd.Payment.AppPreference;
import com.razahamid.medopd.Payment.PaymentActivity;
import com.razahamid.medopd.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Summary extends AppCompatActivity {
    private int MAX_STEP = 0;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private FirebaseRef ref=new FirebaseRef();
    private FirebaseUser firebaseUser;
    private int SelectedDoctor=201;
    private int paymentCode=202;
    private int PatientDetails=203;
    private Bundle bundle;
    private TextInputEditText et_doctorName,et_PhoneNumber,et_patientForm;
    private String PhoneNumber;
    private Bundle DoctorBundle=null;
    private Bundle PatientBundle=null;
    private LoadingDialog loadingDialog;
    private int numberOfDays=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        loadingDialog=new LoadingDialog(Summary.this,R.style.DialogLoadingTheme);
        viewPager =  findViewById(R.id.view_pager);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        bundle=getIntent().getExtras();
        initToolbar();
        bottomProgressDots(0);
        setUpTopBanners();
        setUpProblemValues();
        SetupPackage();
        SetUpFinalPayment();
        DoctorSetUp();
        PatientSetUp();
    }

    private void PatientSetUp() {
        et_patientForm=findViewById(R.id.et_patientForm);
        et_patientForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Summary.this,PatientDetails.class).putExtras(bundle),PatientDetails);
            }
        });
    }

    private void setUpProblemValues() {
        TextView ProblemName,ProblemPrice,previousPrice;
        ImageView ProblemImage;
        ProblemName   = findViewById(R.id.ProblemName);
        ProblemPrice  = findViewById(R.id.ProblemPrice);
        ProblemImage  = findViewById(R.id.ProblemImage);
        previousPrice = findViewById(R.id.previousPrice);
        ProblemName.setText(bundle.getString(ref.ProblemName));
        ProblemPrice.setText("₹ "+String.valueOf(bundle.getLong(ref.ProblemPrice)));
        if (bundle.containsKey(ref.PreviousPrice)){
            previousPrice.setPaintFlags(previousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            previousPrice.setText("₹ "+String.valueOf(bundle.getLong(ref.PreviousPrice)));
        }
        Picasso.get().load(bundle.getString(ref.IconUrl)).into(ProblemImage);

    }
    private void SetupPackage(){
        TextView newFee=findViewById(R.id.newFee);
        TextView previousFee=findViewById(R.id.previousFee);
        newFee.setText("₹ "+String.valueOf(bundle.getLong(ref.ProblemPrice)));
        if (bundle.containsKey(ref.PreviousPrice)){
            previousFee.setPaintFlags(previousFee.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            previousFee.setText("₹ "+String.valueOf(bundle.getLong(ref.PreviousPrice)));
        }
        bundle.putString(ref.TestFee,String.valueOf(bundle.getLong(ref.ProblemPrice)));
        numberOfDays=3;
        final MaterialCheckBox checkBox=findViewById(R.id.firstCheckBox);
        final MaterialCheckBox monthlyPayment=findViewById(R.id.secondCheckBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    numberOfDays=3;
                    monthlyPayment.setChecked(false);
                    findViewById(R.id.payAndStartChat).setClickable(true);
                    findViewById(R.id.payAndStartChat).setAlpha(1);
                    TextView finalFee=findViewById(R.id.finalPayment);
                    finalFee.setText("₹ "+String.valueOf(bundle.getLong(ref.ProblemPrice)));
                    bundle.putString(ref.TestFee,String.valueOf(bundle.getLong(ref.ProblemPrice)));
                }else {
                    if (!monthlyPayment.isChecked()){
                        findViewById(R.id.payAndStartChat).setClickable(false);
                        findViewById(R.id.payAndStartChat).setAlpha(0.5f);
                        TextView finalFee=findViewById(R.id.finalPayment);
                        finalFee.setText("₹ 0.00");
                    }
                }
            }
        });
        monthlyPayment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    numberOfDays=30;
                    checkBox.setChecked(false);
                    findViewById(R.id.payAndStartChat).setClickable(true);
                    findViewById(R.id.payAndStartChat).setAlpha(1);
                    TextView finalFee=findViewById(R.id.finalPayment);
                    finalFee.setText("₹ 599");
                    bundle.putString(ref.TestFee,"599");
                }else {
                    if (!checkBox.isChecked()){
                        findViewById(R.id.payAndStartChat).setClickable(false);
                        findViewById(R.id.payAndStartChat).setAlpha(0.5f);
                        TextView finalFee=findViewById(R.id.finalPayment);
                        finalFee.setText("₹ 0.00");
                    }
                }
            }
        });
    }
    private void DoctorSetUp(){
        et_doctorName=findViewById(R.id.et_doctorName);
        et_doctorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Summary.this,SelectADoctor.class).putExtras(bundle),SelectedDoctor);
            }
        });
    }
    private void SetUpFinalPayment(){
        TextView finalFee=findViewById(R.id.finalPayment);
        finalFee.setText("₹ "+String.valueOf(bundle.getLong(ref.ProblemPrice)));
        findViewById(R.id.payAndStartChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Varified()){
                    Bundle bundle=new Bundle();
                    bundle.putString(ref.TestFee, Summary.this.bundle.getString(ref.TestFee));
                    bundle.putString(ref.PhoneNumber,PhoneNumber);
                    bundle.putString(ref.Email,firebaseUser.getEmail());
                    Log.i("Value",bundle.toString());
                    startActivityForResult(new Intent(Summary.this, PaymentActivity.class).putExtras(bundle),paymentCode);

                }
            }
            private boolean Varified() {
                if (DoctorBundle==null){
                    et_doctorName.setError("Select A Doctor");
                    return false;
                }
                if (PatientBundle==null){
                    et_patientForm.setError("Fill this form");
                    return false;
                }
                et_PhoneNumber=findViewById(R.id.et_PhoneNumber);
                PhoneNumber=et_PhoneNumber.getText().toString();
                if (TextUtils.isEmpty(PhoneNumber)){
                    et_PhoneNumber.setError("Required");
                    et_PhoneNumber.requestFocus();
                    return false;
                }
                if (!isValidPhone(PhoneNumber)){
                    et_PhoneNumber.setError("Phone Number is not valid");
                    et_PhoneNumber.requestFocus();
                    return false;
                }
                return true;
            }
        });
    }
    private void StartChat(String detailsID,String paymentID){
        Bundle bundle=new Bundle();
        bundle.putString(ref.UserName,DoctorBundle.getString(ref.DoctorName));
        bundle.putString(ref.UserImage,DoctorBundle.getString(ref.ImageUrl));
        bundle.putString(ref.ChatLink,DoctorBundle.getString(ref.DoctorId));
        bundle.putString(ref.DetailID,detailsID);
        bundle.putString(ref.PaymentID,paymentID);
        String ChatLink;
        if (Objects.equals(bundle.getString(ref.ChatLink), "Aw1fe17kHiZMP0LQuvtfDgkbmbq1")){
            ChatLink="Aw1fe17kHiZMP0LQuvtfDgkbmbq1"+"_"+firebaseUser.getUid();
        }else {
            if (Objects.requireNonNull(bundle.getString(ref.ChatLink)).compareTo(firebaseUser.getUid())>0){
                ChatLink=bundle.getString(ref.ChatLink)+"_"+firebaseUser.getUid();
            }else {
                ChatLink=firebaseUser.getUid()+"_"+bundle.getString(ref.ChatLink);
            }
        }
        sendChat(ChatLink,"Hi",bundle);
        startActivity(new Intent(Summary.this, UserChat.class).putExtras(bundle));
        finish();
    }

    private void sendChat(String ChatLink ,String msg,Bundle bundle){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(ref.ChatMessages+"/"+ChatLink+"/"+ref.Chat);
        Map<String, Object> user = new HashMap<>();
        user.put(ref.Sender, firebaseUser.getUid());
        user.put(ref.Message, msg);
        user.put(ref.Receiver, bundle.getString(ref.ChatLink));
        user.put(ref.TimeStamp, ServerValue.TIMESTAMP);
        user.put(ref.DetailID,bundle.getString(ref.DetailID));
        user.put(ref.PaymentID,bundle.getString(ref.PaymentID));
        databaseReference.push().setValue(user);
        FirebaseDatabase.getInstance().getReference(ref.LastMessage+"/"+firebaseUser.getUid()+"/"+bundle.getString(ref.ChatLink)).setValue(user);
        FirebaseDatabase.getInstance().getReference(ref.LastMessage+"/"+bundle.getString(ref.ChatLink)+"/"+firebaseUser.getUid()).setValue(user);
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
                            ArrayList<String> arrayList= (ArrayList<String>) documentSnapshot.get(ref.AllImages);
                            MAX_STEP=arrayList.size();
                            myViewPagerAdapter = new MyViewPagerAdapter(Summary.this,arrayList);
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
        LinearLayout dotsLayout = findViewById(R.id.layoutDots);
        ImageView[] dots = new ImageView[MAX_STEP];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(Summary.this);
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

            View view = layoutInflater.inflate(R.layout.item_card_payment, container, false);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==SelectedDoctor){
            Log.i("doctorTest","testone");
            if (resultCode== Activity.RESULT_OK){
                assert data != null;
                DoctorBundle=data.getExtras();
                assert DoctorBundle != null;
                et_doctorName.setText(DoctorBundle.getString(ref.DoctorName));
                et_doctorName.setError(null);
            }
        }else if (requestCode==paymentCode){
            if (resultCode==Activity.RESULT_OK){
                Toast.makeText(Summary.this, "Payment Success", Toast.LENGTH_SHORT).show();
                UploadValuesPayment();
            }else if (resultCode==Activity.RESULT_CANCELED){
                Toast.makeText(Summary.this, "Payment Failed", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(Summary.this, "Request Canceled", Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode==PatientDetails){
            if (resultCode==Activity.RESULT_OK){
                PatientBundle=data.getExtras();
                et_patientForm.setText("Filed");
                et_patientForm.setError(null);
            }
        }
    }

    private void UploadValuesPayment() {
        Map<String, Object> user = new HashMap<>();
        user.put(ref.TimeStamp, FieldValue.serverTimestamp());
        user.put(ref.DoctorId, DoctorBundle.getString(ref.DoctorId));
        user.put(ref.ProblemId, bundle.getString(ref.ProblemId));
        user.put(ref.ProblemPrice,bundle.getString(ref.TestFee));
        user.put(ref.PaymentMethod,0);
        user.put(ref.User,firebaseUser.getUid());
        user.put(ref.NumberOfDays,numberOfDays);
        user.put(ref.PhoneNumber,PhoneNumber);
        if (numberOfDays==30){
            user.put(ref.Package,1);
        }else {
            user.put(ref.Package,0);
        }
        FirebaseFirestore.getInstance().collection(ref.Transactions).add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull final Task<DocumentReference> payment) {
                Map<String, Object> patientDetails = new HashMap<>();
                patientDetails.put(ref.PName,PatientBundle.getString(ref.PName));
                patientDetails.put(ref.PAge,PatientBundle.getString(ref.PAge));
                patientDetails.put(ref.PDetails,PatientBundle.getString(ref.PDetails));
                patientDetails.put(ref.OtherHealthProblem,PatientBundle.getString(ref.OtherHealthProblem));
                patientDetails.put(ref.NumberOfDays,PatientBundle.getString(ref.NumberOfDays));
                patientDetails.put(ref.Gender,PatientBundle.getBoolean(ref.Gender));
                patientDetails.put(ref.Symptoms,(ArrayList<String>)PatientBundle.getSerializable(ref.Symptoms));
                FirebaseFirestore.getInstance().collection(ref.PatientDetails).add(patientDetails).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        loadingDialog.dismiss();
                        if (task.isSuccessful()){
                            StartChat(task.getResult().getId(),payment.getResult().getId());
                        }else {
                            StartChat("","");
                        }
                    }
                });
            }
        });
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static boolean isValidPhone(String phone) {
        Pattern pattern = Pattern.compile(AppPreference.PHONE_PATTERN);

        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
}
