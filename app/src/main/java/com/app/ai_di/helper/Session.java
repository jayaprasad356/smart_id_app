package com.app.ai_di.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.app.ai_di.activities.LoginActivity;
import com.app.ai_di.model.DemoCodeData;
import com.app.ai_di.model.ExtraPlanModel;
import com.app.ai_di.model.PlanListModel;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;


public class Session {
    public static final String PREFER_NAME = "bigwigg";
    final int PRIVATE_MODE = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _activity;

    public Session(Context activity) {
        try {
            this._activity = activity;
            pref = _activity.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
            editor = pref.edit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(String id, String val) {
        editor.putString(id, val);
        editor.commit();
    }

    public void setBoolean(String id, boolean val) {
        editor.putBoolean(id, val);
        editor.commit();
    }

    public void setUserData(
            String id,
            String name,
            String mobile,
            String password,
            String dob,
            String email,
            String city,
            String referred_by,
            String earn,
            String withdrawal,
            String withdrawal_status,
            String total_referrals,
            String today_codes,
            String total_codes,
            String balance,
            String device_id,
            String status,
            String refer_code,
            String refer_bonus_sent,
            String register_bonus_sent,
            String code_generate,
            String code_generate_time,
            String fcm_id,
            String last_updated,
            String joined_date,
            String app_version,
            String per_code_cost,
            String per_code_val,
//            String worked_days,
            String recharge,
            String total_assets,
            String bonus_wallet,
            String team_income,
            String earning_wallet,
            String total_recharge,
            String total_codes_income,
            String total_refer_income,
            String today_earnings,
            String total_earnings,
            String min_withdrawal
//            String planName,
//            String planDescription,
//            String planValidity,
//            String dailyCodes,
//            String perCodeRate,
//            String dailyEarnings
            ) {
        editor.putString(Constant.USER_ID, id);
        editor.putString(Constant.NAME, name);
        editor.putString(Constant.MOBILE, mobile);
        editor.putString(Constant.PASSWORD, password);
        editor.putString(Constant.DOB, dob);
        editor.putString(Constant.EMAIL, email);
        editor.putString(Constant.CITY, city);
        editor.putString(Constant.REFERRED_BY, referred_by);
        editor.putString(Constant.EARN, earn);
        editor.putString(Constant.WITHDRAWAL, withdrawal);
        editor.putString(Constant.WITHDRAWAL_STATUS, withdrawal_status);
        editor.putString(Constant.TOTAL_REFERRALS, total_referrals);
        editor.putString(Constant.TODAY_CODES, today_codes);
        editor.putString(Constant.TOTAL_CODES, total_codes);
        editor.putString(Constant.BALANCE, balance);
        editor.putString(Constant.DEVICE_ID, device_id);
        editor.putString(Constant.STATUS, status);
        editor.putString(Constant.REFER_CODE, refer_code);
        editor.putString(Constant.REFER_BONUS_SENT, refer_bonus_sent);
        editor.putString(Constant.REGISTER_BONUS_SENT, register_bonus_sent);
        editor.putString(Constant.CODE_GENERATE, code_generate);
        editor.putString(Constant.CODE_GENERATE_TIME, code_generate_time);
        editor.putString(Constant.FCM_ID, fcm_id);
        editor.putString(Constant.LAST_UPDATED, last_updated);
        editor.putString(Constant.JOINED_DATE, joined_date);
        editor.putString(Constant.APP_VERSION, app_version);
        editor.putString(Constant.PER_CODE_COST, per_code_cost);
        editor.putString(Constant.PER_CODE_VAL, per_code_val);
//        editor.putString(Constant.WORKED_DAYS, worked_days);
        editor.putString(Constant.RECHARGE, recharge);
        editor.putString(Constant.TOTAL_ASSETS, total_assets);
        editor.putString(Constant.BONUS_WALLET, bonus_wallet);
        editor.putString(Constant.TEAM_INCOME, team_income);
        editor.putString(Constant.EARNING_WALLET, earning_wallet);
        editor.putString(Constant.TOTAL_RECHARGE, total_recharge);
        editor.putString(Constant.TOTAL_CODES_INCOME, total_codes_income);
        editor.putString(Constant.TOTAL_REFERRAL_INCOME, total_refer_income);
        editor.putString(Constant.TODAY_EARNINGS, today_earnings);
        editor.putString(Constant.TOTAL_EARNINGS, total_earnings);
        editor.putString(Constant.MIN_WITHDRAWAL, min_withdrawal);
//        editor.putString(Constant.PLAN_NAME, planName);
//        editor.putString(Constant.PLAN_DESCRIPTION, planDescription);
//        editor.putString(Constant.PLAN_VALIDITY, planValidity);
//        editor.putString(Constant.DAILY_CODES, dailyCodes);
//        editor.putString(Constant.PER_CODE_RATE, perCodeRate);
//        editor.putString(Constant.DAILY_EARNINGS, dailyEarnings);

        editor.commit();
    }

    // Method to store a list of Plan objects as JSON
    public void setPlanData(List<PlanListModel> planList) {
        Gson gson = new Gson();
        String json = gson.toJson(planList);
        editor.putString("plan_activated", json);
        editor.commit();
    }

    // Method to retrieve a list of Plan objects from JSON
    public List<PlanListModel> getPlanData() {
        Gson gson = new Gson();
        String json = pref.getString("plan_activated", null);
        Type type = new TypeToken<List<PlanListModel>>() {}.getType();
        return gson.fromJson(json, type);
    }

    // Method to store a list of Plan objects as JSON
    public void setExtraPlanData(List<ExtraPlanModel> extraPlanModel) {
        Gson gson = new Gson();
        String json = gson.toJson(extraPlanModel);
        editor.putString("extra_plan_activated", json);
        editor.commit();
    }

    // Method to retrieve a list of Plan objects from JSON
    public List<ExtraPlanModel> getExtraPlanData() {
        Gson gson = new Gson();
        String json = pref.getString("extra_plan_activated", null);
        Type type = new TypeToken<List<ExtraPlanModel>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void setDemoDataList(List<DemoCodeData> demoCodeData) {
        Gson gson = new Gson();
        String json = gson.toJson(demoCodeData);
        editor.putString("demo_code_data", json);
        editor.commit();
    }

    public List<DemoCodeData> getDemoDataList() {
        Gson gson = new Gson();
        String json = pref.getString("demo_code_data", null);
        Type type = new TypeToken<List<DemoCodeData>>() {}.getType();
        return gson.fromJson(json, type);
    }


    public String getData(String id) {
        return pref.getString(id, "");
    }
    public int getInt(String id) {
        return pref.getInt(id,0);
    }
    public void setInt(String id, Integer val) {
        editor.putInt(id, val);
        editor.commit();
    }


    public void logoutUser(Activity activity) {
        Intent i = new Intent(activity, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
        activity.finish();
        editor.clear();
        editor.commit();

        new Session(_activity).setBoolean("is_logged_in", false);

    }

    public boolean getBoolean(String id) {
        return pref.getBoolean(id, false);
    }

    // Method to clear data for a specific key
    public void clearData(String id) {
        editor.remove(id);  // Removes the specific key
        editor.commit();    // Commit the changes
    }
}