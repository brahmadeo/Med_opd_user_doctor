package com.razahamid.medopddoctor.Home.ui.Account.Test;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.ExtraFiles.LoadingDialog;
import com.razahamid.medopddoctor.Payment.AppPreference;
import com.razahamid.medopddoctor.Payment.PaymentActivity;
import com.razahamid.medopddoctor.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterTest extends AppCompatActivity {
    private FirebaseRef ref=new FirebaseRef();
    private Bundle bundle;
    private LoadingDialog loadingDialog;
    private FirebaseUser firebaseUser;
    private Spinner CityNameSpinner;
    private ArrayAdapter<String> CityAdopter;
    private ArrayList<String> CitiesNames;
    private Spinner HospitalNameSpinner;
    private ArrayAdapter<String> HospitalAdopter;
    private ArrayList<String> HospitalNames;
    private String PhoneNumber,Name,AddressLineTwo;
    private boolean City=false;
    private boolean Hospital=false;
    private CalendarView calendarView;
    private Date date=new Date();
    private LatLng latLng=null;
    private String MapAddress;
    private int SelectLocation=102;
    private int paymentCode=103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_test);
        loadingDialog=new LoadingDialog(RegisterTest.this,R.style.DialogLoadingTheme);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        calendarView=findViewById(R.id.calendarView);
        calendarView.setMinDate(Calendar.getInstance().getTime().getTime());
        bundle=getIntent().getExtras();
        setTitleValues();
        initToolbar();
        findViewById(R.id.submite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Verified()){

                   UploadValues();
                }
            }
        });

        findViewById(R.id.payNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (Verified()){
                   Bundle bundle=new Bundle();
                   bundle.putString(ref.TestFee,RegisterTest.this.bundle.getString(ref.TestFee));
                   bundle.putString(ref.PhoneNumber,PhoneNumber);
                   bundle.putString(ref.Email,firebaseUser.getEmail());
                   startActivityForResult(new Intent(RegisterTest.this, PaymentActivity.class).putExtras(bundle),paymentCode);
               }
              }
        });
    }

    private void UploadValues() {
        loadingDialog.show();
        Map<String, Object> user = new HashMap<>();
        user.put(ref.User, firebaseUser.getUid());
        user.put(ref.FirstName, Name);
        user.put(ref.PhoneNumber, PhoneNumber);
        user.put(ref.CityName,CityNameSpinner.getSelectedItem());
        user.put(ref.HospitalName,HospitalNameSpinner.getSelectedItem());
        user.put(ref.TestDate,date);
        user.put(ref.Time, FieldValue.serverTimestamp());
        user.put(ref.TestId,bundle.getString(ref.TestId));
        user.put(ref.status,0);
        user.put(ref.TestFee,Long.parseLong(bundle.getString(ref.TestFee)));
        user.put(ref.TestName,bundle.getString(ref.TestName));
        user.put(ref.PaymentMethod,1);
        user.put(ref.AddressLineTwo,AddressLineTwo);
        GeoPoint point=new GeoPoint(latLng.latitude,latLng.longitude);
        user.put(ref.Location,point);
        user.put(ref.MapAddress,MapAddress);
        FirebaseFirestore.getInstance().collection(ref.TestRequests).add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                loadingDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(RegisterTest.this, "Added " , Toast.LENGTH_SHORT).show();
                    finish();
                    Log.i("ID",task.getResult().getId());
                }else {
                    Toast.makeText(RegisterTest.this, "Error try again later" , Toast.LENGTH_SHORT).show();
                }
            }
        });
        Map<String, Object> UpdateData = new HashMap<>();
        UpdateData.put(ref.PhoneNumber, PhoneNumber);
        FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).update(UpdateData);
    }
    private void UploadValuesPayment() {
        loadingDialog.show();
        Map<String, Object> user = new HashMap<>();
        user.put(ref.User, firebaseUser.getUid());
        user.put(ref.FirstName, Name);
        user.put(ref.PhoneNumber, PhoneNumber);
        user.put(ref.CityName,CityNameSpinner.getSelectedItem());
        user.put(ref.HospitalName,HospitalNameSpinner.getSelectedItem());
        user.put(ref.TestDate,date);
        user.put(ref.Time, FieldValue.serverTimestamp());
        user.put(ref.TestId,bundle.getString(ref.TestId));
        user.put(ref.status,0);
        user.put(ref.TestFee,Long.parseLong(bundle.getString(ref.TestFee)));
        user.put(ref.TestName,bundle.getString(ref.TestName));
        user.put(ref.PaymentMethod,0);
        user.put(ref.AddressLineTwo,AddressLineTwo);
        GeoPoint point=new GeoPoint(latLng.latitude,latLng.longitude);
        user.put(ref.Location,point);
        user.put(ref.MapAddress,MapAddress);
        FirebaseFirestore.getInstance().collection(ref.TestRequests).add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                loadingDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(RegisterTest.this, "Added " , Toast.LENGTH_SHORT).show();
                    finish();
                    Log.i("ID",task.getResult().getId());
                }else {
                    Toast.makeText(RegisterTest.this, "Error try again later" , Toast.LENGTH_SHORT).show();
                }
            }
        });
        Map<String, Object> UpdateData = new HashMap<>();
        UpdateData.put(ref.PhoneNumber, PhoneNumber);
        FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).update(UpdateData);
    }
    public static boolean isValidPhone(String phone) {
        Pattern pattern = Pattern.compile(AppPreference.PHONE_PATTERN);

        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private boolean Verified() {
        Name=getTextFormView(R.id.UserName);
        PhoneNumber=getTextFormView(R.id.PhoneNumber);
        AddressLineTwo=getTextFormView(R.id.AddressLineTwo);
        if (checkAndSetError(Name, R.id.UserName)){
            return false;
        }
        if (checkAndSetError(PhoneNumber, R.id.PhoneNumber)){
            return false;
        }
        if (!isValidPhone(PhoneNumber)){
            TextView text=findViewById(R.id.PhoneNumber);
            text.setError("Phone Number is not valid");
            text.requestFocus();
            return false;
        }
        if (!City || !Hospital){
            setTitleValues();
        }
        if (latLng==null){
            TextView mapAddressText=findViewById(R.id.mapAddressText);
            mapAddressText.setError("Required");
            mapAddressText.requestFocus();
            return false;
        }
        date.setTime(calendarView.getDate());

        return true;
    }
    private boolean checkAndSetError(String value,int id) {
        if (TextUtils.isEmpty(value)){
            TextView text=findViewById(id);
            text.setError("Required");
            text.requestFocus();
            return true;
        }
        return false;
    }
    private String getTextFormView(int id) {
        TextView text=findViewById(id);
        return Objects.requireNonNull(text.getText()).toString();
    }
    private void setTitleValues() {
        loadingDialog.show();
        TextView TestName=findViewById(R.id.TestName);
        TestName.setText(bundle.getString(ref.TestName));
        TextView TestFee=findViewById(R.id.Fee);
        TestFee.setText("₹ "+bundle.getString(ref.TestFee));
        if (bundle.containsKey(ref.instructionsText)){
            TextView instructionsText=findViewById(R.id.instructionsText);
            instructionsText.setText(bundle.getString(ref.instructionsText));
            findViewById(R.id.InsBox).setVisibility(View.VISIBLE);
        }
        FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                loadingDialog.dismiss();
            if (task.isSuccessful()){
                 try {
                  DocumentSnapshot documentSnapshot=task.getResult();
                  if (documentSnapshot.exists()){
                      EditText userName=findViewById(R.id.UserName);
                      userName.setText(documentSnapshot.getString(ref.FirstName));
                      if (documentSnapshot.contains(ref.PhoneNumber)){
                          EditText PhoneNumber=findViewById(R.id.PhoneNumber);
                          PhoneNumber.setText(documentSnapshot.getString(ref.PhoneNumber));
                      }
                  }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            }
        });
        FirebaseFirestore.getInstance().collection(ref.AllTestLocations).whereEqualTo(ref.TestName,bundle.getString(ref.TestName)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.i("Data","here");
                if (task.isSuccessful()){
                    CitiesNames=new ArrayList<>();
                    Log.i("Data","here2");
                  for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                      Log.i("Data","here3");
                    if (!CitiesNames.contains(documentSnapshot.getString(ref.CityName))){
                        Log.i("Data","here4");
                        CitiesNames.add(documentSnapshot.getString(ref.CityName));
                        City=true;
                    }
                  }
                    Log.i("Data",CitiesNames.toString());
                    CityNameSpinner =findViewById(R.id.CitySpinner);
                    CityAdopter=new ArrayAdapter<>(RegisterTest.this,R.layout.support_simple_spinner_dropdown_item,CitiesNames);
                    CityAdopter.setNotifyOnChange(true);
                    CityNameSpinner.setAdapter(CityAdopter);
                    CityNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                         loadingDialog.dismiss();
                         loadingDialog.show();
                         FirebaseFirestore.getInstance().collection(ref.AllTestLocations).whereEqualTo(ref.TestName,bundle.getString(ref.TestName))
                                 .whereEqualTo(ref.CityName,CityNameSpinner.getSelectedItem()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                             @Override
                             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                 loadingDialog.dismiss();
                               if (task.isSuccessful()){
                                   HospitalNames=new ArrayList<>();
                                   Log.i("Data","here2");
                                   for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                                       Log.i("Data","here3");
                                       if (!HospitalNames.contains(documentSnapshot.getString(ref.HospitalName))){
                                           Log.i("Data","here4");
                                           HospitalNames.add(documentSnapshot.getString(ref.HospitalName));
                                           Hospital=true;
                                       }
                                   }
                                   Log.i("Data",HospitalNames.toString());
                                   HospitalNameSpinner =findViewById(R.id.spinnerHospital);
                                   HospitalAdopter=new ArrayAdapter<>(RegisterTest.this,R.layout.support_simple_spinner_dropdown_item,HospitalNames);
                                   HospitalAdopter.setNotifyOnChange(true);
                                   HospitalNameSpinner.setAdapter(HospitalAdopter);
                               }
                             }
                         });
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }
        });
    }
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fill Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==SelectLocation){
            if (resultCode== Activity.RESULT_OK){
                try {
                    assert data != null;
                    Bundle bundle=data.getExtras();
                    assert bundle != null;
                    latLng=new LatLng(bundle.getDouble("lat"),bundle.getDouble("log"));
                    MapAddress=bundle.getString("address","");
                    TextView Address=findViewById(R.id.MapAddress);
                    Address.setText(RegisterTest.this.MapAddress);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                Log.i("result","Cancel");
            }
        }else if (requestCode==paymentCode){
            if (resultCode==Activity.RESULT_OK){
                Toast.makeText(RegisterTest.this, "Payment Success", Toast.LENGTH_SHORT).show();
                UploadValuesPayment();
            }else if (resultCode==Activity.RESULT_CANCELED){
                Toast.makeText(RegisterTest.this, "Payment Failed", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(RegisterTest.this, "Request Canceled", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
