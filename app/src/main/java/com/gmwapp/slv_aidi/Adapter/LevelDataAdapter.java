package com.gmwapp.slv_aidi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gmwapp.slv_aidi.R;
import com.gmwapp.slv_aidi.model.LevelIncomeList;

import java.util.List;

public class LevelDataAdapter extends RecyclerView.Adapter<LevelDataAdapter.ItemViewHolder> {

    private List<LevelIncomeList> levelIncomeDatas;

    // Constructor
    public LevelDataAdapter(List<LevelIncomeList> levelIncomeDatas) {
        this.levelIncomeDatas = levelIncomeDatas;
    }

    // ViewHolder class to hold each item
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView serialNumber;
        TextView name;
        TextView mobileNumber;
        TextView registeredDate;
        TextView totalIncome;
        TextView levelIncome;

        public ItemViewHolder(View view) {
            super(view);
            serialNumber = view.findViewById(R.id.tvSerialNumber);
            name = view.findViewById(R.id.tvName);
            mobileNumber = view.findViewById(R.id.tvMobileNumber);
            registeredDate = view.findViewById(R.id.tvRegisteredDate);
            totalIncome = view.findViewById(R.id.tvTotalIncome);
            levelIncome = view.findViewById(R.id.tvLevelIncome);
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_level_income_data, parent, false);
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // Set the serial number as position + 1
        holder.serialNumber.setText(String.valueOf(position + 1));

        // Get the current item at the position
        LevelIncomeList levelIncomeData = levelIncomeDatas.get(position);
        holder.name.setText(levelIncomeData.getName());
        holder.mobileNumber.setText(levelIncomeData.getMobileNumber());
        holder.registeredDate.setText(levelIncomeData.getJoinedDate());
        holder.totalIncome.setText(levelIncomeData.getTotalEarnings());
        holder.levelIncome.setText(levelIncomeData.getTeamIncome());
    }


    @Override
    public int getItemCount() {
        return levelIncomeDatas.size();
    }
}