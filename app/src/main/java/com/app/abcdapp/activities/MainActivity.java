package com.app.abcdapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.app.abcdapp.R;
import com.app.abcdapp.fragment.HomeFragment;
import com.app.abcdapp.fragment.ProfileFragment;
import com.app.abcdapp.fragment.WalletFragment;
import com.app.abcdapp.helper.Constant;
import com.app.abcdapp.helper.Session;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {

    public static FragmentManager fm = null;
    private BottomNavigationView navbar;
    Activity activity;
    Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getSupportFragmentManager();
        setContentView(R.layout.activity_main);
        navbar = findViewById(R.id.bottomNavigation);
        navbar.setSelectedItemId(R.id.Home);
        activity = MainActivity.this;
        session = new Session(activity);
        if (session.getBoolean(Constant.RUN_API)){
            fm.beginTransaction().replace(R.id.Container, new WalletFragment()).commit();

        }
        else {
            fm.beginTransaction().replace(R.id.Container, new HomeFragment()).commit();

        }
        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Profile:
                        fm.beginTransaction().replace(R.id.Container, new ProfileFragment()).commit();
                        break;
                    case R.id.Home:
                        fm.beginTransaction().replace(R.id.Container, new HomeFragment()).commit();
                        break;
                    case R.id.Wallet:

                        fm.beginTransaction().replace(R.id.Container, new WalletFragment()).commit();




                        break;
                }
                return true;
            }
        });
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            session.setData(Constant.FCM_ID, token);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        WalletFragment fragm = new WalletFragment();
        fragm.walletApi(session,activity);
    }
}