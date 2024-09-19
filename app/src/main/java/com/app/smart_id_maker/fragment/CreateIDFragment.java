package com.app.smart_id_maker.fragment;

import static com.app.smart_id_maker.activities.MainActivity.fm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.app.smart_id_maker.R;
import com.app.smart_id_maker.helper.Session;
import com.google.android.material.button.MaterialButton;


public class CreateIDFragment extends Fragment {


    public CreateIDFragment() {
        // Required empty public constructor
    }

    Session session;
    Activity activity;

    MaterialButton btBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_create_id, container, false);
        activity = getActivity();
        session = new Session(activity);

        btBack = root.findViewById(R.id.btBack);

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fm.beginTransaction().replace(R.id.Container, new HomeFragment()).commitAllowingStateLoss();
                } catch (Exception e) {
                    Log.d("Error", "Error : " + e);
                }
            }
        });

        return root;
    }
}
