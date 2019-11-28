package com.andrew.bcamerchantservice.ui.loyalty.point_history.earn;

import com.andrew.bcamerchantservice.model.Loyalty;

import java.util.List;

public interface IEarnView {
    void onLoadEarn(List<Loyalty.Earn> earnList);
}