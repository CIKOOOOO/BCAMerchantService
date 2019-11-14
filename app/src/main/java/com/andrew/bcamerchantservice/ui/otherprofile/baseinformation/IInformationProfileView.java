package com.andrew.bcamerchantservice.ui.otherprofile.baseinformation;

import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public interface IInformationProfileView {
    void onLoadCatalog(List<Merchant.MerchantCatalog> catalogList);
}
