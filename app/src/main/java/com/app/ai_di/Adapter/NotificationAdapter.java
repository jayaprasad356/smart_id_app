package com.app.ai_di.Adapter;

import static androidx.core.content.ContextCompat.startActivity;
import static com.app.ai_di.helper.CustomDialog.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ai_di.R;
import com.app.ai_di.helper.Constant;
import com.app.ai_di.model.NotificationListModal;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<NotificationListModal> notificationListModal;
    private final Activity activity;
    private final FragmentManager fragmentManager;

    // Constructor
    public NotificationAdapter(Activity activity, List<NotificationListModal> notificationListModal, FragmentManager fragmentManager) {
        this.activity = activity;
        this.notificationListModal = notificationListModal;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list_layout, parent, false);
        return new NotificationViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationListModal notification = notificationListModal.get(position);

        // Set data to TextViews
        holder.tvTitle.setText(notification.getTitle());
        holder.tvDateTime.setText(notification.getDatetime());
        holder.tvDescription.setText(notification.getDescription());

        // Load image using Glide (or any other image loading library)
        Glide.with(activity)
                .load(notification.getImage()) // Assuming `getImage()` returns a URL or URI
                .placeholder(R.drawable.image_icon) // Optional: Add a placeholder
                .error(R.drawable.image_icon) // Optional: Add an error image
                .into(holder.ivImage);

        if(notification.getLink() == null || notification.getLink().isEmpty()) {
            holder.btnOpen.setVisibility(View.GONE);
        } else {
            holder.btnOpen.setVisibility(View.VISIBLE);
            // Handle button click
            holder.btnOpen.setOnClickListener(view -> {
                String url = notification.getLink();
                if (url != null && !url.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    activity.startActivity(intent);
//                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
//                        activity.startActivity(intent);
//                    } else {
//                        Toast.makeText(activity, "No application can handle this request", Toast.LENGTH_SHORT).show();
//                    }
                } else {
                    Toast.makeText(activity, "No link available", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Handle "Zoom" button click to open fragment
        holder.btnZoom.setOnClickListener(view -> {

            String imageUrl = "https://img.freepik.com/free-photo/abstract-autumn-beauty-multi-colored-leaf-vein-pattern-generated-by-ai_188544-9871.jpg?t=st=1731926684~exp=1731930284~hmac=6b3925681d269f16f1da56f1f379272235e4c691d198e9d1ba4c0c3143a23b9e&w=1060";

            dialogZoomable(notification.getImage());
        });
    }

    @Override
    public int getItemCount() {
        return notificationListModal.size();
    }

    public void dialogZoomable(String imageUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity); // Context: Activity
        LayoutInflater inflater = activity.getLayoutInflater(); // LayoutInflater
        View dialogView = inflater.inflate(R.layout.dialog_zoomable_image, null);

        com.github.chrisbanes.photoview.PhotoView photoView = dialogView.findViewById(R.id.photoView);

        // Load image with Glide
        Glide.with(activity)
                .load(imageUrl)
                .placeholder(R.drawable.image_icon)
                .error(R.drawable.image_icon)
                .into(photoView);

        // Initialize buttons
        Button btnZoomIn = dialogView.findViewById(R.id.btnZoomIn);
        Button btnZoomOut = dialogView.findViewById(R.id.btnZoomOut);
        ImageButton btnClose = dialogView.findViewById(R.id.btnClose);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // **Zoom In Button**
        btnZoomIn.setOnClickListener(v -> {
            float currentScale = photoView.getScale();
            float maxScale = photoView.getMaximumScale();
            float newScale = Math.min(currentScale * 1.2f, maxScale); // Clamp to maxScale
            photoView.setScale(newScale, true);
        });

        // **Zoom Out Button**
        btnZoomOut.setOnClickListener(v -> {
            float currentScale = photoView.getScale();
            float minScale = photoView.getMinimumScale();
            float newScale = Math.max(currentScale / 1.2f, minScale); // Clamp to minScale
            photoView.setScale(newScale, true);
        });

        // **Close Button**
        btnClose.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        // Set rounded corner background
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }

        // Display the dialog
        dialog.show();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDateTime, tvDescription;
        MaterialButton btnOpen;
        ImageView ivImage;
        Button btnZoom;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnOpen = itemView.findViewById(R.id.btnOpen);
            btnZoom = itemView.findViewById(R.id.btnZoom);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}


