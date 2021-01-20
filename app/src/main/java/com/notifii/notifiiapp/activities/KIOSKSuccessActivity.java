package com.notifii.notifiiapp.activities;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.utils.NTF_Utils;

public class KIOSKSuccessActivity extends AppCompatActivity {

    @BindView(R.id.textViewActionTitle)
    TextView mTextViewActionTitle;
    @BindView(R.id.iv_app_icon)
    ImageView logo;

    Handler handler;
    private boolean needToGoBack=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosksuccess);
        ButterKnife.bind(this);
        mTextViewActionTitle.setText("");
        mTextViewActionTitle.setBackgroundResource(R.drawable.ic_notifii_track_white_logo);
        logo.setVisibility(View.GONE);
        handler = new Handler();
        handler.postDelayed(runnable,5000);
        needToGoBack=false;
    }

    @OnClick(R.id.successmessageLL)
    void onSuccessClicked(){
        Intent loginActivityIntent = new Intent(KIOSKSuccessActivity.this, IdentifyUserActivity.class);
        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginActivityIntent);
        handler.removeCallbacks(runnable);
        finish();
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if (NTF_Utils.isAppOnForeground(KIOSKSuccessActivity.this)){
                Intent loginActivityIntent = new Intent(KIOSKSuccessActivity.this, IdentifyUserActivity.class);
                loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginActivityIntent);
                finish();
            } else {
                needToGoBack=true;
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (needToGoBack){
            needToGoBack=false;
            Intent loginActivityIntent = new Intent(KIOSKSuccessActivity.this, IdentifyUserActivity.class);
            loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginActivityIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        //it disables the back button functionality
    }
}
