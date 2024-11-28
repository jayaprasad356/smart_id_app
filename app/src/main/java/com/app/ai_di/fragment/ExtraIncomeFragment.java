package com.app.ai_di.fragment;

import static com.app.ai_di.helper.Constant.SUCCESS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ai_di.Adapter.JobPlanAdapter;
import com.app.ai_di.Adapter.ReferTargetAdapter;
import com.app.ai_di.ProfileFragment.MyReferFragment;
import com.app.ai_di.R;
import com.app.ai_di.activities.MainActivity;
import com.app.ai_di.helper.ApiConfig;
import com.app.ai_di.helper.Constant;
import com.app.ai_di.model.ExtraPlanModel;
import com.app.ai_di.model.PlanListModel;
import com.app.ai_di.model.RefersTargetModel;
import com.google.android.material.button.MaterialButton;
import com.app.ai_di.helper.Session;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtraIncomeFragment extends Fragment {

    Session session;
    Activity activity;
    private ReferTargetAdapter referTargetAdapter;
    private final List<RefersTargetModel> refersTargetModel = new ArrayList<>(); // Initialize here

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_extra_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Session object
        activity = getActivity();
        session = new Session(activity); // or getContext(), depending on your implementation

        // Initialize RecyclerView
        RecyclerView rvSlabList = view.findViewById(R.id.rvSlabList);
        MaterialButton btActivatePlan = view.findViewById(R.id.btActivatePlan);
        MaterialCardView mcActivatePlan = view.findViewById(R.id.mcActivatePlan);

        rvSlabList.setLayoutManager(new LinearLayoutManager(activity));
        referTargetAdapter = new ReferTargetAdapter(activity, refersTargetModel, this);
        rvSlabList.setAdapter(referTargetAdapter);

        mcActivatePlan.setVisibility(View.VISIBLE);

        loadSlabs();

        btActivatePlan.setOnClickListener(v -> activatedExtraIncomePlan());

        List<ExtraPlanModel> extraPlans = session.getExtraPlanData();

        if (extraPlans != null || !extraPlans.isEmpty()) {
            mcActivatePlan.setVisibility(View.GONE);
        } else {
            mcActivatePlan.setVisibility(View.VISIBLE);
        }
    }

    private void activatedExtraIncomePlan() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.EXTRA_CLAIM_PLAN_ID, "1");

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString(com.app.ai_di.helper.Constant.MESSAGE);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        if (activity instanceof MainActivity) {
                            ((MainActivity) activity).userDetails();
                        }
                        referTargetAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, activity, Constant.EXTRA_INCOME_PLAN_ACTIVATE, params, true);

        Log.d("EXTRA_INCOME_PLAN_ACTIVATE", "EXTRA_INCOME_PLAN_ACTIVATE: " + Constant.EXTRA_INCOME_PLAN_ACTIVATE);
        Log.d("EXTRA_INCOME_PLAN_ACTIVATE", "EXTRA_INCOME_PLAN_ACTIVATE params: " + params);
    }

    public void loadSlabs() {
        Map<String, String> params = new HashMap<>();
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        Gson gson = new Gson();

                        // Clear the list before adding new data
                        refersTargetModel.clear();

                        // Parse response and add to the list
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            RefersTargetModel group = gson.fromJson(jsonObject1.toString(), RefersTargetModel.class);
                            refersTargetModel.add(group);
                        }

                        // Notify the adapter of data changes
                        referTargetAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.REFER_TARGET_URL, params, true);

        Log.d("REFER_TARGET_URL", "REFER_TARGET_URL: " + Constant.REFER_TARGET_URL);
        Log.d("REFER_TARGET_URL", "REFER_TARGET_URL params: " + params);
    }
}

