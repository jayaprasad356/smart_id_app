package com.app.ai_di.fragment;

import static com.app.ai_di.helper.Constant.SUCCESS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ai_di.Adapter.JobPlanAdapter;
import com.app.ai_di.R;
import com.app.ai_di.helper.ApiConfig;
import com.app.ai_di.helper.Constant;
import com.app.ai_di.helper.Session;
import com.app.ai_di.model.PlanListModel;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JobsFragment extends Fragment {

    private RecyclerView rvPlanList;
    private JobPlanAdapter jobPlanAdapter;
    private List<PlanListModel> planListModel;  // Initialize list here
    Session session;
    Activity activity;

    private YouTubePlayerView youtubePlayerView;
    private LinearLayout llWaiting;
    private RelativeLayout rlPlanView;
    private YouTubePlayer youTubePlayerInstance;
    MaterialButton btnRecharge;
    MaterialButton btnFAQ;
    private boolean isPlayerReady = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);

        activity = getActivity();
        session = new Session(activity);

        // Initialize YouTubePlayerView
        youtubePlayerView = view.findViewById(R.id.youtubePlayerView);
        llWaiting = view.findViewById(R.id.llWaiting);
        rlPlanView = view.findViewById(R.id.rlPlanView);
        btnFAQ = view.findViewById(R.id.btnFAQ);

        llWaiting.setVisibility(View.VISIBLE);
        rlPlanView.setVisibility(View.GONE);

        btnFAQ.setOnClickListener(
                v -> {
                    String url = Constant.FQD_URL; // Replace with your demo video URL
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
        );

        // Observe lifecycle to automatically manage player state
        getLifecycle().addObserver(youtubePlayerView);

        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayerInstance = youTubePlayer;
                isPlayerReady = true; // Mark player as ready
                Log.d("YouTubePlayer", "Player is ready");

                String videoUrl = session.getData(Constant.JOB_VIDEO); // Your video URL
                Log.d("YouTubePlayer", "Player is ready: " +  videoUrl);

                // Extract video ID from the URL
                String videoId = extractVideoIdFromUrl(videoUrl);

                if (videoId != null) {
                    // Load the video at 0 seconds without autoplay
                    youTubePlayerInstance.cueVideo(videoId, 0f);
                } else {
                    Toast.makeText(activity, "Invalid video URL", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
                Toast.makeText(activity, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("YouTubePlayer", "Error loading video: " + error.toString());
            }
        });

        return view;
    }

    // Method to extract the video ID from a URL
    private String extractVideoIdFromUrl(String videoUrl) {
        if (videoUrl == null || videoUrl.trim().isEmpty()) {
            return null;
        }

        String videoId = null;

        // Check if the URL contains "v=" parameter
        if (videoUrl.contains("v=")) {
            String[] parts = videoUrl.split("v=");
            if (parts.length > 1) {
                String[] idParts = parts[1].split("&"); // Handle any additional parameters
                videoId = idParts[0];
            }
        } else if (videoUrl.contains("youtu.be/")) {
            // Handle shortened YouTube URLs
            String[] parts = videoUrl.split("youtu.be/");
            if (parts.length > 1) {
                videoId = parts[1];
            }
        }

        return videoId;
    }

    // Only attempt to play video if the player is ready
    private void playVideo() {
        if (isPlayerReady && youTubePlayerInstance != null) {
            youTubePlayerInstance.play(); // Play the video
        } else {
            Log.d("YouTubePlayer", "Player is not ready yet");
        }
    }

    // Only attempt to pause video if the player is ready
    private void pauseVideo() {
        if (isPlayerReady && youTubePlayerInstance != null) {
            youTubePlayerInstance.pause(); // Pause the video
        } else {
            Log.d("YouTubePlayer", "Player is not ready yet");
        }
    }

    // Only attempt to seek video if the player is ready
    private void seekTo(float seconds) {
        if (isPlayerReady && youTubePlayerInstance != null) {
            youTubePlayerInstance.seekTo(seconds); // Seek to the specified time
        } else {
            Log.d("YouTubePlayer", "Player is not ready yet");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (youtubePlayerView != null) {
            youtubePlayerView.release(); // Release the player when the view is destroyed
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        rvPlanList = view.findViewById(R.id.rvPlanList);
        rvPlanList.setLayoutManager(new LinearLayoutManager(getContext()));
        btnRecharge = view.findViewById(R.id.btnRecharge);

        // Initialize the plan list
        planListModel = new ArrayList<>();

        // Load plan data
        loadPlans(false);

        btnRecharge.setOnClickListener(v -> showRechargeDialog());
        btnRecharge.setText("Recharge ₹" + session.getData(Constant.RECHARGE));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadPlans(boolean reload) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(SUCCESS)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        Gson g = new Gson();

                        // Clear the plan list before adding new data
                        planListModel.clear();

                        // Parse response and add to plan list
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            PlanListModel group = g.fromJson(jsonObject1.toString(), PlanListModel.class);
                            planListModel.add(group);
                        }

                        // Set adapter after data is loaded
                        jobPlanAdapter = new JobPlanAdapter(activity, planListModel, this);
                        rvPlanList.setAdapter(jobPlanAdapter);

                        if(reload) {
                            jobPlanAdapter.notifyDataSetChanged();
                        }

                        llWaiting.setVisibility(View.VISIBLE);
                        rlPlanView.setVisibility(View.GONE);
                        new Handler().postDelayed(() -> {
                            llWaiting.setVisibility(View.GONE);
                            rlPlanView.setVisibility(View.VISIBLE);
                            btnRecharge.setOnClickListener(v -> showRechargeDialog());
                            btnRecharge.setText("Recharge ₹" + session.getData(Constant.RECHARGE));
                        }, 2000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.PLAN_LIST_URL, params, true);

        Log.d("PLAN_LIST_URL", "PLAN_LIST_URL: " + Constant.PLAN_LIST_URL);
        Log.d("PLAN_LIST_URL", "PLAN_LIST_URL params: " + params);
    }

    private void showRechargeDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_recharge_custom, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        ImageButton btClose = dialogView.findViewById(R.id.btClose);
        MaterialButton btRechargePayment = dialogView.findViewById(R.id.btRechargePayment);
        TextView btWatchDemo = dialogView.findViewById(R.id.btWatchDemo); // Initialize btWatchDemo

        btClose.setOnClickListener(v -> dialog.dismiss());

        btRechargePayment.setOnClickListener(v -> {
            String url = Constant.RECHARGE_URL; // Replace with your URL
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        btWatchDemo.setOnClickListener(v -> {
            String url = Constant.RECHARGE_DEMO_VIDEO_URL; // Replace with your demo video URL
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        dialog.show();
    }
}

