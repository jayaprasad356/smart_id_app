package com.app.fortuneapp.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fortuneapp.R;
import com.app.fortuneapp.activities.NotificaionActivity;
import com.app.fortuneapp.model.Notification;

import java.util.ArrayList;


public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final Activity activity;
    final ArrayList<Notification> notifications;

    public NotificationAdapter(Activity activity, ArrayList<Notification> notifications) {
        this.activity = activity;
        this.notifications = notifications;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.notification_item, parent, false);
        return new ExploreItemHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderParent, int position) {
        final ExploreItemHolder holder = (ExploreItemHolder) holderParent;
        final Notification notification = notifications.get(position);

        holder.title.setText(notification.getTitle());
        holder.description.setText(notification.getDescription());
        holder.datetime.setText(notification.getDatetime());
        if (notification.getLink() != null){
            holder.btnOpen.setVisibility(View.VISIBLE);
            holder.btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((NotificaionActivity)activity).sendorder(notification.getTitle());
                    String url = notification.getLink();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    activity.startActivity(i);

                }
            });

        }




    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ExploreItemHolder extends RecyclerView.ViewHolder {
        final TextView title,description,datetime;
        final Button btnOpen;
        public ExploreItemHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            datetime = itemView.findViewById(R.id.datetime);
            btnOpen = itemView.findViewById(R.id.btnOpen);



        }
    }
}

