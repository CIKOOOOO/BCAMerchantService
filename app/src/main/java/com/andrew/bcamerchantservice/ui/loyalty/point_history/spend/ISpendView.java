package com.andrew.bcamerchantservice.ui.loyalty.point_history.spend;

import com.andrew.bcamerchantservice.model.Loyalty;

import java.util.List;

public interface ISpendView {
    void onLoadSpendList(List<Loyalty.Spend> spends);

    void onLoadRewards(Loyalty.Rewards rewards);
}
