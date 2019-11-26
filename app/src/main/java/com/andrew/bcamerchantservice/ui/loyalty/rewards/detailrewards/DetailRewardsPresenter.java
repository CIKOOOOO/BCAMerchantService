package com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards;

import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.RandomString;
import com.andrew.bcamerchantservice.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class DetailRewardsPresenter implements IDetailRewardsPresenter {

    private IDetailView view;
    private DatabaseReference dbRef;

    public DetailRewardsPresenter(IDetailView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public void sendReward(final String MID, final Loyalty.Rewards loyalty_rewards, final int point) {
        String path = Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID + "/" + Constant.DB_REFERENCE_MERCHANT_REWARDS;
        final String key = dbRef.child(path).push().getKey();
        String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
        RandomString tickets = new RandomString(23, new SecureRandom(), easy);

        final Merchant.Rewards rewards = new Merchant.Rewards(key, loyalty_rewards.getRewards_id(), tickets.nextString()
                , "", Utils.getTime("dd/MM/yyyy HH:mm"), false);

        dbRef.child(path + "/" + key)
                .setValue(rewards)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String point_path = Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID + "/merchant_point";
                        Map<String, Object> point_map = new HashMap<>();
                        point_map.put(point_path, point);

                        dbRef.updateChildren(point_map);

                        String spend_path = Constant.DB_REFERENCE_LOYALTY + "/" + Constant.DB_REFERENCE_POINT_HISTORY
                                + "/" + MID + "/" + Utils.getTime("MM-yyyy") + "/" + Constant.DB_REFERENCE_POINT_HISTORY_SPEND;
                        String key = dbRef.child(spend_path).push().getKey();

                        Loyalty.Spend spend = new Loyalty.Spend(key, loyalty_rewards.getRewards_id()
                                , Utils.getTime("dd/MM/yyyy"), loyalty_rewards.getRewards_point());

                        dbRef.child(spend_path + "/" + key)
                                .setValue(spend)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        view.onRedeemSuccess(rewards, loyalty_rewards);
                                    }
                                });
                    }
                });
    }
}
