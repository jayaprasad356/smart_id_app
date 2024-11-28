package com.app.ai_di.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ai_di.R;
import com.app.ai_di.model.LeaveListModel;
import com.app.ai_di.model.Transanction;

import java.util.List;

public class LeaveAdapter extends RecyclerView.Adapter<LeaveAdapter.LeaveViewHolder> {

    private final Context context;
    private final List<LeaveListModel> leaveList;

    public LeaveAdapter(Context context, List<LeaveListModel> leaveList) {
        this.context = context;
        this.leaveList = leaveList;
    }

    @NonNull
    @Override
    public LeaveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leave_approval_layout, parent, false);
        return new LeaveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaveViewHolder holder, int position) {
        LeaveListModel leave = leaveList.get(position);

        // Bind data to the views
        holder.tvDateTime.setText(leave.getDatetime());
        holder.tvDescription.setText(leave.getDescription());

        // Example of conditional styling
        if (leave.getStatus().equalsIgnoreCase("approved")) {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.tvStatus.setText("Approved");
        } else if (leave.getStatus().equalsIgnoreCase("rejected")) {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.tvStatus.setText("Rejected");
        } else {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.primaryColor));
            holder.tvStatus.setText("Pending");
        }
    }

    @Override
    public int getItemCount() {
        return leaveList.size();
    }

    static class LeaveViewHolder extends RecyclerView.ViewHolder {
        final TextView tvStatus;
        final TextView tvDateTime;
        final TextView tvDescription;

        public LeaveViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}

