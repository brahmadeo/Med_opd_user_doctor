package com.razahamid.medopddoctor.Adopters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.Models.AllMessages;
import com.razahamid.medopddoctor.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class AllMessagesList extends RecyclerView.Adapter<AllMessagesList.ViewHolder> {

    private ArrayList<AllMessages> allMessages;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private FirebaseRef ref=new FirebaseRef();
    private Context mContext;

    // data is passed into the constructor
    public AllMessagesList(Context context, ArrayList<AllMessages> allMessages) {
        this.mInflater   = LayoutInflater.from(context);
        this.allMessages = allMessages;
        mContext=context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = mInflater.inflate(R.layout.recycle_messages, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AllMessages singleMessage= allMessages.get(position);
        holder.date.setText(singleMessage.Date);
        FirebaseFirestore.getInstance().collection(ref.Users).document(Objects.requireNonNull(singleMessage.snapshot.getKey())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot=task.getResult();
                        assert documentSnapshot != null;
                        holder.from.setText(documentSnapshot.getString(ref.FirstName));
                        singleMessage.setUserName(documentSnapshot.getString(ref.FirstName));
                        if (documentSnapshot.contains(ref.ImageUrl)){
                            singleMessage.setUserImage(documentSnapshot.getString(ref.ImageUrl));
                            Picasso.get().load(documentSnapshot.getString(ref.ImageUrl)).into(holder.image);
                        }
                   }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        if (singleMessage.snapshot.hasChild(ref.MessageType)){
            if (singleMessage.snapshot.child(ref.MessageType).getValue(String.class).equals(ref.Text)){
                holder.message.setText(singleMessage.snapshot.child(ref.Message).getValue(String.class));
                holder.messageImage.setVisibility(View.GONE);
                holder.message.setVisibility(View.VISIBLE);
            }else if (singleMessage.snapshot.child(ref.MessageType).getValue(String.class).equals(ref.Image)){
                holder.messageImage.setVisibility(View.VISIBLE);
                holder.message.setVisibility(View.GONE);
            }
        }else {
            holder.message.setText(singleMessage.snapshot.child(ref.Message).getValue(String.class));
        }
    }

    @Override
    public int getItemCount() {
        return allMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       private TextView from,message,date;
       private ImageView image;
       private LinearLayout messageImage;

        ViewHolder(View itemView) {
            super(itemView);
            from =itemView.findViewById(R.id.from);
            message=itemView.findViewById(R.id.message);
            date =itemView.findViewById(R.id.date);
            image=itemView.findViewById(R.id.image);
            messageImage=itemView.findViewById(R.id.messageImage);
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

    public void insertItems(AllMessages allMessages){

               this.allMessages.add(allMessages);
               notifyItemInserted(getItemCount() - 1);
               notifyDataSetChanged();

    }
    public void insertAtTop(AllMessages allMessages){
         this.allMessages.add(0,allMessages);
            notifyItemInserted(0);
            notifyDataSetChanged();

    }

    public void deleteAllItems(){
        int size= allMessages.size();
        allMessages.clear();
        notifyItemRangeRemoved(0,size);
    }
    public AllMessages getCurrentMessage(int position){
        return allMessages.get(position);
    }

}
