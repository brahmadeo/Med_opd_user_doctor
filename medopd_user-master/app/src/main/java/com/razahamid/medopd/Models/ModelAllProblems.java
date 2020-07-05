package com.razahamid.medopd.Models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;

public class ModelAllProblems  {
    public DocumentSnapshot documentSnapshot;

    public ModelAllProblems(DocumentSnapshot documentSnapshot) {
        this.documentSnapshot = documentSnapshot;
    }
}
