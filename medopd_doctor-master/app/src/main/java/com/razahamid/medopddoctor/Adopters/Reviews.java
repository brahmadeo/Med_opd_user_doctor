package com.razahamid.medopddoctor.Adopters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.Models.ModelReviews;
import com.razahamid.medopddoctor.R;

import java.util.ArrayList;


public class Reviews extends RecyclerView.Adapter<Reviews.ViewHolder> {

    private ArrayList<ModelReviews> modelReviews;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private FirebaseRef ref=new FirebaseRef();
    private Context mContext;

    // data is passed into the constructor
    public Reviews(Context context, ArrayList<ModelReviews> modelReviews) {
        this.mInflater   = LayoutInflater.from(context);
        this.modelReviews = modelReviews;
        mContext=context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = mInflater.inflate(R.layout.reviews_values, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DocumentSnapshot documentSnapshot= modelReviews.get(position).documentSnapshot;
        holder.name.setText(documentSnapshot.getString(ref.FirstName));
        holder.comment.setText(documentSnapshot.getString(ref.Comment));
        holder.rating.setText(documentSnapshot.getString(ref.RatingString));
        Glide.with(mContext).load(documentSnapshot.getString(ref.ImageUrl)).into(holder.profile);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return modelReviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       private TextView name, comment,rating;
       private ImageView profile;

        ViewHolder(View itemView) {
            super(itemView);
            name =itemView.findViewById(R.id.name);
            comment =itemView.findViewById(R.id.comment);
            rating=itemView.findViewById(R.id.rating);
            profile =itemView.findViewById(R.id.image);
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

    public void insertItems(ModelReviews modelReviews){

               this.modelReviews.add(modelReviews);
               notifyItemInserted(getItemCount() - 1);
               notifyDataSetChanged();

    }
    public void insertAtTop(ModelReviews modelReviews){
         this.modelReviews.add(0,modelReviews);
            notifyItemInserted(0);
            notifyDataSetChanged();

    }

    public void deleteAllItems(){
        int size= modelReviews.size();
        modelReviews.clear();
        notifyItemRangeRemoved(0,size);
    }

    public DocumentSnapshot getDocument(int position){
        return modelReviews.get(position).documentSnapshot;
    }
}
