package com.gmwapp.slv_aidi.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmwapp.slv_aidi.R;
import com.gmwapp.slv_aidi.model.ReferPlansModel;

import java.util.List;
import java.util.Objects;

public class ReferUserAdapter extends RecyclerView.Adapter<ReferUserAdapter.referUserAdapterViewHolder> {

    private final List<ReferPlansModel> referPlansModels;
    private final Activity activity;
    // Constructor
    public ReferUserAdapter(Activity activity, List<ReferPlansModel> referPlansModels) {
        this.activity = activity;
        this.referPlansModels = referPlansModels;
    }

    @NonNull
    @Override
    public referUserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.refer_user_layout, parent, false);
        return new referUserAdapterViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull referUserAdapterViewHolder holder, int position) {
        ReferPlansModel referPlansModel = referPlansModels.get(position);

        // Set data
        holder.tvMobileNumber.setText(referPlansModel.getMobile());
        holder.tvRegisterDate.setText(referPlansModel.getJoinedDate());

        holder.llPlanView.setVisibility(View.GONE);
        holder.tvFreePlan.setVisibility(View.GONE);
        holder.tvBasicPlan.setVisibility(View.GONE);
        holder.tvStandardPlan.setVisibility(View.GONE);
        holder.tvAdvancedPlan.setVisibility(View.GONE);

        if (Objects.equals(referPlansModel.getFreeTrail(), "1")) {
            holder.llPlanView.setVisibility(View.VISIBLE);
            holder.tvFreePlan.setVisibility(View.VISIBLE);
        }

        if (Objects.equals(referPlansModel.getBasicPlan(), "1")) {
            holder.llPlanView.setVisibility(View.VISIBLE);
            holder.tvBasicPlan.setVisibility(View.VISIBLE);
        }

        if (Objects.equals(referPlansModel.getStandardPlan(), "1")) {
            holder.llPlanView.setVisibility(View.VISIBLE);
            holder.tvStandardPlan.setVisibility(View.VISIBLE);
        }

        if (Objects.equals(referPlansModel.getAdvancedPlan(), "1")) {
            holder.llPlanView.setVisibility(View.VISIBLE);
            holder.tvAdvancedPlan.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return referPlansModels.size();
    }

    public static class referUserAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvMobileNumber, tvRegisterDate, tvFreePlan, tvBasicPlan, tvStandardPlan, tvAdvancedPlan;

        LinearLayout llPlanView;

        public referUserAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMobileNumber = itemView.findViewById(R.id.tvMobileNumber);
            tvRegisterDate = itemView.findViewById(R.id.tvRegisterDate);
            tvFreePlan = itemView.findViewById(R.id.tvFreePlan);
            tvBasicPlan = itemView.findViewById(R.id.tvBasicPlan);
            tvStandardPlan = itemView.findViewById(R.id.tvStandardPlan);
            tvAdvancedPlan = itemView.findViewById(R.id.tvAdvancedPlan);
            llPlanView = itemView.findViewById(R.id.llPlanView);
        }
    }
}
