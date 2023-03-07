package com.app.fortuneapp.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fortuneapp.databinding.RepayListModelBinding;
import com.app.fortuneapp.model.RepayListModel;

import java.util.ArrayList;

public class RepayListAdapter extends RecyclerView.Adapter<RepayListAdapter.viewHolder> {

    private RepayListModelBinding binding;
    private final ArrayList<RepayListModel> advanceWithdrawlModels;
    public RepayListAdapter(ArrayList<RepayListModel> advanceWithdrawlModels) {
        this.advanceWithdrawlModels = advanceWithdrawlModels;
    }
    protected  class viewHolder extends RecyclerView.ViewHolder {

        public viewHolder(@NonNull RepayListModelBinding itemView) {
            super(itemView.getRoot());
        }
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RepayListModelBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new viewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final RepayListModel model = advanceWithdrawlModels.get(position);
        binding.tvDate.setText(model.getDue_date());
        binding.tvAmount.setText(model.getAmount());
        if (model.getStatus().equals("1")) {
            binding.tvStatus.setTextColor(binding.getRoot().getResources().getColor(android.R.color.holo_green_dark));
            binding.tvStatus.setText("Paid");
        } else {
            binding.tvStatus.setTextColor(binding.getRoot().getResources().getColor(android.R.color.holo_red_dark));
            binding.tvStatus.setText("Unpaid");
        }
    }

    @Override
    public int getItemCount() {
        return advanceWithdrawlModels.size();
    }
}

