package com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards;

import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;

public interface IDetailView {
    void onRedeemSuccess(Merchant.Rewards merchant_rewards, Loyalty.Rewards loyalty_rewards);
}
