package com.app.fortuneapp.activities;

import static com.app.fortuneapp.helper.Constant.REFERRED_BY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.app.fortuneapp.R;
import com.app.fortuneapp.chat.models.Ticket;
import com.app.fortuneapp.helper.ApiConfig;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {

    EditText EtName,EtPhoneNo,edDOB,EtEmail,EtCity,EtCode,EtPassword;
    LinearLayout llDob;
    ImageView backbtn;
    Button btnSignup;
    Session session;
    Activity activity;
    String RandomId;
    DatabaseReference reference;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        activity = SignUpActivity.this;
        session = new Session(activity);


        edDOB = findViewById(R.id.edDOB);
        llDob = findViewById(R.id.llDob);
        backbtn = findViewById(R.id.backbtn);
        EtName = findViewById(R.id.EtName);
        EtPhoneNo = findViewById(R.id.EtPhoneNo);
        EtEmail = findViewById(R.id.EtEmail);
        EtCity = findViewById(R.id.EtCity);
        EtCode = findViewById(R.id.EtCode);
        EtPassword = findViewById(R.id.EtPassword);

        edDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // on below line we are setting date to our text view.
                        edDOB.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                    }
                },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();

            }
        });

        btnSignup = findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(EtName.getText().toString().trim().equals("") ){
                    Toast.makeText(SignUpActivity.this, "Name is empty", Toast.LENGTH_SHORT).show();
                }
               else if(EtName.getText().length() < 4 ){
                    Toast.makeText(SignUpActivity.this, "Name atleast 4 Letter", Toast.LENGTH_SHORT).show();
                }

                else if (EtPhoneNo.getText().toString().trim().equals("")){
                    Toast.makeText(SignUpActivity.this, "Phone Number is empty", Toast.LENGTH_SHORT).show();
                }
                else if (EtPhoneNo.getText().length() != 10){
                    Toast.makeText(SignUpActivity.this, "Phone Number is invalid", Toast.LENGTH_SHORT).show();
                }
                else if (edDOB.getText().toString().trim().equals("")){
                    Toast.makeText(SignUpActivity.this, "DOB is empty", Toast.LENGTH_SHORT).show();
                }
                else if (EtEmail.getText().toString().trim().equals("")){
                    Toast.makeText(SignUpActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
                }
                else if (EtCity.getText().toString().trim().equals("")){
                    Toast.makeText(SignUpActivity.this, "City is empty", Toast.LENGTH_SHORT).show();
                }
                else if (EtPassword.getText().toString().trim().equals("")){
                    Toast.makeText(SignUpActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
                }

                else if (EtPassword.getText().length() < 6){
                    Toast.makeText(SignUpActivity.this, "Password atleast 6 Letter ", Toast.LENGTH_SHORT).show();
                }
                else{


                    Register();
//                    Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
//                    startActivity(intent);
                }
            }
        });



        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void Register() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.NAME,EtName.getText().toString().trim());
        params.put(Constant.MOBILE,EtPhoneNo.getText().toString().trim());
        params.put(Constant.DOB,edDOB.getText().toString().trim());
        params.put(Constant.EMAIL,EtEmail.getText().toString().trim());
        params.put(Constant.CITY,EtCity.getText().toString().trim());
        params.put(REFERRED_BY,EtCode.getText().toString().trim());
        params.put(Constant.PASSWORD,EtPassword.getText().toString().trim());
        params.put(Constant.DEVICE_ID,Constant.getDeviceId(activity));
        ApiConfig.RequestToVolley((result, response) -> {
            Log.d("SIGNUP_RES",response);
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {

                        Toast.makeText(this, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                        session.setData(Constant.MOBILE, EtPhoneNo.getText().toString().trim());
                        session.setData(Constant.PASSWORD, EtPassword.getText().toString().trim());
                        session.setData(Constant.DOB, edDOB.getText().toString().trim());
                        session.setData(Constant.EMAIL, EtEmail.getText().toString().trim());
                        session.setData(Constant.CITY, EtCity.getText().toString().trim());
                        session.setData(REFERRED_BY, EtCode.getText().toString().trim());
                        session.setData(Constant.NAME, EtName.getText().toString().trim());
                        session.setData(Constant.DEVICE_ID, Constant.getDeviceId(activity));
                        // showAlertdialog();
                        String codegenerate = "0", withdrawal_status = "0";
                        JSONArray userArray = jsonObject.getJSONArray(Constant.DATA);
                        JSONArray setArray = jsonObject.getJSONArray(Constant.SETTINGS);
                        session.setData(Constant.SYNC_TIME, setArray.getJSONObject(0).getString(Constant.SYNC_TIME));
                        session.setInt(Constant.SYNC_CODES, Integer.parseInt(setArray.getJSONObject(0).getString(Constant.SYNC_CODES)));
                        session.setData(Constant.REWARD, setArray.getJSONObject(0).getString(Constant.REWARD));
                        session.setData(Constant.AD_SHOW_TIME, setArray.getJSONObject(0).getString(Constant.AD_SHOW_TIME));
                        session.setData(Constant.MIN_WITHDRAWAL, setArray.getJSONObject(0).getString(Constant.MIN_WITHDRAWAL));
                        session.setData(Constant.AD_STATUS, setArray.getJSONObject(0).getString(Constant.AD_STATUS));
                        session.setData(Constant.FETCH_TIME, setArray.getJSONObject(0).getString(Constant.FETCH_TIME));
                        session.setData(Constant.AD_REWARD_ID, setArray.getJSONObject(0).getString(Constant.AD_REWARD_ID));
                        session.setData(Constant.JOIN_CODES, setArray.getJSONObject(0).getString(Constant.JOIN_CODES));
                        session.setData(Constant.REFER_BONUS_CODES, setArray.getJSONObject(0).getString(Constant.REFER_BONUS_CODES));
                        session.setData(Constant.REFER_BONUS_AMOUNT, setArray.getJSONObject(0).getString(Constant.REFER_BONUS_AMOUNT));
                        session.setData(Constant.REFER_DESCRIPTION, setArray.getJSONObject(0).getString(Constant.REFER_DESCRIPTION));
                        session.setData(Constant.CHAMPION_TASK, setArray.getJSONObject(0).getString(Constant.CHAMPION_TASK));
                        session.setData(Constant.CHAMPION_CODES, setArray.getJSONObject(0).getString(Constant.CHAMPION_CODES));
                        session.setData(Constant.CHAMPION_SEARCH_COUNT, setArray.getJSONObject(0).getString(Constant.CHAMPION_SEARCH_COUNT));
                        session.setData(Constant.CHAMPION_DEMO_LINK, setArray.getJSONObject(0).getString(Constant.CHAMPION_DEMO_LINK));
                        session.setData(Constant.MAIN_CONTENT,setArray.getJSONObject(0).getString(Constant.MAIN_CONTENT));

                        if (setArray.getJSONObject(0).getString(Constant.CODE_GENERATE).equals("1")) {
                            codegenerate = userArray.getJSONObject(0).getString(Constant.CODE_GENERATE);
                        }
                        if (setArray.getJSONObject(0).getString(Constant.WITHDRAWAL_STATUS).equals("1")) {
                            withdrawal_status = userArray.getJSONObject(0).getString(Constant.WITHDRAWAL_STATUS);
                        }

                        Toast.makeText(this, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                        session.setUserData(userArray.getJSONObject(0).getString(Constant.ID),
                                userArray.getJSONObject(0).getString(Constant.NAME),
                                userArray.getJSONObject(0).getString(Constant.MOBILE),
                                userArray.getJSONObject(0).getString(Constant.PASSWORD),
                                userArray.getJSONObject(0).getString(Constant.DOB),
                                userArray.getJSONObject(0).getString(Constant.EMAIL),
                                userArray.getJSONObject(0).getString(Constant.CITY),
                                userArray.getJSONObject(0).getString(REFERRED_BY),
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
                                withdrawal_status, userArray.getJSONObject(0).getString(Constant.TASK_TYPE),
                                userArray.getJSONObject(0).getString(Constant.TRIAL_EXPIRED),
                                userArray.getJSONObject(0).getString(Constant.CHAMPION_TASK_ELIGIBLE),
                                userArray.getJSONObject(0).getString(Constant.TRIAL_COUNT),
                                userArray.getJSONObject(0).getString(Constant.MCG_TIMER),
                                userArray.getJSONObject(0).getString(Constant.SECURITY),
                                userArray.getJSONObject(0).getString(Constant.ONGOING_SA_BALANCE),
                                userArray.getJSONObject(0).getString(Constant.SALARY_ADVANCE_BALANCE),
                                userArray.getJSONObject(0).getString(Constant.SA_REFER_COUNT));
                        checkJoining();
                        if (session.getBoolean(Constant.IMPORT_DATA)) {
                            session.setBoolean("is_logged_in", true);
                            startActivity(new Intent(activity, MainActivity.class));
                            finish();

                        } else {
                            startActivity(new Intent(activity, ImportDataActivity.class));
                            finish();
                        }

                    } else {
                        Toast.makeText(this, "" + jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(this, String.valueOf(response) +String.valueOf(result), Toast.LENGTH_SHORT).show();

            }
            //pass url
        }, SignUpActivity.this, Constant.REGISTER_URL, params,true);

    }
    private void showAlertdialog() {
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Registered Successfully");
        builder.setIcon(R.drawable.check);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
            startActivity(new Intent(SignUpActivity.this, CheckInActivity.class));
            finish();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    private void checkJoining() {
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constant.JOINING_TICKET).child(session.getData(Constant.MOBILE));
        FirebaseDatabase.getInstance()
                .getReference(Constant.JOINING_TICKET).child(session.getData(Constant.MOBILE)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Ticket user = dataSnapshot.getValue(Ticket.class);
                            Log.d("NOT_EXIST", user.getType() + " - " + session.getData(Constant.MOBILE));

                        } else {
                            joinTicket();

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
        hashMap.put(REFERRED_BY, session.getData(REFERRED_BY));
        hashMap.put(Constant.TIMESTAMP, tsLong.toString());
        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
            System.out.println("done");
        });
    }
}
