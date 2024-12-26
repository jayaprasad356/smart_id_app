package com.gmwapp.slv_aidi.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gmwapp.slv_aidi.R;
import com.gmwapp.slv_aidi.helper.ApiConfig;
import com.gmwapp.slv_aidi.helper.Constant;
import com.gmwapp.slv_aidi.helper.Session;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ForgoPasswordActivity extends AppCompatActivity {

    private EditText etPhoneNo, etPassword, etConfirmPassword, etOtp;
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
        setContentView(R.layout.activity_forgot_password);

        activity = ForgoPasswordActivity.this;
        session = new Session(activity);

        initializeViews();
        setupButtonListeners();

        otp = generateRandomOtp();
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
        etPhoneNo = findViewById(R.id.EtPhoneNo);
        etConfirmPassword = findViewById(R.id.EtConfirmPassword);
        etPassword = findViewById(R.id.EtPassword);
        etOtp = findViewById(R.id.EtVerifyOtp);
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
            forgotPasswordApi();
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
        if (isEmpty(etPhoneNo)) {
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
        } else if (isEmpty(etConfirmPassword)) {
            showToast("Confirm password is empty");
            return false;
        }
        return true;
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(ForgoPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void forgotPasswordApi() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.MOBILE, etPhoneNo.getText().toString().trim());
        params.put(Constant.CONFIRM_PASSWORD, etConfirmPassword.getText().toString().trim());
        params.put(Constant.PASSWORD, etPassword.getText().toString().trim());
        params.put(Constant.DEVICE_ID, Constant.getDeviceId(activity));

        ApiConfig.RequestToVolley((result, response) -> {
            Log.d("FORGOT_PASSWORD", response);
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(com.gmwapp.slv_aidi.helper.Constant.SUCCESS)) {
                        showToast(jsonObject.getString(Constant.MESSAGE));
                    } else {
                        showToast(jsonObject.getString(Constant.MESSAGE));
                    }
                } catch (Exception e) {
                    showToast("Error: " + e.getMessage());
                }
            } else {
                showToast("Registration failed");
            }
        }, ForgoPasswordActivity.this, Constant.FORGOT_PASSWORD, params, true);
    }

    private void sendOtp(String otp, String phoneNumber) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.MOBILE, phoneNumber);
        params.put(Constant.COUNTRY_CODE, "91");
        params.put(Constant.OTP, otp);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                Toast.makeText(this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();
                findViewById(R.id.btnSendOtp).setBackgroundResource(R.drawable.disabled_button_bg);
                findViewById(R.id.btnSendOtp).setEnabled(false);
                etPhoneNo.setEnabled(false);
            } else {
                Toast.makeText(this, "OTP Failed", Toast.LENGTH_SHORT).show();
            }
        }, ForgoPasswordActivity.this, Constant.OTP_URL, params, true);
        Log.d("OTP_URL", "OTP_URL: " + Constant.OTP_URL);
        Log.d("OTP_URL", "OTP_URL params: " + params);
    }

//    private void sendOtp(String otp, String phoneNumber) {
//        Map<String, String> params = new HashMap<>();
//        ApiConfig.RequestToVolley((result, response) -> {
//            if (result) {
//                Toast.makeText(this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();
//                findViewById(R.id.btnSendOtp).setBackgroundResource(R.drawable.disabled_button_bg);
//                findViewById(R.id.btnSendOtp).setEnabled(false);
//                etPhoneNo.setEnabled(false);
//            } else {
//                // Toast.makeText(this, , Toast.LENGTH_SHORT).show()
//                Toast.makeText(this, "OTP Failed", Toast.LENGTH_SHORT).show();
//            }
//        }, ForgoPasswordActivity.this, Constant.getOTPUrl("64045a300411033f", phoneNumber, otp), params, true);
//    }

}
