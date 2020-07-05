package com.razahamid.medopddoctor.Models;

import com.google.firebase.firestore.DocumentSnapshot;

public class ModelArticles {
    public DocumentSnapshot documentSnapshot;

    public ModelArticles(DocumentSnapshot documentSnapshot) {
        this.documentSnapshot = documentSnapshot;
    }
}
