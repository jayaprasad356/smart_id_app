package com.app.abcdapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.abcdapp.Adapter.TransactionAdapter;
import com.app.abcdapp.R;
import com.app.abcdapp.activities.WithdrawalActivity;
import com.app.abcdapp.helper.ApiConfig;
import com.app.abcdapp.helper.Constant;
import com.app.abcdapp.helper.Session;
import com.app.abcdapp.model.Transanction;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class WalletFragment extends Fragment {

    Button btnWithdrawal;
    RecyclerView recycler;
    TransactionAdapter transactionAdapter;
    Activity activity;
    Session session;
    TextView tvBalance;




    public WalletFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_wallet, container, false);
        btnWithdrawal = root.findViewById(R.id.btnWithdrawal);
        recycler = root.findViewById(R.id.recycler);
        tvBalance = root.findViewById(R.id.tvBalance);
        activity = getActivity();
        session = new Session(activity);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(linearLayoutManager);

        tvBalance.setText("Available Balance = ₹"+session.getData(Constant.BALANCE));


        walletApi(session,activity);

        btnWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WithdrawalActivity.class);
                startActivity(intent);
                
            }
        });
        return root;

    }



    public void walletApi(Session session,Activity activity)
    {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID,session.getData(Constant.USER_ID));
        params.put(Constant.CODES,session.getInt(Constant.CODES)+"");
        params.put(Constant.FCM_ID,session.getData(Constant.FCM_ID));
        session.setInt(Constant.CODES,0);
        ApiConfig.RequestToVolley((result, response) -> {
            Log.d("WALLET_RES",response);
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        session.setBoolean(Constant.RUN_API,false);

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        JSONArray bankArray = jsonObject.getJSONArray(Constant.BANK_DETAILS);
                        String codegenerate = "0",withdrawal_status = "0";
                        JSONArray userArray = jsonObject.getJSONArray(Constant.USER_DETAILS);
                        JSONArray setArray = jsonObject.getJSONArray(Constant.SETTINGS);
                        session.setData(Constant.SYNC_TIME,setArray.getJSONObject(0).getString(Constant.SYNC_TIME));

                        if (setArray.getJSONObject(0).getString(Constant.CODE_GENERATE).equals("1")){
                            codegenerate = userArray.getJSONObject(0).getString(Constant.CODE_GENERATE);
                        }
                        if (setArray.getJSONObject(0).getString(Constant.WITHDRAWAL_STATUS).equals("1")){
                            withdrawal_status = userArray.getJSONObject(0).getString(Constant.WITHDRAWAL_STATUS);
                        }
                        session.setUserData(userArray.getJSONObject(0).getString(Constant.ID),
                                userArray.getJSONObject(0).getString(Constant.NAME),
                                userArray.getJSONObject(0).getString(Constant.MOBILE),
                                userArray.getJSONObject(0).getString(Constant.PASSWORD),
                                userArray.getJSONObject(0).getString(Constant.DOB),
                                userArray.getJSONObject(0).getString(Constant.EMAIL),
                                userArray.getJSONObject(0).getString(Constant.CITY),
                                userArray.getJSONObject(0).getString(Constant.REFERRED_BY),
                                userArray.getJSONObject(0).getString(Constant.EARN),
                                userArray.getJSONObject(0).getString(Constant.WITHDRAWAL),
                                userArray.getJSONObject(0).getString(Constant.TOTAL_REFERRALS),
                                userArray.getJSONObject(0).getInt(Constant.TODAY_CODES),
                                userArray.getJSONObject(0).getInt(Constant.TOTAL_CODES),
                                userArray.getJSONObject(0).getString(Constant.BALANCE),
                                userArray.getJSONObject(0).getString(Constant.DEVICE_ID),
                                userArray.getJSONObject(0).getString(Constant.STATUS),
                                userArray.getJSONObject(0).getString(Constant.REFER_CODE),
                                userArray.getJSONObject(0).getString(Constant.REFER_BONUS_SENT),
                                codegenerate,
                                userArray.getJSONObject(0).getString(Constant.CODE_GENERATE_TIME),
                                userArray.getJSONObject(0).getString(Constant.LAST_UPDATED),
                                userArray.getJSONObject(0).getString(Constant.JOINED_DATE),
                                withdrawal_status);


                        if (session.getData(Constant.STATUS).equals("2")){
                            session.logoutUser(activity);
                        }

                        if (bankArray.length() != 0){
                            session.setData(Constant.ACCOUNT_NUM,bankArray.getJSONObject(0).getString(Constant.ACCOUNT_NUM));
                            session.setData(Constant.HOLDER_NAME,bankArray.getJSONObject(0).getString(Constant.HOLDER_NAME));
                            session.setData(Constant.BANK,bankArray.getJSONObject(0).getString(Constant.BANK));
                            session.setData(Constant.BRANCH,bankArray.getJSONObject(0).getString(Constant.BRANCH));
                            session.setData(Constant.IFSC,bankArray.getJSONObject(0).getString(Constant.IFSC));
                        }

                        if (jsonArray.length() != 0){
                            ArrayList<Transanction> transanctions = new ArrayList<>();
                            Gson g = new Gson();


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                if (jsonObject1 != null) {
                                    Transanction group = g.fromJson(jsonObject1.toString(), Transanction.class);
                                    transanctions.add(group);
                                } else {
                                    break;
                                }
                            }
                            try {

                                tvBalance.setText("Available Balance = ₹"+session.getData(Constant.BALANCE));
                                transactionAdapter = new TransactionAdapter(activity,transanctions);
                                recycler.setAdapter(transactionAdapter);

                            }catch (Exception e){

                            }



                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, String.valueOf(e.getMessage()), Toast.LENGTH_SHORT).show();
                }
            }
        }, activity, Constant.WALLET_URL, params, true);



    }

}