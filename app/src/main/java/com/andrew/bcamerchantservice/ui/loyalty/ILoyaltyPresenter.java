package com.andrew.bcamerchantservice.ui.loyalty;

import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public interface ILoyaltyPresenter {

    void loadLoyaltyType();

    void loadLoyalty(String LID);

    int totalExpLoyalty(List<Loyalty> loyaltyList, Loyalty loyalty);

    void loadMerchantLoyaltyListener(String MID);

    void loadMission(List<Merchant.Mission> missionList, String position_id);

    void sendMission(Loyalty.Mission mission, String MID, int point);
}
