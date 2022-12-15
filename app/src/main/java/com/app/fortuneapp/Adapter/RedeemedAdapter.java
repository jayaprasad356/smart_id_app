package com.app.fortuneapp.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fortuneapp.model.Redeem;
import com.app.fortuneapp.R;

import java.util.ArrayList;

public class RedeemedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final Activity activity;
    final ArrayList<Redeem> redeems;

    public RedeemedAdapter(Activity activity, ArrayList<Redeem> redeems) {
        this.activity = activity;
        this.redeems = redeems;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.redeem_list_layout, parent, false);
        return new ItemHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderParent, int position) {
        final ItemHolder holder = (ItemHolder) holderParent;
        final Redeem redeem = redeems.get(position);

        holder.tvAmount.setText("₹ "+redeem.getAmount());
        if (redeem.getStatus().equals("paid")){
            holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.green));
        }else if (redeem.getStatus().equals("pending")){
            holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.primaryColor));
        }else {
            holder.tvStatus.setTextColor(activity.getResources().getColor(R.color.red));
        }
        holder.tvStatus.setText(redeem.getStatus());
        holder.tvDateTime.setText(redeem.getDatetime());

    }


    @Override
    public int getItemCount() {
        return redeems.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {

        final TextView tvAmount,tvStatus,tvDateTime;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);



        }
    }
}

