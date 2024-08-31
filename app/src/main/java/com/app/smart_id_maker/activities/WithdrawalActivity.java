package com.app.smart_id_maker.activities;

import static com.app.smart_id_maker.helper.Constant.SUCCESS;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.app.smart_id_maker.Adapter.RedeemedAdapter;
import com.app.smart_id_maker.Adapter.RedeemedAdapter;
import com.app.smart_id_maker.R;
import com.app.smart_id_maker.helper.ApiConfig;
import com.app.smart_id_maker.helper.Constant;
import com.app.smart_id_maker.helper.Session;
import com.app.smart_id_maker.model.Redeem;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WithdrawalActivity extends AppCompatActivity {



    ImageView back;
    RecyclerView recycler;
    RedeemedAdapter redeemedAdapter;
    Activity activity;
    Button btnUpdateBank,btnWithdrawal;
    TextView tvBalance,tvminiwithdrawal;
    Session session;
    EditText etAmount;
    String type = "bank_transfer";
    RadioButton rbBanktransfer,rbCash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);
        activity = WithdrawalActivity.this;
        session = new Session(activity);

        back = findViewById(R.id.back);
        recycler = findViewById(R.id.recycler);
        btnUpdateBank = findViewById(R.id.btnUpdateBank);
        tvBalance = findViewById(R.id.tvBalance);
        btnWithdrawal = findViewById(R.id.btnWithdrawal);
        etAmount = findViewById(R.id.etAmount);
        rbBanktransfer = findViewById(R.id.rbBanktransfer);
        rbCash = findViewById(R.id.rbCash);
        tvminiwithdrawal = findViewById(R.id.tvminumumRedeem);
        tvBalance.setText("Available Balance = ₹"+session.getData(Constant.BALANCE));
        tvminiwithdrawal.setText("Minimum Redeem =  ₹"+session.getData(Constant.MIN_WITHDRAWAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recycler.setLayoutManager(linearLayoutManager);
        redeemlist();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnUpdateBank.setVisibility(View.GONE);
//        btnUpdateBank.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(activity,UpdateBankActivity.class);
//                startActivity(intent);
//            }
//        });
        btnWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etAmount.getText().toString().trim().equals("") || etAmount.getText().toString().trim().equals("0")){
                    etAmount.setError("enter amount");
                    etAmount.requestFocus();
                }else if (Double.parseDouble(etAmount.getText().toString().trim()) < 250) {
                    Toast.makeText(activity, "minimum "+session.getData(Constant.MIN_WITHDRAWAL)+" balance required", Toast.LENGTH_SHORT).show();

                }else if (Double.parseDouble(etAmount.getText().toString().trim()) > Double.parseDouble(session.getData(Constant.BALANCE))) {
                    Toast.makeText(activity, "insuffcient balance", Toast.LENGTH_SHORT).show();

                }else {
                    if (rbBanktransfer.isChecked()){
                        type = "bank_transfer";
                    }else {
                        type = "cash_payment";
                    }
                    withdrawalApi();

                }


            }
        });
    }

    private void withdrawalApi() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID,session.getData(Constant.USER_ID));
        params.put(Constant.AMOUNT,etAmount.getText().toString().trim());
        params.put(Constant.TYPE,type);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(SUCCESS)) {
                        Toast.makeText(this, ""+jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                        session.setData(Constant.BALANCE,jsonObject.getString(Constant.BALANCE));
                        startActivity(new Intent(activity, MainActivity.class));
                        finish();
                    }else {
                        Toast.makeText(this, ""+jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.WITHDRAWAL_URL, params, true);


    }

    private void redeemlist() {



        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID,session.getData(Constant.USER_ID));
        ApiConfig.RequestToVolley((result, response) -> {
            Log.d("WITHDRAWAL_RES",response);
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(SUCCESS)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        Gson g = new Gson();
                        ArrayList<Redeem> redeems = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1 != null) {
                                Redeem group = g.fromJson(jsonObject1.toString(), Redeem.class);
                                redeems.add(group);
                            } else {
                                break;
                            }
                        }

                        redeemedAdapter = new RedeemedAdapter(activity,redeems);
                        recycler.setAdapter(redeemedAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.WITHDRAWAL_LIST_URL, params, true);


    }
}