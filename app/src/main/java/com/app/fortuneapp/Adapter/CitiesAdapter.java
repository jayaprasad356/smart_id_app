package com.app.fortuneapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fortuneapp.R;
import com.app.fortuneapp.fragment.FindMissingFragment;
import com.app.fortuneapp.model.GenerateCodes;

import java.util.ArrayList;



public class CitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final Activity activity;
    ArrayList<GenerateCodes> generateCodes;
    Dialog dialog;
    FindMissingFragment findMissingFragment;

    public CitiesAdapter(Activity activity, ArrayList<GenerateCodes> generateCodes, Dialog dialog,FindMissingFragment findMissingFragment) {
        this.activity = activity;
        this.generateCodes = generateCodes;
        this.dialog = dialog;
        this.findMissingFragment = findMissingFragment;
    }





    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.cities_list_layout, parent, false);
        return new ExploreItemHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderParent, @SuppressLint("RecyclerView") int position) {
        final ExploreItemHolder holder = (ExploreItemHolder) holderParent;
        final GenerateCodes cities1 = generateCodes.get(position);

        //   Glide.with(activity).load(quotationList.getImage()).placeholder(R.drawable.logo).into(holder.imgProduct);
        holder.tvCity.setText(cities1.getEcity());
        holder.tvMobileNo.setText(cities1.getId_number());
        holder.tvName.setText(cities1.getStudent_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                findMissingFragment.setCityValue(cities1.getEcity());

            }
        });



    }


    @Override
    public int getItemCount() {
        return generateCodes.size();
    }

    static class ExploreItemHolder extends RecyclerView.ViewHolder {

        final TextView tvName ,tvMobileNo,tvCity;
        public ExploreItemHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvMobileNo = itemView.findViewById(R.id.tvMobileNo);
            tvCity = itemView.findViewById(R.id.tvCity);

        }
    }
}