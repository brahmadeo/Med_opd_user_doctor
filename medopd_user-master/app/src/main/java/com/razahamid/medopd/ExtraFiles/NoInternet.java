package com.razahamid.medopd.ExtraFiles;


import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.razahamid.medopd.R;
import java.util.Objects;

public class NoInternet extends Fragment {
private int id;
    public NoInternet(int id) {
        this.id=id;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no_internet, container, false);
        view.findViewById(R.id.error_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (isOnline()){
                   FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                   if(getActivity().getSupportFragmentManager().findFragmentById(id) != null) {
                       getActivity().getSupportFragmentManager()
                               .beginTransaction().
                               remove(Objects.requireNonNull(getActivity().getSupportFragmentManager().findFragmentById(id))).commit();
                       manager.popBackStack();
                   }
               }
            }
        });
        return view;
    }
    private boolean isOnline() {
        ConnectivityManager con_manager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert con_manager != null;
        return (con_manager.getActiveNetworkInfo() != null && con_manager.getActiveNetworkInfo().isAvailable() && con_manager.getActiveNetworkInfo().isConnected());
    }

}
