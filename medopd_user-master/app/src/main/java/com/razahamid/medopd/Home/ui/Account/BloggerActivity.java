package com.razahamid.medopd.Home.ui.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.razahamid.medopd.R;

public class BloggerActivity extends AppCompatActivity {

    private WebView web_view;
    private String url = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogger);

        if(getIntent().getExtras()!=null && !getIntent().getExtras().getString("postUrl").equals("")){
            url = getIntent().getExtras().getString("postUrl");
        }
        web_view = (WebView)findViewById(R.id.web_view);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        //holder.webView.loadUrl(jsonArray.getJSONObject(position).getString("url"));
        web_view.loadDataWithBaseURL("", url, mimeType, encoding, "");
        //holder.webView.loadData(jsonArray.getJSONObject(position).getString("content"), mimeType, encoding);
        //web_view.loadUrl(url);
    }
}
