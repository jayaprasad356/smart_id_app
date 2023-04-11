package com.app.fortuneapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fortuneapp.Adapter.ReferandEarnAdapter;
import com.app.fortuneapp.R;
import com.app.fortuneapp.helper.ApiConfig;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.Session;
import com.app.fortuneapp.model.Referandearn;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReferDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ReferandEarnAdapter referandEarnAdapter;
    MaterialButton btnAddtoWallet;
    Activity activity;
    Session session;
    TextView tvTotalIncome,tvOngoingIncome;
    ImageView imgBack;
    private static final String PREF_LAST_REFER_EARN_TIME = "pref_last_refer_earn_time";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_details);

        activity = ReferDetailsActivity.this;
        session = new Session(activity);


        recyclerView = findViewById(R.id.recyclerView);
        btnAddtoWallet = findViewById(R.id.btnAddtoWallet);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvOngoingIncome = findViewById(R.id.tvOngoingIncome);
        imgBack = findViewById(R.id.imgBack);

        btnAddtoWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addtowallet();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);



        referearn();
        setReferdetails();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(activity,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setReferdetails() {

        try {
            JSONObject jsonObject = new JSONObject(session.getData(Constant.REFER_LIST_DATA));
            if (jsonObject.getBoolean(Constant.SUCCESS)) {
                JSONObject object = new JSONObject(session.getData(Constant.REFER_LIST_DATA));
                JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                Gson g = new Gson();
                ArrayList<Referandearn> referandearns = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    if (jsonObject1 != null) {
                        Referandearn group = g.fromJson(jsonObject1.toString(), Referandearn.class);
                        referandearns.add(group);
                    } else {
                        break;
                    }
                }

                referandEarnAdapter = new ReferandEarnAdapter(ReferDetailsActivity.this,referandearns);
                recyclerView.setAdapter(referandEarnAdapter);

            }


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, String.valueOf(e), Toast.LENGTH_SHORT).show();
        }

        btnAddtoWallet.setText("Add to wallet ₹"+session.getData(Constant.SYNC_REFER_WALLET));
        tvTotalIncome.setText("₹"+session.getData(Constant.REFER_INCOME));
        tvOngoingIncome.setText("₹"+session.getData(Constant.SYNC_REFER_WALLET));
    }

    private void addtowallet() {

        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID,session.getData(Constant.USER_ID));
        params.put(Constant.AMOUNT,session.getData(Constant.SYNC_REFER_WALLET));

        ApiConfig.RequestToVolley((result, response) -> {
            Log.d("ADD_TO_WALLET",response + " - "+ session.getData(Constant.SYNC_REFER_WALLET));
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        session.setData(Constant.REFER_INCOME,jsonObject.getString(Constant.REFER_INCOME));
                        session.setData(Constant.SYNC_REFER_WALLET,jsonObject.getString(Constant.SYNC_REFER_WALLET));
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }, activity, Constant.ADD_WALLET, params,false);



    }

    private long getLastReferearnTime() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getLong(PREF_LAST_REFER_EARN_TIME, 0);
    }

    private void setLastReferearnTime(long time) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PREF_LAST_REFER_EARN_TIME, time);
        editor.apply();
    }

    private void referearn() {
//        long currentTime = System.currentTimeMillis();
//        long lastReferearnTime = getLastReferearnTime();
//        if (currentTime - lastReferearnTime < 30 * 60 * 1000) { // 30 minutes
//            return;
//        }
//
//        setLastReferearnTime(currentTime);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID,session.getData(Constant.USER_ID));
        ApiConfig.RequestToVolley((result, response) -> {
            Log.d("REFER_DETAILS_RESPONSE",response);

            if (result) {
                try {
                    session.setData(Constant.REFER_LIST_DATA,response);


                    JSONObject jsonObject = new JSONObject(response);
                    session.setData(Constant.REFER_INCOME,jsonObject.getString(Constant.REFER_INCOME));
                    session.setData(Constant.SYNC_REFER_WALLET,jsonObject.getString(Constant.SYNC_REFER_WALLET));
                    setReferdetails();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, String.valueOf(e), Toast.LENGTH_SHORT).show();
                }
            }
        }, activity, Constant.REFERALSLIST, params, true);


    }
}