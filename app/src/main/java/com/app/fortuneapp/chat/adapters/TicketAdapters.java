package com.app.fortuneapp.chat.adapters;



import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_USER_ID;
import static com.app.fortuneapp.chat.constants.IConstants.ONE;
import static com.app.fortuneapp.chat.constants.IConstants.OPENED_TICKET;
import static com.app.fortuneapp.chat.constants.IConstants.TICKET_ID;
import static com.app.fortuneapp.chat.constants.IConstants.ZERO;
import static com.app.fortuneapp.helper.Constant.CATEGORY;
import static com.app.fortuneapp.helper.Constant.DESCRIPTION;
import static com.app.fortuneapp.helper.Constant.NAME;
import static com.app.fortuneapp.helper.Constant.PENDING_TICKET;
import static com.app.fortuneapp.helper.Constant.TYPE;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fortuneapp.R;
import com.app.fortuneapp.chat.MessageActivity;
import com.app.fortuneapp.chat.managers.Utils;
import com.app.fortuneapp.chat.models.Ticket;
import com.app.fortuneapp.chat.views.SingleClickListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TicketAdapters extends RecyclerView.Adapter<TicketAdapters.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private final Activity mContext;
    private final ArrayList<Ticket> mTickets;


    public TicketAdapters(Activity mContext, ArrayList<Ticket> ticketsList) {
        this.mContext = mContext;
        this.mTickets = ticketsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_tickets, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Ticket ticket = mTickets.get(i);
        viewHolder.tvName.setText(ticket.getName());
        viewHolder.tvMobile.setText(ticket.getMobile());
        viewHolder.tvDescription.setText(ticket.getDescription());
        viewHolder.tvCategory.setText(ticket.getCategory());
        if (ticket.getType().equals(PENDING_TICKET)){
            viewHolder.tvType.setText("Pending");
            viewHolder.tvType.setTextColor(ContextCompat.getColor(mContext, R.color.yellow_900));

        }else if (ticket.getType().equals(OPENED_TICKET)){
            viewHolder.tvType.setText("Opened");
            viewHolder.tvType.setTextColor(ContextCompat.getColor(mContext, R.color.green_900));

        }
        else {
            viewHolder.tvType.setText("Closed");
            viewHolder.tvType.setTextColor(ContextCompat.getColor(mContext, R.color.red_900));

        }

        viewHolder.itemView.setOnClickListener(new SingleClickListener() {
            @Override
            public void onClickView(View v) {
                final Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra(EXTRA_USER_ID, ticket.getId());
                intent.putExtra(TICKET_ID, ticket.getId());
                intent.putExtra(NAME, ticket.getName());
                intent.putExtra(TYPE, ticket.getType());
                intent.putExtra(DESCRIPTION, ticket.getDescription());
                intent.putExtra(CATEGORY, ticket.getCategory());
                mContext.startActivity(intent);
            }
        });


    }

    @NonNull
    @NotNull
    @Override
    public String getSectionName(final int position) {
        if (!Utils.isEmpty(mTickets)) {
            return mTickets.get(position).getName().substring(ZERO, ONE);
        } else {
            return null;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName,tvMobile,tvDescription,tvCategory,tvType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvType = itemView.findViewById(R.id.tvType);
        }
    }

    @Override
    public int getItemCount() {
        return mTickets.size();
    }
}
