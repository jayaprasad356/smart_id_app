package com.app.fortuneapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


import com.app.fortuneapp.R;
import com.app.fortuneapp.helper.ApiConfig;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    Handler handler;
    Session session;
    Activity activity;
    String link, description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        activity = SplashActivity.this;
        session = new Session(activity);


        handler = new Handler();
        checkVersion();
    }

    private void checkVersion() {
        Map<String, String> params = new HashMap<>();
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        String codegenerate = "0", withdrawal_status = "0";
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        JSONArray jsonArray2 = jsonObject.getJSONArray(Constant.SETTINGS);


                        session.setData(Constant.PAYMENT_LINK, jsonArray2.getJSONObject(0).getString(Constant.PAYMENT_LINK));
                        session.setData(Constant.WHATSAPP, jsonArray2.getJSONObject(0).getString(Constant.WHATSAPP));
                        session.setData(Constant.JOB_DETAILS_LINK, jsonArray2.getJSONObject(0).getString(Constant.JOB_DETAILS_LINK));
                        session.setData(Constant.SYNC_TIME, jsonArray2.getJSONObject(0).getString(Constant.SYNC_TIME));
                        session.setInt(Constant.SYNC_CODES, Integer.parseInt(jsonArray2.getJSONObject(0).getString(Constant.SYNC_CODES)));
                        session.setData(Constant.REWARD, jsonArray2.getJSONObject(0).getString(Constant.REWARD));
                        session.setData(Constant.AD_SHOW_TIME, jsonArray2.getJSONObject(0).getString(Constant.AD_SHOW_TIME));
                        session.setData(Constant.MIN_WITHDRAWAL, jsonArray2.getJSONObject(0).getString(Constant.MIN_WITHDRAWAL));
                        session.setData(Constant.AD_STATUS, jsonArray2.getJSONObject(0).getString(Constant.AD_STATUS));
                        session.setData(Constant.FETCH_TIME, jsonArray2.getJSONObject(0).getString(Constant.FETCH_TIME));
                        session.setData(Constant.AD_REWARD_ID, jsonArray2.getJSONObject(0).getString(Constant.AD_REWARD_ID));
                        if (jsonObject.has(Constant.USER_DETAILS)) {
                            JSONArray userArray = jsonObject.getJSONArray(Constant.USER_DETAILS);
                            if (userArray.length() != 0) {
                                session.setData(Constant.STATUS, userArray.getJSONObject(0).getString(Constant.STATUS));
                                session.setData(Constant.TOTAL_REFERRALS, userArray.getJSONObject(0).getString(Constant.TOTAL_REFERRALS));
                                session.setData(Constant.WITHDRAWAL, userArray.getJSONObject(0).getString(Constant.WITHDRAWAL));
                                session.setData(Constant.CODE_GENERATE_TIME, userArray.getJSONObject(0).getString(Constant.CODE_GENERATE_TIME));
                                session.setData(Constant.JOINED_DATE, userArray.getJSONObject(0).getString(Constant.JOINED_DATE));
                                if (jsonArray2.getJSONObject(0).getString(Constant.CODE_GENERATE).equals("1")) {
                                    codegenerate = userArray.getJSONObject(0).getString(Constant.CODE_GENERATE);
                                }
                                if (jsonArray2.getJSONObject(0).getString(Constant.WITHDRAWAL_STATUS).equals("1")) {
                                    withdrawal_status = userArray.getJSONObject(0).getString(Constant.WITHDRAWAL_STATUS);
                                }
                                session.setData(Constant.CODE_GENERATE, codegenerate);
                                session.setData(Constant.WITHDRAWAL_STATUS, withdrawal_status);
                            }
                        }
                        link = jsonArray.getJSONObject(0).getString(Constant.LINK);
                        description = jsonArray.getJSONObject(0).getString(Constant.DESCRIPTION);
                        String latestversion = jsonArray.getJSONObject(0).getString(Constant.VERSION);
                        String currentversion = "";
                        try {
                            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                            currentversion = pInfo.versionCode + "";
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        if (Integer.parseInt(currentversion) >= Integer.parseInt(latestversion)) {
                            GotoActivity();

                        } else {
                            updateAlertDialog();
                        }

                    } else {
                        Log.d("MAINACTIVITY", jsonObject.getString(Constant.MESSAGE));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("APP_ERRROR", e.getMessage());
                }
            }

        }, activity, Constant.APPUPDATE_URL, params, false);

    }

    private void updateAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New update Available");
        builder.setMessage(description);
        builder.setCancelable(false);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }


    private void GotoActivity() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Session session = new Session(SplashActivity.this);
                if (session.getBoolean("is_logged_in")) {
                    if (session.getData(Constant.STATUS).equals("2")) {
                        session.logoutUser(activity);
                    } else {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }else if (session.getBoolean(Constant.CHECKIN)){
                    Intent intent=new Intent(SplashActivity.this, CheckInActivity.class);
                    startActivity(intent);
                    finish();

                }else{
                    Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();

                }


            }
        }, 2000);
    }
}