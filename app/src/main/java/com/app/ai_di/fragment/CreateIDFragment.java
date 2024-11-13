package com.app.ai_di.fragment;

import static com.app.ai_di.activities.MainActivity.fm;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.app.ai_di.R;
import com.app.ai_di.helper.Session;
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
        View root = inflater.inflate(R.layout.fragment_create_id, container, false);
        activity = getActivity();
        session = new Session(activity);

        // Retrieve data from the arguments
        Bundle args = getArguments();
        String schoolName = args != null ? args.getString("schoolName") : "";
        String studentName = args != null ? args.getString("studentName") : "";
        String rollNumber = args != null ? args.getString("rollNumber") : "";
        String dob = args != null ? args.getString("dob") : "";

        // Find your TextViews and set the values
        TextView tvSchoolName = root.findViewById(R.id.tvSchoolName);
        TextView tvStudentName = root.findViewById(R.id.tvStudentName);
        TextView tvRollNumber = root.findViewById(R.id.tvRollNumber);
        TextView tvDOB = root.findViewById(R.id.tvDOB);

        tvSchoolName.setText(schoolName);
        tvStudentName.setText(studentName);
        tvRollNumber.setText(rollNumber);
        tvDOB.setText(dob);

        btBack = root.findViewById(R.id.btBack);
        btBack.setVisibility(View.GONE);

        fetchSessionDataAndInitialize();

        return root;
    }

    private void fetchSessionDataAndInitialize() {
        new Handler().postDelayed(() -> {
            fm.beginTransaction().replace(R.id.Container, new HomeFragment()).commitAllowingStateLoss();
        }, 3000);
    }

}
