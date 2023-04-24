package com.app.fortuneapp.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.fortuneapp.R;
import com.app.fortuneapp.activities.MainActivity;
import com.app.fortuneapp.helper.ApiConfig;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GenrateQRFragment extends Fragment {
    Handler handler;
    Session session;
    Activity activity;
    String tasktype = "regular";


    public GenrateQRFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_genrate_q_r, container, false);
        activity = getActivity();
        session = new Session(activity);

        if(getArguments() != null){
            tasktype = getArguments().getString(Constant.TASK_TYPE);

        }
        handler = new Handler();
//        SimpleDateFormat df = new SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.getDefault());
//        Date c = Calendar.getInstance().getTime();
//        String currentDate = df.format(c);
//
//        if (!session.getBoolean(Constant.LAST_UPDATED_DATE_STATUS)){
//            session.setData(Constant.LAST_UPDATED_DATE,currentDate);
//            session.setBoolean(Constant.LAST_UPDATED_DATE_STATUS,true);
//
//        }
//        Date date1 = null;
//        try {
//            date1 = df.parse(currentDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Date date2 = null;
//        try {
//            date2 = df.parse(session.getData(Constant.LAST_UPDATED_DATE));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        long different = date1.getTime() - date2.getTime();
//        long secondsInMilli = 1000;
//        long minutesInMilli = secondsInMilli * 60;
//        long hoursInMilli = minutesInMilli * 60;
//        long elapsedHours = different / hoursInMilli;
//        long elapsedMinutue = different / minutesInMilli;
//
//        if (elapsedMinutue >= Long.parseLong(session.getData(Constant.SYNC_TIME))){
//
//            session.setBoolean(Constant.RUN_API,true);
//            session.setBoolean(Constant.LAST_UPDATED_DATE_STATUS,false);
//
//        }
//        else {
//            session.setBoolean(Constant.RUN_API,false);
//        }
        GotoActivity();
        return root;


    }

    public void walletApi() {
        if (ApiConfig.isConnected(activity)) {
            if (session.getInt(Constant.CODES) != 0) {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
                params.put(Constant.CODES, session.getInt(Constant.CODES) + "");
                session.setInt(Constant.CODES, 0);
                ApiConfig.RequestToVolley((result, response) -> {
                    Log.d("WALLET_RES", response);
                    if (result) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean(Constant.SUCCESS)) {
                                session.setBoolean(Constant.RUN_API, false);
                                session.setInt(Constant.TODAY_CODES, Integer.parseInt(jsonObject.getString(Constant.TODAY_CODES)));
                                session.setInt(Constant.TOTAL_CODES, Integer.parseInt(jsonObject.getString(Constant.TOTAL_CODES)));
                                session.setData(Constant.BALANCE, jsonObject.getString(Constant.BALANCE));


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, activity, Constant.WALLET_URL, params, true);


            }

        }
    }

    private void GotoActivity()
    {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (session.getData(Constant.TASK_TYPE).equals(Constant.CHAMPION) || tasktype.equals(Constant.CHAMPION)){
                    MainActivity.fm.beginTransaction().replace(R.id.Container, new FindMissingFragment()).commitAllowingStateLoss();

                }else {
                    MainActivity.fm.beginTransaction().replace(R.id.Container, new HomeFragment()).commitAllowingStateLoss();


                }

            }
        },1000);
    }
}