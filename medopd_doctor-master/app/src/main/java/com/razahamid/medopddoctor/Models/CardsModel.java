package com.razahamid.medopddoctor.Models;

import com.google.firebase.firestore.DocumentSnapshot;
import com.razahamid.medopddoctor.PaymentMethod.Adopter.UserCards;

public class CardsModel {
    public DocumentSnapshot documentSnapshot;
    public boolean selected;
    public UserCards.ViewHolder view;

    public CardsModel(DocumentSnapshot documentSnapshot) {
        this.documentSnapshot = documentSnapshot;
        this.view=null;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public UserCards.ViewHolder getView() {
        return view;
    }

    public void setView(UserCards.ViewHolder view) {
        this.view = view;
    }
}
