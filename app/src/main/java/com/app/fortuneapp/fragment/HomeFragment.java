package com.app.fortuneapp.fragment;

import static com.app.fortuneapp.helper.Constant.AD_STATUS;
import static com.app.fortuneapp.helper.Constant.getHistoryDays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fortuneapp.activities.CheckInActivity;
import com.app.fortuneapp.activities.SignUpActivity;
import com.app.fortuneapp.helper.ApiConfig;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.DatabaseHelper;
import com.app.fortuneapp.helper.Session;
import com.app.fortuneapp.java.GenericTextWatcher;
import com.app.fortuneapp.R;
import com.app.fortuneapp.model.GenerateCodes;
import com.google.android.material.progressindicator.CircularProgressIndicator;

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

    TextView tvName, tvPincode, tvCity, tvId, tvTodayCodes, tvTotalCodes, tvHistorydays, tvCodes, tvBalance;
    EditText edName, edPincode, edCity;
    Button btnGenerate, btnsyncNow;
    CircularProgressIndicator cbCodes;


    EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six, otp_textbox_seven, otp_textbox_eight, otp_textbox_nine, otp_textbox_ten;
    DatabaseHelper databaseHelper;
    ArrayList<GenerateCodes> generateCodes = new ArrayList<GenerateCodes>();
    Session session;
    Activity activity;
    ScrollView frame;
    LinearLayout llWaiting;

    String Idnumber = "";

    Handler handler;
    long code_generate_time = 0;
    public static Dialog dialog = null;

    ProgressDialog progressDialog;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        activity = getActivity();
        session = new Session(activity);
        if (session.getData(Constant.SECURITY).equals("1")) {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
            );
        }
        databaseHelper = new DatabaseHelper(getActivity());
        progressDialog = new ProgressDialog(activity);

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


        tvName = root.findViewById(R.id.tvName);
        tvPincode = root.findViewById(R.id.tvPincode);
        tvCity = root.findViewById(R.id.tvCity);
        tvId = root.findViewById(R.id.tvId);
        edName = root.findViewById(R.id.edName);
        edPincode = root.findViewById(R.id.edPincode);
        edCity = root.findViewById(R.id.edCity);
        tvTodayCodes = root.findViewById(R.id.tvTodayCodes);
        tvTotalCodes = root.findViewById(R.id.tvTotalCodes);
        tvHistorydays = root.findViewById(R.id.tvHistorydays);
        btnGenerate = root.findViewById(R.id.btnGenerate);
        frame = root.findViewById(R.id.frame);
        llWaiting = root.findViewById(R.id.llWaiting);
        tvCodes = root.findViewById(R.id.tvCodes);
        cbCodes = root.findViewById(R.id.cbCodes);
        btnsyncNow = root.findViewById(R.id.btnsyncNow);
        tvBalance = root.findViewById(R.id.tvBalance);

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

        setCodeValue();
        btnsyncNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnsyncNow.setBackground(ContextCompat.getDrawable(activity, R.drawable.syncbg_disabled));
                btnsyncNow.setEnabled(false);
                progressDialog.setTitle("Your Codes are Uploading");
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        walletApi();

                    }
                }, 2000);


            }
        });
        EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six, otp_textbox_seven, otp_textbox_eight, otp_textbox_nine, otp_textbox_ten};
        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit));
        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit));
        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit));
        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit));
        otp_textbox_five.addTextChangedListener(new GenericTextWatcher(otp_textbox_five, edit));
        otp_textbox_six.addTextChangedListener(new GenericTextWatcher(otp_textbox_six, edit));
        otp_textbox_seven.addTextChangedListener(new GenericTextWatcher(otp_textbox_seven, edit));
        otp_textbox_eight.addTextChangedListener(new GenericTextWatcher(otp_textbox_eight, edit));
        otp_textbox_nine.addTextChangedListener(new GenericTextWatcher(otp_textbox_nine, edit));
        otp_textbox_ten.addTextChangedListener(new GenericTextWatcher(otp_textbox_ten, edit));
        generateCodes = databaseHelper.getAllCodes();


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

                        // Toast.makeText(getActivity(), "Name not match", Toast.LENGTH_SHORT).show();
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
                        if (ApiConfig.isConnected(activity)) {
                            if (session.getData(Constant.CODE_GENERATE).equals("1")) {
                                session.setInt(Constant.CODES, session.getInt(Constant.CODES) + 1);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                fm.beginTransaction().replace(R.id.Container, new GenrateQRFragment()).commit();

                            } else {
                                Toast.makeText(activity, "You are Restricted for Generating Code", Toast.LENGTH_SHORT).show();
                            }

                        }


                    }
                }
            }
        });


        return root;
    }

    public void walletApi() {
        if (ApiConfig.isConnected(activity)) {
            if (session.getInt(Constant.CODES) != 0) {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
                params.put(Constant.CODES, session.getInt(Constant.CODES) + "");
                ApiConfig.RequestToVolley((result, response) -> {
                    progressDialog.dismiss();
                    Log.d("WALLET_RES", response);
                    if (result) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean(Constant.SUCCESS)) {
                                session.setInt(Constant.CODES, 0);
                                session.setInt(Constant.TODAY_CODES, Integer.parseInt(jsonObject.getString(Constant.TODAY_CODES)));
                                session.setInt(Constant.TOTAL_CODES, Integer.parseInt(jsonObject.getString(Constant.TOTAL_CODES)));
                                session.setData(Constant.BALANCE, jsonObject.getString(Constant.BALANCE));
                                session.setData(Constant.CODE_GENERATE, jsonObject.getString(Constant.CODE_GENERATE));
                                session.setData(Constant.STATUS, jsonObject.getString(Constant.STATUS));
                                setCodeValue();


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, activity, Constant.WALLET_URL, params, false);


            }

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
            int Todaycodes = Integer.parseInt(session.getData(Constant.TODAY_CODES));
            int Totalcodes = Integer.parseInt(session.getData(Constant.TOTAL_CODES));
            SharedPreferences spreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            SharedPreferences.Editor editor = spreferences.edit();
            editor.remove(Constant.TODAY_CODES);
            editor.remove(Constant.TOTAL_CODES);
            editor.commit();
            session.setInt(Constant.TODAY_CODES, Todaycodes);
            session.setInt(Constant.TOTAL_CODES, Totalcodes);
            setCodeValue();
        }
        tvHistorydays.setText(getHistoryDays(session.getData(Constant.JOINED_DATE)));

    }

    private void GotoActivity() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                frame.setVisibility(View.VISIBLE);
                dialog.cancel();
                if (session.getData(AD_STATUS).equals("1")) {
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

                    }


                }


            }
        }, code_generate_time);
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