package com.app.fortuneapp.activities;

import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_ATTACH_TYPE;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_DATETIME;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_MESSAGE;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_RECEIVER;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_SEEN;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_SENDER;
import static com.app.fortuneapp.chat.constants.IConstants.EXTRA_TYPE;
import static com.app.fortuneapp.chat.constants.IConstants.FALSE;
import static com.app.fortuneapp.chat.constants.IConstants.REF_CHATS;
import static com.app.fortuneapp.chat.constants.IConstants.SLASH;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.fortuneapp.R;
import com.app.fortuneapp.chat.constants.IConstants;
import com.app.fortuneapp.chat.managers.Utils;
import com.app.fortuneapp.helper.ApiConfig;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RiseTicketActivity extends AppCompatActivity {

    DatabaseReference reference;
    Session session;
    Activity activity;
    Spinner spinCategory;
    EditText etDescription;
    String RandomId;
    String TYPE_TEXT = "TEXT";
    String strSender,strReceiver,currentId,userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rise_ticket);

        activity = RiseTicketActivity.this;
        session = new Session(activity);

        etDescription = findViewById(R.id.etDescription);
        spinCategory = findViewById(R.id.spinCategory);
        Button btnRiseToken = findViewById(R.id.btnRiseToken);

        btnRiseToken.setOnClickListener(view -> {
            if (etDescription.getText().toString().trim().equals("")){
                Toast.makeText(RiseTicketActivity.this, "Description is Empty", Toast.LENGTH_SHORT).show();
            }else {
                if (session.getBoolean(Constant.RISE_TICKET_STATUS)){
                    SimpleDateFormat df = new SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.getDefault());
                    Date c = Calendar.getInstance().getTime();
                    String currentDate = df.format(c);

                    if (!session.getBoolean(Constant.LAST_UPDATED_DATE_SETTINGS)){
                        session.setData(Constant.LAST_UPDATED_SETTINGS_DATE,currentDate);
                        session.setBoolean(Constant.LAST_UPDATED_DATE_SETTINGS,true);
                    }
                    Date date1 = null;
                    try {
                        date1 = df.parse(currentDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date date2 = null;
                    try {
                        date2 = df.parse(session.getData(Constant.LAST_UPDATED_SETTINGS_DATE));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long different = date1.getTime() - date2.getTime();
                    long secondsInMilli = 1000;
                    long minutesInMilli = secondsInMilli * 60;
                    long hoursInMilli = minutesInMilli * 60;
                    long elapsedHours = different / hoursInMilli;
                    long elapsedMinutue = different / minutesInMilli;

                    if (elapsedMinutue >= Long.parseLong("3")){

                        session.setBoolean(Constant.LAST_UPDATED_DATE_SETTINGS,false);
                        riseTicketCheck();

                    }
                    else {
                        Toast.makeText(RiseTicketActivity.this, "Please rise ticket only on Working hours Mon - Sat 10 AM to 6 PM", Toast.LENGTH_SHORT).show();

                    }

                }else {
                    riseTicketCheck();

                }



            }
        });

    }

    private void riseTicketCheck() {
        Map<String, String> params = new HashMap<>();
        ApiConfig.RequestToVolley((result, response) -> {
            Log.d("SETTINGS_DATA",response);
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        JSONArray setArray = jsonObject.getJSONArray(Constant.DATA);
                        if (setArray.getJSONObject(0).getString(Constant.CHAT_SUPPORT).equals("1")){
                            session.setBoolean(Constant.RISE_TICKET_STATUS,false);
                            riseTicket();
                        }else {
                            session.setBoolean(Constant.RISE_TICKET_STATUS,true);
                            Toast.makeText(RiseTicketActivity.this, "Please rise ticket only on Working hours Mon - Sat 10 AM to 6 PM", Toast.LENGTH_SHORT).show();

                        }


                    }else {
                        Toast.makeText(RiseTicketActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(RiseTicketActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();

                }



            }

            //pass url
        }, activity, Constant.SETTINGS_URL, params,false);

    }

    private void riseTicket() {

        Query qref;
        qref = Utils.getQueryPendingTicketByMyId(session.getData(Constant.MOBILE));
        qref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {

                    sendTicket();
                }
                else {
                    Toast.makeText(activity, "Your Ticket is Already in Pending Status,You Can't Send Another Ticket", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendDescriptionMsg()
    {
        currentId = RandomId;
        userId = "admin_1";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(EXTRA_SENDER, RandomId);
        hashMap.put(EXTRA_RECEIVER, userId);
        hashMap.put(EXTRA_MESSAGE, etDescription.getText().toString().trim());
        hashMap.put(EXTRA_ATTACH_TYPE, TYPE_TEXT);
        hashMap.put(EXTRA_TYPE, TYPE_TEXT);//This is for older version users(Default TEXT, all other set as IMAGE)



        hashMap.put(EXTRA_SEEN, FALSE);
        hashMap.put(EXTRA_DATETIME, Utils.getDateTime());

        final String key = Utils.getChatUniqueId();


        strSender = currentId + SLASH + userId;
        strReceiver = userId + SLASH + currentId;
        hashMap.put(IConstants.ID, key);
        reference.child(REF_CHATS).child(strSender).child(key).setValue(hashMap);
        reference.child(REF_CHATS).child(strReceiver).child(key).setValue(hashMap);

        sendAutoMessage();
    }

    private void sendAutoMessage() {
        String msg = "";
        if (spinCategory.getSelectedItem().toString().trim().equals("I want to refer friend")){
            msg = "Hi, Thanks for your message. Please share your friends whatsapp number who is going to join, We will guide him.";

        }
        else if (spinCategory.getSelectedItem().toString().trim().equals("App issue")){
            msg = "Hi, Thanks for your message, Please share the screen shot of the issue you are facing. Share the details and wait for our reply, We will chat with you shortly.";

        }
        else if (spinCategory.getSelectedItem().toString().trim().equals("Withdrawal not received")){
            msg = "Hi, Thanks for your message. Please share the below details. Date Of Withdrawal, Amount Withdrawn, Bank Account Details. Share the details and wait for our reply, We will chat with you shortly.";

        }
        else if (spinCategory.getSelectedItem().toString().trim().equals("Refer bonus not received")){
            msg = "Hi, Thanks for your message. Please share the below details. Date of referral, Joined person Name & Contact number. Share the details and wait for our reply, We will chat with you shortly.";
        }
        else {
            msg = "Hi, Thanks for your message. Please share the query and wait for our reply, We will chat with you shortly.";
        }
        final String sender = currentId;
        final String receiver = userId;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(EXTRA_SENDER, receiver);
        hashMap.put(EXTRA_RECEIVER, sender);
        hashMap.put(EXTRA_MESSAGE, msg);
        hashMap.put(EXTRA_ATTACH_TYPE, TYPE_TEXT);
        hashMap.put(EXTRA_TYPE, TYPE_TEXT);
        hashMap.put(EXTRA_SEEN, FALSE);
        hashMap.put(EXTRA_DATETIME, Utils.getDateTime());
        final String key = Utils.getChatUniqueId();
        hashMap.put(IConstants.ID, key);
        reference.child(REF_CHATS).child(strSender).child(key).setValue(hashMap);
        reference.child(REF_CHATS).child(strReceiver).child(key).setValue(hashMap);
        Toast.makeText(activity, "Ticket Sent Successfully", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(intent);
    }

    private void sendTicket() {
        Long tsLong = System.currentTimeMillis()/1000;
        RandomId = session.getData(Constant.USER_ID) +"_"+ tsLong.toString();
        reference = FirebaseDatabase.getInstance().getReference(Constant.PENDING_TICKET).child(RandomId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.ID, RandomId);
        hashMap.put(Constant.CATEGORY, spinCategory.getSelectedItem().toString().trim());
        hashMap.put(Constant.DESCRIPTION, etDescription.getText().toString().trim());
        hashMap.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        hashMap.put(Constant.NAME, session.getData(Constant.NAME));
        hashMap.put(Constant.MOBILE, session.getData(Constant.MOBILE));
        hashMap.put(Constant.TYPE, Constant.PENDING_TICKET);
        hashMap.put(Constant.SUPPORT, "Admin");
        hashMap.put(Constant.TIMESTAMP, tsLong.toString());
        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
            sendDescriptionMsg();

        });
    }
}