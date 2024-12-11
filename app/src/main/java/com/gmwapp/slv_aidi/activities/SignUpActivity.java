package com.gmwapp.slv_aidi.activities;

import static com.gmwapp.slv_aidi.helper.Constant.REFERRED_BY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gmwapp.slv_aidi.R;
import com.gmwapp.slv_aidi.helper.ApiConfig;
import com.gmwapp.slv_aidi.helper.Constant;
import com.gmwapp.slv_aidi.helper.Session;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etPhoneNo, edDOB, etEmail, etCity, etCode, etPassword, etOtp;
    private Session session;
    private Activity activity;

    MaterialButton btnVerifyOtp;
    MaterialButton btnSendOtp;

    boolean isVerified = false;

    String otp = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        activity = SignUpActivity.this;
        session = new Session(activity);

        initializeViews();
        setupDOBPicker();
        setupButtonListeners();

        otp = generateRandomOtp();

//        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String enteredOtp = otp_view.getOTP(); // Get the OTP entered by the user
//
//                if (enteredOtp.length() == 6) {
//                    if (enteredOtp.equals("011011")) {
//                        // If the default OTP is entered, bypass the actual OTP verification
////                        login();
//                        Toast.makeText(activity, "verifed successfully", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // Proceed with the normal OTP verification
//                        verifyOtp();
//                    }
//                } else {
//                    Toast.makeText(activity, "Enter OTP", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void verifyOtp() {
        //  binding.otpView =  000000

        if (otp.equals(etOtp.getText().toString().trim())) {
            findViewById(R.id.btnVerifyOtp).setBackgroundResource(R.drawable.button_green_bg);
            Toast.makeText(activity, "verified successfully", Toast.LENGTH_SHORT).show();
            isVerified = true;
        }
        else {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
        }

    }

    private String generateRandomOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
        return String.valueOf(otp);
    }

    private void initializeViews() {
        edDOB = findViewById(R.id.edDOB);
        etName = findViewById(R.id.EtName);
        etPhoneNo = findViewById(R.id.EtPhoneNo);
        etEmail = findViewById(R.id.EtEmail);
        etCity = findViewById(R.id.EtCity);
        etCode = findViewById(R.id.EtCode);
        etPassword = findViewById(R.id.EtPassword);
        etOtp = findViewById(R.id.EtVerifyOtp);
    }

    private void setupDOBPicker() {
        edDOB.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(
                    SignUpActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> edDOB.setText(year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth),
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupButtonListeners() {
        findViewById(R.id.btnSendOtp).setBackgroundResource(R.drawable.button_bg);
        findViewById(R.id.btnSendOtp).setEnabled(true);
        findViewById(R.id.btnSignup).setOnClickListener(v -> validateAndRegister());
        findViewById(R.id.btnSendOtp).setOnClickListener(v -> btnSentOTP());
        findViewById(R.id.btnVerifyOtp).setOnClickListener(v -> otpVerification());
        findViewById(R.id.backbtn).setOnClickListener(v -> onBackPressed());
    }

    private void otpVerification() {
        if (isOTPInputValid()) {
            String enteredOtp = etOtp.getText().toString().trim();

                if (enteredOtp.equals("011011")) {
                    // If the default OTP is entered, bypass the actual OTP verification
//                        login();
                    findViewById(R.id.btnVerifyOtp).setBackgroundResource(R.drawable.button_green_bg);
                    Toast.makeText(activity, "verified successfully", Toast.LENGTH_SHORT).show();
                    isVerified = true;
                } else {
                    // Proceed with the normal OTP verification
                    verifyOtp();
                }
        }
    }

    private void validateAndRegister() {
        if (isInputValid()) {
            registerUser();
        }
    }

    private void btnSentOTP() {
        if (isPhoneInputValid()) {
            sendOtp(otp, etPhoneNo.getText().toString().trim());
        }
    }

    private boolean isPhoneInputValid() {
        if (isEmpty(etPhoneNo)) {
            showToast("Phone Number is empty");
            return false;
        } else if (etPhoneNo.getText().length() != 10) {
            showToast("Phone Number is invalid");
            return false;
        }
        return true;
    }

    private boolean isOTPInputValid() {
        if (isEmpty(etPhoneNo)) {
            showToast("Phone Number is empty");
            return false;
        } else if (etPhoneNo.getText().length() != 10) {
            showToast("Phone Number is invalid");
            return false;
        } else if (isEmpty(etOtp)) {
            showToast("OTP is empty");
            return false;
        } else if (etOtp.getText().length() != 6) {
            showToast("Enter 6 digit  OTP");
            return false;
        }
        return true;
    }
    private boolean isInputValid() {
        if (isEmpty(etName)) {
            showToast("Name is empty");
            return false;
        } else if (etName.getText().length() < 4) {
            showToast("Name must be at least 4 letters");
            return false;
        } else if (isEmpty(etPhoneNo)) {
            showToast("Phone Number is empty");
            return false;
        } else if (etPhoneNo.getText().length() != 10) {
            showToast("Phone Number is invalid");
            return false;
        } else if (!isVerified) {
            showToast("Mobile number is not verified");
            return false;
        } else if (isEmpty(etPassword)) {
            showToast("Password is empty");
            return false;
        } else if (etPassword.getText().length() < 6) {
            showToast("Password must be at least 6 letters");
            return false;
        } else if (isEmpty(edDOB)) {
            showToast("DOB is empty");
            return false;
        } else if (isEmpty(etEmail)) {
            showToast("Email is empty");
            return false;
        } else if (isEmpty(etCity)) {
            showToast("City is empty");
            return false;
        }
        return true;
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void registerUser() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.NAME, etName.getText().toString().trim());
        params.put(Constant.MOBILE, etPhoneNo.getText().toString().trim());
        params.put(Constant.DOB, edDOB.getText().toString().trim());
        params.put(Constant.EMAIL, etEmail.getText().toString().trim());
        params.put(Constant.CITY, etCity.getText().toString().trim());
        params.put(REFERRED_BY, etCode.getText().toString().trim());
        params.put(Constant.PASSWORD, etPassword.getText().toString().trim());
        params.put(Constant.DEVICE_ID, Constant.getDeviceId(activity));

        ApiConfig.RequestToVolley((result, response) -> {
            Log.d("SIGNUP_RES", response);
            if (result) {
                handleRegistrationResponse(response);
//                try {
//                        JSONObject jsonObject = new JSONObject(response);
//                        if (jsonObject.getBoolean(com.gmwapp.slv_aidi.helper.Constant.SUCCESS)) {
//                            showToast(jsonObject.getString(Constant.MESSAGE));
//                        } else {
//                            showToast(jsonObject.getString(Constant.MESSAGE));
//                        }
//                    } catch (Exception e) {
//                        showToast("Error: " + e.getMessage());
//                    }
            } else {
                showToast("Registration failed");
            }
        }, SignUpActivity.this, Constant.REGISTER_URL, params, true);
    }

    private void handleRegistrationResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean(Constant.SUCCESS)) {
                showToast(jsonObject.getString(Constant.MESSAGE));
                saveSessionData(jsonObject);
                checkJoining();
                navigateToNextScreen();
            } else {
                showToast(jsonObject.getString(Constant.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveSessionData(JSONObject jsonObject) throws JSONException {
        session.setData(Constant.MOBILE, etPhoneNo.getText().toString().trim());
        session.setData(Constant.PASSWORD, etPassword.getText().toString().trim());
        session.setData(Constant.DOB, edDOB.getText().toString().trim());
        session.setData(Constant.EMAIL, etEmail.getText().toString().trim());
        session.setData(Constant.CITY, etCity.getText().toString().trim());
        session.setData(REFERRED_BY, etCode.getText().toString().trim());
        session.setData(Constant.NAME, etName.getText().toString().trim());
        session.setData(Constant.DEVICE_ID, Constant.getDeviceId(activity));

        JSONArray userArray = jsonObject.getJSONArray(Constant.DATA);
        JSONArray setArray = jsonObject.getJSONArray(Constant.SETTINGS);
        saveSettingsData(setArray);
        saveUserData(userArray);
    }

    private void saveSettingsData(JSONArray setArray) throws JSONException {
        JSONObject settings = setArray.getJSONObject(0);
        session.setData(Constant.SYNC_TIME, settings.getString(Constant.SYNC_TIME));
        session.setInt(Constant.SYNC_CODES, settings.getInt(Constant.SYNC_CODES));
        session.setData(Constant.REWARD, settings.getString(Constant.REWARD));
        session.setData(Constant.AD_SHOW_TIME, settings.getString(Constant.AD_SHOW_TIME));
        session.setData(Constant.MIN_WITHDRAWAL, settings.getString(Constant.MIN_WITHDRAWAL));
        session.setData(Constant.AD_STATUS, settings.getString(Constant.AD_STATUS));
        session.setData(Constant.FETCH_TIME, settings.getString(Constant.FETCH_TIME));
        session.setData(Constant.AD_REWARD_ID, settings.getString(Constant.AD_REWARD_ID));
        session.setData(Constant.JOIN_CODES, settings.getString(Constant.JOIN_CODES));
        session.setData(Constant.REFER_BONUS_CODES, settings.getString(Constant.REFER_BONUS_CODES));
        session.setData(Constant.REFER_BONUS_AMOUNT, settings.getString(Constant.REFER_BONUS_AMOUNT));
        session.setData(Constant.REFER_DESCRIPTION, settings.getString(Constant.REFER_DESCRIPTION));
        session.setData(Constant.CHAMPION_TASK, settings.getString(Constant.CHAMPION_TASK));
        session.setData(Constant.CHAMPION_CODES, settings.getString(Constant.CHAMPION_CODES));
        session.setData(Constant.CHAMPION_SEARCH_COUNT, settings.getString(Constant.CHAMPION_SEARCH_COUNT));
        session.setData(Constant.CHAMPION_DEMO_LINK, settings.getString(Constant.CHAMPION_DEMO_LINK));
        session.setData(Constant.MAIN_CONTENT, settings.getString(Constant.MAIN_CONTENT));
    }

    private void saveUserData(JSONArray userArray) throws JSONException {
        JSONObject user = userArray.getJSONObject(0);
        String codegenerate = "0", withdrawal_status = "0";


        JSONObject userData = userArray.getJSONObject(0);
        session.setData(Constant.USER_ID, userData.getString(Constant.ID));
//        session.setUserData(
//                userData.getString(Constant.ID)
//
//        );
    }

    private void navigateToNextScreen() {
        if (session.getBoolean(Constant.IMPORT_DATA)) {
            session.setBoolean("is_logged_in", true);
            startActivity(new Intent(activity, MainActivity.class));
        } else {
            startActivity(new Intent(activity, ImportDataActivity.class));
        }
        finish();
    }

    private void checkJoining() {
        FirebaseDatabase.getInstance()
                .getReference(Constant.JOINING_TICKET).child(session.getData(Constant.MOBILE))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
//                            Ticket user = dataSnapshot.getValue(Ticket.class);
//                            Log.d("NOT_EXIST", user.getType() + " - " + session.getData(Constant.MOBILE));
                        } else {
                            joinTicket();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    private void joinTicket() {
        long timestamp = System.currentTimeMillis() / 1000;
        String randomId = session.getData(Constant.USER_ID) + "_" + timestamp;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.JOINING_TICKET)
                .child(session.getData(Constant.MOBILE));

        Map<String, Object> ticketData = new HashMap<>();
        ticketData.put(Constant.ID, randomId);
        ticketData.put(Constant.CATEGORY, "Joining");
        ticketData.put(Constant.DESCRIPTION, "Enquiry For Joining");
        ticketData.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        ticketData.put(Constant.NAME, session.getData(Constant.NAME));
        ticketData.put(Constant.MOBILE, session.getData(Constant.MOBILE));
        ticketData.put(Constant.TYPE, Constant.JOINING_TICKET);
        ticketData.put(Constant.SUPPORT, "Admin");
        ticketData.put(REFERRED_BY, session.getData(REFERRED_BY));
        ticketData.put(Constant.TIMESTAMP, String.valueOf(timestamp));

        reference.setValue(ticketData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("TICKET_JOIN", "Joining ticket created successfully.");
            } else {
                Log.e("TICKET_JOIN", "Failed to create joining ticket.", task.getException());
            }
        });
    }

//    public void otp(String mobileNumber) {
//        Map<String, String> params = new HashMap<>();
//        params.put(Constant.MOBILE, mobileNumber);
//        ApiConfig.RequestToVolley((result, response) -> {
//            if (result) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    Log.d("SIGNUP_RES", response);
//                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
//                        JSONObject dataObject = jsonObject.getJSONObject(Constant.DATA);
//                        String otp = dataObject.getString("otp");
//
//                        // Uncomment if you want to show the OTP in a toast for debugging purposes
//                        // Toast.makeText(activity, otp, Toast.LENGTH_SHORT).show();
//                        // Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
//
//                        sendOtp(otp);
//                    } else {
//                        Toast.makeText(activity, "1" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
//            }
//        }, this, Constant.OTP, params, true);
//
//        Log.d("OTP", "OTP: " + Constant.OTP);
//        Log.d("OTP", "OTP params: " + params);
//    }

    private void sendOtp(String otp, String phoneNumber) {
        Map<String, String> params = new HashMap<>();
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                Toast.makeText(this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();
                findViewById(R.id.btnSendOtp).setBackgroundResource(R.drawable.disabled_button_bg);
                findViewById(R.id.btnSendOtp).setEnabled(false);
                etPhoneNo.setEnabled(false);
            } else {
                // Toast.makeText(this, , Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "OTP Failed", Toast.LENGTH_SHORT).show();
            }
        }, SignUpActivity.this, Constant.getOTPUrl("64045a300411033f", phoneNumber, otp), params, true);
    }

}
