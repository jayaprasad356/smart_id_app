package com.app.fortuneapp.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;


import com.app.fortuneapp.R;
import com.app.fortuneapp.activities.MainActivity;
import com.app.fortuneapp.activities.RiseTicketActivity;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.Session;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends BaseActivity{
    private CircleImageView mImageView;
    private TextView mTxtUsername;
    private ViewPager2 mViewPager;
    private long exitTime = 0;
    Fragment chatFragment,ticketFragment;
    public static FragmentManager fm = null;
    Button btnRiseTicket;
    Session session;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mImageView = findViewById(R.id.imageView);
        mTxtUsername = findViewById(R.id.txtUsername);
        btnRiseTicket = findViewById(R.id.btnRiseTicket);
        activity = ChatActivity.this;
        session = new Session(activity);
        fm = getSupportFragmentManager();
        final Toolbar mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");




        chatFragment = new ChatsFragment();
        ticketFragment = new TicketFragment();
        fm.beginTransaction().replace(R.id.container, ticketFragment).commit();
        mTxtUsername.setText(session.getData(Constant.NAME));

        btnRiseTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, RiseTicketActivity.class);
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Utils.readStatus(STATUS_ONLINE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(intent);
        finish();

    }
}