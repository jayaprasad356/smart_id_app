package com.app.fortuneapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fortuneapp.R;
import com.app.fortuneapp.fragment.FindMissingFragment;
import com.app.fortuneapp.fragment.HomeFragment;
import com.app.fortuneapp.fragment.ProfileFragment;
import com.app.fortuneapp.fragment.TicketFragment;
import com.app.fortuneapp.fragment.WalletFragment;
import com.app.fortuneapp.helper.ApiConfig;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.Session;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    public static FragmentManager fm = null;
    private BottomNavigationView navbar;
    Activity activity;
    Session session;
    String NOTIFY_CHAT;
    long fetch_time;
    Dialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getSupportFragmentManager();
        setContentView(R.layout.activity_main);
        navbar = findViewById(R.id.bottomNavigation);
        navbar.setSelectedItemId(R.id.Home);
        activity = MainActivity.this;
        session = new Session(activity);
        NOTIFY_CHAT = getIntent().getStringExtra("NOTIFY_CHAT");
        dialog=new Dialog(activity);
        if (NOTIFY_CHAT != null){
            navbar.setSelectedItemId(R.id.Support);
            fm.beginTransaction().replace(R.id.Container, new TicketFragment()).commit();

        }
//        if (session.getBoolean(Constant.RUN_API)){
//            fm.beginTransaction().replace(R.id.Container, new WalletFragment()).commit();
//
//        }
//        else {
//            fm.beginTransaction().replace(R.id.Container, new HomeFragment()).commit();
//
//        }
        else {
            if (session.getData(Constant.WORK_ACTIVITY).equals("Find Missing")){
                fm.beginTransaction().replace(R.id.Container, new FindMissingFragment()).commit();



            }else {
                fm.beginTransaction().replace(R.id.Container, new HomeFragment()).commit();


            }


        }
        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Profile:
                        fm.beginTransaction().replace(R.id.Container, new ProfileFragment()).commit();
                        break;
                    case R.id.Home:
                        fm.beginTransaction().replace(R.id.Container, new HomeFragment()).commit();
                        break;
                    case R.id.Wallet:

                        try {
                            fetch_time = Long.parseLong(session.getData(Constant.FETCH_TIME)) * 1000;


                        }catch (Exception e){
                            fetch_time = 5 * 1000;


                        }
                        showAlertDialog();



                        break;

                    case R.id.Support:
                        fm.beginTransaction().replace(R.id.Container,new TicketFragment()).commitAllowingStateLoss();
                        break;
                }
                return true;
            }
        });
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            session.setData(Constant.FCM_ID, token);
        });

    }
    @Override
    protected void onStop() {
        super.onStop();
        walletApi();
    }
    boolean doubleBackToExitPressedOnce = false;

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            onStop();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    public void walletApi() {
        if (ApiConfig.isConnected(activity)){
            Map<String, String> params = new HashMap<>();
            params.put(Constant.USER_ID,session.getData(Constant.USER_ID));
            params.put(Constant.CODES,"0");
            ApiConfig.RequestToVolley((result, response) -> {
                Log.d("WALLET_API",response);
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean(Constant.SUCCESS)) {
                            session.setInt(Constant.TODAY_CODES,Integer.parseInt(jsonObject.getString(Constant.TODAY_CODES)));
                            session.setInt(Constant.TOTAL_CODES,Integer.parseInt(jsonObject.getString(Constant.TOTAL_CODES)));
                            session.setData(Constant.BALANCE, jsonObject.getString(Constant.BALANCE));
                            session.setData(Constant.CODE_GENERATE, jsonObject.getString(Constant.CODE_GENERATE));
                            session.setData(Constant.STATUS, jsonObject.getString(Constant.STATUS));
                            finishAffinity();
                        }else {

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, activity, Constant.WALLET_URL, params, true);

        }

    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        WalletFragment fragm = new WalletFragment();
//        fragm.walletApi(session,activity);
//    }

    private void showAlertDialog() {

        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.wallet_dialog);
        EditText edMobile = dialog.findViewById(R.id.edMobile);
        TextView tvViewWallet = dialog.findViewById(R.id.tvViewWallet);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        tvViewWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edMobile.getText().toString().equals(session.getData(Constant.MOBILE))){
                    edMobile.getText().clear();
                    Toast.makeText(activity, "Invalid Mobile number", Toast.LENGTH_SHORT).show();

                }else {
                    dialog.dismiss();
                    ProgressDialog progressDialog = new ProgressDialog(activity);
                    progressDialog.setTitle("Please wait...");
                    progressDialog.setMessage("Fetching Transactions");
                    progressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            progressDialog.dismiss();
                            fm.beginTransaction().replace(R.id.Container, new WalletFragment()).commitAllowingStateLoss();


                        }
                    },fetch_time);

                }
            }
        });

    }
}