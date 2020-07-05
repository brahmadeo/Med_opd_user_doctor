package com.razahamid.medopddoctor.PaymentMethod.Adopter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WithdrawRequests extends RecyclerView.Adapter<WithdrawRequests.ViewHolder> {

    private ArrayList<DocumentSnapshot> Document;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private FirebaseRef ref=new FirebaseRef();

    // data is passed into the constructor
    public WithdrawRequests(Context context, ArrayList<DocumentSnapshot> document) {
        this.mInflater   = LayoutInflater.from(context);
        this.Document=document;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = mInflater.inflate(R.layout.recycle_transactions, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder( ViewHolder holder,  int position) {
        Date date=Document.get(position).getDate(ref.Time);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMM YYYY hh:mm", Locale.ENGLISH);
        assert date != null;
        holder.Date.setText(simpleDateFormat.format(date));
        holder.Status.setText(Document.get(position).getString(ref.Status));
        holder.Ammount.setText(Document.get(position).getString(ref.Amount));
    }
     // total number of rows
    @Override
    public int getItemCount() {
        return Document.size();
    }





    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       private TextView Date,Ammount,Status;
        ViewHolder(View itemView) {
            super(itemView);
            Date =itemView.findViewById(R.id.Date);
            Ammount=itemView.findViewById(R.id.Ammount);
            Status=itemView.findViewById(R.id.Status);
            itemView.setOnClickListener(this);
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

    public void insertItems(DocumentSnapshot documentSnapshot){
         Document.add(documentSnapshot);
         notifyItemInserted(getItemCount() - 1);
         notifyDataSetChanged();
    }
    public void insertAtTop(DocumentSnapshot documentSnapshot){
        Document.add(0,documentSnapshot);
        notifyItemInserted(0);
        notifyDataSetChanged();
    }

    public void deleteAllItems(){
        int size= Document.size();
        Document.clear();
        notifyItemRangeRemoved(0,size);
    }
    public String Instructions(int position){
        return Document.get(position).getString(ref.Instruction);
    }
    public DocumentSnapshot getDocument(int position){
        return Document.get(position);
    }
}
