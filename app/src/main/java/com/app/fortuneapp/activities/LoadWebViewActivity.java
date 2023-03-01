package com.app.fortuneapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.app.fortuneapp.R;
import com.app.fortuneapp.helper.Constant;

import im.delight.android.webview.AdvancedWebView;

public class LoadWebViewActivity extends AppCompatActivity implements AdvancedWebView.Listener {
    private AdvancedWebView mWebView;
    String Url;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_web_view);
        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        Url = getIntent().getStringExtra(Constant.URL);

        CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.setListener(LoadWebViewActivity.this, this);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.getSettings().setSupportZoom(true);
        // disable third-party cookies only
        mWebView.setThirdPartyCookiesEnabled(true);
// or disable cookies in general
        mWebView.setCookiesEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setClickable(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl(Url);
        mWebView.getSettings().setSupportMultipleWindows(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getHost().equals("www.shrinke.me") || url.equals("https://abcd.greymatterworks.in/claim_codes.php")) {
                    // This is your web site, so do not override; let the WebView load the page
                    return false;
                }
                Log.d("OTHER_LINKS",url);
                // Otherwise, the link is not for a page on your site, so launch another Activity that handles URLs
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });
// myWebView.getSettings().setJavaScriptEnabled(true);
// myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);



    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
//            mWebView.goBack();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoadWebViewActivity.this,MainActivity.class);
        startActivity(intent);
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
    public void onPageFinished(String url) {

    }

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

}