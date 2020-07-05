package com.razahamid.medopddoctor.Models;

import com.google.firebase.firestore.DocumentSnapshot;

public class ModelReviews {
    public DocumentSnapshot documentSnapshot;

    public ModelReviews(DocumentSnapshot documentSnapshot) {
        this.documentSnapshot = documentSnapshot;
    }
}
