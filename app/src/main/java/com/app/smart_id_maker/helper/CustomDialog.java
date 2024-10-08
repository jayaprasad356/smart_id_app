package com.app.smart_id_maker.helper;

import android.app.Activity;
import android.app.Dialog;
import android.view.ViewGroup;

import com.app.smart_id_maker.R;

public class CustomDialog {
    public static Dialog dialog = null;


    public CustomDialog(Activity activity) {
        dialog = new Dialog(activity);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.setContentView(R.layout.customdia);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
    }
    public void showDialog() {
        dialog.show();
    }
    public void closeDialog() {
        dialog.cancel();
    }

}
