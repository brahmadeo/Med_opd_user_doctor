package com.razahamid.medopddoctor.Profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.razahamid.medopddoctor.Adopters.Reviews;
import com.razahamid.medopddoctor.Adopters.Skills;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.ExtraFiles.LoadingDialog;
import com.razahamid.medopddoctor.Models.ModelReviews;
import com.razahamid.medopddoctor.Models.ModelSkills;
import com.razahamid.medopddoctor.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile extends Fragment {
private View view;
private FirebaseRef ref=new FirebaseRef();
private FirebaseUser firebaseUser;
private LoadingDialog loadingDialog;
private boolean editIcon =true;
private int PICK_IMAGE=101;
private Uri imageUri=null;
private Skills skills;
private int addNewSpeciality;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_profile, container, false);
        loadingDialog=new LoadingDialog(getContext(),R.style.DialogLoadingTheme);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        yourReviewSetUp();
        yourSkillsSetUp();
        setUpProfile();
        view.findViewById(R.id.edithImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        hideEditOption();
        view.findViewById(R.id.AddNewCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(),SelectAHealthProblem.class),addNewSpeciality);
            }
        });


        TextInputEditText professionalHeading=view.findViewById(R.id.professionalHeading);
        TextInputEditText AboutMe=view.findViewById(R.id.AboutMe);
        professionalHeading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView professionCounter=view.findViewById(R.id.professionCounter);
                professionCounter.setText(String.valueOf(s.length())+"/45");
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        AboutMe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView aboutCounter=view.findViewById(R.id.aboutCounter);
                aboutCounter.setText(String.valueOf(s.length())+"/200");
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }
    private void setUpProfile() {
        loadingDialog.show();
        FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                loadingDialog.dismiss();
              try {
                  if (documentSnapshot.contains(ref.ImageUrl)){
                      Glide.with(getContext()).load(documentSnapshot.getString(ref.ImageUrl)).into((CircularImageView) view.findViewById(R.id.image));
                  }
                 TextView name=view.findViewById(R.id.name);
                  name.setText(documentSnapshot.getString(ref.FirstName));
                  TextView phone=view.findViewById(R.id.phoneNumber);
                  phone.setText(documentSnapshot.getString(ref.PhoneNumber));
                  TextView professionalHeading=view.findViewById(R.id.professionalHeading);
                  professionalHeading.setText(documentSnapshot.getString(ref.professionalHeading));
                  TextView aboutMe=view.findViewById(R.id.AboutMe);
                  aboutMe.setText(documentSnapshot.getString(ref.AboutMe));
              }catch (Exception e1){
                  e1.printStackTrace();
              }
            }
        });

    }
    private void yourReviewSetUp() {
        final RecyclerView recyclerView = view.findViewById(R.id.reviewsRecycle);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        final Reviews allReviews =new Reviews(getContext(),new ArrayList<ModelReviews>());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(allReviews);
        FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).collection(ref.Comment).orderBy(ref.Time, Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                allReviews.deleteAllItems();
                assert queryDocumentSnapshots != null;
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    allReviews.insertItems(new ModelReviews(documentSnapshot));
                }
            }
        });
        FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).collection(ref.OverView).document(ref.Rating).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               try {
                   long oneStar=0,twoStar=0,threeStar=0,fourStar=0,fiveStar=0,total=0;
                   DocumentSnapshot documentSnapshot=task.getResult();
                   if (documentSnapshot.contains(ref.Total)){
                       total=documentSnapshot.getLong(ref.Total);
                       TextView overAllReating=view.findViewById(R.id.overAllReating);
                       overAllReating.setText(String.valueOf(total));
                   }
                   if (documentSnapshot.contains(ref.OneStar)){
                       oneStar=documentSnapshot.getLong(ref.OneStar);
                       StarValues(R.id.oneStarProgress,R.id.oneStarText, (int) ((oneStar/total)*100),String.valueOf(oneStar));
                   }
                   if (documentSnapshot.contains(ref.TwoStar)){
                       twoStar=documentSnapshot.getLong(ref.TwoStar);
                       StarValues(R.id.twoStarProgress,R.id.twoStartext, (int) ((twoStar/total)*100),String.valueOf(twoStar));
                   }
                   if (documentSnapshot.contains(ref.ThreeStar)){
                       threeStar=documentSnapshot.getLong(ref.ThreeStar);
                       StarValues(R.id.threeStarProgress,R.id.threeStarText, (int) ((threeStar/total)*100),String.valueOf(threeStar));
                   }
                   if (documentSnapshot.contains(ref.FourStar)){
                       fourStar=documentSnapshot.getLong(ref.FourStar);
                       StarValues(R.id.fourStarProgress,R.id.fourStartext, (int) ((fourStar/total)*100),String.valueOf(fourStar));
                   }
                   if (documentSnapshot.contains(ref.FiveStar)){
                       fiveStar=documentSnapshot.getLong(ref.FiveStar);
                       StarValues(R.id.fiveStartprogress,R.id.fiveStartext, (int) ((fiveStar/total)*100),String.valueOf(fiveStar));
                   }
               }catch (Exception e){
                   e.printStackTrace();
               }
            }
        });
    }
    private void yourSkillsSetUp() {
        final RecyclerView recyclerView = view.findViewById(R.id.skillsRecycle);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        skills =new Skills(getContext(),new ArrayList<ModelSkills>(),firebaseUser.getUid());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(skills);
        FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).collection(ref.Speciality).orderBy(ref.Time, Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                skills.deleteAllItems();
                assert queryDocumentSnapshots != null;
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    skills.insertItems(new ModelSkills(documentSnapshot));
                }
            }
        });
    }
    private void StarValues(int progressId,int textId,int progressValue,String textValue){
     ProgressBar progressBar=view.findViewById(progressId);
     TextView textView=view.findViewById(textId);
     textView.setText(textValue);
     progressBar.setProgress(progressValue);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        menu.findItem(R.id.action_edit).setVisible(editIcon);
        menu.findItem(R.id.action_save).setVisible(!editIcon);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            showEditOptionsDialog();
            return true;
        }else if (item.getItemId()==R.id.action_save){
            updateInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateInfo() {

        loadingDialog.show();
        if (imageUri!=null){
            final StorageReference   photoRef= FirebaseStorage.getInstance().getReference().child("UserImage/").child(imageUri.getLastPathSegment());
            photoRef.putFile(imageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                }
            })
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            // Forward any exceptions
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return photoRef.getDownloadUrl();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(@NonNull Uri downloadUri) {
                            imageUri=null;
                            String uri= downloadUri.toString();
                            uploadInfo(uri);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });
        }else {
            uploadInfo(null);
        }
    }

    private void uploadInfo(String uri) {
        Map<String, Object> user = new HashMap<>();
        TextInputEditText professionalHeading=view.findViewById(R.id.professionalHeading);
        TextInputEditText AboutMe=view.findViewById(R.id.AboutMe);
        user.put(ref.professionalHeading,professionalHeading.getText().toString());
        user.put(ref.AboutMe, AboutMe.getText().toString());
        if (uri!=null){
            user.put(ref.ImageUrl,uri);
        }
        FirebaseFirestore.getInstance().collection(ref.Users).document(firebaseUser.getUid()).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingDialog.dismiss();
             if (task.isSuccessful()){
                   hideEditOption();
             }
            }
        });
    }

    private void hideEditOption() {
        view.clearFocus();
        view.findViewById(R.id.edithImage).setVisibility(View.GONE);
        view.findViewById(R.id.edithable).setClickable(true);
        editIcon=true;
        skills.hideDeleteOption();
        getActivity().invalidateOptionsMenu();
    }
    private void showEditOptionsDialog() {
        view.findViewById(R.id.edithImage).setVisibility(View.VISIBLE);
        view.findViewById(R.id.edithable).setClickable(false);
        editIcon=false;
        skills.showDeleteOption();
        getActivity().invalidateOptionsMenu();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (resultCode== Activity.RESULT_OK){
                if (data!=null)
                {imageUri=data.getData();
                Glide.with(getContext()).load(imageUri).into((CircularImageView)view.findViewById(R.id.image));}
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
