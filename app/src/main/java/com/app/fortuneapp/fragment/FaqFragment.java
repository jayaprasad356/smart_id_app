package com.app.fortuneapp.fragment;

import static com.app.fortuneapp.helper.Constant.SUCCESS;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fortuneapp.Adapter.FAQAdapter;
import com.app.fortuneapp.R;
import com.app.fortuneapp.activities.TicketActivity;
import com.app.fortuneapp.helper.ApiConfig;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.model.FAQS;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FaqFragment extends Fragment {

    RecyclerView recyclerView;
    ImageButton toolbar;
    View view;
    Activity activity;
    Button ticketButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_faq, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        toolbar = view.findViewById(R.id.toolbar);
        ticketButton = view.findViewById(R.id.chatSupport);
        activity = getActivity();
        ticketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TicketActivity.class);
                startActivity(intent);
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        getFAQS();

        return view;
    }


    private void getFAQS() {

        if (ApiConfig.isConnected(activity)) {
            Map<String, String> params = new HashMap<>();
            ApiConfig.RequestToVolley((result, response) -> {
                Log.d("Faq Api", response);
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean(Constant.SUCCESS)) {
                            JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                            ArrayList<FAQS> faqs = new ArrayList<>();
                            Gson g = new Gson();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                if (jsonObject1 != null) {
                                    FAQS group = g.fromJson(jsonObject1.toString(), FAQS.class);
                                    faqs.add(group);
                                } else {
                                    break;
                                }
                            }
                            FAQAdapter productAdapter = new FAQAdapter(faqs, activity);
                            recyclerView.setAdapter(productAdapter);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, activity, Constant.FAQ_LIST, params, true);
        }

    }
}