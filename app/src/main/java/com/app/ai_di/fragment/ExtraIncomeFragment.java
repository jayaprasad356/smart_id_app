package com.app.ai_di.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.app.ai_di.R;
import com.app.ai_di.helper.Constant;
import com.google.android.material.button.MaterialButton;
import com.app.ai_di.helper.Session;

public class ExtraIncomeFragment extends Fragment {

    private Session session;

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
        session = new Session(getActivity()); // or getContext(), depending on your implementation

        // Find the MaterialButton by its ID
        MaterialButton btnReferText = view.findViewById(R.id.btnReferText);
        MaterialButton btnRefer = view.findViewById(R.id.btnRefer);

        // Set click listener for the "Refer" button
        if (session != null && session.getData(Constant.REFER_CODE) != null) {
        btnRefer.setOnClickListener(v -> {
            String baseUrl = "https://aidiapp.in/"; // Replace with your actual base URL
            shareTextAndUrl("Click this link to join Ai-Di App ☺\uFE0F\n" +
                    "Use My Refer Code " + session.getData(Constant.REFER_CODE) + " While Creating Account.", baseUrl);
        });
        } else {
            String baseUrl = "https://aidiapp.in/"; // Replace with your actual base URL
            shareTextAndUrl("Click this link to join Ai-Di App ☺\uFE0F\n" +
                    "Use My Refer Code ID123 While Creating Account.", baseUrl);
        }

        // Check if session and data are not null, then set the text
        if (session != null && session.getData(Constant.REFER_CODE) != null) {
            btnReferText.setText(session.getData(Constant.REFER_CODE));
        } else {
            btnReferText.setText("123456");
        }
    }

    // Function to generate referral URL
    private String generateReferralUrl(String baseUrl, String referralCode) {
        return baseUrl + "?ref=" + referralCode;
    }

    // Function to share text and URL
    private void shareTextAndUrl(String message, String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message + "\n" + url);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
