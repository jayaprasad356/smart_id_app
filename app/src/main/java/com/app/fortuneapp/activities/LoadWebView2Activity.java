package com.app.fortuneapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.fortuneapp.R;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.Session;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import im.delight.android.webview.AdvancedWebView;

public class LoadWebView2Activity extends AppCompatActivity implements AdvancedWebView.Listener {

    private AdvancedWebView mWebView;
    String Url;
    Session session;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_web_view2);
        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        session = new Session(LoadWebView2Activity.this);

        Url = getIntent().getStringExtra(Constant.URL);
        mWebView.setListener(LoadWebView2Activity.this, this);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setClickable(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl("https://shrinke.me/erGcZzB");

        showAlertDialogButtonClicked();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mWebView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }



    @Override
    public void onPageStarted(String url, Bitmap favicon) { }

    @Override
    public void onPageFinished(String url) { }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        Log.d("EXTERNAL_REQUEST_ERR",failingUrl);
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }

    @Override
    public void onExternalPageRequest(String url) {
        Log.d("EXTERNAL_REQUEST",url);
    }



    public void showAlertDialogButtonClicked()
    {

        // Create an alert builder
        AlertDialog.Builder builder
                = new AlertDialog.Builder(this);


        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.timer_layout, null);
        builder.setView(customLayout);
        AlertDialog dialog
                = builder.create();

        dialog.setCancelable(false);

        TextView tvTimer = customLayout.findViewById(R.id.tvTimer);

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("0");

                long hour = (millisUntilFinished / 3600000) % 24;

                long min = (millisUntilFinished / 60000) % 60;

                long sec = (millisUntilFinished / 1000) % 60;

                tvTimer.setText(f.format(sec));

            }

            // When the task is over it will print 00:00:00 there

            public void onFinish() {
                dialog.dismiss();
                tvTimer.setText("Code Rewarded!!");
                int codereward = Integer.parseInt(session.getData(Constant.REWARD));
                session.setInt(Constant.CODES,session.getInt(Constant.CODES) + codereward);
                Intent intent = new Intent(LoadWebView2Activity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }

        }.start();
        // add a button


        // create and show
        // the alert dialog

        dialog.show();
    }

    // Do something with the data
    // coming from the AlertDialog
    private void sendDialogDataToActivity(String data)
    {
        Toast.makeText(this,
                        data,
                        Toast.LENGTH_SHORT)
                .show();
    }

}