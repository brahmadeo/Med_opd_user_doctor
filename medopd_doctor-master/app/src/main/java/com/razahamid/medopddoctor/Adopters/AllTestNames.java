package com.razahamid.medopddoctor.Adopters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.Models.ModelAllTestNames;
import com.razahamid.medopddoctor.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AllTestNames extends RecyclerView.Adapter<AllTestNames.ViewHolder> {

    private ArrayList<ModelAllTestNames> modelAllTestNames;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private FirebaseRef ref=new FirebaseRef();
    private Context mContext;

    // data is passed into the constructor
    public AllTestNames(Context context, ArrayList<ModelAllTestNames> modelAllTestNames) {
        this.mInflater   = LayoutInflater.from(context);
        this.modelAllTestNames = modelAllTestNames;
        mContext=context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = mInflater.inflate(R.layout.recycle_select_test, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DocumentSnapshot documentSnapshot= modelAllTestNames.get(position).documentSnapshot;
        holder.TestName.setText(documentSnapshot.getString(ref.TestName));
        holder.TestPrice.setText("₹ "+String.valueOf(documentSnapshot.getLong(ref.TestFee)));
       if (documentSnapshot.contains(ref.PreviousPrice)){
           holder.previousPrice.setPaintFlags(holder.previousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
           holder.previousPrice.setText("₹ "+String.valueOf(documentSnapshot.getLong(ref.PreviousPrice)));
       }
        Picasso.get().load(documentSnapshot.getString(ref.TestImage)).into(holder.TestImage);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return modelAllTestNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       private TextView TestName, TestPrice,previousPrice;
       private ImageView TestImage;

        ViewHolder(View itemView) {
            super(itemView);
            TestName =itemView.findViewById(R.id.TestName);
            TestPrice =itemView.findViewById(R.id.TestPrice);
            TestImage =itemView.findViewById(R.id.TestImage);
            previousPrice=itemView.findViewById(R.id.previousPrice);
            itemView.findViewById(R.id.cardViewMain).setOnClickListener(this);
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

    public void insertItems(ModelAllTestNames ModelAllTestNames){

               this.modelAllTestNames.add(ModelAllTestNames);
               notifyItemInserted(getItemCount() - 1);
               notifyDataSetChanged();

    }
    public void insertAtTop(ModelAllTestNames modelAllTestNames){
         this.modelAllTestNames.add(0,modelAllTestNames);
            notifyItemInserted(0);
            notifyDataSetChanged();

    }

    public void deleteAllItems(){
        int size= modelAllTestNames.size();
        modelAllTestNames.clear();
        notifyItemRangeRemoved(0,size);
    }

    public DocumentSnapshot getDocument(int position){
        return modelAllTestNames.get(position).documentSnapshot;
    }
}
