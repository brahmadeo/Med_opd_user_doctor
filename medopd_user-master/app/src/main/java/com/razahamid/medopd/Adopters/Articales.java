package com.razahamid.medopd.Adopters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.Models.ModelArticles;
import com.razahamid.medopd.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Articales extends RecyclerView.Adapter<Articales.ViewHolder> {

    private ArrayList<ModelArticles> modelArticles;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private FirebaseRef ref=new FirebaseRef();
    private Context mContext;

    // data is passed into the constructor
    public Articales(Context context, ArrayList<ModelArticles> modelArticles) {
        this.mInflater   = LayoutInflater.from(context);
        this.modelArticles = modelArticles;
        mContext=context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = mInflater.inflate(R.layout.articals_recycle, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DocumentSnapshot documentSnapshot=modelArticles.get(position).documentSnapshot;
        holder.Title.setText(documentSnapshot.getString(ref.Heading));
        Picasso.get().load(documentSnapshot.getString(ref.IconUrl)).into(holder.TaskImage);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return modelArticles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       private TextView Title;
       private ImageView TaskImage;

        ViewHolder(View itemView) {
            super(itemView);
            Title =itemView.findViewById(R.id.ActicalName);
            TaskImage=itemView.findViewById(R.id.ArticleImage);
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

    public void insertItems(ModelArticles modelArticles){

               this.modelArticles.add(modelArticles);
               notifyItemInserted(getItemCount() - 1);
               notifyDataSetChanged();

    }
    public void insertAtTop(ModelArticles modelArticles){
         this.modelArticles.add(0,modelArticles);
            notifyItemInserted(0);
            notifyDataSetChanged();

    }

    public void deleteAllItems(){
        int size= modelArticles.size();
        modelArticles.clear();
        notifyItemRangeRemoved(0,size);
    }

    public DocumentSnapshot getDocument(int position){
        return modelArticles.get(position).documentSnapshot;
    }
}
