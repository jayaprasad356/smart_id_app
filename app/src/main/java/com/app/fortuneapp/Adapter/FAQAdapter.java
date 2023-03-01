package com.app.fortuneapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fortuneapp.R;
import com.app.fortuneapp.model.FAQS;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter <FAQAdapter.ViewHolder>{
    List<FAQS> datumList;
    Context context;
    boolean quesclick = false;
    public FAQAdapter(List<FAQS> faqsList, Context context) {
        this.datumList = faqsList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_lyt,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.question.setText(datumList.get(position).getQuestion());
        holder.answer.setText(datumList.get(position).getAnswer());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quesclick){
                    quesclick = false;
                    holder.answer.setVisibility(View.GONE);

                }
                else {
                    quesclick = true;
                    holder.answer.setVisibility(View.VISIBLE);

                }

            }
        });

    }
    @Override
    public int getItemCount() {
        return datumList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView question,answer;
        LinearLayout queslyt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
            queslyt = itemView.findViewById(R.id.queslyt);



        }
    }
}
