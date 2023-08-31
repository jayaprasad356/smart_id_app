package com.app.fortuneapp.fragment;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.app.fortuneapp.chat.constants.IConstants.CATEGORY;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_USER_ID;
import static com.app.fortuneapp.chat.constants.IConstants.NAME;
import static com.app.fortuneapp.chat.constants.IConstants.TICKET_ID;
import static com.app.fortuneapp.chat.constants.IConstants.TYPE;
import static com.app.fortuneapp.helper.Constant.AD_AVAILABLE;
import static com.app.fortuneapp.helper.Constant.AD_STATUS;
import static com.app.fortuneapp.helper.Constant.AD_TYPE;
import static com.app.fortuneapp.helper.Constant.CHAMPION_TASK;
import static com.app.fortuneapp.helper.Constant.DESCRIPTION;
import static com.app.fortuneapp.helper.Constant.JOINING_TICKET;
import static com.app.fortuneapp.helper.Constant.MOBILE;
import static com.app.fortuneapp.helper.Constant.PER_CODE_VAL;
import static com.app.fortuneapp.helper.Constant.SUCCESS;
import static com.app.fortuneapp.helper.Constant.getHistoryDays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fortuneapp.activities.LoadWebView2Activity;
import com.app.fortuneapp.activities.NotificaionActivity;
import com.app.fortuneapp.chat.MessageActivity;
import com.app.fortuneapp.chat.managers.Utils;
import com.app.fortuneapp.chat.models.Ticket;
import com.app.fortuneapp.helper.ApiConfig;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.DatabaseHelper;
import com.app.fortuneapp.helper.Session;
import com.app.fortuneapp.java.GenericTextWatcher;
import com.app.fortuneapp.R;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class HomeFragment extends Fragment {

    TextView tvName, tvPincode, tvCity, tvId, tvTodayCodes, tvTotalCodes, tvLevels, tvHistorydays, tvTrialPeriod;
    LinearLayout lTrial;
    EditText edName, edPincode, edCity;
    Button btnGenerate, btnsyncNow;


    EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six, otp_textbox_seven, otp_textbox_eight, otp_textbox_nine, otp_textbox_ten;
    DatabaseHelper databaseHelper;
    ArrayList<GenerateCodes> generateCodes = new ArrayList<GenerateCodes>();
    Session session;
    Activity activity;
    ScrollView frame;

    String Idnumber = "";

    Handler handler;
    long code_generate_time = 0;
    public static Dialog dialog = null;
    Button btnFindMissing, btnChampiontask, btnNavChampionTask;
    TextView tvBalance;
    View root;
    LinearLayout championLayout;
    private String AdId = "";
    TextView tvCodes;
    CircularProgressIndicator cbCodes;
    TextView tvHightlight, tvInfo;
    ProgressDialog progressDialog;
    long st_timestamp;
    ClipboardManager clipBoard;
    LinearLayout lltrail, llPayed;
    String RandomId;
    DatabaseReference reference;
    TextView tvChampionTask;

    int totaltype = 0,totaltext = 0;
    int namecount = 0,idcount = 0,citycount = 0 , pincodecount = 0;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false);

        activity = getActivity();
        session = new Session(activity);
        if (session.getData(Constant.SECURITY).equals("1")) {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
            );
        }
        progressDialog = new ProgressDialog(activity);

        //session.setInt(Constant.CODES,60);

        clipBoard = (ClipboardManager) getActivity().getApplicationContext().getSystemService(CLIPBOARD_SERVICE);

        AdId = session.getData(Constant.AD_REWARD_ID);
        databaseHelper = new DatabaseHelper(getActivity());
        MobileAds.initialize(activity);
        loadRewardedVideoAd();
        championLayout = root.findViewById(R.id.ll_champion);
        if (session.getData(Constant.CHAMPION_TASK_ELIGIBLE).equals("1") && session.getData(CHAMPION_TASK).equals("1")) {
            championLayout.setVisibility(View.VISIBLE);
        }
        handler = new Handler();
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


        tvBalance = root.findViewById(R.id.tvBalance);
        tvName = root.findViewById(R.id.tvName);
        tvPincode = root.findViewById(R.id.tvPincode);
        tvCity = root.findViewById(R.id.tvCity);
        tvId = root.findViewById(R.id.tvId);
        edName = root.findViewById(R.id.edName);
        edPincode = root.findViewById(R.id.edPincode);
        edCity = root.findViewById(R.id.edCity);
        tvTodayCodes = root.findViewById(R.id.tvTodayCodes);
        tvLevels = root.findViewById(R.id.tvLevels);
        tvTotalCodes = root.findViewById(R.id.tvTotalCodes);
        tvHistorydays = root.findViewById(R.id.tvHistorydays);
        tvChampionTask = root.findViewById(R.id.tvChampionTask);
        btnGenerate = root.findViewById(R.id.btnGenerate);
        btnFindMissing = root.findViewById(R.id.btnFindMissing);
        btnChampiontask = root.findViewById(R.id.btnChampiontask);
        btnNavChampionTask = root.findViewById(R.id.btnChampionTaskNav);
        frame = root.findViewById(R.id.frame);
        tvHightlight = root.findViewById(R.id.tvHightlight);
        tvInfo = root.findViewById(R.id.tvInfo);
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
        tvCodes = root.findViewById(R.id.tvCodes);
        cbCodes = root.findViewById(R.id.cbCodes);
        btnsyncNow = root.findViewById(R.id.btnsyncNow);
        tvTrialPeriod = root.findViewById(R.id.tvTrialPeriod);
        lltrail = root.findViewById(R.id.lTrial);
        llPayed = root.findViewById(R.id.llPayed);
        Animation blink = new AlphaAnimation(0.0f, 1.0f);
        blink.setDuration(500);
        blink.setInterpolator(new LinearInterpolator());
        blink.setRepeatCount(Animation.INFINITE);
        blink.setRepeatMode(Animation.REVERSE);
        tvHightlight.startAnimation(blink);
        setCodeValue();

        TextView tvWorkingCodes = root.findViewById(R.id.tvWorkingCodes);
        TextView tvBonusCodes = root.findViewById(R.id.tvBonusCodes);



        edName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                namecount = namecount + 1;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                citycount = citycount + 1;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edPincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pincodecount = pincodecount + 1;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        int working_codes  = session.getInt(Constant.TODAY_CODES)  / Integer.parseInt(session.getData(PER_CODE_VAL));
        int bonus_codes  = session.getInt(Constant.TODAY_CODES)  - working_codes;


        Log.d("code_value", String.valueOf(Integer.parseInt(session.getData(PER_CODE_VAL))));
        Log.d("today_code", String.valueOf(session.getInt(Constant.TODAY_CODES)  / Integer.parseInt(session.getData(PER_CODE_VAL))));


        tvWorkingCodes.setText(""+session.getInt(Constant.TODAY_CODES)  / Integer.parseInt(session.getData(PER_CODE_VAL)));
        tvBonusCodes.setText(""+bonus_codes );



        tvChampionTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.getInt(Constant.CODES) == 0){
                    session.setData(Constant.MY_TASK,"champion");
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.beginTransaction().replace(R.id.Container, new FindMissingFragment()).commit();

                }else {
                    Toast.makeText(activity, "Sync Codes then Move to Next Task", Toast.LENGTH_SHORT).show();
                }



            }
        });


        if (session.getData(Constant.STATUS).equals("0")) {
            tvHightlight.setVisibility(View.GONE);
            lltrail.setVisibility(View.VISIBLE);
            llPayed.setVisibility(View.GONE);
            tvTrialPeriod.setText("Trial Period " + session.getInt(Constant.REGULAR_TRIAL_COUNT) + "/10");
        }
        tvInfo.startAnimation(blink);

        btnsyncNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!session.getBoolean(AD_AVAILABLE)) {
                    btnsyncNow.setBackground(ContextCompat.getDrawable(activity, R.drawable.syncbg_disabled));
                    btnsyncNow.setEnabled(false);
                    walletApi();

                } else {
                    Toast.makeText(activity, "Please watch ad and claim ad bonus then sync codes", Toast.LENGTH_SHORT).show();
                }


            }
        });


        EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six, otp_textbox_seven, otp_textbox_eight, otp_textbox_nine, otp_textbox_ten};
        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit,idcount));
        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit, idcount));
        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit, idcount));
        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit, idcount));
        otp_textbox_five.addTextChangedListener(new GenericTextWatcher(otp_textbox_five, edit, idcount));
        otp_textbox_six.addTextChangedListener(new GenericTextWatcher(otp_textbox_six, edit, idcount));
        otp_textbox_seven.addTextChangedListener(new GenericTextWatcher(otp_textbox_seven, edit, idcount));
        otp_textbox_eight.addTextChangedListener(new GenericTextWatcher(otp_textbox_eight, edit, idcount));
        otp_textbox_nine.addTextChangedListener(new GenericTextWatcher(otp_textbox_nine, edit, idcount));
        otp_textbox_ten.addTextChangedListener(new GenericTextWatcher(otp_textbox_ten, edit, idcount));
        generateCodes = databaseHelper.getLimitCodes();

        //adCheckApi();


        btnFindMissing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Container, new FindMissingFragment()).commit();
            }
        });
        btnChampiontask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Container, new FindMissingFragment()).commit();
            }
        });
        btnNavChampionTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Container, new FindMissingFragment()).commit();
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnsyncNow.isEnabled()) {
                    Toast.makeText(activity, "Please Sync Your Codes", Toast.LENGTH_SHORT).show();

                } else {
                    Idnumber = otp_textbox_one.getText().toString().trim() + otp_textbox_two.getText().toString().trim() +
                            otp_textbox_three.getText().toString().trim() + otp_textbox_four.getText().toString().trim() + otp_textbox_five.getText().toString().trim() +
                            otp_textbox_six.getText().toString().trim() + otp_textbox_seven.getText().toString().trim() + otp_textbox_eight.getText().toString().trim() +
                            otp_textbox_nine.getText().toString().trim() + otp_textbox_ten.getText().toString().trim();
                    if (!tvName.getText().toString().trim().equals(edName.getText().toString().trim())) {

                        edName.setError("Name not match");
                        edName.requestFocus();
                        return;

                    } else if (!tvId.getText().toString().trim().equals(Idnumber.toString().trim())) {


                        // Toast.makeText(getActivity(), "Id number not match", Toast.LENGTH_SHORT).show();
                        otp_textbox_ten.setError("Id number not match");
                        otp_textbox_ten.requestFocus();
                        return;
                    } else if (!tvCity.getText().toString().trim().equals(edCity.getText().toString().trim())) {

                        // Toast.makeText(getActivity(), "City not match", Toast.LENGTH_SHORT).show();
                        edCity.setError("City not match");
                        edCity.requestFocus();
                        return;
                    } else if (!tvPincode.getText().toString().trim().equals(edPincode.getText().toString().trim())) {

                        // Toast.makeText(getActivity(), "Pin code not match", Toast.LENGTH_SHORT).show();
                        edPincode.setError("Pin code not match");
                        edPincode.requestFocus();
                        return;
                    } else {
                        long ed_timestamp = System.currentTimeMillis();

                        long difference = ed_timestamp - st_timestamp;
                        long seconds = difference / 1000;
                        int speed_time = Integer.parseInt(session.getData(Constant.MCG_TIMER)) - (int) seconds;
                        int positiveValue = Math.max(speed_time, 0);
                        if (ApiConfig.isConnected(activity)) {
                            if (session.getData(Constant.STATUS).equals("0")) {
                                if (session.getInt(Constant.REGULAR_TRIAL_COUNT) >= 10) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                    builder.setMessage("Congratulations. You have successfully completed your trail codes. Chat with us to start actual job by purchasing database.")
                                            .setCancelable(false)
                                            .setPositiveButton("Chat Now", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    checkJoining();

                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();

                                } else {
                                    session.setInt(Constant.REGULAR_TRIAL_COUNT, session.getInt(Constant.REGULAR_TRIAL_COUNT) + 1);

                                    if (session.getInt(Constant.REGULAR_TRIAL_COUNT) >= 10) {
                                        Bundle bundle = new Bundle();
                                        bundle.putInt(Constant.MCG_TIMER, positiveValue);
                                        bundle.putString(Constant.TASK_TYPE, Constant.REGULAR);
                                        Fragment fragment = new GenrateQRFragment();
                                        fragment.setArguments(bundle);
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        ft.replace(R.id.Container, fragment);
                                        ft.commit();
                                    } else {
                                        Bundle bundle = new Bundle();
                                        bundle.putInt(Constant.MCG_TIMER, positiveValue);
                                        bundle.putString(Constant.TASK_TYPE, Constant.REGULAR);
                                        Fragment fragment = new GenrateQRFragment();
                                        fragment.setArguments(bundle);
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        ft.replace(R.id.Container, fragment);
                                        ft.commit();

                                    }

                                }
                            } else {
                                if (session.getData(Constant.CODE_GENERATE).equals("1")) {
                                    totaltype = tvName.getText().toString().trim().length() + tvId.getText().toString().trim().length() + tvPincode.getText().toString().trim().length() + tvCity.getText().toString().trim().length();
                                    totaltext = namecount + idcount + citycount + pincodecount;

                                    session.setInt(Constant.CODES, session.getInt(Constant.CODES) + Integer.parseInt(session.getData(PER_CODE_VAL)));

                                    if(session.getData(Constant.BLACK_BOX).equals("1") && session.getInt(Constant.CODES) == 60){
                                        suspectApi();
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(Constant.MCG_TIMER, positiveValue);
                                    bundle.putString(Constant.TASK_TYPE, Constant.REGULAR);
                                    Fragment fragment = new GenrateQRFragment();
                                    fragment.setArguments(bundle);
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.Container, fragment);
                                    ft.commit();

                                } else {
                                    Toast.makeText(activity, "You are Restricted for Generating Code", Toast.LENGTH_SHORT).show();

                                }

                            }


                        }


                    }
                }


            }
        });


        return root;
    }
    private void suspectApi() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.CODES, session.getInt(Constant.CODES) + "");
        params.put("total_text", totaltext + "");
        params.put("typed_text", totaltype + "");
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Log.d("TRIAL_COMPLETION", response);

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }, activity, Constant.SUSPECT_CODES_URL, params, false);
    }


    private void checkJoining() {
        reference = FirebaseDatabase.getInstance().getReference(Constant.JOINING_TICKET).child(session.getData(Constant.MOBILE));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    joinTicket();
                } else {

                    Ticket user = dataSnapshot.getValue(Ticket.class);

                    sendChat(user.getId(), user.getName(), user.getCategory(), user.getType(), user.getDescription());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void joinTicket() {
        Long tsLong = System.currentTimeMillis() / 1000;
        RandomId = session.getData(Constant.USER_ID) + "_" + tsLong.toString();
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


    public void showSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(root.findViewById(R.id.edPincode), InputMethodManager.SHOW_IMPLICIT);
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

    public void walletApi() {
        if (ApiConfig.isConnected(activity)) {
            progressDialog.setTitle("Codes are Syncing");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            if (session.getInt(Constant.CODES) != 0) {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
                params.put(Constant.CODES, session.getInt(Constant.CODES) + "");
                params.put(Constant.BLACK_BOX, session.getData(Constant.BLACK_BOX));
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
                                session.setData(Constant.BLACK_BOX, jsonObject.getString(Constant.BLACK_BOX));

                                session.setData(Constant.STATUS, jsonObject.getString(Constant.STATUS));
                                setCodeValue();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                }, 2000);

                            } else {
                                btnsyncNow.setBackground(ContextCompat.getDrawable(activity, R.drawable.syncbg));
                                btnsyncNow.setEnabled(true);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        try {
                                            Toast.makeText(activity, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }, 2000);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, activity, Constant.WALLET_URL, params, true);


            }

        } else {
            btnsyncNow.setBackground(ContextCompat.getDrawable(activity, R.drawable.syncbg));
            btnsyncNow.setEnabled(true);
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
        tvLevels.setText(session.getData(Constant.LEVEL));


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

    public void showRewardedVideoAd() {
        if (AdMobrewardedVideoAd.isLoaded()) {
            AdMobrewardedVideoAd.show();
        } else {
            AdMobrewardedVideoAd.loadAd(
                    AdId, new AdRequest.Builder().build());
        }
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

                } else {
                    Intent intent = new Intent(activity, LoadWebView2Activity.class);
                    startActivity(intent);
                }

            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();


        tvName.setText(generateCodes.get(0).getStudent_name());
        tvPincode.setText(generateCodes.get(0).getPin_code());
        tvCity.setText(generateCodes.get(0).getEcity());
        tvId.setText(generateCodes.get(0).getId_number());



    }


}