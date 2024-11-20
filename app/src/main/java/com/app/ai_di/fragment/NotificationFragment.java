package com.app.ai_di.fragment;

import static com.app.ai_di.helper.Constant.SUCCESS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ai_di.Adapter.JobPlanAdapter;
import com.app.ai_di.Adapter.NotificationAdapter;
import com.app.ai_di.R;
import com.app.ai_di.activities.MainActivity;
import com.app.ai_di.helper.ApiConfig;
import com.app.ai_di.helper.Constant;
import com.app.ai_di.helper.Session;
import com.app.ai_di.model.NotificationListModal;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationFragment extends Fragment {

    private RecyclerView rvNotificationList;
    private NotificationAdapter notificationAdapter;
    private List<NotificationListModal> notificationListModal;
    private Session session;
    private Activity activity;
    private LinearLayout llWaiting;
    private RelativeLayout rlNotificationView;
    private ImageButton ibBack;

    FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        activity = getActivity();
        session = new Session(activity);

        llWaiting = view.findViewById(R.id.llWaiting);
        rlNotificationView = view.findViewById(R.id.rlNotificationView);
        rvNotificationList = view.findViewById(R.id.rvNotificationList);
        ibBack = view.findViewById(R.id.ibBack);

        fragmentManager = getParentFragmentManager();

        // Hide BottomNavigationView
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).hideBottomNavigation();
        }

        // Back button click listener
        ibBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Set initial visibility
        llWaiting.setVisibility(View.VISIBLE);
        rlNotificationView.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView setup
        rvNotificationList.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationListModal = new ArrayList<>();

        // Load notifications
        loadNotificationAdapter(false);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadNotificationAdapter(boolean reload) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        Gson gson = new Gson();

                        // Clear the existing list
                        notificationListModal.clear();

                        // Parse and add notifications
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            NotificationListModal notification = gson.fromJson(obj.toString(), NotificationListModal.class);
                            notificationListModal.add(notification);
                        }

                        // Initialize adapter if null or reload is true
                        if (notificationAdapter == null || reload) {
                            notificationAdapter = new NotificationAdapter(activity, notificationListModal, fragmentManager);
                            rvNotificationList.setAdapter(notificationAdapter);
                        } else {
                            notificationAdapter.notifyDataSetChanged();
                        }

                        // Manage loading visibility
                        if (notificationListModal.isEmpty()) {
                            llWaiting.setVisibility(View.VISIBLE);
                            rlNotificationView.setVisibility(View.GONE);
                        } else {
                            new Handler().postDelayed(() -> {
                                llWaiting.setVisibility(View.GONE);
                                rlNotificationView.setVisibility(View.VISIBLE);
                            }, 1000);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.NOTIFICATION_LIST_URL, params, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Show BottomNavigationView again when fragment is destroyed
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).showBottomNavigation();
        }
    }
}

