package com.razahamid.medopd.Adopters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.Home.ui.Account.BloggerActivity;
import com.razahamid.medopd.Models.ModelArticles;
import com.razahamid.medopd.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class BlogpostAdapter extends RecyclerView.Adapter<BlogpostAdapter.ViewHolder> {

    private ArrayList<ModelArticles> modelArticles;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private FirebaseRef ref=new FirebaseRef();
    private Context mContext;
    private JSONArray jsonArray;
    private String orientation;
    private int remainPosition = 0;
    private int count = 5;

    // data is passed into the constructor
    public BlogpostAdapter(Context context, JSONArray jsonArray, String orientation) {
        this.mInflater   = LayoutInflater.from(context);
        this.modelArticles = modelArticles;
        this.jsonArray = jsonArray;
        mContext=context;
        this.orientation = orientation;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (orientation.equals("horizontal")){
            view = mInflater.inflate(R.layout.blogpost_item_row, parent, false);
        }else {
            view = mInflater.inflate(R.layout.blogpost_all_item_row, parent, false);
        }
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int totalPost = jsonArray.length();
        remainPosition = totalPost - count -1;

        try {
            final String mimeType = "text/html";
            final String encoding = "UTF-8";

            holder.webView.requestFocus();
            holder.webView.getSettings().setLoadsImagesAutomatically(true);

            //holder.webView.loadUrl(jsonArray.getJSONObject(position).getString("url"));
            holder.webView.loadDataWithBaseURL("", jsonArray.getJSONObject(position).getString("content"), mimeType, encoding, "");
            //holder.webView.loadData(jsonArray.getJSONObject(position).getString("content"), mimeType, encoding);

            holder.webView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            holder.name_tv.setText(jsonArray.getJSONObject(position).getString("title"));
            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        mContext.startActivity(new Intent(mContext, BloggerActivity.class)
                                .putExtra("postUrl", jsonArray.getJSONObject(position).getString("content")));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       private TextView name_tv;
       private WebView webView;
       private CardView card_view;

        ViewHolder(View itemView) {
            super(itemView);
            name_tv =itemView.findViewById(R.id.name_tv);
            webView=itemView.findViewById(R.id.webView);
            card_view = itemView.findViewById(R.id.card_view);
            //itemView.findViewById(R.id.card_view).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            /*if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());*/
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
