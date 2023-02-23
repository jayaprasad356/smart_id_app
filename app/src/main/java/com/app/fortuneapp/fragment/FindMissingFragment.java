package com.app.fortuneapp.fragment;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.app.fortuneapp.chat.constants.IConstants.CATEGORY;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_USER_ID;
import static com.app.fortuneapp.chat.constants.IConstants.NAME;
import static com.app.fortuneapp.chat.constants.IConstants.TICKET_ID;
import static com.app.fortuneapp.chat.constants.IConstants.TYPE;
import static com.app.fortuneapp.helper.Constant.AD_AVAILABLE;
import static com.app.fortuneapp.helper.Constant.AD_STATUS;
import static com.app.fortuneapp.helper.Constant.AD_TYPE;
import static com.app.fortuneapp.helper.Constant.CHAMPION_CODES;
import static com.app.fortuneapp.helper.Constant.DESCRIPTION;
import static com.app.fortuneapp.helper.Constant.SUCCESS;
import static com.app.fortuneapp.helper.Constant.TASK_TYPE;
import static com.app.fortuneapp.helper.Constant.getHistoryDays;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fortuneapp.Adapter.CitiesAdapter;
import com.app.fortuneapp.Adapter.PincodeAdapter;
import com.app.fortuneapp.R;
import com.app.fortuneapp.activities.LoadWebView2Activity;
import com.app.fortuneapp.activities.LoginActivity;
import com.app.fortuneapp.activities.MainActivity;
import com.app.fortuneapp.activities.SplashActivity;
import com.app.fortuneapp.chat.MessageActivity;
import com.app.fortuneapp.chat.models.Ticket;
import com.app.fortuneapp.helper.ApiConfig;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.DatabaseHelper;
import com.app.fortuneapp.helper.Session;
import com.app.fortuneapp.model.GenerateCodes;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class FindMissingFragment extends Fragment {

    Button btnCities, btnpincode, btnVerfifyPincode, btnVerfifyCity, btnGenerate;
    LinearLayout llCityLocation, llPincode;
    EditText edCity, edPincode;
    Dialog dialog, championDialog;
    CitiesAdapter citiesAdapter;
    PincodeAdapter pincodeAdapter;
    Activity activity;
    DatabaseHelper databaseHelper;
    RecyclerView rvCityData, rvPincodeData;
    EditText edName;
    TextView tvCodes;
    EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six, otp_textbox_seven, otp_textbox_eight, otp_textbox_nine, otp_textbox_ten;
    String id, c_City, p_Pincode;
    Session session;
    CircularProgressIndicator cbCodes;


    ArrayList<GenerateCodes> generateCodes = new ArrayList<GenerateCodes>();
    Button btnCreateCode, btnsyncNow;

    TextView tvTodayCodes, tvTotalCodes, tvHistorydays, tvInfo, tvTrialPeriod;
    TextView tvBalance;
    ProgressDialog progressDialog;
    LinearLayout lSurvey, lTrial;
    Button btnDemoVideo;
    long code_generate_time = 0;
    Handler handler;
    ScrollView frame;
    View root;
    private String AdId = "";
    int search_count = 25;
    long st_timestamp;
    String RandomId;
    DatabaseReference reference;

    public FindMissingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_find_missing, container, false);
        activity = getActivity();
        session = new Session(activity);
        if (session.getData(Constant.SECURITY).equals("1")) {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
            );
        }
        progressDialog = new ProgressDialog(activity);
        databaseHelper = new DatabaseHelper(activity);
        handler = new Handler();
       // MobileAds.initialize(activity);
        loadRewardedVideoAd();
        AdId = session.getData(Constant.AD_REWARD_ID);
        frame = root.findViewById(R.id.frame);

        //checkStatus();

        btnCities = root.findViewById(R.id.btnCities);
        btnDemoVideo = root.findViewById(R.id.btnDemoVideo);
        btnpincode = root.findViewById(R.id.btnpincode);
        llPincode = root.findViewById(R.id.llPincode);
        lTrial = root.findViewById(R.id.lTrial);
        llCityLocation = root.findViewById(R.id.llCityLocation);
        btnVerfifyCity = root.findViewById(R.id.btnVerfifyCity);
        btnVerfifyPincode = root.findViewById(R.id.btnVerfifyPincode);
        edName = root.findViewById(R.id.edName);
        edCity = root.findViewById(R.id.edCity);
        edPincode = root.findViewById(R.id.edPincode);
        btnGenerate = root.findViewById(R.id.btnGenerate);
        tvBalance = root.findViewById(R.id.tvBalance);
        btnCreateCode = root.findViewById(R.id.btnCreateCode);
        tvTodayCodes = root.findViewById(R.id.tvTodayCodes);
        tvTotalCodes = root.findViewById(R.id.tvTotalCodes);
        tvHistorydays = root.findViewById(R.id.tvHistorydays);
        tvCodes = root.findViewById(R.id.tvCodes);
        tvCodes.setText(session.getInt(Constant.CODES) + "");
        btnsyncNow = root.findViewById(R.id.btnsyncNow);
        cbCodes = root.findViewById(R.id.cbCodes);
        lSurvey = root.findViewById(R.id.lSurvey);
        tvTrialPeriod = root.findViewById(R.id.tvTrialPeriod);


        otp_textbox_one = root.findViewById(R.id.otp_edit_box1);
        otp_textbox_two = root.findViewById(R.id.otp_edit_box2);
        otp_textbox_three = root.findViewById(R.id.otp_edit_box3);
        otp_textbox_four = root.findViewById(R.id.otp_edit_box4);
        otp_textbox_five = root.findViewById(R.id.otp_edit_box5);
        otp_textbox_six = root.findViewById(R.id.otp_edit_box6);
        otp_textbox_seven = root.findViewById(R.id.otp_edit_box7);
        otp_textbox_eight = root.findViewById(R.id.otp_edit_box8);
        otp_textbox_nine = root.findViewById(R.id.otp_edit_box9);
        otp_textbox_ten = root.findViewById(R.id.otp_edit_box10);
        tvInfo = root.findViewById(R.id.tvInfo);

        btnDemoVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("" + session.getData(Constant.CHAMPION_DEMO_LINK)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        try {
            code_generate_time = Long.parseLong(session.getData(Constant.CODE_GENERATE_TIME)) * 1000;


        } catch (Exception e) {
            code_generate_time = 3 * 1000;


        }

        dialog = new Dialog(activity);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.customdia2);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();
        GotoActivity();

        Animation blink = new AlphaAnimation(0.0f, 1.0f);
        blink.setDuration(500);
        blink.setInterpolator(new LinearInterpolator());
        blink.setRepeatCount(Animation.INFINITE);
        blink.setRepeatMode(Animation.REVERSE);
        tvInfo.startAnimation(blink);

        tvTrialPeriod.setText("Trial Period " + session.getData(Constant.TRIAL_COUNT) + "/10");


        tvInfo.setText("Champions Earn " + session.getData(Constant.CHAMPION_CODES) + " codes \nfor Generating 1 QR Code = " + (Double.parseDouble(session.getData(Constant.CHAMPION_CODES)) * 0.17) + " Paise.");

        if (session.getData(Constant.TASK_TYPE).equals(Constant.CHAMPION)) {
            lTrial.setVisibility(View.GONE);
            lSurvey.setVisibility(View.VISIBLE);
            search_count = Integer.parseInt(session.getData(Constant.CHAMPION_SEARCH_COUNT));
        } else {
            lTrial.setVisibility(View.VISIBLE);
            lSurvey.setVisibility(View.GONE);
        }
        btnsyncNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!session.getBoolean(AD_AVAILABLE)) {
                    btnsyncNow.setBackground(ContextCompat.getDrawable(activity, R.drawable.syncbg_disabled));
                    btnsyncNow.setEnabled(false);
                    walletApi();

                } else {
                    Toast.makeText(activity, "Please watch ad and claim bonus then sync codes", Toast.LENGTH_SHORT).show();
                }

            }
        });
        generateCodes = databaseHelper.getMissingCodes(search_count);
        final int min = 0;
        final int max = search_count - 1;
        final int random = new Random().nextInt((max - min) + 1) + min;
        edName.setText(generateCodes.get(random).getStudent_name());
        id = generateCodes.get(random).getId();
        c_City = generateCodes.get(random).getEcity();
        p_Pincode = generateCodes.get(random).getPin_code();
        setIdValue(generateCodes.get(random).getId_number());
        setCodeValue();
        btnCreateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setData(Constant.WORK_ACTIVITY, "Create Code");
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Container, new HomeFragment()).commit();
            }
        });
        //adCheckApi();


        btnCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new Dialog(activity);
                dialog.setContentView(R.layout.cities_dialog_layout);
                rvCityData = dialog.findViewById(R.id.rvCityData);
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
                rvCityData.setLayoutManager(linearLayoutManager);

                citylist();
                dialog.show();


//                Intent intent = new Intent(activity,CitiesActivity.class);
//                startActivity(intent);
            }
        });
        btnpincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(activity);
                dialog.setContentView(R.layout.pincode_dialog_layout);
                rvPincodeData = dialog.findViewById(R.id.rvPincodeData);
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
                rvPincodeData.setLayoutManager(linearLayoutManager);

                pincodelist();
                dialog.show();
//                Intent intent = new Intent(activity,PincodeActivity.class);
//                startActivity(intent);
            }
        });

        btnVerfifyCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edCity.getText().toString().trim().equals("")) {
                    if (c_City.equals(edCity.getText().toString().trim())) {
                        Toast.makeText(activity, "Verify Successfully", Toast.LENGTH_SHORT).show();

                        llPincode.setVisibility(View.VISIBLE);
                        btnpincode.setVisibility(View.VISIBLE);
                        btnCities.setVisibility(View.GONE);
                        btnVerfifyCity.setVisibility(View.GONE);
                        edCity.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(activity, R.drawable.ic_baseline_check_24), null);
                        edCity.setEnabled(false);

                    } else {
                        Toast.makeText(activity, "City Wrong", Toast.LENGTH_SHORT).show();
                        btnCities.setVisibility(View.VISIBLE);
                        btnVerfifyCity.setVisibility(View.GONE);


                    }

                }

            }
        });
        btnVerfifyPincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edPincode.getText().toString().trim().equals("")) {
                    if (p_Pincode.equals(edPincode.getText().toString().trim())) {
                        Toast.makeText(activity, "Verify Successfully", Toast.LENGTH_SHORT).show();
                        btnpincode.setVisibility(View.GONE);
                        btnCities.setVisibility(View.GONE);
                        btnVerfifyPincode.setVisibility(View.GONE);
                        edPincode.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(activity, R.drawable.ic_baseline_check_24), null);
                        btnGenerate.setVisibility(View.VISIBLE);
                        edPincode.setEnabled(false);

                    } else {
                        Toast.makeText(activity, "Pincode Wrong", Toast.LENGTH_SHORT).show();
                        btnpincode.setVisibility(View.VISIBLE);
                        btnVerfifyPincode.setVisibility(View.GONE);


                    }

                }

            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long ed_timestamp = System.currentTimeMillis();

                long difference = ed_timestamp - st_timestamp;
                long seconds = difference / 1000;
                int speed_time = Integer.parseInt(session.getData(Constant.MCG_TIMER)) - (int) seconds;
                int positiveValue = Math.max(speed_time, 0);
                if (ApiConfig.isConnected(activity)) {
                    if (btnsyncNow.isEnabled()) {
                        Toast.makeText(activity, "Please Sync Your Codes", Toast.LENGTH_SHORT).show();
                    } else {
                        if (session.getData(Constant.TASK_TYPE).equals("champion")) {
                            if (ApiConfig.isConnected(activity)) {
                                if (session.getData(Constant.CODE_GENERATE).equals("1")) {
                                    session.setInt(Constant.CODES, session.getInt(Constant.CODES) + Integer.parseInt(session.getData(CHAMPION_CODES)));
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(Constant.MCG_TIMER, positiveValue);
                                    bundle.putString(Constant.TASK_TYPE, TASK_TYPE);
                                    Fragment fragment = new GenrateQRFragment();
                                    fragment.setArguments(bundle);
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.Container, fragment);
                                    ft.commit();

                                } else {
                                    Toast.makeText(activity, "You are Restricted for Generating Code", Toast.LENGTH_SHORT).show();
                                }

                            }

                        } else {
                            registerGeneration();

                        }


                    }
                } else {
                    Toast.makeText(activity, "You are Restricted for Generating Code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;


    }

    private void checkStatus() {
        if (session.getData(Constant.STATUS).equals("0")) {
            int i = session.getInt(Constant.CHAMPION_TRIAL_COUNT);
            if (i == 0) {
                session.setInt(Constant.CHAMPION_TRIAL_COUNT, 1);
            } else {
                if (i > 10) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("Congratulations, You have completed your trail codes. Pay Rs 3000 and start your 6 ways earnings ( 100% refunded on daily basis) . Chat with us and get your account activated.")
                            .setCancelable(false)
                            .setPositiveButton("Chat Now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    checkJoining();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    int temp = session.getInt(Constant.CHAMPION_TRIAL_COUNT) + 1;
                    session.setInt(Constant.CHAMPION_TRIAL_COUNT, temp);
                }
            }
        }
    }

    // creating RewardedVideoAd object
    private RewardedVideoAd AdMobrewardedVideoAd;

    // AdMob Rewarded Video Ad Id


    void loadRewardedVideoAd() {

        AdMobrewardedVideoAd
                = MobileAds.getRewardedVideoAdInstance(activity);
        AdMobrewardedVideoAd.setRewardedVideoAdListener(
                new RewardedVideoAdListener() {
                    @Override
                    public void onRewardedVideoAdLoaded() {
                        Log.d("REWARDED", "onRewardedVideoAdLoaded");
                    }

                    @Override
                    public void onRewardedVideoAdOpened() {
                        Log.d("REWARDED", "onRewardedVideoAdOpened");
                    }

                    @Override
                    public void onRewardedVideoStarted() {
                        Log.d("REWARDED", "onRewardedVideoStarted");
                    }

                    @Override
                    public void onRewardedVideoAdClosed() {
                        Log.d("REWARDED", "onRewardedVideoAdClosed");
                    }

                    @Override
                    public void onRewarded(
                            RewardItem rewardItem) {
                        addRewardCode();

                        Log.d("REWARDED", "onRewarded");

                    }

                    @Override
                    public void
                    onRewardedVideoAdLeftApplication() {
                        Log.d("REWARDED", "onRewardedVideoAdLeftApplication");

                    }

                    @Override
                    public void onRewardedVideoAdFailedToLoad(
                            int i) {
                        Log.d("REWARDED", "onRewardedVideoAdFailedToLoad");

                    }

                    @Override
                    public void onRewardedVideoCompleted() {
                        Log.d("REWARDED", "onRewardedVideoCompleted");

                    }
                });

        // Loading Rewarded Video Ad
        AdMobrewardedVideoAd.loadAd(
                AdId, new AdRequest.Builder().build());
    }

    @SuppressLint("ShowToast")
    private void addRewardCode() {
        int codereward = Integer.parseInt(session.getData(Constant.REWARD));
        session.setInt(Constant.CODES, session.getInt(Constant.CODES) + codereward);
        Snackbar snackbar = Snackbar.make(root, "Code Rewarded Successfully", 5000);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(activity, R.color.green_500));
        View view = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.CENTER;
        root.setLayoutParams(params);
        snackbar.show();
        setCodeValue();
        dialog.cancel();
    }


    private void GotoActivity() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                st_timestamp = System.currentTimeMillis();
                frame.setVisibility(View.VISIBLE);
                dialog.cancel();
                if (session.getData(AD_STATUS).equals("1") && session.getData(AD_TYPE).equals("1")) {
                    SimpleDateFormat df = new SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.getDefault());
                    Date c = Calendar.getInstance().getTime();
                    String currentDate = df.format(c);
                    if (!session.getBoolean(Constant.LAST_UPDATED_DATE_STATUS_AD)) {
                        session.setData(Constant.LAST_UPDATED_DATE_AD, currentDate);
                        session.setBoolean(Constant.LAST_UPDATED_DATE_STATUS_AD, true);

                    }
                    Date date1 = null;
                    try {
                        date1 = df.parse(currentDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Date date2 = null;
                    try {
                        date2 = df.parse(session.getData(Constant.LAST_UPDATED_DATE_AD));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long different = date1.getTime() - date2.getTime();
                    long secondsInMilli = 1000;
                    long minutesInMilli = secondsInMilli * 60;
                    long hoursInMilli = minutesInMilli * 60;
                    long elapsedHours = different / hoursInMilli;
                    long elapsedMinutue = different / minutesInMilli;

                    if (elapsedMinutue >= Long.parseLong(session.getData(Constant.AD_SHOW_TIME))) {
                        session.setBoolean(Constant.LAST_UPDATED_DATE_STATUS_AD, false);
                        showAdDialog();


                    }


                }


            }
        }, code_generate_time);
    }

    private void showAdDialog() {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.ad_code_layout);
        dialog.setCancelable(false);
        ImageView imgClose = dialog.findViewById(R.id.imgClose);
        Button btnReward = dialog.findViewById(R.id.btnReward);
        btnReward.setText("Click to Get " + session.getData(Constant.REWARD) + " Codes Free");
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (session.getData(AD_TYPE).equals("1")) {
                    showRewardedVideoAd();

                }

            }
        });


    }

    public void showRewardedVideoAd() {
        if (AdMobrewardedVideoAd.isLoaded()) {
            AdMobrewardedVideoAd.show();
        } else {
            AdMobrewardedVideoAd.loadAd(
                    AdId, new AdRequest.Builder().build());
        }
    }


    private void registerGeneration() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        session.setData(Constant.TRIAL_COUNT, jsonObject.getString(Constant.TRIAL_COUNT));
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.TASK_TYPE, Constant.CHAMPION);
                        bundle.putInt(Constant.MCG_TIMER, 0);
                        Fragment fragment = new GenrateQRFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.Container, fragment);
                        ft.commit();
                    } else {
                        if (session.getData(Constant.STATUS).equals("0")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage("Congratulations, You have completed your trail codes. Pay Rs 3000 and start your 6 ways earnings ( 100% refunded on daily basis) . Chat with us and get your account activated.")
                                    .setCancelable(false)
                                    .setPositiveButton("Chat Now", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            checkJoining();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            session.setData(Constant.TRIAL_EXPIRED, "1");
                            session.setData(Constant.TRIAL_COUNT, jsonObject.getString(Constant.TRIAL_COUNT));
                            championDialog = new Dialog(activity);
                            championDialog.setContentView(R.layout.more_champion_lyt);
                            CheckBox checkAgreed = championDialog.findViewById(R.id.checkAgreed);
                            ImageView imgClose = championDialog.findViewById(R.id.imgClose);
                            imgClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    championDialog.dismiss();
                                }
                            });
                            championDialog.findViewById(R.id.btn_switch_now).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (checkAgreed.isChecked()) {
                                        championDialog.dismiss();
                                        switchNowApiCall();

                                    } else {
                                        Toast.makeText(activity, "Please Tick the Check box", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            Window windows = championDialog.getWindow();
                            windows.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            championDialog.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }, activity, Constant.CHAMPION_TASK_TRIAL, params, true);
    }
    private void checkJoining() {
        reference = FirebaseDatabase.getInstance().getReference(Constant.JOINING_TICKET).child(session.getData(Constant.MOBILE));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    joinTicket();
                }
                else {

                    Ticket user = dataSnapshot.getValue(Ticket.class);

                    sendChat(user.getId(),user.getName(),user.getCategory(),user.getType(),user.getDescription());


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


    private void switchNowApiCall() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        session.setData(Constant.TASK_TYPE, "champion");
                        Intent intent = new Intent(getActivity(), SplashActivity.class);
                        startActivity(intent);
                        championDialog.cancel();


                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }, activity, Constant.SWITCH_CHAMPIONTASK, params, true);
    }

    private void pincodelist() {
        Collections.shuffle(generateCodes);

        pincodeAdapter = new PincodeAdapter(activity, generateCodes, dialog, FindMissingFragment.this);
        rvPincodeData.setAdapter(pincodeAdapter);


    }

    public void walletApi() {
        if (ApiConfig.isConnected(activity)) {
            progressDialog.setTitle("Codes are Syncing");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            }, 2000);
            if (session.getInt(Constant.CODES) != 0) {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
                params.put(Constant.CODES, session.getInt(Constant.CODES) + "");
                ApiConfig.RequestToVolley((result, response) -> {
                    Log.d("WALLET_RES", response);
                    if (result) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean(Constant.SUCCESS)) {
                                session.setInt(Constant.CODES, 0);
                                session.setInt(Constant.TODAY_CODES, Integer.parseInt(jsonObject.getString(Constant.TODAY_CODES)));
                                session.setInt(Constant.TOTAL_CODES, Integer.parseInt(jsonObject.getString(Constant.TOTAL_CODES)));
                                session.setData(Constant.BALANCE, jsonObject.getString(Constant.BALANCE));
                                session.setData(Constant.STATUS, jsonObject.getString(Constant.STATUS));
                                setCodeValue();


                            } else {
                                btnsyncNow.setBackground(ContextCompat.getDrawable(activity, R.drawable.syncbg));
                                btnsyncNow.setEnabled(true);
                                Toast.makeText(activity, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, activity, Constant.WALLET_URL, params, false);


            }

        } else {
            btnsyncNow.setBackground(ContextCompat.getDrawable(activity, R.drawable.syncbg));
            btnsyncNow.setEnabled(true);
        }
    }

    private void setCodeValue() {
        if (session.getInt(Constant.CODES) >= session.getInt(Constant.SYNC_CODES)) {
            btnsyncNow.setBackground(ContextCompat.getDrawable(activity, R.drawable.syncbg));
            btnsyncNow.setEnabled(true);

        } else {
            btnsyncNow.setBackground(ContextCompat.getDrawable(activity, R.drawable.syncbg_disabled));
            btnsyncNow.setEnabled(false);

        }
        tvCodes.setText(session.getInt(Constant.CODES) + "");
        cbCodes.setProgress(session.getInt(Constant.CODES));
        cbCodes.setMax(session.getInt(Constant.SYNC_CODES));
        try {
            tvTodayCodes.setText(session.getInt(Constant.TODAY_CODES) + " + " + session.getInt(Constant.CODES));
            tvTotalCodes.setText(session.getInt(Constant.TOTAL_CODES) + " + " + session.getInt(Constant.CODES));
            double current_bal = (double) (session.getInt(Constant.CODES) * 0.17);
            tvBalance.setText(session.getData(Constant.BALANCE) + " + " + String.format("%.2f", current_bal) + "");
        } catch (Exception e) {
        }
        tvHistorydays.setText(getHistoryDays(session.getData(Constant.JOINED_DATE)));

    }

    private void adCheckApi() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(SUCCESS)) {
                        session.setBoolean(AD_AVAILABLE, true);

                    } else {
                        session.setBoolean(AD_AVAILABLE, false);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.URLS_LIST_URL, params, true);

    }


    private void setIdValue(String id_number) {
        String id1, id2, id3, id4, id5, id6, id7, id8, id9, id10;
        id1 = id_number.substring(0, 1);
        id2 = id_number.substring(1, 2);
        id3 = id_number.substring(2, 3);
        id4 = id_number.substring(3, 4);
        id5 = id_number.substring(4, 5);
        id6 = id_number.substring(5, 6);
        id7 = id_number.substring(6, 7);
        id8 = id_number.substring(7, 8);
        id9 = id_number.substring(8, 9);
        id10 = id_number.substring(9, 10);
        otp_textbox_one.setText(id1);
        otp_textbox_two.setText(id2);
        otp_textbox_three.setText(id3);
        otp_textbox_four.setText(id4);
        otp_textbox_five.setText(id5);
        otp_textbox_six.setText(id6);
        otp_textbox_seven.setText(id7);
        otp_textbox_eight.setText(id8);
        otp_textbox_nine.setText(id9);
        otp_textbox_ten.setText(id10);

    }

    public void setCityValue(String ecity) {
        edCity.setText(ecity);
        btnVerfifyCity.setVisibility(View.VISIBLE);
        btnCities.setVisibility(View.GONE);
    }

    public void setPincodeValue(String pincode) {
        edPincode.setText(pincode);
        btnVerfifyPincode.setVisibility(View.VISIBLE);
        btnpincode.setVisibility(View.GONE);
    }


    private void citylist() {
        Collections.shuffle(generateCodes);
        citiesAdapter = new CitiesAdapter(activity, generateCodes, dialog, FindMissingFragment.this);
        rvCityData.setAdapter(citiesAdapter);


    }
}