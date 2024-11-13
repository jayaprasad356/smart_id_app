package com.app.ai_di.fragment;

import static com.app.ai_di.helper.Constant.SUCCESS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ai_di.Adapter.JobPlanAdapter;
import com.app.ai_di.R;
import com.app.ai_di.helper.ApiConfig;
import com.app.ai_di.helper.Constant;
import com.app.ai_di.helper.Session;
import com.app.ai_di.model.PlanListModel;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JobsFragment extends Fragment {

    private RecyclerView rvPlanList;
    private JobPlanAdapter jobPlanAdapter;
    private List<PlanListModel> planListModel;  // Initialize list here
    Session session;
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);

        activity = getActivity();
        session = new Session(activity);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        rvPlanList = view.findViewById(R.id.rvPlanList);
        rvPlanList.setLayoutManager(new LinearLayoutManager(getContext()));
        MaterialButton btnRecharge = view.findViewById(R.id.btnRecharge);

        // Initialize the plan list
        planListModel = new ArrayList<>();

        // Load plan data
        loadPlans(false);

        btnRecharge.setOnClickListener(v -> showRechargeDialog());
        btnRecharge.setText("Recharge ₹" + session.getData(Constant.RECHARGE));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadPlans(boolean reload) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(SUCCESS)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        Gson g = new Gson();

                        // Clear the plan list before adding new data
                        planListModel.clear();

                        // Parse response and add to plan list
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            PlanListModel group = g.fromJson(jsonObject1.toString(), PlanListModel.class);
                            planListModel.add(group);
                        }

                        // Set adapter after data is loaded
                        jobPlanAdapter = new JobPlanAdapter(activity, planListModel, this);
                        rvPlanList.setAdapter(jobPlanAdapter);

                        if(reload) {
                            jobPlanAdapter.notifyDataSetChanged();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.PLAN_LIST_URL, params, true);

        Log.d("PLAN_LIST_URL", "PLAN_LIST_URL: " + Constant.PLAN_LIST_URL);
        Log.d("PLAN_LIST_URL", "PLAN_LIST_URL params: " + params);
    }

    private void showRechargeDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_recharge_custom, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        ImageButton btClose = dialogView.findViewById(R.id.btClose);
        MaterialButton btRechargePayment = dialogView.findViewById(R.id.btRechargePayment);

        btClose.setOnClickListener(v -> dialog.dismiss());

        btRechargePayment.setOnClickListener(v -> {
            String url = Constant.RECHARGE_URL; // Replace with your URL
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        dialog.show();
    }

}

