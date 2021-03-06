package com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards;

import android.util.Log;

import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.RandomString;
import com.andrew.bcamerchantservice.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
                , "", Utils.getTime("dd/MM/yyyy HH:mm"), "", false);

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

    @Override
    public void useReward(String MID, String merchant_rewards_id) {
        final String current_date = Utils.getTime("dd/MM/yyyy HH:mm");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(Utils.getTime("dd/MM/yyyy HH:mm"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(myDate);
        calendar.add(Calendar.DAY_OF_YEAR, +7);
        Date newDate = calendar.getTime();
        String valid_date = dateFormat.format(newDate);

        String path = Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID + "/"
                + Constant.DB_REFERENCE_MERCHANT_REWARDS + "/" + merchant_rewards_id;

        Map<String, Object> rewardUpdate = new HashMap<>();
        rewardUpdate.put(path + "/merchant_rewards_date_collect", current_date);
        rewardUpdate.put(path + "/rewards_is_used", true);
        rewardUpdate.put(path + "/merchant_voucher_valid_date", valid_date);

        dbRef.updateChildren(rewardUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.onUseSuccess(current_date);
                    }
                });
    }
}
