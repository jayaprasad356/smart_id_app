package com.app.smart_id_maker.activities;

import static com.app.smart_id_maker.helper.Constant.REFERRED_BY;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.app.smart_id_maker.R;
import com.app.smart_id_maker.fragment.HomeFragment;
import com.app.smart_id_maker.fragment.ExtraIncomeFragment;
import com.app.smart_id_maker.fragment.NewProfileFragment;
import com.app.smart_id_maker.fragment.TeamFragment;
import com.app.smart_id_maker.fragment.WalletFragment;
import com.app.smart_id_maker.helper.ApiConfig;
import com.app.smart_id_maker.helper.Constant;
import com.app.smart_id_maker.helper.DatabaseHelper;
import com.app.smart_id_maker.helper.Session;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
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
    Dialog dialog = null;
    long fetch_time;
    DatabaseHelper databaseHelper;
    String RandomId;
    DatabaseReference reference;
    Button btnChatUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getSupportFragmentManager();
        setContentView(R.layout.activity_main);
        navbar = findViewById(R.id.bottomNavigation);
        navbar.setSelectedItemId(R.id.Home);
        activity = MainActivity.this;
        databaseHelper = new DatabaseHelper(activity);
        session = new Session(activity);
        NOTIFY_CHAT = getIntent().getStringExtra("NOTIFY_CHAT");
        if (session.getBoolean(Constant.CHECK_NOTIFICATION)) {
            showReadNotificationPopup();
        }

        fm.beginTransaction().replace(R.id.Container, new HomeFragment()).commit();

        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Profile:
                        if (session.getInt(Constant.CODES) < session.getInt(Constant.SYNC_CODES)) {
                            fm.beginTransaction().replace(R.id.Container, new NewProfileFragment()).commitAllowingStateLoss();
                        } else {
                            Toast.makeText(activity, "Please Sync Codes", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.Team:
                        if (session.getInt(Constant.CODES) < session.getInt(Constant.SYNC_CODES)) {
                            fm.beginTransaction().replace(R.id.Container, new TeamFragment()).commitAllowingStateLoss();
                        } else {
                            Toast.makeText(activity, "Please Sync Codes", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.Home:
                        fm.beginTransaction().replace(R.id.Container, new HomeFragment()).commitAllowingStateLoss();
                        break;
                    case R.id.job:
                        fm.beginTransaction().replace(R.id.Container, new ExtraIncomeFragment()).commitAllowingStateLoss();
                        break;
                }
                return true;
            }
        });

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            session.setData(Constant.FCM_ID, token);
        });

    }
//    private void checkJoining() {
//        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constant.JOINING_TICKET).child(session.getData(Constant.MOBILE));
//        FirebaseDatabase.getInstance()
//                .getReference(Constant.JOINING_TICKET).child(session.getData(Constant.MOBILE)).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            Ticket user = dataSnapshot.getValue(Ticket.class);
//                            Log.d("NOT_EXIST",user.getType() + " - "+session.getData(Constant.MOBILE));
//
//                            sendChat(user.getId(),user.getName(),user.getCategory(),user.getType(),user.getDescription());
//
//                        }
//                        else {
//                            joinTicket();
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//    }

    private void joinTicket() {
        Long tsLong = System.currentTimeMillis()/1000;
        RandomId = session.getData(Constant.USER_ID) +"_"+ tsLong.toString();
        reference = FirebaseDatabase.getInstance().getReference(Constant.JOINING_TICKET).child(session.getData(Constant.MOBILE));
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.ID, RandomId);
        hashMap.put(Constant.CATEGORY, "Joining");
        hashMap.put(Constant.DESCRIPTION, "Enquiry For Joining");
        hashMap.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        hashMap.put(Constant.NAME, session.getData(Constant.NAME));
        hashMap.put(Constant.MOBILE, session.getData(Constant.MOBILE));
        hashMap.put(Constant.TYPE, Constant.JOINING_TICKET);
        hashMap.put(Constant.SUPPORT, "Admin");
        hashMap.put(Constant.REFERRED_BY, session.getData(REFERRED_BY));
        hashMap.put(Constant.TIMESTAMP, tsLong.toString());
        reference.setValue(hashMap).addOnCompleteListener(task1 -> {

//            sendChat(RandomId, session.getData(Constant.NAME), "Joining", Constant.JOINING_TICKET, "Enquiry For Joining");

        });
    }

//    private void sendChat(String id, String name, String category, String type, String description) {
//
//        //Log.d("CHAT_DETAILS","USER_ID - "+id + "\nName - "+name+"\nCategory - "+category+"\nType - "+type +"Description - "+description);
//        final Intent intent = new Intent(activity, MessageActivity.class);
//        intent.putExtra(EXTRA_USER_ID, id);
//        intent.putExtra(TICKET_ID, id);
//        intent.putExtra(NAME, name);
//        intent.putExtra(TYPE, type);
//        intent.putExtra(DESCRIPTION, description);
//        intent.putExtra(CATEGORY, category);
//        startActivity(intent);
//    }

    private void importUrl() {
        Map<String, String> params = new HashMap<>();
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        databaseHelper.deleteUrls();
                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1 != null) {
                                databaseHelper.AddtoUrl(jsonObject1.getString(Constant.ID), jsonObject1.getString(Constant.URL), "0");
                            } else {
                                break;
                            }
                        }


                    } else {


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.IMPORT_URLS, params, false);

    }

    private void exploreApi(String type) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.TYPE, type);
        ApiConfig.RequestToVolley((result, response) -> {


        }, activity, Constant.EXPLORE_URL, params, false);


    }

    // Do something with the data
    // coming from the AlertDialog
    private void sendDialogDataToActivity(String data) {
        Toast.makeText(this,
                        data,
                        Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    boolean doubleBackToExitPressedOnce = false;

//    @Override
//    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            onStop();
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = false;
//            }
//        }, 2000);
//    }

    public void walletApi() {
        finishAffinity();

    }

    @Override
    protected void onStop() {
        super.onStop();
        walletApi();
    }


    private void showWallet() {


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
        }, fetch_time);


    }

    public void showReadNotificationPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("New Notification Available.")
                .setCancelable(false)
                .setPositiveButton("Click here to read", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        Intent intent = new Intent(activity, NotificaionActivity.class);
//                        startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}