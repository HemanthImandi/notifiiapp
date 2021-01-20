package com.notifii.notifiiapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.refresh.EventHandler;
import com.notifii.notifiiapp.utils.NTF_Utils;

public class SsoLoginActivity extends NTF_BaseActivity {

    @BindView(R.id.ssoWebView)
    WebView webView;
    @BindView(R.id.backImage)
    View backLL;

    public static final String SAMLRESPONSE = "saml_response";
    String sso_setting_type, sso_account_id, account_id, user_id, user_email, stay_login_time;
    private boolean isSamlDetected = false;
    private boolean isFromKiosk = false;
    private static EventHandler eventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sso_login);
        ButterKnife.bind(this);
        backLL.setVisibility(View.VISIBLE);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");
        isSamlDetected = false;
        String ssoStringURL = getIntent().getStringExtra("sso_url");
        sso_setting_type = getIntent().getStringExtra("sso_setting_type");
        sso_account_id = getIntent().getStringExtra("sso_account_id");
        account_id = getIntent().getStringExtra("account_id");
        user_id = getIntent().getStringExtra("user_id");
        user_email = getIntent().getStringExtra("user_email");
        stay_login_time = getIntent().getStringExtra(SsoEmailActivity.STAY_LOGIN_KEY);
        isFromKiosk = getIntent().getBooleanExtra("isfromkiosk", false);


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("URL::", url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("URL::Started::", url);
                if (!isSamlDetected && (url.equals("https://staging.notifii.com/sso-login.php?sso")
                        || url.equals("https://portal.notifii.com/sso-login.php?sso"))) {
                    webView.setVisibility(View.GONE);
                    NTF_Utils.showAlertForFinish(SsoLoginActivity.this, "", "Unable to login. Please try after some time.");
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("URL::Finished::", url);
                if (!isSamlDetected) {
                    NTF_Utils.hideProgressDialog();
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(null);
        } else {
            CookieManager.getInstance().removeAllCookie();
        }
        webView.setPictureListener(new MyPictureListener());
        webView.loadUrl(ssoStringURL);
        NTF_Utils.showProgressDialog(this);
    }

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(final String html) {
            if (!isSamlDetected) {
                isSamlDetected = true;
                Intent intent = new Intent(SsoLoginActivity.this, SsoProgressActivity.class);
                intent.putExtra("sso_setting_type", sso_setting_type);
                intent.putExtra("sso_account_id", sso_account_id);
                intent.putExtra("account_id", account_id);
                intent.putExtra("user_id", user_id);
                intent.putExtra("user_email", user_email);
                intent.putExtra(SsoEmailActivity.STAY_LOGIN_KEY, stay_login_time);
                intent.putExtra(SAMLRESPONSE, html);
                intent.putExtra("isfromkiosk", isFromKiosk);
                startActivity(intent);
                finish();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        eventHandler.onRefresh();
    }

    @OnClick(R.id.backImage)
    void onBackClick(){
        onBackPressed();
    }

    class MyPictureListener implements WebView.PictureListener {

        @Override
        public void onNewPicture(WebView view, Picture arg1) {
            // put code here that needs to run when the page has finished loading and
            // a new "picture" is on the webview.
            view.loadUrl("javascript:window.HtmlViewer.showHTML(document.getElementsByName('SAMLResponse')[0].value);");
        }
    }

    public static void setEventHandler(EventHandler handler) {
        eventHandler = handler;
    }


}
