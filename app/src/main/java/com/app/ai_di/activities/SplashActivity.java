package com.app.ai_di.activities;

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

import androidx.appcompat.app.AppCompatActivity;

import com.app.ai_di.R;
import com.app.ai_di.helper.ApiConfig;
import com.app.ai_di.helper.Constant;
import com.app.ai_di.helper.Session;
import com.zoho.commons.InitConfig;
import com.zoho.livechat.android.listeners.InitListener;
import com.zoho.salesiqembed.ZohoSalesIQ;

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
    String currentversion = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        activity = SplashActivity.this;
        session = new Session(activity);


        handler = new Handler();


        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            currentversion = pInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        checkVersion();
//        initializeZohoSalesIQ();
    }

//    private void initializeZohoSalesIQ() {
//        ZohoSalesIQ.init(SplashActivity.this.getApplication(),
//                "5spwCGjIKo%2Fz6ssVNakmHbMTvtsszyor90%2BhrhHmnNgJcnpMvghcPXmu4dO6kxpO_in",
//                "4%2Fd2z2OovwP9rRaj3CO5TQtzMKPKxu%2FFaEkvD5l3RKcCLPKYaPjW%2B%2BzKEVzDx8I3UedpF6j3RR3PecllV1z3JrF3PMI%2BXoxRDSvLRDVerhOt%2FtApSWo%2FVw%3D%3D");
//
//        ZohoSalesIQ.Chat.showOperatorImageInLauncher(true);
//    }


    private void checkVersion() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.FCM_ID, session.getData(Constant.FCM_ID));
        params.put(Constant.DEVICE_ID, Constant.getDeviceId(activity));
        params.put(Constant.APP_VERSION, currentversion);

        ApiConfig.RequestToVolley((result, response) -> {
            Log.d("SPLASH_VER", response);

            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        JSONArray dataArray = jsonObject.getJSONArray(Constant.DATA);
                        JSONArray settingsArray = jsonObject.getJSONArray(Constant.SETTINGS);

                        if (dataArray.length() > 0) {
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            String link = dataObject.getString(Constant.LINK);
                            String description = dataObject.getString(Constant.DESCRIPTION);
                            String latestVersion = dataObject.getString(Constant.VERSION);

                            if (Integer.parseInt(currentversion) >= Integer.parseInt(latestVersion)) {
                                GotoActivity();
                            } else {
                                updateAlertDialog(link);
                            }
                        }

                        if (settingsArray.length() > 0) {
                            JSONObject settingsObject = settingsArray.getJSONObject(0);
                            setSessionData(settingsObject);
                        }

                        if (jsonObject.has(Constant.USER_DETAILS)) {
                            JSONArray userArray = jsonObject.getJSONArray(Constant.USER_DETAILS);
                            if (userArray.length() != 0) {
                                setUserDetails(userArray.getJSONObject(0));
                            }
                        }
                    } else {
                        Log.d("MAINACTIVITY", jsonObject.getString(Constant.MESSAGE));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("APP_ERROR", e.getMessage());
                }
            }
        }, activity, Constant.APPUPDATE_URL, params, false);
        Log.d("APPUPDATE_URL","APPUPDATE_URL: " + Constant.APPUPDATE_URL);
        Log.d("APPUPDATE_URL","APPUPDATE_URL params: " + params);
    }

    private void setSessionData(JSONObject settingsObject) throws JSONException {
        session.setData(Constant.PAYMENT_LINK, settingsObject.optString(Constant.PAYMENT_LINK, ""));
        session.setData(Constant.WHATSAPP, settingsObject.optString(Constant.WHATSAPP, ""));
        session.setData(Constant.JOB_DETAILS_LINK, settingsObject.optString(Constant.JOB_DETAILS_LINK, ""));
        session.setData(Constant.SYNC_TIME, settingsObject.optString(Constant.SYNC_TIME, ""));
        session.setInt(Constant.SYNC_CODES, settingsObject.optInt(Constant.SYNC_CODES, 0));
        session.setData(Constant.REWARD, settingsObject.optString(Constant.REWARD, ""));
        session.setData(Constant.AD_SHOW_TIME, settingsObject.optString(Constant.AD_SHOW_TIME, ""));
        session.setData(Constant.MIN_WITHDRAWAL, settingsObject.optString(Constant.MIN_WITHDRAWAL, ""));
        session.setData(Constant.AD_STATUS, settingsObject.optString(Constant.AD_STATUS, ""));
        session.setData(Constant.FETCH_TIME, settingsObject.optString(Constant.FETCH_TIME, ""));
        session.setData(Constant.AD_REWARD_ID, settingsObject.optString(Constant.AD_REWARD_ID, ""));
        session.setData(Constant.JOIN_CODES, settingsObject.optString(Constant.JOIN_CODES, ""));
        session.setData(Constant.REFER_BONUS_CODES, settingsObject.optString(Constant.REFER_BONUS_CODES, ""));
        session.setData(Constant.REFER_BONUS_AMOUNT, settingsObject.optString(Constant.REFER_BONUS_AMOUNT, ""));
        session.setData(Constant.REFER_DESCRIPTION, settingsObject.optString(Constant.REFER_DESCRIPTION, ""));
        session.setData(Constant.AD_TYPE, settingsObject.optString(Constant.AD_TYPE, ""));
        session.setData(Constant.CHAMPION_TASK, settingsObject.optString(Constant.CHAMPION_TASK, ""));
        session.setData(Constant.CHAMPION_CODES, settingsObject.optString(Constant.CHAMPION_CODES, ""));
        session.setData(Constant.CHAMPION_SEARCH_COUNT, settingsObject.optString(Constant.CHAMPION_SEARCH_COUNT, ""));
        session.setData(Constant.CHAMPION_DEMO_LINK, settingsObject.optString(Constant.CHAMPION_DEMO_LINK, ""));
        session.setData(Constant.MAIN_CONTENT, settingsObject.optString(Constant.MAIN_CONTENT, ""));
    }

    private void setUserDetails(JSONObject userDetails) throws JSONException {
        session.setData(Constant.STATUS, userDetails.optString(Constant.STATUS, ""));
        session.setData(Constant.TOTAL_REFERRALS, userDetails.optString(Constant.TOTAL_REFERRALS, ""));
        session.setData(Constant.WITHDRAWAL, userDetails.optString(Constant.WITHDRAWAL, ""));
        session.setData(Constant.CODE_GENERATE_TIME, userDetails.optString(Constant.CODE_GENERATE_TIME, ""));
        session.setData(Constant.JOINED_DATE, userDetails.optString(Constant.JOINED_DATE, ""));
        session.setData(Constant.TASK_TYPE, userDetails.optString(Constant.TASK_TYPE, ""));
        session.setData(Constant.TRIAL_COUNT, userDetails.optString(Constant.TRIAL_COUNT, ""));
        session.setData(Constant.TRIAL_EXPIRED, userDetails.optString(Constant.TRIAL_EXPIRED, ""));
        session.setData(Constant.CHAMPION_TASK_ELIGIBLE, userDetails.optString(Constant.CHAMPION_TASK_ELIGIBLE, ""));
        session.setData(Constant.MCG_TIMER, userDetails.optString(Constant.MCG_TIMER, ""));
        session.setData(Constant.SECURITY, userDetails.optString(Constant.SECURITY, ""));
        session.setData(Constant.ONGOING_SA_BALANCE, userDetails.optString(Constant.ONGOING_SA_BALANCE, ""));
        session.setData(Constant.SALARY_ADVANCE_BALANCE, userDetails.optString(Constant.SALARY_ADVANCE_BALANCE, ""));
        session.setData(Constant.SA_REFER_COUNT, userDetails.optString(Constant.SA_REFER_COUNT, ""));
        session.setData(Constant.PER_CODE_VAL, userDetails.optString(Constant.PER_CODE_VAL, ""));
        session.setData(Constant.LEVEL, userDetails.optString(Constant.LEVEL, ""));
    }

    private void updateAlertDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New update Available");
        builder.setMessage(description);
        builder.setCancelable(false);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));

            }
        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_HOME);
//                startActivity(intent);
//                finish();
//            }
//        });
        builder.show();
    }


    private void GotoActivity() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Session session = new Session(SplashActivity.this);
//                session.setData(com.app.ai_di.helper.Constant.USER_ID, "13473");
//                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
                Log.d("is_logged_in","is_logged_in " + session.getBoolean("is_logged_in"));
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