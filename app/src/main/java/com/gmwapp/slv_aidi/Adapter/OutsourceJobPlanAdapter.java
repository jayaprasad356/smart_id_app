package com.gmwapp.slv_aidi.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.gmwapp.slv_aidi.R;
import com.gmwapp.slv_aidi.activities.MainActivity;
import com.gmwapp.slv_aidi.fragment.JobsFragment;
import com.gmwapp.slv_aidi.fragment.OutsourceJobFragment;
import com.gmwapp.slv_aidi.helper.ApiConfig;
import com.gmwapp.slv_aidi.helper.Constant;
import com.gmwapp.slv_aidi.helper.Session;
import com.gmwapp.slv_aidi.model.OutsourcePlanList;
import com.gmwapp.slv_aidi.model.PlanListModel;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OutsourceJobPlanAdapter extends RecyclerView.Adapter<OutsourceJobPlanAdapter.PlanViewHolder> {
    private final List<OutsourcePlanList> outsourcePlanList;
    private final Session session;
    private final Activity activity;
    private final OutsourceJobFragment outsourceJobFragment;

    // Constructor
    public OutsourceJobPlanAdapter(Activity activity, List<OutsourcePlanList> outsourcePlanList, OutsourceJobFragment outsourceJobFragment) {
        this.activity = activity;
        this.outsourcePlanList = outsourcePlanList;
        this.session = new Session(activity);
        this.outsourceJobFragment = outsourceJobFragment;
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
        OutsourcePlanList plan = outsourcePlanList.get(position);

        holder.tvPlanName.setText(plan.getName());
        holder.tvPerCode.setText("₹" + plan.getSync_cost());
        holder.tvCourseCharges.setText("₹" + plan.getPrice());
        holder.tvTitlePerCode.setText("Per Day Earnings");
        holder.tvMonthlyEarning.setText("₹" + plan.getMonthly_earnings());
        holder.tvTitleMonthEarning.setText("Per Month Earnings");
        holder.tvMonthlyTarget.setText("₹" + plan.getYearly_earnings());
        holder.tvTitleMonthlyTarget.setText("Per Year Earnings");
        holder.tvDescription.setText(plan.getDescription());

        // Load image using Glide (or any other image loading library)
        Glide.with(activity)
                .load(plan.getImage()) // Assuming `getImage()` returns a URL or URI
                .placeholder(R.drawable.image_icon) // Optional: Add a placeholder
                .error(R.drawable.image_icon) // Optional: Add an error image
                .into(holder.ivImage);

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
        holder.btnZoom.setOnClickListener(view -> {
            dialogZoomable(plan.getImage());
        });

        // Activate plan button logic
        holder.btActivatePlan.setOnClickListener(view -> {
            activatedPlan(plan.getId());
        });

        holder.btActivatePlan.setVisibility(View.VISIBLE);

        // Set the activate button style based on plan status
        if (plan.getStatus().equals(2)) {
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

    public void dialogZoomable(String imageUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity); // Context: Activity
        LayoutInflater inflater = activity.getLayoutInflater(); // LayoutInflater
        View dialogView = inflater.inflate(R.layout.dialog_zoomable_image, null);

        com.github.chrisbanes.photoview.PhotoView photoView = dialogView.findViewById(R.id.photoView);

        // Load image with Glide
        Glide.with(activity)
                .load(imageUrl)
                .placeholder(R.drawable.image_icon)
                .error(R.drawable.image_icon)
                .into(photoView);

        // Initialize buttons
        Button btnZoomIn = dialogView.findViewById(R.id.btnZoomIn);
        Button btnZoomOut = dialogView.findViewById(R.id.btnZoomOut);
        ImageButton btnClose = dialogView.findViewById(R.id.btnClose);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // **Zoom In Button**
        btnZoomIn.setOnClickListener(v -> {
            float currentScale = photoView.getScale();
            float maxScale = photoView.getMaximumScale();
            float newScale = Math.min(currentScale * 1.2f, maxScale); // Clamp to maxScale
            photoView.setScale(newScale, true);
        });

        // **Zoom Out Button**
        btnZoomOut.setOnClickListener(v -> {
            float currentScale = photoView.getScale();
            float minScale = photoView.getMinimumScale();
            float newScale = Math.max(currentScale / 1.2f, minScale); // Clamp to minScale
            photoView.setScale(newScale, true);
        });

        // **Close Button**
        btnClose.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        // Set rounded corner background
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }

        // Display the dialog
        dialog.show();
    }

    private void activatedPlan(String planId) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.PLAN_ID, planId);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString(com.gmwapp.slv_aidi.helper.Constant.MESSAGE);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        outsourceJobFragment.loadPlans(true);
                        if (activity instanceof MainActivity) {
                            ((MainActivity) activity).userDetails();
                        }
                    } else {
                        Toast.makeText(activity, "Please upload your course certificate to activate the plan", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, activity, Constant.OUTSOURCE_ACTIVATE_PLAN, params, true);

        Log.d("OUTSOURCE_ACTIVATE_PLAN", "OUTSOURCE_ACTIVATE_PLAN: " + Constant.OUTSOURCE_ACTIVATE_PLAN);
        Log.d("OUTSOURCE_ACTIVATE_PLAN", "OUTSOURCE_ACTIVATE_PLAN params: " + params);
    }

    @Override
    public int getItemCount() {
        return outsourcePlanList.size();
    }

    private void activatedPlan() {
        // Create an AlertDialog
        new AlertDialog.Builder(activity)
                .setMessage("Hi, Please Sync 50 ID's In Home Screen To Start Another Work Or Logout & Login Again To Change The Work.")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // User canceled - dismiss the dialog
                    dialog.dismiss();
                })
                .show();
    }


    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlanName, tvCourseCharges, tvPerCode, tvMonthlyEarning, tvMonthlyTarget, tvDescription, tvTitlePerCode, tvTitleMonthEarning, tvTitleMonthlyTarget;
        MaterialButton btActivatePlan, btnZoom;
        PhotoView ivImage;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlanName = itemView.findViewById(R.id.tvPlanName);
            tvCourseCharges = itemView.findViewById(R.id.tvCourseCharges);
            tvPerCode = itemView.findViewById(R.id.tvPerCode);
            tvTitlePerCode = itemView.findViewById(R.id.tvTitlePerCode);
            tvMonthlyEarning = itemView.findViewById(R.id.tvMonthlyEarning);
            tvTitleMonthEarning = itemView.findViewById(R.id.tvTitleMonthEarning);
            tvMonthlyTarget = itemView.findViewById(R.id.tvMonthlyTarget);
            tvTitleMonthlyTarget = itemView.findViewById(R.id.tvTitleMonthlyTarget);
            btActivatePlan = itemView.findViewById(R.id.btActivatePlan);
            btnZoom = itemView.findViewById(R.id.btnZoom);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
//            tvMore = itemView.findViewById(R.id.tvMore);
        }
    }
}
