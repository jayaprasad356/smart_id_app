package com.app.fortuneapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fortuneapp.R;
import com.app.fortuneapp.model.Referandearn;

import java.util.ArrayList;


public class ReferandEarnAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final Activity activity;
    ArrayList<Referandearn> referandearns;



    public ReferandEarnAdapter(Activity activity, ArrayList<Referandearn> referandearns) {
        this.activity = activity;
        this.referandearns = referandearns;

    }





    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.referearn_layout, parent, false);
        return new ExploreItemHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderParent, @SuppressLint("RecyclerView") int position) {
        final ExploreItemHolder holder = (ExploreItemHolder) holderParent;
        final Referandearn referandearn = referandearns.get(position);

        //   Glide.with(activity).load(quotationList.getImage()).placeholder(R.drawable.logo).into(holder.imgProduct);
        holder.tvIncome.setText(referandearn.getBalance());
        holder.tvName.setText(referandearn.getName());
        holder.tvrefercode.setText(referandearn.getMobile());





    }


    @Override
    public int getItemCount() {
        return referandearns.size();
    }

    static class ExploreItemHolder extends RecyclerView.ViewHolder {

        final TextView tvIncome,tvName,tvrefercode;
        public ExploreItemHolder(@NonNull View itemView) {
            super(itemView);
            tvIncome = itemView.findViewById(R.id.tvIncome);
            tvName = itemView.findViewById(R.id.tvName);
            tvrefercode = itemView.findViewById(R.id.tvrefercode);

        }
    }
}