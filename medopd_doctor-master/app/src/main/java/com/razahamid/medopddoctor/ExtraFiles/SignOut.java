package com.razahamid.medopddoctor.ExtraFiles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.razahamid.medopddoctor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignOut extends Fragment {

    public SignOut() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_out, container, false);
    }
}