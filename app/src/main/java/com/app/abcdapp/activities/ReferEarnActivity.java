package com.app.abcdapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.abcdapp.BuildConfig;
import com.app.abcdapp.R;
import com.app.abcdapp.helper.Constant;
import com.app.abcdapp.helper.Session;

public class ReferEarnActivity extends AppCompatActivity {


    ImageView backbtn;
    TextView tvRefercode;
    Button btncopy;

    private ClipboardManager myClipboard;
    private ClipData myClip;
    String text;
    Session session;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_earn);


        backbtn = findViewById(R.id.backbtn);
        tvRefercode = findViewById(R.id.tvRefercode);
        btncopy = findViewById(R.id.btncopy);
        activity = ReferEarnActivity.this;
        session = new Session(activity);

        tvRefercode.setText(session.getData(Constant.REFER_CODE));

        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        text = tvRefercode.getText().toString();


        btncopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//                sharingIntent.setType("text/plain");
              String shareBody = "DOWNLOAD THE APP AND GET UNLIMITED EARNING .you can also Download App from below link and enter referral code while login-"+"\n"+text+"\n"+"https://play.google.com/store/apps/details?id=abcdjob.workonline.com.qrcode";
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
//                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//                startActivity(Intent.createChooser(sharingIntent, "Share via"));
//
//                myClip = ClipData.newPlainText("text", shareBody);
//                myClipboard.setPrimaryClip(myClip);


                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage= "\nDOWNLOAD THE APP AND GET UNLIMITED EARNING .you can also Download App from below link and enter referral code while login-"+"\n"+text+"\n";
                    shareMessage = shareMessage +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }




//
//                Toast.makeText(getApplicationContext(), "Text Copied",
//                        Toast.LENGTH_SHORT).show();


            }
        });



        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}