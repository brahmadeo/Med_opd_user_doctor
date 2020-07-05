package com.razahamid.medopddoctor.Home.ui.Messages;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


class CallRequest extends AsyncTask<String, String, String> {
    private  Context mContext;
    protected void onPreExecute() {
        super.onPreExecute();

    }

    public CallRequest(Context context) {
        this.mContext = context;
    }

    protected String doInBackground(String... params) {


        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");

            }
            String result=buffer.toString();
            Log.i("result",result);
            try {
                JSONObject jsonObject=new JSONObject(result);
                if (jsonObject.has("status:")){
                    if (jsonObject.getString("status:").equals("success")){
                        return jsonObject.getString("call-id:");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result!=null){
            Log.i("result",result);
            Toast.makeText(mContext, "Connecting Now", Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(result);
    }
}