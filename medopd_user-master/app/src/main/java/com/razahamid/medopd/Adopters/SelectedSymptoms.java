package com.razahamid.medopd.Adopters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.R;
import java.util.ArrayList;


public class SelectedSymptoms extends RecyclerView.Adapter<SelectedSymptoms.ViewHolder> {

    private ArrayList<String> selectedSymptoms;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public SelectedSymptoms(Context context, ArrayList<String> selectedSymptoms) {
        this.mInflater   = LayoutInflater.from(context);
        this.selectedSymptoms = selectedSymptoms;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = mInflater.inflate(R.layout.symptoms_card_recycle, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.value.setText(selectedSymptoms.get(position));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return selectedSymptoms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       private TextView value;

        ViewHolder(View itemView) {
            super(itemView);
            value =itemView.findViewById(R.id.value);

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

    public void insertItems(String selectedSymptoms){

               this.selectedSymptoms.add(selectedSymptoms);
               notifyItemInserted(getItemCount() - 1);
               notifyDataSetChanged();

    }
    public void insertAtTop(String selectedSymptoms){
         this.selectedSymptoms.add(0,selectedSymptoms);
            notifyItemInserted(0);
            notifyDataSetChanged();

    }

    public void deleteAllItems(){
        int size= selectedSymptoms.size();
        selectedSymptoms.clear();
        notifyItemRangeRemoved(0,size);
    }
  public void  UpdateList(ArrayList<String> list){
    selectedSymptoms=list;
    notifyDataSetChanged();
  }
}
