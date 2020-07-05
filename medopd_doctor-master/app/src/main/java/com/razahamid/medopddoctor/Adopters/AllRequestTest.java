package com.razahamid.medopddoctor.Adopters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.Models.ModelAllRequestTest;
import com.razahamid.medopddoctor.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AllRequestTest extends RecyclerView.Adapter<AllRequestTest.ViewHolder> {

    private ArrayList<ModelAllRequestTest> modelAllLocations;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private FirebaseRef ref=new FirebaseRef();
    private Context mContext;

    // data is passed into the constructor
    public AllRequestTest(Context context, ArrayList<ModelAllRequestTest> modelAllLocations) {
        this.mInflater   = LayoutInflater.from(context);
        this.modelAllLocations = modelAllLocations;
        mContext=context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = mInflater.inflate(R.layout.recycle_test_location, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DocumentSnapshot documentSnapshot= modelAllLocations.get(position).documentSnapshot;
        holder.TestName.setText(documentSnapshot.getString(ref.TestName));
        holder.Fee.setText("₹ "+String.valueOf(documentSnapshot.getLong(ref.TestFee)));
        holder.Address.setText(documentSnapshot.getString(ref.Address));
        holder.CityName.setText(documentSnapshot.getString(ref.CityName));
        holder.hopitalName.setText(documentSnapshot.getString(ref.HospitalName));
        holder.SeeOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Show Location on map", Toast.LENGTH_SHORT).show();
            }
        });
        /*
        switch (Integer.parseInt(String.valueOf(documentSnapshot.getLong(ref.status)))){
            case 0:
                holder.status.setText("Submitted");
                break;
            case 1:
                holder.status.setText("Approved");
                break;
            case 2:
                holder.status.setText("Rejected");
                break;
            case 3:
                holder.status.setText("Test Completed");
                break;
            default:
                holder.status.setText("Submitted");
                break;
        }*/
        holder.Date.setText("Test Date: "+new SimpleDateFormat("MMM dd").format(documentSnapshot.getDate(ref.TestDate)));
        holder.testID.setText("Test ID: "+documentSnapshot.getId());
    }
    // total number of rows
    @Override
    public int getItemCount() {
        return modelAllLocations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       private TextView TestName, Fee, CityName,hopitalName,Address,Date,status,testID;
       private Button SeeOnMap;
        ViewHolder(View itemView) {
            super(itemView);
            TestName =itemView.findViewById(R.id.TestName);
            Fee =itemView.findViewById(R.id.Fee);
            CityName =itemView.findViewById(R.id.CityName);
            hopitalName=itemView.findViewById(R.id.hopitalName);
            Address=itemView.findViewById(R.id.Address);
            Date=itemView.findViewById(R.id.Date);
            SeeOnMap=itemView.findViewById(R.id.SeeOnMap);
            status=itemView.findViewById(R.id.status);
            testID=itemView.findViewById(R.id.testID);
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

    public void insertItems(ModelAllRequestTest modelAllLocations){

               this.modelAllLocations.add(modelAllLocations);
               notifyItemInserted(getItemCount() - 1);
               notifyDataSetChanged();

    }
    public void insertAtTop(ModelAllRequestTest modelAllLocations){
         this.modelAllLocations.add(0,modelAllLocations);
            notifyItemInserted(0);
            notifyDataSetChanged();

    }

    public void deleteAllItems(){
        int size= modelAllLocations.size();
        modelAllLocations.clear();
        notifyItemRangeRemoved(0,size);
    }

    public DocumentSnapshot getDocument(int position){
        return modelAllLocations.get(position).documentSnapshot;
    }
}
