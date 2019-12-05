package com.andrew.bcamerchantservice.ui.loyalty.rewards.myrewards;

import android.support.annotation.NonNull;
import android.util.Log;

import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyRewardsPresenter implements IMyRewardsPresenter {
    private IMyRewardsView view;
    private DatabaseReference dbRef;

    public MyRewardsPresenter(IMyRewardsView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    private void loadRewardLoyalty(final String rewards_id) {
        //
        dbRef.child(Constant.DB_REFERENCE_LOYALTY + "/" + Constant.DB_REFERENCE_REWARDS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.child(rewards_id).getValue() != null) {
                                Loyalty.Rewards rewards = snapshot.child(rewards_id).getValue(Loyalty.Rewards.class);
                                view.onLoadRewards(rewards);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void loadRewards(String MID) {
        dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID + "/" + Constant.DB_REFERENCE_MERCHANT_REWARDS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Merchant.Rewards> rewardsList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Merchant.Rewards rewards = snapshot.getValue(Merchant.Rewards.class);
                            try {
                                Date date = new SimpleDateFormat("dd/MM/yyyy").parse(rewards.getMerchant_voucher_valid_date());

                                if (new Date().before(date) || Utils.getTime("dd/MM/yyyy")
                                        .equals(Utils.formatDateFromDateString("dd/MM/yyyy HH:mm"
                                                , "dd/MM/yyyy", rewards.getMerchant_voucher_valid_date()))) {
                                    loadRewardLoyalty(rewards.getRewards_id());
                                    rewardsList.add(rewards);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                        view.onLoadMerchantRewards(rewardsList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
