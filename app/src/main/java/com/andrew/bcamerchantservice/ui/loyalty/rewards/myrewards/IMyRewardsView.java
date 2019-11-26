package com.andrew.bcamerchantservice.ui.loyalty.rewards.myrewards;

import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public interface IMyRewardsView {
    void onLoadMerchantRewards(List<Merchant.Rewards> rewards);

    void onLoadRewards(Loyalty.Rewards rewards);
}
