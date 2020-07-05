package com.razahamid.medopd.Home.ui.Account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.razahamid.medopd.Adopters.Articales;
import com.razahamid.medopd.Adopters.BlogpostAdapter;
import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.Home.ui.Account.Test.AllTestNames;
import com.razahamid.medopd.Home.ui.Messages.UserChat;
import com.razahamid.medopd.Home.ui.Account.ConsultMe.SelectAHealthProblem;
import com.razahamid.medopd.Models.ModelArticles;
import com.razahamid.medopd.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MyAccountFragment extends Fragment {
    private int MAX_STEP = 0;
    private Articales articales;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private FirebaseRef ref=new FirebaseRef();
    private FirebaseUser firebaseUser;
    private RequestQueue mRequestQueue;
    private TextView textViewMoreBlog;



  private   View root;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         root = inflater.inflate(R.layout.fragment_my_account, container, false);
        viewPager =  root.findViewById(R.id.view_pager);
        textViewMoreBlog = (TextView)root.findViewById(R.id.textViewMoreBlog);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        bottomProgressDots(0);
        yourRecycledViewSetUp();
        setUpAllButtons();
        setUpTopBanners();
        VollyApiCallForBlogpost();

        textViewMoreBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AllBlogPostActivity.class));
            }
        });
        return root;
    }

    private void setUpTopBanners() {
        FirebaseFirestore.getInstance().collection(ref.AppData).document(ref.HomePage).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot=task.getResult();
                        assert documentSnapshot != null;
                        if (documentSnapshot.exists()){
                           ArrayList<String> arrayList= (ArrayList<String>) documentSnapshot.get(ref.AllImages);
                            MAX_STEP=arrayList.size();
                            myViewPagerAdapter = new MyViewPagerAdapter(getContext(),arrayList);
                            viewPager.setAdapter(myViewPagerAdapter);
                            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
                            viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin_overlap_payment));
                            viewPager.setOffscreenPageLimit(MAX_STEP);

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void setUpAllButtons() {
        root.findViewById(R.id.consultButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SelectAHealthProblem.class));
            }
        });
        root.findViewById(R.id.helpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString(ref.UserName,getString(R.string.app_name));
                bundle.putString(ref.UserImage,"https://firebasestorage.googleapis.com/v0/b/medopd-62177.appspot.com/o/userImages%2Flogo.png?alt=media&token=7b17c63a-3d57-4685-bc1e-bbbbbc081da9");
                bundle.putString(ref.ChatLink,"Aw1fe17kHiZMP0LQuvtfDgkbmbq1");
                startActivity(new Intent(getContext(), UserChat.class).putExtras(bundle));
            }
        });
        root.findViewById(R.id.testButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(getContext(), AllTestNames.class));
            }
        });
        root.findViewById(R.id.orderButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              try {
//                  Intent sendIntent = new Intent(Intent.ACTION_SEND);
//                  sendIntent.setType("text/plain");
//                  sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi medopd Help me to get medicine");
//                  sendIntent.putExtra("jid", "919470244795" + "@s.whatsapp.net"); //phone number without "+" prefix
//                  sendIntent.setPackage("com.whatsapp");
//                  sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                  startActivity(sendIntent);
//              }catch (Exception e){
//                  e.printStackTrace();
//              }
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setTitle("Order Medicine");
                dialogBuilder.setMessage("Coming Soon");
                dialogBuilder.create().show();
            }
        });
    }

    private void yourRecycledViewSetUp() {
        final RecyclerView recyclerView = root.findViewById(R.id.Articals);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
        articales=new Articales(getContext(),new ArrayList<ModelArticles>());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(articales);
        articales.setClickListener(new Articales.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle=new Bundle();
                DocumentSnapshot documentSnapshot= articales.getDocument(position);
                bundle.putString(ref.PublishDate,new SimpleDateFormat("dd MMM ", Locale.ENGLISH).format(Objects.requireNonNull(documentSnapshot.getDate(ref.PublishDate))));
                bundle.putString(ref.Heading,documentSnapshot.getString(ref.Heading));
                bundle.putString(ref.SubHeading,documentSnapshot.getString(ref.SubHeading));
                bundle.putString(ref.IconUrl,documentSnapshot.getString(ref.IconUrl));
                bundle.putString(ref.Details,documentSnapshot.getString(ref.Details));
                bundle.putString(ref.WriterName,documentSnapshot.getString(ref.WriterName));
                startActivity(new Intent(getContext(),ArticleMedium.class).putExtras(bundle));
            }
        });
        FirebaseFirestore.getInstance().collection(ref.MedArticles).orderBy(ref.Time, Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                   articales.deleteAllItems();
                   assert queryDocumentSnapshots != null;
                  for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                      articales.insertItems(new ModelArticles(documentSnapshot));
                   }
            }
        });
    }
    private void VollyApiCallForBlogpost() {
        //RequestQueue initialized
        String url = "https://www.googleapis.com/blogger/v3/blogs/4311409198584569518/posts?key=AIzaSyDatq6yWt3JeCquAeM77AaCTiUkJ_AlgjI";
        mRequestQueue = Volley.newRequestQueue(getContext());

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
    private void openDialog(String response){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(response);


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void blogPostSetUp(JSONArray jsonArray){
        final RecyclerView recyclerView = root.findViewById(R.id.recyclerViewBlogpost);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        BlogpostAdapter adapter = new BlogpostAdapter(getContext(), jsonArray, "horizontal");
        recyclerView.setAdapter(adapter);
    }
    private ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            bottomProgressDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private void bottomProgressDots(int current_index) {
        LinearLayout dotsLayout = (LinearLayout) root.findViewById(R.id.layoutDots);
        ImageView[] dots = new ImageView[MAX_STEP];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(getContext());
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current_index].setImageResource(R.drawable.shape_circle);
            dots[current_index].setColorFilter(getResources().getColor(R.color.light_green_600), PorterDuff.Mode.SRC_IN);
        }
    }
    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private Context context;
        private ArrayList<String> arrayListImages;
        public MyViewPagerAdapter(Context context,ArrayList<String> arrayListImages) {
            this.context=context;
            this.arrayListImages = arrayListImages;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.item_card_payment, container, false);
            if (arrayListImages.size()>0){
                Glide.with(context).load(arrayListImages.get(position)).into((ImageView) view.findViewById(R.id.mainImage));
            }
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return MAX_STEP;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
        public void AddArrayList(ArrayList arrayList){
            this.arrayListImages=arrayList;
            notifyDataSetChanged();
        }
    }
}
