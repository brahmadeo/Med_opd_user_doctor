package com.razahamid.medopddoctor.ExtraFiles;


import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.razahamid.medopddoctor.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoInternet extends Fragment {

    public NoInternet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no_internet, container, false);
        view.findViewById(R.id.error_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (isOnline()){
                   FragmentManager manager = getActivity().getSupportFragmentManager();
                   if(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_frame) != null) {
                       getActivity().getSupportFragmentManager()
                               .beginTransaction().
                               remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_frame)).commit();
                       manager.popBackStack();
                   }
               }
            }
        });
        return view;
    }
    @SuppressWarnings("deprecation")
    private boolean isOnline() {
        ConnectivityManager con_manager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert con_manager != null;
        return (con_manager.getActiveNetworkInfo() != null && con_manager.getActiveNetworkInfo().isAvailable() && con_manager.getActiveNetworkInfo().isConnected());
    }

}
