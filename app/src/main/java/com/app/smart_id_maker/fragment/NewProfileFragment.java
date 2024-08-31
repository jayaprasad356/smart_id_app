package com.app.smart_id_maker.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.app.smart_id_maker.R;
import com.app.smart_id_maker.activities.InviteActivity;
import com.app.smart_id_maker.activities.MyProfileActivity;
import com.app.smart_id_maker.activities.MybanckActivity;
import com.app.smart_id_maker.activities.SetPasswordActivity;
import com.app.smart_id_maker.activities.TransactionActivity;
import com.app.smart_id_maker.activities.UpdateBankActivity;
import com.app.smart_id_maker.activities.WithdrawalActivity;
import com.app.smart_id_maker.activities.WithdrawalStatusActivity;
import com.app.smart_id_maker.helper.Constant;
import com.app.smart_id_maker.helper.Session;


public class NewProfileFragment extends Fragment {


    public NewProfileFragment() {
        // Required empty public constructor
    }

    Session session;
    Activity activity;



    TextView tvName,tvMobile;

    RelativeLayout rlwithdrawhistory,rlhistory,rlUpdateprofile,rlChangepassword,rlmyBank,rlInvite,rlLogout,rlWithdraw,rlUpdateBank;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_new_profile, container, false);
        activity = getActivity();
        session = new Session(activity);



        tvName = root.findViewById(R.id.tvName);
        tvMobile = root.findViewById(R.id.tvMobile);


        tvName.setText(session.getData(Constant.NAME));
        tvMobile.setText(session.getData(Constant.MOBILE));

        rlwithdrawhistory = root.findViewById(R.id.rlwithdrawhistory);
        rlhistory = root.findViewById(R.id.rlhistory);
        rlUpdateprofile = root.findViewById(R.id.rlUpdateprofile);
        rlChangepassword = root.findViewById(R.id.rlChangepassword);
        rlmyBank = root.findViewById(R.id.rlmyBank);
        rlLogout = root.findViewById(R.id.rlLogout);
        rlInvite = root.findViewById(R.id.rlInvite);
        rlWithdraw = root.findViewById(R.id.rlWithdraw);
        rlUpdateBank = root.findViewById(R.id.rlUpdateBank);


        rlwithdrawhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, WithdrawalStatusActivity.class));
            }


        });


        rlUpdateBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, UpdateBankActivity.class);
                startActivity(intent);
            }
        });

        rlWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WithdrawalActivity.class);
                startActivity(intent);
            }
        });

        rlInvite.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), InviteActivity.class);
            startActivity(intent);
        });

//        rlInvite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               Intent intent =  new Intent(activity, InviteActivity.class);
//               activity.startActivity(intent);
//            }
//        });

        rlhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, TransactionActivity.class));
            }
        });


        rlUpdateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, MyProfileActivity.class));
            }
        });
        rlmyBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, MybanckActivity.class));
            }
        });

        rlChangepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, SetPasswordActivity.class));
            }
        });


        rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser(activity);
            }
        });






        return root;

    }
}