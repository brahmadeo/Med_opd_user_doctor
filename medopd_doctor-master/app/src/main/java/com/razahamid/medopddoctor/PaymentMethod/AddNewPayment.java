package com.razahamid.medopddoctor.PaymentMethod;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.razahamid.medopddoctor.R;


public class AddNewPayment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_payment);
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            if (bundle.containsKey("history")){
                if (bundle.getBoolean("history")){
                    loadFragment(new AllTransections((Toolbar) findViewById(R.id.toolbar)));
                }
            }
        }else {
            loadFragment(new SelectAMethod((Toolbar) findViewById(R.id.toolbar)));
        }
    }

    private void loadFragment(Fragment fragment){
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.paymentFrame, fragment);
            ft.commit();
        }
    }
}
