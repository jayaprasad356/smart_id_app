package com.app.ai_di.Adapter;

import static com.app.ai_di.helper.Constant.SUCCESS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ai_di.R;
import com.app.ai_di.activities.MainActivity;
import com.app.ai_di.fragment.JobsFragment;
import com.app.ai_di.helper.ApiConfig;
import com.app.ai_di.helper.Constant;
import com.app.ai_di.helper.Session;
import com.app.ai_di.model.PlanListModel;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobPlanAdapter extends RecyclerView.Adapter<JobPlanAdapter.PlanViewHolder> {
    private final List<PlanListModel> planListModels;
    private final Session session;
    private final Activity activity;
    private final JobsFragment jobsFragment;

    // Constructor
    public JobPlanAdapter(Activity activity, List<PlanListModel> planListModels, JobsFragment jobsFragment) {
        this.activity = activity;
        this.planListModels = planListModels;
        this.session = new Session(activity);
        this.jobsFragment = jobsFragment;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_plan_layout, parent, false);
        return new PlanViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        PlanListModel plan = planListModels.get(position);

        holder.tvPlanName.setText(plan.getName());
        holder.tvPerCode.setText("₹" + plan.getPer_code_cost());
        holder.tvMonthlyEarning.setText("₹" + plan.getMonthly_earnings());
        holder.tvMonthlyTarget.setText(plan.getMonthly_codes());
        holder.tvDescription.setText(plan.getDescription());

        if (plan.getDescription().isEmpty()){
            holder.tvDescription.setVisibility(View.GONE);
        } else {
            holder.tvDescription.setVisibility(View.VISIBLE);
        }

//        // Show only two lines of description initially
//        holder.tvDescription.setMaxLines(2);
//        holder.tvMore.setVisibility(View.GONE); // Hide "More" button initially
//
//        // Measure the number of lines after text has been set
//        holder.tvDescription.post(() -> {
//            if (holder.tvDescription.getLineCount() > 2) {
//                holder.tvMore.setVisibility(View.VISIBLE); // Show "More" if text exceeds 2 lines
//            }
//        });
//
//        // Toggle full description with "More" / "Less"
//        holder.tvMore.setOnClickListener(view -> {
//            if (holder.tvMore.getText().toString().equals("More")) {
//                holder.tvDescription.setMaxLines(Integer.MAX_VALUE); // Expand to show full text
//                holder.tvMore.setText("Less");
//            } else {
//                holder.tvDescription.setMaxLines(2); // Collapse to show only two lines
//                holder.tvMore.setText("More");
//            }
//        });

        // Activate plan button logic
        holder.btActivatePlan.setOnClickListener(view -> {
            activatedPlan(plan.getId());
        });

        holder.btActivatePlan.setVisibility(View.VISIBLE);

        // Set the activate button style based on plan status
        if (plan.getStatus().equals(1)) {
            Log.d("START_WORK", "START_WORK 1");
//            holder.btActivatePlan.setBackgroundResource(R.drawable.button_green_bg);
//            holder.btActivatePlan.setText("Start work");
            holder.btActivatePlan.setEnabled(true);
            boolean isPlanChange = session.getBoolean(Constant.IS_PLAN_CHANGE);
            Log.d("START_WORK", "START_WORK isPlanChange: " + isPlanChange);
            holder.btActivatePlan.setVisibility(View.VISIBLE);
            if(isPlanChange) {
                Log.d("START_WORK", "START_WORK 2");
                holder.btActivatePlan.setText("Start work");
                holder.btActivatePlan.setBackgroundResource(R.drawable.button_green_bg);
                holder.btActivatePlan.setOnClickListener(view -> {
                    session.setData(Constant.START_WORK, plan.getId());
                    Log.d("START_WORK", "START_WORK isPlanChange: " + session.getData(Constant.START_WORK));
                    session.setData(Constant.START_WORK_PLAN_NAME, plan.getName());
                    Log.d("START_WORK", "START_WORK isPlanChange: " + session.getData(Constant.START_WORK_PLAN_NAME));
                    Toast.makeText(activity, "Start work with " + plan.getName(), Toast.LENGTH_SHORT).show();
                    // Navigate to home screen after activating plan
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).navigateToHome();
                    }
                });
                Log.d("START_WORK", "START_WORK 3");
            } else {
                Log.d("START_WORK", "START_WORK 4");
                Log.d("START_WORK", "START_WORK isEnabled...");
                holder.btActivatePlan.setVisibility(View.VISIBLE);
                holder.btActivatePlan.setText("Start work");
                holder.btActivatePlan.setBackgroundResource(R.drawable.disabled_button_bg);
                holder.btActivatePlan.setEnabled(false);
                Log.d("START_WORK", "START_WORK 5");
            }
            Log.d("START_WORK", "START_WORK 6");
        } else if (plan.getStatus().equals(2)) {
            Log.d("START_WORK", "START_WORK 7");
            holder.btActivatePlan.setEnabled(true);
            holder.btActivatePlan.setVisibility(View.GONE);
            Log.d("START_WORK", "START_WORK 8");
        } else {
            Log.d("START_WORK", "START_WORK 9");
            holder.btActivatePlan.setEnabled(true);
            holder.btActivatePlan.setVisibility(View.VISIBLE);
            holder.btActivatePlan.setBackgroundResource(R.drawable.button_bg);
            holder.btActivatePlan.setText("Activate Plan");
            Log.d("START_WORK", "START_WORK 10");
        }
    }

    private void activatedPlan(String planId) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.PLAN_ID, planId);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString(com.app.ai_di.helper.Constant.MESSAGE);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        jobsFragment.loadPlans(true);
                        if (activity instanceof MainActivity) {
                            ((MainActivity) activity).userDetails();
                        }
                    } else {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, activity, Constant.ACTIVATE_PLAN, params, true);

        Log.d("ACTIVATE_PLAN", "ACTIVATE_PLAN: " + Constant.ACTIVATE_PLAN);
        Log.d("ACTIVATE_PLAN", "ACTIVATE_PLAN params: " + params);
    }

    @Override
    public int getItemCount() {
        return planListModels.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlanName, tvPerCode, tvMonthlyEarning, tvMonthlyTarget, tvDescription;
        MaterialButton btActivatePlan;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlanName = itemView.findViewById(R.id.tvPlanName);
            tvPerCode = itemView.findViewById(R.id.tvPerCode);
            tvMonthlyEarning = itemView.findViewById(R.id.tvMonthlyEarning);
            tvMonthlyTarget = itemView.findViewById(R.id.tvMonthlyTarget);
            btActivatePlan = itemView.findViewById(R.id.btActivatePlan);
            tvDescription = itemView.findViewById(R.id.tvDescription);
//            tvMore = itemView.findViewById(R.id.tvMore);
        }
    }
}

