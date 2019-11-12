package com.andrew.bcamerchantservice.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.utils.BaseActivity;
import com.andrew.bcamerchantservice.utils.Constant;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity implements ILoginView, LoginAdapter.onClickItem {

    private ILoginPresenter iLoginPresenter;

    private LoginAdapter loginAdapter;

    private boolean exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initVar();
    }

    private void initVar() {
        exit = false;
        iLoginPresenter = new LoginPresenter(this);

        RecyclerView recycler_merchant_list = findViewById(R.id.recycler_merchant_login);

        loginAdapter = new LoginAdapter(this, new ArrayList<Merchant>(), this);

        recycler_merchant_list.setLayoutManager(new LinearLayoutManager(this));
        recycler_merchant_list.setAdapter(loginAdapter);

        iLoginPresenter.onLoadData();
    }

    @Override
    public void onLoginResult(Merchant merchant, String res) {
        getPrefConfig().insertMerchantData(merchant);
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onLoadData(List<Merchant> merchants) {
        loginAdapter.setMerchantList(merchants);
        loginAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoginFailed(String result) {
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(Merchant merchant) {
        getPrefConfig().insertMerchantData(merchant);
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finishAffinity();
        } else {
            Toast.makeText(this, getResources().getText(R.string.exit),
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, Constant.DELAY);
        }
    }
}
