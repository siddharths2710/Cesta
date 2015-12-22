package com.cesta.cesta;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserFragment extends Fragment {

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences p = getActivity().getSharedPreferences(MainActivity.PREF, Context.MODE_PRIVATE);

        String name = p.getString("name", "Anonymous"); //getIntent().getStringExtra("name");
        String email = p.getString("email", "you@domain.com");//getIntent().getStringExtra("photo");
        String photoUrl = p.getString("photoUrl", "");//getIntent().getStringExtra("email");

        ((TextView) getActivity().findViewById(R.id.alltext)).setText("Name : " + name
                + "\nEmail : " + email + "\nPhoto Url " + photoUrl);

        getActivity().findViewById(R.id.sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MainActivity.startMap = false;
                //startActivity(new Intent(getActivity(), MainActivity.class));
                //((MainActivity) getActivity()).changeFragment(new SignUpFragment());
                ((UserActivity) getActivity()).selectItem(1);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }
}
