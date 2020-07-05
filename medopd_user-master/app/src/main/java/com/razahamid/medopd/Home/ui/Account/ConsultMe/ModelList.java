package com.razahamid.medopd.Home.ui.Account.ConsultMe;

import com.google.firebase.firestore.DocumentSnapshot;

public class ModelList {
    public DocumentSnapshot documentSnapshot;

    public ModelList(DocumentSnapshot documentSnapshot) {
        this.documentSnapshot = documentSnapshot;
    }
}
