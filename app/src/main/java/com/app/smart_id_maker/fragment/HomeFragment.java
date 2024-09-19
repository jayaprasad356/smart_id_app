package com.app.smart_id_maker.fragment;

import static com.app.smart_id_maker.activities.MainActivity.fm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.app.smart_id_maker.R;
import com.app.smart_id_maker.helper.Session;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    Session session;
    Activity activity;

    MaterialButton btCreate;
    EditText edSchoolName;
    EditText edStudentName;
    EditText edRollNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        activity = getActivity();
        session = new Session(activity);

        btCreate = root.findViewById(R.id.btCreate);
        edSchoolName = root.findViewById(R.id.edSchoolName);
        edStudentName = root.findViewById(R.id.edStudentName);
        edRollNumber = root.findViewById(R.id.edRollNumber);

        // Navigate to CreateIDFragment on button click
        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction().replace(R.id.Container, new CreateIDFragment()).commitAllowingStateLoss();
            }
        });

        // enable paste option in EditText
        edSchoolName.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });

        edSchoolName.setLongClickable(false); // enable long click
        edSchoolName.setTextIsSelectable(false); // enable long click

        edStudentName.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });

        edStudentName.setLongClickable(false);

        edRollNumber.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });

        edRollNumber.setLongClickable(false);

        return root;
    }
}
