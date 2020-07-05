package com.razahamid.medopddoctor.ExtraFiles;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.maps.model.LatLng;

public class Helper {
    private static final String DIRECTION_API = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    private static final String StaticPArtOne="https://maps.googleapis.com/maps/api/staticmap?center=";
    private static final String StaticPartTwo="&zoom=15&size=200x200&markers=color:blue%7Clabel:S%7C";
    public static final String API_KEY = "AIzaSyBYLr6usgo5NqrCQA5lenjcx6xSsr4NKnY";
    public static final int MY_SOCKET_TIMEOUT_MS = 5000;
    public static String getUrl(String originLat, String originLon, String destinationLat, String destinationLon){
        return Helper.DIRECTION_API + originLat+","+originLon+"&destination="+destinationLat+","+destinationLon+"&key="+API_KEY;
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static  String stillImageUrl(LatLng latLng){
        return Helper.StaticPArtOne+latLng.latitude+","+latLng.longitude+Helper.StaticPartTwo+latLng.latitude+","+latLng.longitude+"&key="+API_KEY;
    }
}