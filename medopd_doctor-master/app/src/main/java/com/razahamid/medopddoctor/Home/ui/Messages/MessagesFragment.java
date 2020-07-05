package com.razahamid.medopddoctor.Home.ui.Messages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razahamid.medopddoctor.Adopters.AllMessagesList;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.Home.MainActivity;
import com.razahamid.medopddoctor.Models.AllMessages;
import com.razahamid.medopddoctor.R;
import com.razahamid.medopddoctor.utils.Tools;
import com.razahamid.medopddoctor.widget.LineItemDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public class MessagesFragment extends Fragment {
private View view;
private AllMessagesList allMessagesList;
private FirebaseRef ref=new FirebaseRef();
private FirebaseUser firebaseUser;
private Calendar calendar;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_messages, container, false);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        calendar=Calendar.getInstance();;
        setUpRecycleView();
        FloatingActionButton fab=view.findViewById(R.id.fab);
        fab.hide();
        return view;
    }

    private void setUpRecycleView() {
      RecyclerView recyclerView = view.findViewById(R.id.allMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new LineItemDecoration(getContext(), LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);
        allMessagesList=new AllMessagesList(getContext(),new ArrayList<AllMessages>());
        recyclerView.setAdapter(allMessagesList);
        allMessagesList.setClickListener(new AllMessagesList.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle=new Bundle();
                AllMessages allMessages=allMessagesList.getCurrentMessage(position);
                bundle.putString(ref.UserName,allMessages.userName);
                bundle.putString(ref.UserImage,allMessages.userImage);
                bundle.putString(ref.ChatLink,allMessages.snapshot.getKey());
                bundle.putString(ref.PaymentID,allMessages.snapshot.child(ref.PaymentID).getValue(String.class));
                bundle.putString(ref.DetailID,allMessages.snapshot.child(ref.DetailID).getValue(String.class));
                startActivity(new Intent(getContext(), UserChat.class).putExtras(bundle));
            }
        });
    }

    private void UpdateValue() {
        FirebaseDatabase.getInstance().getReference(ref.LastMessage+"/"+firebaseUser.getUid()).orderByChild(ref.TimeStamp).limitToLast(30).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              try {
                  if (dataSnapshot.exists()){
                      allMessagesList.deleteAllItems();
                      for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                          Calendar MessageTime=Calendar.getInstance();
                          MessageTime.setTimeInMillis(snapshot.child(ref.TimeStamp).getValue(Long.class));
                           if (calendar.get(Calendar.DAY_OF_YEAR)==MessageTime.get(Calendar.DAY_OF_YEAR)){
                               allMessagesList.insertAtTop(new AllMessages(Tools.getFormattedTimeEvent(snapshot.child(ref.TimeStamp).getValue(Long.class)),snapshot));
                           }else if (calendar.get(Calendar.DAY_OF_YEAR)==MessageTime.get(Calendar.DAY_OF_YEAR)-1){
                               allMessagesList.insertAtTop(new AllMessages("Yesterday "+ Tools.getFormattedTimeEvent(snapshot.child(ref.TimeStamp).getValue(Long.class)),snapshot));
                           }else {
                               allMessagesList.insertAtTop(new AllMessages(Tools.getFormattedDateSimple(snapshot.child(ref.TimeStamp).getValue(Long.class)),snapshot));
                           }
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

    @Override
    public void onResume() {
     ((MainActivity) getActivity()).getSupportActionBar().setTitle("Messages");
        super.onResume();
        UpdateValue();
    }

}
