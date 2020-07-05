package com.razahamid.medopddoctor.Models;

import com.google.firebase.firestore.DocumentSnapshot;

public class ModelAllProblems {
    public DocumentSnapshot documentSnapshot;

    public ModelAllProblems(DocumentSnapshot documentSnapshot) {
        this.documentSnapshot = documentSnapshot;
    }
}
