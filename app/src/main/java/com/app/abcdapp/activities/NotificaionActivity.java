package com.app.abcdapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.abcdapp.Adapter.NotificationAdapter;
import com.app.abcdapp.R;
import com.app.abcdapp.helper.ApiConfig;
import com.app.abcdapp.helper.Constant;
import com.app.abcdapp.helper.Session;
import com.app.abcdapp.model.Notification;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificaionActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    Activity activity;
    NotificationAdapter notificationAdapter;
    Session session;
    ImageView ivBackbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaion);
        activity = NotificaionActivity.this;
        session = new Session(activity);
        recyclerView = findViewById(R.id.recyclerView);
        ivBackbtn = findViewById(R.id.backbtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));

        notificationList();
        ivBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private void notificationList()
    {
        Map<String, String> params = new HashMap<>();
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                        Gson g = new Gson();
                        ArrayList<Notification> notifications = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            if (jsonObject1 != null) {
                                Notification group = g.fromJson(jsonObject1.toString(), Notification.class);
                                notifications.add(group);
                            } else {
                                break;
                            }
                        }
                        notificationAdapter = new NotificationAdapter(activity, notifications);
                        recyclerView.setAdapter(notificationAdapter);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.NOTIFICATION_LIST_URL, params, true);

    }

}