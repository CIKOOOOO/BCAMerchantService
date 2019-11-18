package com.andrew.bcamerchantservice.ui.loyalty;

import com.andrew.bcamerchantservice.model.Loyalty;

import java.util.List;

public interface ILoyaltyPresenter {

    void loadLoyaltyType();

    void loadLoyalty(String LID);

    int totalExpLoyalty(List<Loyalty> loyaltyList, Loyalty loyalty);

    void loadMerchantLoyaltyListener(String MID);
}
