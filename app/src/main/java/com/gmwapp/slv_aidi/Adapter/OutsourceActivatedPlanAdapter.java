package com.gmwapp.slv_aidi.Adapter;

import static com.gmwapp.slv_aidi.helper.Constant.SUCCESS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gmwapp.slv_aidi.R;
import com.gmwapp.slv_aidi.activities.MainActivity;
import com.gmwapp.slv_aidi.helper.ApiConfig;
import com.gmwapp.slv_aidi.helper.Constant;
import com.gmwapp.slv_aidi.helper.CustomDialog;
import com.gmwapp.slv_aidi.helper.Session;
import com.gmwapp.slv_aidi.model.NotificationListModal;
import com.gmwapp.slv_aidi.model.OutsourcePlanModel;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class OutsourceActivatedPlanAdapter extends RecyclerView.Adapter<OutsourceActivatedPlanAdapter.OutsourceViewHolder> {

    private final List<OutsourcePlanModel> outsourcePlanModel;
    private final Session session;
    private final Activity activity;
    private final FragmentManager fragmentManager;

    // Constructor
    public OutsourceActivatedPlanAdapter(Activity activity, List<OutsourcePlanModel> outsourcePlanModel, FragmentManager fragmentManager) {
        this.activity = activity;
        this.session = new Session(activity);
        this.outsourcePlanModel = outsourcePlanModel;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public OutsourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_outsource_plan, parent, false);
        return new OutsourceViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OutsourceViewHolder holder, int position) {
        OutsourcePlanModel plan = outsourcePlanModel.get(position);

        // Set data to TextViews
        holder.tvPerDayEarning.setText(plan.getSyncCost());
        holder.tvPlanName.setText(plan.getName());
        holder.tvMonthlyEarning.setText(plan.getMonthly_earnings());
        holder.tvDate.setText(plan.getJoinedDate());

        // Load image using Glide (or any other image loading library)
        Glide.with(activity)
                .load(plan.getImage()) // Assuming `getImage()` returns a URL or URI
                .placeholder(R.drawable.image_icon) // Optional: Add a placeholder
                .error(R.drawable.image_icon) // Optional: Add an error image
                .into(holder.ivImage);

        setBtSyncNow(holder, plan.getPlanId(), plan.getId());

        // Handle "Zoom" button click to open fragment
        holder.btnZoom.setOnClickListener(view -> {

            String imageUrl = "https://img.freepik.com/free-photo/abstract-autumn-beauty-multi-colored-leaf-vein-pattern-generated-by-ai_188544-9871.jpg?t=st=1731926684~exp=1731930284~hmac=6b3925681d269f16f1da56f1f379272235e4c691d198e9d1ba4c0c3143a23b9e&w=1060";

            dialogZoomable(plan.getImage());
        });
    }

    private void setBtSyncNow(OutsourceViewHolder holder, String planId, String userPlanId) {
        holder.btClaim.setOnClickListener(v -> {
            // Show the progress bar
            final CustomDialog customDialog = new CustomDialog(activity);
            customDialog.showDialog();

            // Start a 3-second timer
            new Handler().postDelayed(() -> {
                // Hide the progress bar
                customDialog.closeDialog();

                syncNowApi(planId, userPlanId);
            }, 3000); // 3-second delay
        });
    }


    public void syncNowApi(String planId, String userPlanId) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.OUTSOURCE_USER_PLAN_ID, userPlanId);
        params.put(Constant.PLAN_ID, planId);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    if (jsonObject.getBoolean(SUCCESS)) {
//                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        if (activity instanceof MainActivity) {
                            ((MainActivity) activity).userDetails();
                        }
                    } else {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("SYNC_CODE", "SYNC_CODE Error: " + e.getMessage());
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, activity, Constant.OUTSOURCE_SYNC, params, true);

        Log.d("OUTSOURCE_SYNC", "OUTSOURCE_SYNC: " + Constant.OUTSOURCE_SYNC);
        Log.d("OUTSOURCE_SYNC", "OUTSOURCE_SYNC params: " + params);
    }

    @Override
    public int getItemCount() {
        return outsourcePlanModel.size();
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

    public static class OutsourceViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlanName, tvDate, tvDescription, tvPerDayEarning, tvMonthlyEarning;
        MaterialButton btClaim;
        ImageView ivImage;
        Button btnZoom;

        public OutsourceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlanName = itemView.findViewById(R.id.tvPlanName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btClaim = itemView.findViewById(R.id.btClaim);
            btnZoom = itemView.findViewById(R.id.btnZoom);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvPerDayEarning = itemView.findViewById(R.id.tvPerDayEarning);
            tvMonthlyEarning = itemView.findViewById(R.id.tvMonthlyEarning);
        }
    }
}