package com.razahamid.medopddoctor.PaymentMethod;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.razahamid.medopddoctor.R;

import java.util.Objects;

public class SelectAMethod extends Fragment {
private View view;
private Toolbar toolbar;
    public SelectAMethod(Toolbar toolbar) {
        this.toolbar=toolbar;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_select_a_method, container, false);
        TextView title=toolbar.findViewById(R.id.title);
        title.setText("Add Payment Method");
        toolbar.findViewById(R.id.exitButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        view.findViewById(R.id.bankMethod).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new BankAcount(toolbar));
            }
        });
        view.findViewById(R.id.upiPayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               loadFragment(new PUIAccount(toolbar));
            }
        });
        return view;
    }
    private void loadFragment(Fragment fragment){
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.paymentFrame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
}
