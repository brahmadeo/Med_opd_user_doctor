package com.razahamid.medopd.Home.ui.Messages;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.razahamid.medopd.Adopters.AdapterChat;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.Models.Message;
import com.razahamid.medopd.Models.UploadFilesModel;
import com.razahamid.medopd.Payment.AppPreference;
import com.razahamid.medopd.R;
import com.razahamid.medopd.Services.MyUploadService;
import com.razahamid.medopd.utils.Tools;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserChat extends AppCompatActivity {
    private ImageView btn_send;
    private EditText et_content;
    private AdapterChat adapter;
    private RecyclerView recycler_view;
    private Bundle bundle;
    private String ChatLink=null;
    private FirebaseRef ref=new FirebaseRef();
    private FirebaseUser firebaseUser;
    private DocumentSnapshot documentSnapshot=null;
    private Uri imageUri=null;
    private int PICK_IMAGE=101;
    private ArrayList<UploadFilesModel> selectedFiles=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bundle=getIntent().getExtras();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (Objects.equals(bundle.getString(ref.ChatLink), "Aw1fe17kHiZMP0LQuvtfDgkbmbq1")){
            ChatLink="Aw1fe17kHiZMP0LQuvtfDgkbmbq1"+"_"+firebaseUser.getUid();
        }else {
            paymentSetUp();
            if (Objects.requireNonNull(bundle.getString(ref.ChatLink)).compareTo(firebaseUser.getUid())>0){
                ChatLink=bundle.getString(ref.ChatLink)+"_"+firebaseUser.getUid();
            }else {
                ChatLink=firebaseUser.getUid()+"_"+bundle.getString(ref.ChatLink);
            }
        }

        initToolbar();
        iniComponent();
        findViewById(R.id.selectImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        findViewById(R.id.callRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCall();
            }
        });
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            }, 1);
        }
    }
    private void paymentSetUp() {
        FirebaseFirestore.getInstance().collection(ref.Transactions).document(bundle.getString(ref.PaymentID)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                UserChat.this.documentSnapshot=documentSnapshot;
               CheckForTime();
            }
        });
    }
    private void CheckForTime(){
        try {
            if (documentSnapshot==null){
                return;
            }
            Date date=documentSnapshot.getDate(ref.TimeStamp);
            Calendar CurrentDate=Calendar.getInstance();
            Calendar endDate=Calendar.getInstance();
            endDate.setTime(date);
            endDate.set(Calendar.DAY_OF_YEAR,endDate.get(Calendar.DAY_OF_YEAR)+  Objects.requireNonNull(documentSnapshot.getLong(ref.NumberOfDays)).intValue());
            if ((CurrentDate.getTimeInMillis()-endDate.getTimeInMillis())>0){
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(UserChat.this,  "Time out kindly buy new bundle ", Toast.LENGTH_SHORT).show();
                    }
                });
               findViewById(R.id.callRequest).setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Toast.makeText(UserChat.this,  "Time out kindly buy new bundle ", Toast.LENGTH_SHORT).show();
                   }
               });
            }
            TextView ExpireTime=findViewById(R.id.ExpireTime);
            ExpireTime.setText("Expire at: "+new SimpleDateFormat("MMM dd HH:mm").format(endDate.getTime()));
        }catch (Exception e1){
            e1.printStackTrace();
        }

    }
    public void initToolbar() {
        TextView UserName=findViewById(R.id.UserName);
        UserName.setText(bundle.getString(ref.UserName));
        try {
            Picasso.get().load(bundle.getString(ref.UserImage)).into((CircularImageView) findViewById(R.id.image));
        }catch (Exception e){
            e.printStackTrace();
        }
        FirebaseDatabase.getInstance().getReference(ref.Users+"/"+bundle.getString(ref.ChatLink)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             try {
                 if (dataSnapshot.exists()){
                     if (dataSnapshot.hasChild(ref.LastOnline)){
                         TextView lastOnline=findViewById(R.id.lastOnline);
                         lastOnline.setText(dataSnapshot.child(ref.LastOnline).getValue(String.class));
                     }
                 }
             }catch (Exception e){
                 e.printStackTrace();
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void iniComponent() {
        recycler_view = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setHasFixedSize(true);

        adapter = new AdapterChat(this);
        recycler_view.setAdapter(adapter);
        btn_send = findViewById(R.id.btn_send);
        et_content = findViewById(R.id.text_content);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendChat();
            }
        });
        et_content.addTextChangedListener(contentWatcher);

        (findViewById(R.id.lyt_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        /*FirebaseDatabase.getInstance().getReference(ref.ChatMessages+"/"+ChatLink+"/"+ref.Chat).limitToLast(30).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot mainSnapShort) {
                try {
                    if (mainSnapShort.exists()){
                        for (DataSnapshot dataSnapshot:mainSnapShort.getChildren()){
                            adapter.insertItem(new Message(adapter.getItemCount(), dataSnapshot.child(ref.Message).getValue(String.class),dataSnapshot.child(ref.Sender).getValue(String.class).equals(firebaseUser.getUid()), adapter.getItemCount() % 5 == 0, Tools.getFormattedTimeEvent(dataSnapshot.child(ref.TimeStamp).getValue(Long.class))));
                            recycler_view.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        FirebaseDatabase.getInstance().getReference(ref.ChatMessages+"/"+ChatLink+"/"+ref.Chat).limitToLast(30).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("valueOnadded",dataSnapshot.toString());
              try {
                  if (dataSnapshot.exists()){
                      adapter.insertItem(new Message(adapter.getItemCount(),dataSnapshot,dataSnapshot.child(ref.Sender).getValue(String.class).equals(firebaseUser.getUid())));
                      recycler_view.scrollToPosition(adapter.getItemCount() - 1);
                  }
              }catch (Exception e){
                  e.printStackTrace();
              }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("valueOnChanged",dataSnapshot.toString());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.i("valueOnRemoved",dataSnapshot.toString());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("valueOnMoved",dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
    private void sendChat(){
            CheckForTime();
        final String msg = et_content.getText().toString();
        if (imageUri!=null){
            findViewById(R.id.selectedImage).setVisibility(View.GONE);
            selectedFiles.add(new UploadFilesModel(imageUri,ChatLink,firebaseUser.getUid(),msg,bundle.getString(ref.ChatLink)));
            imageUri=null;
            uploadFromUri();
            return;
        }
        if (msg.isEmpty()){
            return;
        }
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference(ref.ChatMessages+"/"+ChatLink+"/"+ref.Chat);
        Map<String, Object> user = new HashMap<>();
        user.put(ref.Sender, firebaseUser.getUid());
        user.put(ref.Message, msg);
        user.put(ref.Receiver, bundle.getString(ref.ChatLink));
        user.put(ref.TimeStamp, ServerValue.TIMESTAMP);
        user.put(ref.MessageType,ref.Text);
        et_content.setText("");
        databaseReference.push().setValue(user);
        FirebaseDatabase.getInstance().getReference(ref.LastMessage+"/"+firebaseUser.getUid()+"/"+bundle.getString(ref.ChatLink)).updateChildren(user);
        FirebaseDatabase.getInstance().getReference(ref.LastMessage+"/"+bundle.getString(ref.ChatLink)+"/"+firebaseUser.getUid()).updateChildren(user);
    }
    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        hideKeyboard();
    }
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private TextWatcher contentWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable etd) {
            if (imageUri==null){
                if (etd.toString().trim().length() == 0) {
                    btn_send.setImageResource(R.drawable.ic_mic);
                } else {
                    btn_send.setImageResource(R.drawable.ic_send);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (resultCode== Activity.RESULT_OK){
                if (data!=null)
                {imageUri=data.getData();
                    Glide.with(UserChat.this).load(imageUri).into((ImageView) findViewById(R.id.selectedImage));
                    findViewById(R.id.selectedImage).setVisibility(View.VISIBLE);
                    btn_send.setImageResource(R.drawable.ic_send);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadFromUri() {
        if (isMyServiceRunning(MyUploadService.class)){
            Bundle bundle=new Bundle();
            bundle.putSerializable(MyUploadService.EXTRA_FILE_URI,selectedFiles);
            startService(new Intent(UserChat.this, MyUploadService.class)
                    .putExtras(bundle)
                    .setAction(MyUploadService.Add_New_File));
        }else {
            Bundle bundle=new Bundle();
            bundle.putSerializable(MyUploadService.EXTRA_FILE_URI,selectedFiles);
            startService(new Intent(UserChat.this, MyUploadService.class)
                    .putExtras(bundle)
                    .setAction(MyUploadService.ACTION_UPLOAD));
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void showDialogCall() {
        final Dialog dialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_call);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        final TextInputEditText phoneNumberText=dialog.findViewById(R.id.et_number);
        if (documentSnapshot!=null){
            if (documentSnapshot.contains(ref.PhoneNumber)){
                phoneNumberText.setText(documentSnapshot.getString(ref.PhoneNumber));
            }
        }
        dialog.findViewById(R.id.bt_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Varified()){
                    String  PhoneNumber=phoneNumberText.getText().toString();
                  final ProgressDialog  progressDialog = ProgressDialog.show(UserChat.this, "Sending Call Request", "Please Wait...", true);
                    Map<String, Object> user = new HashMap<>();
                    user.put(ref.ChatLink, ChatLink);
                    user.put(ref.TimeStamp, FieldValue.serverTimestamp());
                    user.put(ref.User, firebaseUser.getUid());
                    user.put(ref.PhoneNumber,PhoneNumber);
                    user.put(ref.Status,0);
                  FirebaseFirestore.getInstance().collection(ref.CallRequest).add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                      @Override
                      public void onComplete(@NonNull Task<DocumentReference> task) {
                          progressDialog.dismiss();
                          try {
                              dialog.dismiss();
                          }catch (Exception e){
                              e.printStackTrace();
                          }
                          Toast.makeText(UserChat.this,  "Call Request Sent", Toast.LENGTH_SHORT).show();
                      }
                  });
                }
            }

            private boolean Varified() {
              String  PhoneNumber=phoneNumberText.getText().toString();
                if (TextUtils.isEmpty(PhoneNumber)){
                    phoneNumberText.setError("Required");
                    phoneNumberText.requestFocus();
                    return false;
                }
                if (!isValidPhone(PhoneNumber)){
                    phoneNumberText.setError("Phone Number is not valid");
                    phoneNumberText.requestFocus();
                    return false;
                }
                return true;
            }
        });

        dialog.findViewById(R.id.closeDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static boolean isValidPhone(String phone) {
        Pattern pattern = Pattern.compile(AppPreference.PHONE_PATTERN);

        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
}