package com.app.fortuneapp.activities;


import static com.app.fortuneapp.helper.Constant.MESSAGE;
import static com.app.fortuneapp.helper.Constant.SUCCESS;
import static com.app.fortuneapp.helper.Constant.TOTAL_LEAVES;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fortuneapp.Adapter.LeavesAdapter;
import com.app.fortuneapp.R;
import com.app.fortuneapp.helper.ApiConfig;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.Session;
import com.app.fortuneapp.model.Leaves;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ApplyLeaveActivity extends AppCompatActivity {
    EditText edDate, edReason;
    Button btnApply;
    Activity activity;
    Session session;
    boolean selectdate = false;
    TextView tvLeaveBalance;
    RecyclerView recyclerView;
    LeavesAdapter leavesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_leave);

        activity = ApplyLeaveActivity.this;
        session = new Session(activity);

        edDate = findViewById(R.id.edDate);
        edReason = findViewById(R.id.edReason);
        btnApply = findViewById(R.id.btnApply);
        tvLeaveBalance = findViewById(R.id.tvLeaveBalance);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        edDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showDatePickerDialog(edDate);
                }
                return false;
            }
        });

        leaveList();
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edReason.getText().toString().trim().equals("")) {
                    Toast.makeText(activity, "Reason is Empty", Toast.LENGTH_SHORT).show();

                } else if (!selectdate) {
                    Toast.makeText(activity, "Select Date", Toast.LENGTH_SHORT).show();


                } else {
                    applyLeave();

                }


            }
        });
    }

    private void applyLeave() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.LEAVE_DATE, edDate.getText().toString());
        params.put(Constant.REASON, edReason.getText().toString());
        ApiConfig.RequestToVolley((result, response) -> {
            Log.d("LEAVE_LIST", response);
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(SUCCESS)) {
                        Toast.makeText(activity, "" + jsonObject.getString(MESSAGE), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(activity, "" + jsonObject.getString(MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.APPLY_LEAVE_URL, params, true);
    }

    private void leaveList() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(SUCCESS)) {
                        tvLeaveBalance.setText("Balance Leave = " + jsonObject.getString(TOTAL_LEAVES));

                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                        Gson g = new Gson();
                        ArrayList<Leaves> leaves = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            if (jsonObject1 != null) {
                                Leaves group = g.fromJson(jsonObject1.toString(), Leaves.class);
                                leaves.add(group);
                            } else {
                                break;
                            }
                        }
                        leavesAdapter = new LeavesAdapter(activity, leaves);
                        recyclerView.setAdapter(leavesAdapter);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.MYLEAVES_LIST_URL, params, true);
    }

    private void showDatePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);

                        // Compare selected date with today's date
                        Calendar today = Calendar.getInstance();
                        today.setTimeInMillis(System.currentTimeMillis());

                        if (selectedDate.before(today)) {
                            // If selected date is before today, show an error message
                            Toast.makeText(getApplicationContext(), "Please select a future date", Toast.LENGTH_LONG).show();
                        } else if (selectedDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                                && selectedDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                            Toast.makeText(getApplicationContext(), "You cannot Select Today date", Toast.LENGTH_LONG).show();


                        } else {
                            String dateString = year + "-" + (month + 1) + "-" + dayOfMonth;
                            editText.setText(dateString);
                            selectdate = true;
                        }
                    }
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }
}