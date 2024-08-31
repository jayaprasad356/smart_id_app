package com.app.smart_id_maker.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.smart_id_maker.R;
import com.app.smart_id_maker.helper.ApiConfig;
import com.app.smart_id_maker.helper.Constant;
import com.app.smart_id_maker.helper.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CheckInActivity extends AppCompatActivity {

    String Mobile, Password;
    Activity activity;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        activity = CheckInActivity.this;
        session = new Session(activity);
        Mobile = session.getData(Constant.MOBILE);
        Password = session.getData(Constant.PASSWORD);

        Login();
    }

    private void Login() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.MOBILE, Mobile);
        params.put(Constant.PASSWORD, Password);
        params.put(Constant.DEVICE_ID, Constant.getDeviceId(activity));

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        if (jsonObject.getBoolean(Constant.USER_VERIFY)) {
                            if (jsonObject.getBoolean(Constant.DEVICE_VERIFY)) {
                                String codegenerate = "0", withdrawal_status = "0";
                                JSONArray userArray = jsonObject.getJSONArray(Constant.DATA);
                                JSONArray setArray = jsonObject.getJSONArray(Constant.SETTINGS);

                                JSONObject settings = setArray.getJSONObject(0);
                                session.setData(Constant.SYNC_TIME, settings.getString(Constant.SYNC_TIME));
                                session.setInt(Constant.SYNC_CODES, settings.getInt(Constant.SYNC_CODES));
                                session.setData(Constant.REWARD, settings.getString(Constant.REWARD));
                                session.setData(Constant.AD_SHOW_TIME, settings.getString(Constant.AD_SHOW_TIME));
                                session.setData(Constant.MIN_WITHDRAWAL, settings.getString(Constant.MIN_WITHDRAWAL));
                                session.setData(Constant.AD_STATUS, settings.getString(Constant.AD_STATUS));
                                session.setData(Constant.FETCH_TIME, settings.getString(Constant.FETCH_TIME));
                                session.setData(Constant.AD_REWARD_ID, settings.getString(Constant.AD_REWARD_ID));
                                session.setData(Constant.JOIN_CODES, settings.getString(Constant.JOIN_CODES));
                                session.setData(Constant.REFER_BONUS_CODES, settings.getString(Constant.REFER_BONUS_CODES));
                                session.setData(Constant.REFER_BONUS_AMOUNT, settings.getString(Constant.REFER_BONUS_AMOUNT));
                                session.setData(Constant.REFER_DESCRIPTION, settings.getString(Constant.REFER_DESCRIPTION));
                                session.setData(Constant.CHAMPION_TASK, settings.getString(Constant.CHAMPION_TASK));
                                session.setData(Constant.CHAMPION_CODES, settings.getString(Constant.CHAMPION_CODES));
                                session.setData(Constant.CHAMPION_SEARCH_COUNT, settings.getString(Constant.CHAMPION_SEARCH_COUNT));
                                session.setData(Constant.CHAMPION_DEMO_LINK, settings.getString(Constant.CHAMPION_DEMO_LINK));

                                if (settings.getString(Constant.CODE_GENERATE).equals("1")) {
                                    codegenerate = userArray.getJSONObject(0).getString(Constant.CODE_GENERATE);
                                }
                                if (settings.getString(Constant.WITHDRAWAL_STATUS).equals("1")) {
                                    withdrawal_status = userArray.getJSONObject(0).getString(Constant.WITHDRAWAL_STATUS);
                                }
                                session.setBoolean(Constant.CHECKIN, false);

                                Toast.makeText(this, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();

                                JSONObject userData = userArray.getJSONObject(0);
                                session.setUserData(
                                        userData.getString(Constant.ID),
                                        userData.getString(Constant.NAME),
                                        userData.getString(Constant.MOBILE),
                                        userData.getString(Constant.PASSWORD),
                                        userData.getString(Constant.DOB),
                                        userData.getString(Constant.EMAIL),
                                        userData.getString(Constant.CITY),
                                        userData.getString(Constant.REFERRED_BY),
                                        userData.getString(Constant.EARN),
                                        userData.getString(Constant.WITHDRAWAL),
                                        userData.getString(Constant.TOTAL_REFERRALS),
                                        userData.getInt(Constant.TODAY_CODES),
                                        userData.getInt(Constant.TOTAL_CODES),
                                        userData.getString(Constant.BALANCE),
                                        userData.getString(Constant.DEVICE_ID),
                                        userData.getString(Constant.STATUS),
                                        userData.getString(Constant.REFER_CODE),
                                        userData.getString(Constant.REFER_BONUS_SENT),
                                        codegenerate,
                                        userData.getString(Constant.CODE_GENERATE_TIME),
                                        userData.getString(Constant.LAST_UPDATED),
                                        userData.getString(Constant.JOINED_DATE)

                                );

                                if (session.getBoolean(Constant.IMPORT_DATA)) {
                                    session.setBoolean("is_logged_in", true);
                                    startActivity(new Intent(activity, MainActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(activity, ImportDataActivity.class));
                                    finish();
                                }
                            } else {
                                showAlertdialog();
                            }
                        } else {
                            ApprovalAlertdialog(jsonObject.getString(Constant.MESSAGE));
                        }
                    } else {
                        Toast.makeText(this, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, response + result, Toast.LENGTH_SHORT).show();
            }
        }, activity, Constant.LOGIN_URL, params, true);
    }

    private void showAlertdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Would you request to change device ?");
        builder.setTitle("Device verification failed !");
        builder.setCancelable(false);
        builder.setPositiveButton("Send request to admin", (dialog, which) -> {
            changeDeviceApi(dialog);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void changeDeviceApi(DialogInterface dialog) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.MOBILE, Mobile);
        params.put(Constant.PASSWORD, Password);
        params.put(Constant.DEVICE_ID, Constant.getDeviceId(activity));

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(this, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    } else {
                        Toast.makeText(this, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, response + result, Toast.LENGTH_SHORT).show();
            }
        }, activity, Constant.CHANGE_DEVICE_LIST_URL, params, true);
    }

    private void ApprovalAlertdialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg);
        builder.setTitle("Failed to Login");
        builder.setCancelable(false);
        builder.setPositiveButton("ok", (dialog, which) -> {
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
