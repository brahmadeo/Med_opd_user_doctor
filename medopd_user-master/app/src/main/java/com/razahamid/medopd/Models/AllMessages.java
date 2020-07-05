package com.razahamid.medopd.Models;

import com.google.firebase.database.DataSnapshot;

public class AllMessages {
    public String Date;
    public DataSnapshot snapshot;
    public String userName="";
    public String userImage="";

    public AllMessages(String date, DataSnapshot dataSnapshot) {
        Date = date;
        this.snapshot=dataSnapshot;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
