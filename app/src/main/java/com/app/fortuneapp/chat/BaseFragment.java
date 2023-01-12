package com.app.fortuneapp.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fortuneapp.chat.adapters.UserAdapters;
import com.app.fortuneapp.chat.models.User;

import java.util.ArrayList;


public class BaseFragment extends Fragment {

    public RecyclerView mRecyclerView;
    public ArrayList<User> mUsers;
    public UserAdapters userAdapters;
    public Activity mActivity;
    public Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mContext = getContext();
    }


}