package com.razahamid.medopddoctor.Models;

import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;
import com.razahamid.medopddoctor.Adopters.Skills;

public class ModelSkills {
    public DocumentSnapshot documentSnapshot;
    public Skills.ViewHolder viewHolder;

    public ModelSkills(DocumentSnapshot documentSnapshot) {
        this.documentSnapshot = documentSnapshot;
    }

    public void setViewHolder(Skills.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }
}
