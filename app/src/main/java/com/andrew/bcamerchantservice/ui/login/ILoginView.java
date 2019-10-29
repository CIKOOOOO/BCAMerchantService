package com.andrew.bcamerchantservice.ui.login;

import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public interface ILoginView {
    void onLoginResult(Merchant merchant, String result);

    void onLoadData(List<Merchant> merchants);

    void onLoginFailed(String result);
}
