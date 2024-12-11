package com.gmwapp.slv_aidi.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmwapp.slv_aidi.Adapter.ReferTargetAdapter;
import com.gmwapp.slv_aidi.R;
import com.gmwapp.slv_aidi.activities.MainActivity;
import com.gmwapp.slv_aidi.helper.ApiConfig;
import com.gmwapp.slv_aidi.helper.Constant;
import com.gmwapp.slv_aidi.model.ExtraPlanModel;
import com.gmwapp.slv_aidi.model.RefersTargetModel;
import com.google.android.material.button.MaterialButton;
import com.gmwapp.slv_aidi.helper.Session;
import com.google.android.material.card.MaterialCardView;
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

public class ExtraIncomeFragment extends Fragment {

    Session session;
    Activity activity;
    private ReferTargetAdapter referTargetAdapter;
    private LinearLayout llWaiting;
    private NestedScrollView frame;
    private YouTubePlayerView youtubePlayerView;
    private YouTubePlayer youTubePlayerInstance;
    private boolean isPlayerReady = false;
    private final List<RefersTargetModel> refersTargetModel = new ArrayList<>(); // Initialize here

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_extra_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Session object
        activity = getActivity();
        session = new Session(activity); // or getContext(), depending on your implementation

        // Initialize RecyclerView
        RecyclerView rvSlabList = view.findViewById(R.id.rvSlabList);
        MaterialButton btActivatePlan = view.findViewById(R.id.btActivatePlan);
        MaterialCardView mcActivatePlan = view.findViewById(R.id.mcActivatePlan);
        MaterialButton btnRefer = view.findViewById(R.id.btnRefer);
        MaterialButton btnReferText = view.findViewById(R.id.btnReferText);
        youtubePlayerView = view.findViewById(R.id.youtubePlayerView);

        llWaiting = view.findViewById(R.id.llWaiting);
        frame = view.findViewById(R.id.frame);

        llWaiting.setVisibility(View.VISIBLE);
        frame.setVisibility(View.GONE);

        rvSlabList.setLayoutManager(new LinearLayoutManager(activity));
        referTargetAdapter = new ReferTargetAdapter(activity, refersTargetModel, this);
        rvSlabList.setAdapter(referTargetAdapter);

        mcActivatePlan.setVisibility(View.VISIBLE);

        loadSlabs(false);

        btActivatePlan.setOnClickListener(v -> activatedExtraIncomePlan());

        // Check saved extra plans and adjust UI accordingly
        List<ExtraPlanModel> extraPlansData = session.getExtraPlanData();
        if (extraPlansData != null && !extraPlansData.isEmpty()) {
            mcActivatePlan.setVisibility(View.GONE);
        } else {
            mcActivatePlan.setVisibility(View.VISIBLE);
        }

        setupReferButton(btnRefer, btnReferText);

        // Observe lifecycle to automatically manage player state
        getLifecycle().addObserver(youtubePlayerView);

        // Set up YouTubePlayerListener
        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayerInstance = youTubePlayer;
                isPlayerReady = true; // Mark player as ready
                Log.d("YouTubePlayer", "Player is ready");

                String videoUrl = session.getData(Constant.GROW_VIDEO); // Your video URL
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

    private void activatedPlanSuccess() {
        // Create an AlertDialog
        new AlertDialog.Builder(activity)
                .setMessage("Congratulations, You have activated Grow plan")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // User canceled - dismiss the dialog
                    dialog.dismiss();
                })
                .show();
    }

    private void setupReferButton(MaterialButton btnRefer, MaterialButton btnReferText) {
        final String[] referCode = {session.getData(Constant.REFER_CODE)};
        String baseUrl = "https://aidiapp.in/";

        if (referCode[0] != null) {
            btnRefer.setOnClickListener(v -> shareTextAndUrl(
                    "Click this link to join Ai-Di App ☺️\nUse My Refer Code " + referCode[0] + " While Creating Account.", baseUrl));
            btnReferText.setText(referCode[0]);
        } else {
            btnRefer.setOnClickListener(v -> shareTextAndUrl(
                    "Click this link to join Ai-Di App ☺️\nUse My Refer Code ID123 While Creating Account.", baseUrl));
            btnReferText.setText("123456");
        }

        btnReferText.setOnClickListener(v -> {
            if (referCode[0] == null || referCode[0].isEmpty()) {
                referCode[0] = "123456"; // Default refer code
            }

            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Refer Code", referCode[0]);
            clipboard.setPrimaryClip(clip);

        });
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

    private void shareTextAndUrl(String message, String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message + "\n" + url);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void activatedExtraIncomePlan() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.EXTRA_CLAIM_PLAN_ID, "1");

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString(com.gmwapp.slv_aidi.helper.Constant.MESSAGE);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        activatedPlanSuccess();
                        if (activity instanceof MainActivity) {
                            ((MainActivity) activity).userDetails();
                        }
                        llWaiting.setVisibility(View.VISIBLE);
                        frame.setVisibility(View.GONE);
                        new Handler().postDelayed(() -> {
                            referTargetAdapter.notifyDataSetChanged();
                            llWaiting.setVisibility(View.GONE);
                            frame.setVisibility(View.VISIBLE);
                        }, 2000);
                    } else {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, activity, Constant.EXTRA_INCOME_PLAN_ACTIVATE, params, true);

        Log.d("EXTRA_INCOME_PLAN_ACTIVATE", "EXTRA_INCOME_PLAN_ACTIVATE: " + Constant.EXTRA_INCOME_PLAN_ACTIVATE);
        Log.d("EXTRA_INCOME_PLAN_ACTIVATE", "EXTRA_INCOME_PLAN_ACTIVATE params: " + params);
    }

    public void loadSlabs(boolean reload) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        Gson gson = new Gson();

                        // Clear the list before adding new data
                        refersTargetModel.clear();

                        // Parse response and add to the list
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            RefersTargetModel group = gson.fromJson(jsonObject1.toString(), RefersTargetModel.class);
                            refersTargetModel.add(group);
                        }

                        if(reload) {
                            referTargetAdapter.notifyDataSetChanged();
                        }

                        llWaiting.setVisibility(View.VISIBLE);
                        frame.setVisibility(View.GONE);
                        new Handler().postDelayed(() -> {
                            llWaiting.setVisibility(View.GONE);
                            frame.setVisibility(View.VISIBLE);
                        }, 2000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("REFER_TARGET_URL", "REFER_TARGET_URL E: " + e.getMessage());
                }
            }
        }, activity, Constant.REFER_TARGET_URL, params, true);

        Log.d("REFER_TARGET_URL", "REFER_TARGET_URL: " + Constant.REFER_TARGET_URL);
        Log.d("REFER_TARGET_URL", "REFER_TARGET_URL params: " + params);
    }
}

