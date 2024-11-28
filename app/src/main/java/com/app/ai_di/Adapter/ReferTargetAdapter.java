package com.app.ai_di.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ai_di.ProfileFragment.MyReferFragment;
import com.app.ai_di.R;
import com.app.ai_di.activities.MainActivity;
import com.app.ai_di.fragment.ExtraIncomeFragment;
import com.app.ai_di.helper.ApiConfig;
import com.app.ai_di.helper.Constant;
import com.app.ai_di.helper.Session;
import com.app.ai_di.model.ReferPlansModel;
import com.app.ai_di.model.RefersTargetModel;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReferTargetAdapter extends RecyclerView.Adapter<ReferTargetAdapter.referTargetAdapterViewHolder> {

    private final List<RefersTargetModel> refersTargetModels;
    private final Activity activity;
    private final Session session;

    ExtraIncomeFragment extraIncomeFragment;
    // Constructor
    public ReferTargetAdapter(Activity activity, List<RefersTargetModel> refersTargetModels, ExtraIncomeFragment extraIncomeFragment) {
        this.activity = activity;
        this.refersTargetModels = refersTargetModels;
        this.session = new Session(activity);
        this.extraIncomeFragment = extraIncomeFragment;
    }

    @NonNull
    @Override
    public referTargetAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slab_layout, parent, false);
        return new referTargetAdapterViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull referTargetAdapterViewHolder holder, int position) {
        RefersTargetModel refersTargetModel = refersTargetModels.get(position);

        // Set data
        holder.tvTitle.setText(refersTargetModel.getTitle());
        holder.tvTotalTarget.setText("/" + refersTargetModel.getReferCount());
        holder.tvMyRefer.setText(session.getData(Constant.TOTAL_REFERRALS));

        holder.btClaimSlab.setText("claim ₹" + refersTargetModel.getBonus());

        if (Objects.equals(refersTargetModel.getStatus(), "1")) {
            holder.btClaimSlab.setBackgroundResource(R.drawable.button_bg);
            holder.btClaimSlab.setEnabled(true);
            holder.btClaimSlab.setOnClickListener(view -> {
                claimSlabPlan(refersTargetModel.getId());
            });
        } else {
            holder.btClaimSlab.setBackgroundResource(R.drawable.disabled_button_bg);
            holder.btClaimSlab.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return refersTargetModels.size();
    }

    private void claimSlabPlan(String referId) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.REFER_ID, referId);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString(com.app.ai_di.helper.Constant.MESSAGE);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        extraIncomeFragment.loadSlabs(true);
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
        }, activity, Constant.CLAIM_EXTRA_INCOME, params, true);

        Log.d("CLAIM_EXTRA_INCOME", "CLAIM_EXTRA_INCOME: " + Constant.CLAIM_EXTRA_INCOME);
        Log.d("CLAIM_EXTRA_INCOME", "CLAIM_EXTRA_INCOME params: " + params);
    }

    public static class referTargetAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTotalTarget,tvMyRefer;

        MaterialButton btClaimSlab;


        public referTargetAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTotalTarget = itemView.findViewById(R.id.tvTotalTarget);
            tvMyRefer = itemView.findViewById(R.id.tvMyRefer);
            btClaimSlab = itemView.findViewById(R.id.btClaimSlab);
        }
    }
}
