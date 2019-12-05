package com.andrew.bcamerchantservice.ui.login;

import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public interface ILoginView {
    void onLoadData(List<Merchant> merchants);

    void onLoadPositionData(Merchant.Position position);
}
