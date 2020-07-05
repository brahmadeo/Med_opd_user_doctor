package com.razahamid.medopd.Adopters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.Models.AllSymptomsModel;
import com.razahamid.medopd.R;

import java.util.ArrayList;


public class AllSymptoms extends RecyclerView.Adapter<AllSymptoms.ViewHolder> {

    private ArrayList<AllSymptomsModel> allSymptomsModels;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ArrayList<String> allSelected;
    public AllSymptoms(Context context, ArrayList<AllSymptomsModel> allSymptomsModels,ArrayList<String> allSelected) {
        this.mInflater   = LayoutInflater.from(context);
        this.allSymptomsModels = allSymptomsModels;
        this.allSelected=allSelected;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = mInflater.inflate(R.layout.all_symptoms_card_recycle, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.CheckBox.setText(allSymptomsModels.get(position).getSymptoms());
        allSymptomsModels.get(position).setMaterialCheckBox(holder.CheckBox);
        if (allSelected.contains(allSymptomsModels.get(position).getSymptoms())){
            holder.CheckBox.setChecked(true);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return allSymptomsModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       private MaterialCheckBox CheckBox;

        ViewHolder(View itemView) {
            super(itemView);
            CheckBox =itemView.findViewById(R.id.CheckBox);

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

    public void insertItems(AllSymptomsModel selectedSymptoms){
               this.allSymptomsModels.add(selectedSymptoms);
               notifyItemInserted(getItemCount() - 1);
               notifyDataSetChanged();

    }
    public void insertAtTop(AllSymptomsModel selectedSymptoms){
         this.allSymptomsModels.add(0,selectedSymptoms);
            notifyItemInserted(0);
            notifyDataSetChanged();

    }

    public void deleteAllItems(){
        int size= allSymptomsModels.size();
        allSymptomsModels.clear();
        notifyItemRangeRemoved(0,size);
    }

    public ArrayList<String> getAllSelected(){
        ArrayList<String> list=new ArrayList<>();
        for (int i=0;i<allSymptomsModels.size();i++){
            if(allSymptomsModels.get(i).getMaterialCheckBox()!=null){
                if (allSymptomsModels.get(i).getMaterialCheckBox().isChecked()){
                    list.add(allSymptomsModels.get(i).getMaterialCheckBox().getText().toString());
                }
            }
        }
        return list;
    }
}
