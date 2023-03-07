package com.app.fortuneapp.activities;


import static com.app.fortuneapp.helper.Constant.MESSAGE;
import static com.app.fortuneapp.helper.Constant.SUCCESS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fortuneapp.Adapter.SalaryAdvanceAdapter;
import com.app.fortuneapp.R;
import com.app.fortuneapp.databinding.ActivitySalaryAdvanceBinding;
import com.app.fortuneapp.helper.ApiConfig;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.Session;
import com.app.fortuneapp.model.AdvanceWithdrawlModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SalaryAdvanceActivity extends AppCompatActivity {

    private ActivitySalaryAdvanceBinding activitySalaryAdvanceBinding;
    private SalaryAdvanceAdapter advanceAdapter;
    public Activity activity;
    Session session;
    String amount;
    TextView tvReferCount,tvBalance,tvOngoingAdvance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySalaryAdvanceBinding = ActivitySalaryAdvanceBinding.inflate(getLayoutInflater());
        setContentView(activitySalaryAdvanceBinding.getRoot());
        activity=SalaryAdvanceActivity.this;
        session=new Session(activity);
        tvReferCount=findViewById(R.id.tvReferCount);
        tvBalance=findViewById(R.id.tvBalance);
        tvOngoingAdvance=findViewById(R.id.tvOngoingAdvance);
        tvReferCount.setText("Total Refer: "+session.getData(Constant.SA_REFER_COUNT));
        tvBalance.setText("Salary Advance Balance - ₹ "+session.getData(Constant.SALARY_ADVANCE_BALANCE));
        tvOngoingAdvance.setText("Ongoing Advance - ₹ "+session.getData(Constant.ONGOING_SA_BALANCE));
        Spinner mySpinner = (Spinner) findViewById(R.id.my_spinner);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    amount="0";
                }else {
                    String selectedValue = parent.getItemAtPosition(position).toString();
                    // Do something with the selected value, such as:
                    amount = selectedValue;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        showWithdrawals();

        activitySalaryAdvanceBinding.recycler.setLayoutManager(new LinearLayoutManager(this));

        activitySalaryAdvanceBinding.btnRepayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalaryAdvanceActivity.this,RepayListActivity.class));
            }
        });

        activitySalaryAdvanceBinding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalaryAdvanceActivity.this,MainActivity.class));
                finish();
            }
        });
        activitySalaryAdvanceBinding.btnRequestwithdrawl.setOnClickListener(v -> {
            if (amount.equals("0")){
                Toast.makeText(activity, "Please Select Amount", Toast.LENGTH_SHORT).show();
            }else {
                requestWithdrawal();
            }

        });
    }

    private void showWithdrawals()
    {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID,session.getData(Constant.USER_ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(SUCCESS)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        ArrayList<AdvanceWithdrawlModel> advanceWithdrawlModels = new ArrayList<>();
                        Gson g = new Gson();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1 != null) {
                                AdvanceWithdrawlModel group = g.fromJson(jsonObject1.toString(), AdvanceWithdrawlModel.class);
                                advanceWithdrawlModels.add(group);
                            } else {
                                break;
                            }
                        }
                        advanceAdapter = new SalaryAdvanceAdapter(activity,advanceWithdrawlModels);
                        activitySalaryAdvanceBinding.recycler.setAdapter(advanceAdapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.MY_SA_TRANSLIST_URL, params, true);


    }
    private void requestWithdrawal()
    {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID,session.getData(Constant.USER_ID));
        params.put(Constant.AMOUNT,amount);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(SUCCESS)) {
                        Toast.makeText(activity,jsonObject.getString(MESSAGE) , Toast.LENGTH_SHORT).show();
                        session.setData(Constant.SALARY_ADVANCE_BALANCE,jsonObject.getString(Constant.SALARY_ADVANCE_BALANCE));
                        session.setData(Constant.ONGOING_SA_BALANCE,jsonObject.getString(Constant.ONGOING_SA_BALANCE));
                      //  session.setData(Constant.REFER_BALANCE,jsonObject.getString(Constant.REFER_BALANCE));
                    }else {
                        Toast.makeText(activity,jsonObject.getString(MESSAGE) , Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.SA_WITHDRAWAL_URL, params, true);

    }
}


