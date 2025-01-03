package com.gmwapp.slv_aidi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmwapp.slv_aidi.R;
import com.gmwapp.slv_aidi.model.LevelTypeData;

import java.util.List;

public class LevelTypeAdapter extends RecyclerView.Adapter<LevelTypeAdapter.LevelTypeViewHolder> {

    private final List<LevelTypeData> levelTypeList;
    private int selectedPosition = 0; // Default to the first item
    private final OnLevelSelectedListener listener;

    public interface OnLevelSelectedListener {
        void onLevelSelected(String levelId);
    }

    public LevelTypeAdapter(List<LevelTypeData> levelTypeList, OnLevelSelectedListener listener) {
        this.levelTypeList = levelTypeList;
        this.listener = listener;
    }

    public static class LevelTypeViewHolder extends RecyclerView.ViewHolder {
        TextView levelItem;

        public LevelTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            levelItem = itemView.findViewById(R.id.level_item);
        }
    }

    @NonNull
    @Override
    public LevelTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_level_icome, parent, false);
        return new LevelTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelTypeViewHolder holder, int position) {
        LevelTypeData levelTypeData = levelTypeList.get(position);

        // Set item text
        holder.levelItem.setText(levelTypeData.getName());

        // Highlight selected item
        if (position == selectedPosition) {
            holder.levelItem.setBackgroundResource(R.drawable.selected_background);
            holder.levelItem.setTextColor(holder.itemView.getContext().getColor(R.color.white));
        } else {
            holder.levelItem.setBackgroundResource(R.drawable.round_border);
            holder.levelItem.setTextColor(holder.itemView.getContext().getColor(R.color.black));
        }

        // Handle click
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = position;

            if (previousPosition != -1) {
                notifyItemChanged(previousPosition); // Unselect previous
            }
            notifyItemChanged(selectedPosition); // Select new

            // Notify listener
            listener.onLevelSelected(levelTypeData.getId());
        });
    }

    @Override
    public int getItemCount() {
        return levelTypeList.size();
    }
}



