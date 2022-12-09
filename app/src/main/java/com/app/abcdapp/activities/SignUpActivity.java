package com.app.abcdapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.app.abcdapp.R;
import com.app.abcdapp.helper.ApiConfig;
import com.app.abcdapp.helper.Constant;
import com.app.abcdapp.helper.Session;

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
        params.put(Constant.REFERRED_BY,EtCode.getText().toString().trim());
        params.put(Constant.PASSWORD,EtPassword.getText().toString().trim());
        params.put(Constant.DEVICE_ID,Constant.getDeviceId(activity));
        ApiConfig.RequestToVolley((result, response) -> {
            Log.d("SIGNUP_RES",response);
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(this, ""+jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(this, ""+jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
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
}
