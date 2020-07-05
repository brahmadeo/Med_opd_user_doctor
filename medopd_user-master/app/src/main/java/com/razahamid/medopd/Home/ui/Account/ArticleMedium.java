package com.razahamid.medopd.Home.ui.Account;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.razahamid.medopd.ExtraFiles.FirebaseRef;
import com.razahamid.medopd.R;
import com.razahamid.medopd.utils.Tools;
import com.squareup.picasso.Picasso;

public class ArticleMedium extends AppCompatActivity {
private Bundle bundle;
private FirebaseRef ref=new FirebaseRef();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_medium);
        bundle=getIntent().getExtras();
        initToolbar();
        PutAllValues();
    }

    private void PutAllValues() {
        TextView ArticleDate=findViewById(R.id.ArticleDate);
        ArticleDate.setText(bundle.getString(ref.PublishDate));
        TextView ArticleHeading=findViewById(R.id.ArticleHeading);
        ArticleHeading.setText(bundle.getString(ref.Heading));
        TextView ArticleSubHeading=findViewById(R.id.ArticleSubHeading);
        ArticleSubHeading.setText(bundle.getString(ref.SubHeading));
        Picasso.get().load(bundle.getString(ref.IconUrl)).into((ImageView) findViewById(R.id.ArticleImage));
        TextView ArticleDetails=findViewById(R.id.ArticleDetails);
        ArticleDetails.setText(bundle.getString(ref.Details));
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView WriterName=toolbar.findViewById(R.id.WriterName);
        WriterName.setText(bundle.getString(ref.WriterName));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
