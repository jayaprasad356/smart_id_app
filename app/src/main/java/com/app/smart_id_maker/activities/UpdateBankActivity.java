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

public class UpdateBankActivity extends AppCompatActivity {

    ImageView back;
    Button btnUpdateBank;
    Activity activity;
    EditText etAccountnum,etHolderName,etBankname,etBranch,etIFSC;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bank);
        activity = UpdateBankActivity.this;
        session = new Session(activity);
        back = findViewById(R.id.back);
        btnUpdateBank = findViewById(R.id.btnUpdateBank);
        etAccountnum = findViewById(R.id.etAccountnum);
        etHolderName = findViewById(R.id.etHolderName);
        etBankname = findViewById(R.id.etBankname);
        etBranch = findViewById(R.id.etBranch);
        etIFSC = findViewById(R.id.etIFSC);

        etAccountnum.setText(session.getData(Constant.ACCOUNT_NUM));
        etHolderName.setText(session.getData(Constant.HOLDER_NAME));
        etBankname.setText(session.getData(Constant.BANK));
        etBranch.setText(session.getData(Constant.BRANCH));
        etIFSC.setText(session.getData(Constant.IFSC));

        bankDetailsApi();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnUpdateBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etAccountnum.getText().toString().trim().equals("")){
                    etAccountnum.setError("empty");
                    etAccountnum.requestFocus();
                }
                else if (etHolderName.getText().toString().trim().equals("")){
                    etHolderName.setError("empty");
                    etHolderName.requestFocus();
                }
                else if (etBankname.getText().toString().trim().equals("")){
                    etBankname.setError("empty");
                    etBankname.requestFocus();
                }
                else if (etBranch.getText().toString().trim().equals("")){
                    etBranch.setError("empty");
                    etBranch.requestFocus();
                }
                else if (etIFSC.getText().toString().trim().equals("")){
                    etIFSC.setError("empty");
                    etIFSC.requestFocus();
                }
                else {
                    updateBank();

                }

            }
        });
    }

    private void updateBank() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID,session.getData(Constant.USER_ID));
        params.put(Constant.ACCOUNT_NUM,etAccountnum.getText().toString().trim());
        params.put(Constant.HOLDER_NAME,etHolderName.getText().toString().trim());
        params.put(Constant.BANK,etBankname.getText().toString().trim());
        params.put(Constant.BRANCH,etBranch.getText().toString().trim());
        params.put(Constant.IFSC,etIFSC.getText().toString().trim());
        ApiConfig.RequestToVolley((result, response) -> {
            Log.d("BAN_DETAILS_RES",response);
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(SUCCESS)) {
                        Toast.makeText(this, ""+jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                        JSONArray bankArray = jsonObject.getJSONArray(Constant.DATA);
                        session.setData(Constant.ACCOUNT_NUM,bankArray.getJSONObject(0).getString(Constant.ACCOUNT_NUM));
                        session.setData(Constant.HOLDER_NAME,bankArray.getJSONObject(0).getString(Constant.HOLDER_NAME));
                        session.setData(Constant.BANK,bankArray.getJSONObject(0).getString(Constant.BANK));
                        session.setData(Constant.BRANCH,bankArray.getJSONObject(0).getString(Constant.BRANCH));
                        session.setData(Constant.IFSC,bankArray.getJSONObject(0).getString(Constant.IFSC));
                        startActivity(new Intent(activity, WithdrawalActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.UPDATE_BANK_URL, params, true);



    }
    private void bankDetailsApi() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID,session.getData(Constant.USER_ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(SUCCESS)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        etAccountnum.setText(jsonArray.getJSONObject(0).getString(Constant.ACCOUNT_NUM));
                        etHolderName.setText(jsonArray.getJSONObject(0).getString(Constant.HOLDER_NAME));
                        etBankname.setText(jsonArray.getJSONObject(0).getString(Constant.BANK));
                        etBranch.setText(jsonArray.getJSONObject(0).getString(Constant.BRANCH));
                        etIFSC.setText(jsonArray.getJSONObject(0).getString(Constant.IFSC));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.BANK_DETAILS_URL, params, true);

    }
}