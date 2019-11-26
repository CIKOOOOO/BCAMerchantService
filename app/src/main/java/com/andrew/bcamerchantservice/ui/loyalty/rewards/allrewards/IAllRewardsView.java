package com.andrew.bcamerchantservice.ui.loyalty.rewards.allrewards;

import com.andrew.bcamerchantservice.model.Loyalty;

import java.util.List;
import java.util.Map;

public interface IAllRewardsView {
    void onLoadLoyalty(List<Loyalty> loyaltyList, Map<String, List<Loyalty.Rewards>> map);
}
