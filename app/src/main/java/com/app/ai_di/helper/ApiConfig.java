package com.app.ai_di.helper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.FirebaseDatabase;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;
import com.zoho.salesiqembed.ZohoSalesIQ;

import java.util.Map;

public class ApiConfig extends Application {
    static ApiConfig mInstance;
    public static final String TAG = ApiConfig.class.getSimpleName();
    RequestQueue mRequestQueue;

    public static String VolleyErrorMessage(VolleyError error) {
        String message = "";
        try {
            message = "";
//            if (error instanceof NetworkError) {
//                message = "Cannot connect to Internet...Please check your connection!";
//            } else if (error instanceof ServerError) {
//                message = "The server could not be found. Please try again after some time!";
//            } else if (error instanceof AuthFailureError) {
//                message = "Cannot connect to Internet...Please check your connection!";
//            } else if (error instanceof ParseError) {
//                message = "Parsing error! Please try again after some time!";
//            } else if (error instanceof TimeoutError) {
//                message = "Connection TimeOut! Please check your internet connection.";
//            } else
//                message = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void RequestToVolley(final VolleyCallback callback, final Activity activity, final String url, final Map<String, String> params, final boolean isProgress) {
        if (CustomDialog.dialog != null) {
            CustomDialog.dialog.cancel();
        }

        final CustomDialog customDialog = new CustomDialog(activity);

        if (isProgress)
            customDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (ApiConfig.isConnected(activity))
                callback.onSuccess(true, response);
            if (isProgress)
                customDialog.closeDialog();
        }, error -> {
            if (isProgress)
                customDialog.closeDialog();
            if (ApiConfig.isConnected(activity))
                callback.onSuccess(false, "");
            String message = VolleyErrorMessage(error);
            if (!message.equals(""))
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        }) {


            @Override
            protected Map<String, String> getParams() {

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        ApiConfig.getInstance().getRequestQueue().getCache().clear();
        ApiConfig.getInstance().addToRequestQueue(stringRequest);
    }
    public static void RequestToVolley(final VolleyCallback callback, final Activity activity, final String url, final Map<String, String> params, final Map<String, String> fileParams) {
        if(isConnected(activity)) {
            VolleyMultiPartRequest multipartRequest = new VolleyMultiPartRequest(url,
                    response -> callback.onSuccess(true, response),
                    error -> callback.onSuccess(false, "")) {
                @Override
                public Map<String, String> getDefaultParams() {
                    return params;
                }


                @Override
                public Map<String, String> getFileParams() {
                    return fileParams;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            getInstance().getRequestQueue().getCache().clear();
            getInstance().addToRequestQueue(multipartRequest);
        }
    }

    public static synchronized ApiConfig getInstance() {
        return mInstance;
    }


    public static Boolean isConnected(final Activity activity) {
        boolean check = false;
        try {
            ConnectivityManager ConnectionManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                check = true;
            } else {
                //Toast.makeText(activity, "No Internet", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        EmojiManager.install(new IosEmojiProvider());

        ZohoSalesIQ.init(this,
                "5spwCGjIKo%2Fz6ssVNakmHbMTvtsszyor90%2BhrhHmnNgJcnpMvghcPXmu4dO6kxpO_in",
                "4%2Fd2z2OovwP9rRaj3CO5TQtzMKPKxu%2FFaEkvD5l3RKcCLPKYaPjW%2B%2BzKEVzDx8I3UedpF6j3RR3PecllV1z3JrF3PMI%2BXoxRDSvLRDVerhOt%2FtApSWo%2FVw%3D%3D");
        ZohoSalesIQ.showLauncher(true);
//        // Replace these values with your Zoho SalesIQ App Key and Access Key
//        String appKey = "YOUR_APP_KEY";
//        String accessKey = "YOUR_ACCESS_KEY";
//
//        // Initialize Zoho SalesIQ with App Key and Access Key
//        SalesIQ.init(this, appKey, accessKey);

    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }



}