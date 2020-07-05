package com.razahamid.medopddoctor.Home.ui.Messages;

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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.razahamid.medopddoctor.Adopters.AdapterChat;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.Models.Message;
import com.razahamid.medopddoctor.Models.UploadFilesModel;
import com.razahamid.medopddoctor.Payment.AppPreference;
import com.razahamid.medopddoctor.R;
import com.razahamid.medopddoctor.Services.MyUploadService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
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
    private String ChatLink = null;
    private FirebaseRef ref = new FirebaseRef();
    private FirebaseUser firebaseUser;
    private int PICK_IMAGE = 101;
    private Uri imageUri = null;
    private ArrayList<UploadFilesModel> selectedFiles = new ArrayList<>();
    private DocumentSnapshot callDocoment = null;
    private FirebaseAuth mAuth;
    String totalEarning = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bundle = getIntent().getExtras();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (Objects.equals(bundle.getString(ref.ChatLink), "Aw1fe17kHiZMP0LQuvtfDgkbmbq1")) {
            ChatLink = "Aw1fe17kHiZMP0LQuvtfDgkbmbq1" + "_" + firebaseUser.getUid();
            findViewById(R.id.PatientDetails).setVisibility(View.GONE);
        } else {
            if (Objects.requireNonNull(bundle.getString(ref.ChatLink)).compareTo(firebaseUser.getUid()) > 0) {
                ChatLink = bundle.getString(ref.ChatLink) + "_" + firebaseUser.getUid();
            } else {
                ChatLink = firebaseUser.getUid() + "_" + bundle.getString(ref.ChatLink);
            }
        }
        initToolbar();
        iniComponent();
        checkForCall();
        findViewById(R.id.btn_add_prescription).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserChat.this, AddPrescriptionActivity.class).putExtras(bundle));
            }
        });

        findViewById(R.id.PatientDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserChat.this, PatientDetails.class).putExtras(bundle));
            }
        });

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
                final ProgressDialog progressDialog = ProgressDialog.show(UserChat.this, "Checking Details", "Please Wait...", true);
                FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            try {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.contains(ref.PhoneNumber)) {
                                    showDialogCall(documentSnapshot.getString(ref.PhoneNumber));
                                } else {
                                    showDialogCall("");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
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

    private void checkForCall() {
        FirebaseFirestore.getInstance().collection(ref.CallRequest).whereEqualTo(ref.ChatLink, ChatLink).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                try {
                    assert queryDocumentSnapshots != null;
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        if (documentSnapshot.exists()) {
                            callDocoment = documentSnapshot;
                            findViewById(R.id.callRequest).setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void initToolbar() {
        TextView UserName = findViewById(R.id.UserName);
        UserName.setText(bundle.getString(ref.UserName));
        try {
            Picasso.get().load(bundle.getString(ref.UserImage)).into((CircularImageView) findViewById(R.id.image));
        } catch (Exception e) {
            e.printStackTrace();
        }
        FirebaseDatabase.getInstance().getReference(ref.Users + "/" + bundle.getString(ref.ChatLink)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChild(ref.LastOnline)) {
                            TextView lastOnline = findViewById(R.id.lastOnline);
                            lastOnline.setText(dataSnapshot.child(ref.LastOnline).getValue(String.class));
                        }
                    }
                } catch (Exception e) {
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

        adapter = new AdapterChat(this, firebaseUser.getUid(), bundle.getString(ref.DetailID));
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
        FirebaseDatabase.getInstance().getReference(ref.ChatMessages + "/" + ChatLink + "/" + ref.Chat).limitToLast(30).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("valueOnadded", dataSnapshot.toString());
                try {
                    if (dataSnapshot.exists()) {
                        adapter.insertItem(new Message(adapter.getItemCount(), dataSnapshot, dataSnapshot.child(ref.Sender).getValue(String.class).equals(firebaseUser.getUid())));
                        recycler_view.scrollToPosition(adapter.getItemCount() - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("valueOnChanged", dataSnapshot.toString());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.i("valueOnRemoved", dataSnapshot.toString());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("valueOnMoved", dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void sendChat() {
        final String msg = et_content.getText().toString();
        if (imageUri != null) {
            findViewById(R.id.selectedImage).setVisibility(View.GONE);
            selectedFiles.add(new UploadFilesModel(imageUri, ChatLink, firebaseUser.getUid(), msg, bundle.getString(ref.ChatLink)));
            imageUri = null;
            uploadFromUri();
            return;
        }
        if (msg.isEmpty()) {
            return;
        }
        findViewById(R.id.selectedImage).setVisibility(View.GONE);
        imageUri = null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(ref.ChatMessages + "/" + ChatLink + "/" + ref.Chat);
        Map<String, Object> user = new HashMap<>();
        user.put(ref.Sender, firebaseUser.getUid());
        user.put(ref.Message, msg);
        user.put(ref.Receiver, bundle.getString(ref.ChatLink));
        user.put(ref.TimeStamp, ServerValue.TIMESTAMP);
        et_content.setText("");
        databaseReference.push().setValue(user);
        FirebaseDatabase.getInstance().getReference(ref.LastMessage + "/" + firebaseUser.getUid() + "/" + bundle.getString(ref.ChatLink)).updateChildren(user);
        FirebaseDatabase.getInstance().getReference(ref.LastMessage + "/" + bundle.getString(ref.ChatLink) + "/" + firebaseUser.getUid()).updateChildren(user);
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
            if (etd.toString().trim().length() == 0) {
                btn_send.setImageResource(R.drawable.ic_mic);
            } else {
                btn_send.setImageResource(R.drawable.ic_send);
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
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();
                    Glide.with(UserChat.this).load(imageUri).into((ImageView) findViewById(R.id.selectedImage));
                    findViewById(R.id.selectedImage).setVisibility(View.VISIBLE);
                    btn_send.setImageResource(R.drawable.ic_send);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadFromUri() {
        if (isMyServiceRunning(MyUploadService.class)) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(MyUploadService.EXTRA_FILE_URI, selectedFiles);
            startService(new Intent(UserChat.this, MyUploadService.class)
                    .putExtras(bundle)
                    .setAction(MyUploadService.Add_New_File));
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(MyUploadService.EXTRA_FILE_URI, selectedFiles);
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


    private void showDialogCall(String number) {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_call);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        final TextInputEditText phoneNumberText = dialog.findViewById(R.id.et_number);
        phoneNumberText.setText(number);
        dialog.findViewById(R.id.bt_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Varified()) {
                    // save Earning of doctor
                    EarningReport();

                    String PhoneNumber = phoneNumberText.getText().toString();
                    RequestQueue queue = Volley.newRequestQueue(UserChat.this);
                    String url = "http://c2c.minavo.in/make-call-c2n.php?mobile1=" + callDocoment.getString(ref.PhoneNumber) + "&mobile2=" + PhoneNumber + "&apikey=4859043a-a103-4986-a7d6-1aa3d3ca65e4&secret=582c0f85a7daa&call_priority=1";
          //       url ="http://c2c.minavo.in/make-call-c2n.php?mobile1=9544200866&mobile2=9470244795&apikey=4859043a-a103-4986-a7d6-1aa3d3ca65e4&secret=582c0f85a7daa&call_priority=1";
                    Log.i("url", url);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("response", response);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("response", error.getLocalizedMessage());
                        }
                    });
                    queue.add(stringRequest);
                    dialog.dismiss();
                }
            }

            private boolean Varified() {
                String PhoneNumber = phoneNumberText.getText().toString();
                if (TextUtils.isEmpty(PhoneNumber)) {
                    phoneNumberText.setError("Required");
                    phoneNumberText.requestFocus();
                    return false;
                }
                if (!isValidPhone(PhoneNumber)) {
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
    private void EarningReport(){
        final FirebaseUser user = mAuth.getCurrentUser();
        Log.i("value",user.getUid());
        FirebaseFirestore.getInstance().collection(ref.EarningReport).document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot!=null && documentSnapshot.contains(ref.EarningReport) ){
                        Log.i("status","not null");
                        totalEarning = documentSnapshot.getString(ref.Earning);
                        SaveEarning(totalEarning);
                    }else {
                        Log.i("status","null");
                    }
                }else {
                    Toast.makeText(UserChat.this , "Failed to fetch try again", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserChat.this , "Failed to fetch try again", Toast.LENGTH_SHORT).show();
            }
        }) ;
    }
    private void SaveEarning(String totalEarning){
        Map<String, Object> user = new HashMap<>();
        int newTotal = Integer.parseInt(totalEarning) + 100;
        user.put(ref.Earning, newTotal);

        FirebaseFirestore.getInstance().collection(ref.EarningReport).document(firebaseUser.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(UserChat.this, " Earning saved", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(UserChat.this, "Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public static boolean isValidPhone(String phone) {
        Pattern pattern = Pattern.compile(AppPreference.PHONE_PATTERN);

        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

}