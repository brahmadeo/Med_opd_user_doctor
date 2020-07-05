package com.razahamid.medopd.Home.ui.Account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.razahamid.medopd.Adopters.AllBlogpostAdapter;
import com.razahamid.medopd.Adopters.BlogpostAdapter;
import com.razahamid.medopd.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AllBlogPostActivity extends AppCompatActivity {

    private RecyclerView AllArticalsRecycler;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_blog_post);

        VollyApiCallForBlogpost();
    }
    private void VollyApiCallForBlogpost() {
        //RequestQueue initialized
        String url = "https://www.googleapis.com/blogger/v3/blogs/4311409198584569518/posts?key=AIzaSyDatq6yWt3JeCquAeM77AaCTiUkJ_AlgjI";
        mRequestQueue = Volley.newRequestQueue(this);

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.i("Response", response.toString());
                        try {
                            blogPostSetUp(response.getJSONArray("items"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", error.toString());
                    }
                }
        );
        // add it to the RequestQueue
        mRequestQueue.add(getRequest);
    }
    private void blogPostSetUp(JSONArray jsonArray){
        AllArticalsRecycler = (RecyclerView)findViewById(R.id.AllArticalsRecycler);
        AllArticalsRecycler.setHasFixedSize(true);
        AllArticalsRecycler.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        AllBlogpostAdapter adapter = new AllBlogpostAdapter(this, jsonArray, "vertical");
        AllArticalsRecycler.setAdapter(adapter);
    }
}
