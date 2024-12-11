package com.gmwapp.slv_aidi.helper;

import android.app.Activity;
import android.app.Dialog;
import android.view.ViewGroup;

import com.gmwapp.slv_aidi.R;

import android.content.Context;

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


