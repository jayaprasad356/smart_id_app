package com.app.fortuneapp.activities;

import static com.app.fortuneapp.chat.constants.IConstants.CATEGORY;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_USER_ID;
import static com.app.fortuneapp.chat.constants.IConstants.NAME;
import static com.app.fortuneapp.chat.constants.IConstants.TICKET_ID;
import static com.app.fortuneapp.chat.constants.IConstants.TYPE;
import static com.app.fortuneapp.helper.Constant.DESCRIPTION;
import static com.app.fortuneapp.helper.Constant.REFERRED_BY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.fortuneapp.R;
import com.app.fortuneapp.chat.MessageActivity;
import com.app.fortuneapp.chat.TicketFragment;
import com.app.fortuneapp.chat.models.Ticket;
import com.app.fortuneapp.fragment.FaqFragment;
import com.app.fortuneapp.fragment.FindMissingFragment;
import com.app.fortuneapp.fragment.HomeFragment;
import com.app.fortuneapp.fragment.InFoFragment;
import com.app.fortuneapp.fragment.ProfileFragment;
import com.app.fortuneapp.fragment.WalletFragment;
import com.app.fortuneapp.helper.ApiConfig;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.DatabaseHelper;
import com.app.fortuneapp.helper.Session;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

        //importUrl();


        if (NOTIFY_CHAT != null) {
            if (NOTIFY_CHAT.equals("join_chat")) {
                checkJoining();
            }else {
                navbar.setSelectedItemId(R.id.Support);
                fm.beginTransaction().replace(R.id.Container, new TicketFragment()).commit();


            }


        } else {
            if (session.getData(Constant.STATUS).equals("0")) {
                fm.beginTransaction().replace(R.id.Container, new InFoFragment()).commitAllowingStateLoss();
            } else {
                if (session.getData(Constant.TASK_TYPE).equals("champion")) {
                    fm.beginTransaction().replace(R.id.Container, new FindMissingFragment()).commit();
                } else {
                    fm.beginTransaction().replace(R.id.Container, new HomeFragment()).commit();
                }
            }

        }

        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Profile:
                        fm.beginTransaction().replace(R.id.Container, new ProfileFragment()).commitAllowingStateLoss();
                        break;
                    case R.id.Home:
                        if (session.getData(Constant.STATUS).equals("0")) {
                            fm.beginTransaction().replace(R.id.Container, new InFoFragment()).commitAllowingStateLoss();
                        } else {
                            if (session.getData(Constant.TASK_TYPE).equals("champion"))
                                fm.beginTransaction().replace(R.id.Container, new FindMissingFragment()).commitAllowingStateLoss();
                            else
                                fm.beginTransaction().replace(R.id.Container, new HomeFragment()).commitAllowingStateLoss();
                        }
                        break;
                    case R.id.Wallet:
                        try {
                            fetch_time = Long.parseLong(session.getData(Constant.FETCH_TIME)) * 1000;
                        } catch (Exception e) {
                            fetch_time = 5 * 1000;


                        }
                        showWallet();

                        break;

                    case R.id.Support:
                        if (session.getData(Constant.STATUS).equals("0")) {
                            checkJoining();
                        } else {
                            fm.beginTransaction().replace(R.id.Container, new FaqFragment()).commitAllowingStateLoss();
                        }
                        break;
                }
                return true;
            }
        });
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            session.setData(Constant.FCM_ID, token);
        });

    }
    private void checkJoining() {
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constant.JOINING_TICKET).child(session.getData(Constant.MOBILE));
        FirebaseDatabase.getInstance()
                .getReference(Constant.JOINING_TICKET).child(session.getData(Constant.MOBILE)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Ticket user = dataSnapshot.getValue(Ticket.class);
                            Log.d("NOT_EXIST",user.getType() + " - "+session.getData(Constant.MOBILE));

                            sendChat(user.getId(),user.getName(),user.getCategory(),user.getType(),user.getDescription());

                        }
                        else {
                            joinTicket();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

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

            sendChat(RandomId, session.getData(Constant.NAME), "Joining", Constant.JOINING_TICKET, "Enquiry For Joining");

        });
    }

    private void sendChat(String id, String name, String category, String type, String description) {

        //Log.d("CHAT_DETAILS","USER_ID - "+id + "\nName - "+name+"\nCategory - "+category+"\nType - "+type +"Description - "+description);
        final Intent intent = new Intent(activity, MessageActivity.class);
        intent.putExtra(EXTRA_USER_ID, id);
        intent.putExtra(TICKET_ID, id);
        intent.putExtra(NAME, name);
        intent.putExtra(TYPE, type);
        intent.putExtra(DESCRIPTION, description);
        intent.putExtra(CATEGORY, category);
        startActivity(intent);
    }

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

    private void showAlertDialogExplore() {


        // Create an alert builder
        AlertDialog.Builder builder
                = new AlertDialog.Builder(this);
        builder.setTitle("Explore");

// set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.explore_dialog,
                        null);
        builder.setView(customLayout);

// add a button
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(
                    DialogInterface dialog,
                    int which) {

// send data from the
//// AlertDialog to the Activity
            }
        });


        CardView cvSharechat = customLayout.findViewById(R.id.cvSharechat);
        CardView cvShorts = customLayout.findViewById(R.id.cvShorts);
        CardView cvGames = customLayout.findViewById(R.id.cvGames);
        AlertDialog dialog = builder.create();


        cvSharechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                exploreApi("share_chat");
                Intent intent = new Intent(activity, LoadWebViewActivity.class);
                intent.putExtra(Constant.URL, "https://sharechat.com/");
                startActivity(intent);

            }
        });
        cvShorts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                exploreApi("shorts");
                Intent intent = new Intent(activity, LoadWebViewActivity.class);
                intent.putExtra(Constant.URL, "https://www.youtube.com/hashtag/shorts/shorts");
                startActivity(intent);

            }
        });
        cvGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                exploreApi("games");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://poki.com/"));
                startActivity(browserIntent);

            }
        });

// create and show
// the alert dialog

        dialog.show();
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            onStop();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

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
                        Intent intent = new Intent(activity, NotificaionActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}