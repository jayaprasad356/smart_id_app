package com.app.ai_di.helper;

import android.app.Activity;
import android.app.Dialog;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.ai_di.R;
import com.app.ai_di.activities.SignUpActivity;

import java.util.Objects;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.Toast;

public class CustomDialog {
    public static Dialog dialog = null;

    private Context context;


    public CustomDialog(Activity activity) {
        this.context = activity;  // Store the activity context
        dialog = new Dialog(activity);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.setContentView(R.layout.customdia);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }

    public void showDialog() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void closeDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
//            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show(); // Use the context here
        }
    }
}


