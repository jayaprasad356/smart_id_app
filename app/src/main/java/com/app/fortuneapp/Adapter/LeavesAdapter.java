package com.app.fortuneapp.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.app.fortuneapp.R;
import com.app.fortuneapp.model.Leaves;

import java.util.ArrayList;


public class LeavesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final Activity activity;
    final ArrayList<Leaves> leaves;

    public LeavesAdapter(Activity activity, ArrayList<Leaves> leaves) {
        this.activity = activity;
        this.leaves = leaves;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.leave_item, parent, false);
        return new ExploreItemHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderParent, int position) {
        final ExploreItemHolder holder = (ExploreItemHolder) holderParent;
        final Leaves leave = leaves.get(position);
        holder.tvDate.setText(leave.getDate());
        holder.tvReason.setText(leave.getReason());

        if (leave.getStatus().equals("1")){
            holder.tvStatus.setText("Approved");
            holder.tvStatus.setTextColor(Color.GREEN);

        }else if (leave.getStatus().equals("2")) {
            holder.tvStatus.setText("Not Approved");
            holder.tvStatus.setTextColor(Color.RED);

        }else {
            holder.tvStatus.setText("Pending");
            holder.tvStatus.setTextColor(Color.BLUE);

        }



    }

    @Override
    public int getItemCount() {
        return leaves.size();
    }

    static class ExploreItemHolder extends RecyclerView.ViewHolder {
        final TextView tvDate,tvReason,tvStatus;
        public ExploreItemHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvStatus = itemView.findViewById(R.id.tvStatus);


        }
    }
}

