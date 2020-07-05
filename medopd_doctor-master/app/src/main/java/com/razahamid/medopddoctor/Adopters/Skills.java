package com.razahamid.medopddoctor.Adopters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.Models.ModelSkills;
import com.razahamid.medopddoctor.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class Skills extends RecyclerView.Adapter<Skills.ViewHolder> {

    private ArrayList<ModelSkills> modelSkills;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private FirebaseRef ref=new FirebaseRef();
    private Context mContext;
    private String userUID;

    // data is passed into the constructor
    public Skills(Context context, ArrayList<ModelSkills> modelSkills,String userUID) {
        this.mInflater   = LayoutInflater.from(context);
        this.modelSkills = modelSkills;
        this.userUID=userUID;
        mContext=context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = mInflater.inflate(R.layout.speciality_card_recycle, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DocumentSnapshot documentSnapshot= modelSkills.get(position).documentSnapshot;
        modelSkills.get(position).setViewHolder(holder);
        holder.specialityName.setText(documentSnapshot.getString(ref.Speciality));
        if (documentSnapshot.getLong(ref.status)==0){
            holder.status.setText("pending");
        }else if (documentSnapshot.getLong(ref.status)==1){
            holder.status.setText("Accepted");
        }else if (documentSnapshot.getLong(ref.status)==2){
            holder.status.setText("Rejected");
        }
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection(ref.Users).document(userUID).collection(ref.Speciality).document(documentSnapshot.getId()).delete();
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection(ref.Users).document(userUID);
                documentReference.update(ref.Specialities, FieldValue.arrayRemove(documentSnapshot.getString(ref.SpecialityId))).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(mContext, "removed", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(mContext, "not added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return modelSkills.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       private TextView specialityName,status;
       private ImageView deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            specialityName =itemView.findViewById(R.id.specialityName);
            status=itemView.findViewById(R.id.status);
            deleteButton =itemView.findViewById(R.id.deleteSkill);
        }

        @Override
        public void onClick(View view) {

            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

   public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void insertItems(ModelSkills modelSkills){

               this.modelSkills.add(modelSkills);
               notifyItemInserted(getItemCount() - 1);
               notifyDataSetChanged();

    }
    public void insertAtTop(ModelSkills modelSkills){
         this.modelSkills.add(0,modelSkills);
            notifyItemInserted(0);
            notifyDataSetChanged();

    }

    public void deleteAllItems(){
        int size= modelSkills.size();
        modelSkills.clear();
        notifyItemRangeRemoved(0,size);
    }

    public DocumentSnapshot getDocument(int position){
        return modelSkills.get(position).documentSnapshot;
    }
    public void showDeleteOption(){
        for (int i=0;i<modelSkills.size();i++){
            modelSkills.get(i).viewHolder.deleteButton.setVisibility(View.VISIBLE);
        }
    }
    public void hideDeleteOption(){
        for (int i=0;i<modelSkills.size();i++){
            modelSkills.get(i).viewHolder.deleteButton.setVisibility(View.GONE);
        }
    }
}
