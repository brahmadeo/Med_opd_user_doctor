package com.razahamid.medopddoctor.LogIn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.razahamid.medopddoctor.R;
import java.util.ArrayList;

public class ListAdopter extends RecyclerView.Adapter<ListAdopter.ViewHolder> {

    private ArrayList<ModelList> modelLists;
    public LayoutInflater     mInflater;
    public ItemClickListener  mClickListener;
    private Context mContext;
    public ListAdopter(Context context, ArrayList<ModelList> modelLists) {
        this.mInflater   = LayoutInflater.from(context);
        this.modelLists = modelLists;
        this.mContext=context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.simple_list, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
       holder.name.setText(modelLists.get(position).Name);
    }
    @Override
    public int getItemCount() {
        return modelLists.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView mainBack;
        TextView name;
        ViewHolder(View itemView) {
            super(itemView);
            mainBack =itemView.findViewById(R.id.mainBack);
            name=itemView.findViewById(R.id.name);
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

    public interface ItemLongClickListener {
        void OnLongClick(View view, int position);
    }
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public void insertItems(ModelList modelList){
        this.modelLists.add(modelList);
        notifyItemInserted(getItemCount() - 1);
        notifyDataSetChanged();
    }
    public void deleteAllItems(){
        int size= modelLists.size();
        modelLists.clear();
        notifyItemRangeRemoved(0,size);
    }
    public ModelList getModelList(int position){
        return modelLists.get(position);
    }
}
