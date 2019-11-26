package com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards;

import com.andrew.bcamerchantservice.model.Loyalty;

public interface IDetailRewardsPresenter {
    void sendReward(String MID, Loyalty.Rewards rewards, int point);
}
