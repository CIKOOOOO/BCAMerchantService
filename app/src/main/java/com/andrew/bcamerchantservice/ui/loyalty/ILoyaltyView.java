package com.andrew.bcamerchantservice.ui.loyalty;

import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public interface ILoyaltyView {

    void onLoadRankType(List<Loyalty> loyaltyList);

    void loyaltyListener(Loyalty loyalty);

    void onMerchantListener(Merchant merchant);
}
