package com.andrew.bcamerchantservice.ui.profile.mystoreinformation;

import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public interface IMyStoreInformationView {
    void onSuccessEditProfile(String value, String KEY);

    void onLoadCatalog(List<Merchant.MerchantCatalog> catalogList);

    void onSuccessDeleteCatalog(int pos);
}
