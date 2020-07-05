package com.razahamid.medopddoctor.Adopters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.razahamid.medopddoctor.Models.MedicineModel;
import com.razahamid.medopddoctor.R;

import java.util.ArrayList;

public class AdapterMedicineList extends RecyclerView.Adapter<AdapterMedicineList.ViewHolder> {
    private Context mContext;
    private ArrayList<MedicineModel> list;
    private boolean isShowOptions = true;

    public AdapterMedicineList(Context mContext, ArrayList<MedicineModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public AdapterMedicineList(Context mContext, ArrayList<MedicineModel> list, boolean isShowOptions) {
        this.mContext = mContext;
        this.list = list;
        this.isShowOptions = isShowOptions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.list_medicine_item, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if(!isShowOptions){
            holder.btnRemove.setVisibility(View.GONE);
        }else{
            holder.btnRemove.setVisibility(View.VISIBLE);
        }

        holder.tvSlNo.setText(String.valueOf(position + 1));
        holder.tvMedicineName.setText(list.get(position).medicineName);
        holder.tvDosage.setText(list.get(position).getDosageMsg());
        holder.tvDuration.setText(list.get(position).getDurationMsg());
        holder.tvInstruction.setText(list.get(position).instruction);
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvMedicineName;
        final TextView tvDuration;
        final TextView tvDosage;
        final TextView tvInstruction;
        final TextView tvSlNo;
        final ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tv_medicine_name);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvDosage = itemView.findViewById(R.id.tv_dosage);
            tvInstruction = itemView.findViewById(R.id.tv_instruction);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            tvSlNo = itemView.findViewById(R.id.tv_sl_no);
        }
    }

}
