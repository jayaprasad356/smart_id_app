package com.app.abcdapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.app.abcdapp.R;
import com.app.abcdapp.helper.Constant;
import com.app.abcdapp.helper.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GenrateQRActivity extends AppCompatActivity {


    Handler handler;
    Session session;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genrate_qractivity);
        activity = GenrateQRActivity.this;
        session = new Session(activity);


        handler = new Handler();
        SimpleDateFormat df = new SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.getDefault());
        Date c = Calendar.getInstance().getTime();
        String currentDate = df.format(c);

        if (!session.getBoolean(Constant.LAST_UPDATED_DATE_STATUS)){
            session.setData(Constant.LAST_UPDATED_DATE,currentDate);
            session.setBoolean(Constant.LAST_UPDATED_DATE_STATUS,true);

        }
        Date date1 = null;
        try {
            date1 = df.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = df.parse(session.getData(Constant.LAST_UPDATED_DATE));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long different = date1.getTime() - date2.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long elapsedMinutue = different / minutesInMilli *5;
        Toast.makeText(activity, ""+elapsedMinutue, Toast.LENGTH_SHORT).show();

        if (elapsedMinutue >= 2){

            session.setBoolean(Constant.RUN_API,true);
        }
        else {
            session.setBoolean(Constant.RUN_API,false);
        }
        GotoActivity();


    }



    private void GotoActivity()
    {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {



                Intent intent=new Intent(GenrateQRActivity.this,MainActivity.class);
                startActivity(intent);
                finish();



            }
        },1000);
    }
}