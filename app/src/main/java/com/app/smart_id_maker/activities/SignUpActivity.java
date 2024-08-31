package com.app.smart_id_maker.activities;

import static com.app.smart_id_maker.helper.Constant.REFERRED_BY;

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

import com.app.smart_id_maker.R;
import com.app.smart_id_maker.helper.ApiConfig;
import com.app.smart_id_maker.helper.Constant;
import com.app.smart_id_maker.helper.Session;
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

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etPhoneNo, edDOB, etEmail, etCity, etCode, etPassword;
    private Session session;
    private Activity activity;

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
    }

    private void initializeViews() {
        edDOB = findViewById(R.id.edDOB);
        etName = findViewById(R.id.EtName);
        etPhoneNo = findViewById(R.id.EtPhoneNo);
        etEmail = findViewById(R.id.EtEmail);
        etCity = findViewById(R.id.EtCity);
        etCode = findViewById(R.id.EtCode);
        etPassword = findViewById(R.id.EtPassword);
    }

    private void setupDOBPicker() {
        edDOB.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    SignUpActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> edDOB.setText(year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth),
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupButtonListeners() {
        findViewById(R.id.btnSignup).setOnClickListener(v -> validateAndRegister());
        findViewById(R.id.backbtn).setOnClickListener(v -> onBackPressed());
    }

    private void validateAndRegister() {
        if (isInputValid()) {
            registerUser();
        }
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
        } else if (isEmpty(edDOB)) {
            showToast("DOB is empty");
            return false;
        } else if (isEmpty(etEmail)) {
            showToast("Email is empty");
            return false;
        } else if (isEmpty(etCity)) {
            showToast("City is empty");
            return false;
        } else if (isEmpty(etPassword)) {
            showToast("Password is empty");
            return false;
        } else if (etPassword.getText().length() < 6) {
            showToast("Password must be at least 6 letters");
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
        session.setUserData(
                userData.getString(Constant.ID),
                userData.getString(Constant.NAME),
                userData.getString(Constant.MOBILE),
                userData.getString(Constant.PASSWORD),
                userData.getString(Constant.DOB),
                userData.getString(Constant.EMAIL),
                userData.getString(Constant.CITY),
                userData.getString(Constant.REFERRED_BY),
                userData.getString(Constant.EARN),
                userData.getString(Constant.WITHDRAWAL),
                userData.getString(Constant.TOTAL_REFERRALS),
                userData.getInt(Constant.TODAY_CODES),
                userData.getInt(Constant.TOTAL_CODES),
                userData.getString(Constant.BALANCE),
                userData.getString(Constant.DEVICE_ID),
                userData.getString(Constant.STATUS),
                userData.getString(Constant.REFER_CODE),
                userData.getString(Constant.REFER_BONUS_SENT),
                codegenerate,
                userData.getString(Constant.CODE_GENERATE_TIME),
                userData.getString(Constant.LAST_UPDATED),
                userData.getString(Constant.JOINED_DATE)

        );
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
}
