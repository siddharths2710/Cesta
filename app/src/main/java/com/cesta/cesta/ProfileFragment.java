package com.cesta.cesta;

import android.support.v4.app.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class ProfileFragment extends Fragment {

    Account ac;
    private TextView textView;

    // TextView textView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile2, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        ac = ((MapsActivity) getActivity()).getAccount();
        //  textView = (TextView)getView().findViewById(R.id.profile_list);
        //ArrayList<String> list =
        //  new ArrayList<String>(Arrays.asList(ac.getName(), ac.getEmail(), "Age :" + ac.getAge()));
        //ArrayAdapter<String> arrAdapt = new ArrayAdapter<String>(getContext(),R.layout.fragment_profile2,R.id.profile_list,list);
        textView = (TextView) getActivity().findViewById(R.id.profile_list);
        textView.setText(ac.getName() + ", " + ac.getEmail() + ", " + ac.getImagePath());
    }


}
