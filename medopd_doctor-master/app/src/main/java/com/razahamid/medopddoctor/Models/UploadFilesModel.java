package com.razahamid.medopddoctor.Models;

import android.net.Uri;


import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class UploadFilesModel implements Serializable  {
    private String Data;
    public UploadFilesModel(Uri uri, String link,String userUID,String message,String receiver) {
        JSONObject  jsonObject=new JSONObject();
        try {
            FirebaseRef ref = new FirebaseRef();
            jsonObject.put(ref.ChatLink,link);
            jsonObject.put(ref.ImageUrl,uri);
            jsonObject.put(ref.User,userUID);
            jsonObject.put(ref.Message,message);
            jsonObject.put(ref.Receiver,receiver);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Data=jsonObject.toString();
    }

    public String getData() {
        return Data;
    }
}
