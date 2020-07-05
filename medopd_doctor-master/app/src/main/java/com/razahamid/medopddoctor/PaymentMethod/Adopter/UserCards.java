package com.razahamid.medopddoctor.PaymentMethod.Adopter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;
import com.razahamid.medopddoctor.Models.CardsModel;
import com.razahamid.medopddoctor.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class UserCards extends RecyclerView.Adapter<UserCards.ViewHolder> {

    private ArrayList<CardsModel> cardsModels;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private FirebaseRef ref=new FirebaseRef();
    private Context context;
    // data is passed into the constructor
    public UserCards(Context context, ArrayList<CardsModel> cardsModels) {
        this.mInflater   = LayoutInflater.from(context);
        this.cardsModels = cardsModels;
        this.context=context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = mInflater.inflate(R.layout.payment_card_recycle, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NotNull final ViewHolder holder, final int position) {
      if (Objects.equals(cardsModels.get(position).documentSnapshot.getString(ref.CardType), "u")){
          holder.cardImage.setImageDrawable(context.getDrawable(R.drawable.ic_upi));
          holder.CardNumber.setText(cardsModels.get(position).documentSnapshot.getString(ref.UserUPI));
      }else {
          holder.cardImage.setImageDrawable(context.getDrawable(R.drawable.ic_bank));
          holder.CardNumber.setText(cardsModels.get(position).documentSnapshot.getString(ref.AccountNumber));
      }
      holder.selectedCard.setChecked(cardsModels.get(position).selected);
      cardsModels.get(position).setView(holder);
      holder.selectedCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              if (isChecked){
                  cardsModels.get(position).setSelected(true);
                  for (int i=0;i<cardsModels.size();i++){
                      if (position!=i){
                          Log.i("values",String.valueOf(position));
                          Log.i("values",String.valueOf(i));
                          if (cardsModels.get(i).view!=null){
                              cardsModels.get(i).view.selectedCard.setChecked(false);
                              cardsModels.get(i).setSelected(false);
                          }
                      }
                  }
              }else {
                  cardsModels.get(position).setSelected(false);
              }
              if (mClickListener != null) {
                  mClickListener.onItemClick(holder.itemView, position);
              }
          }
      });
    }
     // total number of rows
    @Override
    public int getItemCount() {
        return cardsModels.size();
    }





    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
       private TextView CardNumber;
       private AppCompatCheckBox selectedCard;
       private ImageView cardImage;
        ViewHolder(View itemView) {
            super(itemView);
            CardNumber =itemView.findViewById(R.id.cardNumber);
            selectedCard=itemView.findViewById(R.id.selectedCard);
            cardImage=itemView.findViewById(R.id.cardImage);
        }


    }

   public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void insertItems(CardsModel cardsModel){
         cardsModels.add(cardsModel);
         notifyItemInserted(getItemCount() - 1);
         notifyDataSetChanged();
    }
    public void insertAtTop(CardsModel cardsModel){
        cardsModels.add(0,cardsModel);
        notifyItemInserted(0);
        notifyDataSetChanged();
    }

    public void deleteAllItems(){
        int size= cardsModels.size();
        cardsModels.clear();
        notifyItemRangeRemoved(0,size);
    }

    public DocumentSnapshot getDocument(int position){
        return cardsModels.get(position).documentSnapshot;
    }
    public String getSelectedCardValue(){
        for (int i=0;i<cardsModels.size();i++){
            if (cardsModels.get(i).selected){
                return cardsModels.get(i).documentSnapshot.getId();
            }
        }
        return null;
    }
}
