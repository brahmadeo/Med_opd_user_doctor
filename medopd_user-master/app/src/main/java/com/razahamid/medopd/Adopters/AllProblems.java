package com.razahamid.medopd.Adopters;

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
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.Models.ModelAllProblems;
import com.razahamid.medopd.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AllProblems extends RecyclerView.Adapter<AllProblems.ViewHolder> {

    private ArrayList<ModelAllProblems> modelAllProblems;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private FirebaseRef ref=new FirebaseRef();
    private Context mContext;

    // data is passed into the constructor
    public AllProblems(Context context, ArrayList<ModelAllProblems> modelAllProblems) {
        this.mInflater   = LayoutInflater.from(context);
        this.modelAllProblems = modelAllProblems;
        mContext=context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = mInflater.inflate(R.layout.recycle_select_problem, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DocumentSnapshot documentSnapshot= modelAllProblems.get(position).documentSnapshot;
        holder.ProblemName.setText(documentSnapshot.getString(ref.ProblemName));
        holder.ProblemPrice.setText("₹ "+String.valueOf(documentSnapshot.getLong(ref.ProblemPrice)));
       if (documentSnapshot.contains(ref.PreviousPrice)){
           holder.previousPrice.setPaintFlags(holder.previousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
           holder.previousPrice.setText("₹ "+String.valueOf(documentSnapshot.getLong(ref.PreviousPrice)));
       }
        Picasso.get().load(documentSnapshot.getString(ref.IconUrl)).into(holder.ProblemImage);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return modelAllProblems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       private TextView ProblemName,ProblemPrice,previousPrice;
       private ImageView ProblemImage;

        ViewHolder(View itemView) {
            super(itemView);
            ProblemName =itemView.findViewById(R.id.ProblemName);
            ProblemPrice=itemView.findViewById(R.id.ProblemPrice);
            ProblemImage =itemView.findViewById(R.id.ProblemImage);
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

    public void insertItems(ModelAllProblems ModelAllProblems){

               this.modelAllProblems.add(ModelAllProblems);
               notifyItemInserted(getItemCount() - 1);
               notifyDataSetChanged();

    }
    public void insertAtTop(ModelAllProblems ModelAllProblems){
         this.modelAllProblems.add(0,ModelAllProblems);
            notifyItemInserted(0);
            notifyDataSetChanged();

    }

    public void deleteAllItems(){
        int size= modelAllProblems.size();
        modelAllProblems.clear();
        notifyItemRangeRemoved(0,size);
    }

    public DocumentSnapshot getDocument(int position){
        return modelAllProblems.get(position).documentSnapshot;
    }
    public ModelAllProblems getSelectedProblem(int position){
        return modelAllProblems.get(position);
    }
}
