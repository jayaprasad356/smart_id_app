package com.app.fortuneapp.chat;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.google.firebase.database.FirebaseDatabase;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

public class UIApplication extends Application implements DefaultLifecycleObserver {
    @Override
    public void onCreate() {
        super.onCreate();



        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        EmojiManager.install(new IosEmojiProvider());

    }

    @Override
    public void onResume(LifecycleOwner owner) {
//        App visible/foreground
        if (owner.getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
            try {
                final Handler handler = new Handler(Looper.getMainLooper());
                //handler.postDelayed(() -> Utils.readStatus(STATUS_ONLINE), ONE);
            } catch (Exception ignored) {
            }

        }
    }

    @Override
    public void onPause(LifecycleOwner owner) {
//        App in background
        if (owner.getLifecycle().getCurrentState() == Lifecycle.State.STARTED) {
            try {
                final Handler handler = new Handler(Looper.getMainLooper());
                //handler.postDelayed(() -> Utils.readStatus(STATUS_OFFLINE), ONE);
            } catch (Exception ignored) {
            }
        }
    }
}
