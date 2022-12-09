package com.app.abcdapp.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.app.abcdapp.R;


public class ProgressDisplay {

    public static ProgressBar mProgressBar;

    @SuppressLint("UseCompatLoadingForDrawables")
    public ProgressDisplay(Activity activity) {
        try {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            ViewGroup layout = (ViewGroup) (activity).findViewById(android.R.id.content).getRootView();

            mProgressBar = new ProgressBar(activity, null, android.R.attr.progressBarStyle);
            mProgressBar.setIndeterminateDrawable(activity.getDrawable(R.drawable.custom_progress_dialog));
            mProgressBar.setIndeterminate(true);


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            RelativeLayout rl = new RelativeLayout(activity);
            rl.setGravity(Gravity.CENTER);
            rl.addView(mProgressBar);
            layout.addView(rl, params);
            hideProgress(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showProgress() {
        if (mProgressBar.getVisibility() == View.GONE)
            mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress(Activity activity) {
        mProgressBar.setVisibility(View.GONE);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }
}
