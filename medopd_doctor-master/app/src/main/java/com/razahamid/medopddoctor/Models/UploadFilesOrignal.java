package com.razahamid.medopddoctor.Models;

import android.net.Uri;

import java.io.Serializable;

public class UploadFilesOrignal implements Serializable  {
    private Uri uri;
    private String link;
    private String userUid;
    private String message;
    private String receiver;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public UploadFilesOrignal(Uri uri, String link, String userUid, String message, String receiver) {
        this.uri = uri;
        this.link = link;
        this.userUid=userUid;
        this.message=message;
        this.receiver=receiver;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
