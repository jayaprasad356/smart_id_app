package com.gmwapp.slv_aidi.fragment;

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

import com.gmwapp.slv_aidi.Adapter.NotificationAdapter;
import com.gmwapp.slv_aidi.Adapter.OutsourceActivatedPlanAdapter;
import com.gmwapp.slv_aidi.R;
import com.gmwapp.slv_aidi.activities.MainActivity;
import com.gmwapp.slv_aidi.helper.ApiConfig;
import com.gmwapp.slv_aidi.helper.Constant;
import com.gmwapp.slv_aidi.helper.Session;
import com.gmwapp.slv_aidi.model.NotificationListModal;
import com.gmwapp.slv_aidi.model.OutsourcePlanModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutsourceActivatedFragment extends Fragment {

    private RecyclerView rvOutsourcePlanList;
    private OutsourceActivatedPlanAdapter outsourceActivatedPlanAdapter;
    private List<OutsourcePlanModel> outsourcePlanModel;
    private Session session;
    private Activity activity;
    private LinearLayout llWaiting;
    private RelativeLayout rlNotificationView;
    private RelativeLayout rvNoData;
    private ImageButton ibBack;

    FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outsource_activated, container, false);

        activity = getActivity();
        session = new Session(activity);

        llWaiting = view.findViewById(R.id.llWaiting);
        rlNotificationView = view.findViewById(R.id.rlNotificationView);
        rvNoData = view.findViewById(R.id.rvNoData);
        rvOutsourcePlanList = view.findViewById(R.id.rvOutsourcePlanList);
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
        rvNoData.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView setup
        rvOutsourcePlanList.setLayoutManager(new LinearLayoutManager(getContext()));
        outsourcePlanModel = new ArrayList<>();

        // Load notifications
        loadOutsourcePlan(false);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadOutsourcePlan(boolean reload) {
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
                        outsourcePlanModel.clear();

                        // Parse and add notifications
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            OutsourcePlanModel outsourcePlan = gson.fromJson(obj.toString(), OutsourcePlanModel.class);
                            outsourcePlanModel.add(outsourcePlan);
                        }

                        // Initialize adapter if null or reload is true
                        if (outsourceActivatedPlanAdapter == null || reload) {
                            outsourceActivatedPlanAdapter = new OutsourceActivatedPlanAdapter(activity, outsourcePlanModel, fragmentManager);
                            rvOutsourcePlanList.setAdapter(outsourceActivatedPlanAdapter);
                        } else {
                            outsourceActivatedPlanAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Manage loading visibility
                if (outsourcePlanModel.isEmpty()) {
                    rvNoData.setVisibility(View.VISIBLE);
                    rlNotificationView.setVisibility(View.GONE);
                    llWaiting.setVisibility(View.GONE);
                } else {
                    new Handler().postDelayed(() -> {
                        rvNoData.setVisibility(View.GONE);
                        llWaiting.setVisibility(View.GONE);
                        rlNotificationView.setVisibility(View.VISIBLE);
                    }, 1000);
                }
            }
        }, activity, Constant.OUTSOURCE_USER_PLAN_LIST, params, true);

        Log.d("OUTSOURCE_USER_PLAN_LIST", "OUTSOURCE_USER_PLAN_LIST: " + Constant.OUTSOURCE_USER_PLAN_LIST);
        Log.d("OUTSOURCE_USER_PLAN_LIST", "OUTSOURCE_USER_PLAN_LIST params: " + params);
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
