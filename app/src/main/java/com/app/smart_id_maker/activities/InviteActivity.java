package com.app.smart_id_maker.activities;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.app.smart_id_maker.R;

public class InviteActivity extends AppCompatActivity {


    ImageButton ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);


        ibBack = findViewById(R.id.ibBack);

        ibBack.setOnClickListener(v -> onBackPressed());


//        ibBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//
//        } );

    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }


}