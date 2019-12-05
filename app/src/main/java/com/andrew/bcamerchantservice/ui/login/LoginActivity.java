package com.andrew.bcamerchantservice.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.utils.BaseActivity;
import com.andrew.bcamerchantservice.utils.Constant;

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
    public void onLoadData(List<Merchant> merchants) {
        loginAdapter.setMerchantList(merchants);
        loginAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadPositionData(Merchant.Position position) {
        findViewById(R.id.custom_loading_login).setVisibility(View.GONE);
        getPrefConfig().insertMerchantPosition(position);
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onClick(Merchant merchant) {
        findViewById(R.id.custom_loading_login).setVisibility(View.VISIBLE);
        iLoginPresenter.onGetPositionDetail(merchant.getMerchant_position());
        getPrefConfig().insertMerchantData(merchant);
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
