package com.andrew.bcamerchantservice.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.ui.login.LoginActivity;
import com.andrew.bcamerchantservice.utils.BaseActivity;
import com.andrew.bcamerchantservice.utils.Constant;

public class SplashScreen extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            }
        }, Constant.DELAY);
    }
}
