package com.andrew.bcamerchantservice.ui.loyalty.rewards.allrewards;

import android.support.annotation.NonNull;
import android.util.Log;

import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllRewardsPresenter implements IAllRewardsPresenter {

    private IAllRewardsView view;
    private DatabaseReference dbRef;

    public AllRewardsPresenter(IAllRewardsView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void loadRewardsLoyalty(final String position_id) {
        dbRef.child(Constant.DB_REFERENCE_LOYALTY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, List<Loyalty.Rewards>> rewardsMap = new HashMap<>();
                        List<Loyalty> loyaltyList = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.child(Constant.DB_REFERENCE_LOYALTY_TYPE).getChildren()) {
                            loyaltyList.add(snapshot.getValue(Loyalty.class));
                        }

                        for (DataSnapshot snapshot : dataSnapshot.child(Constant.DB_REFERENCE_REWARDS).getChildren()) {
                            List<Loyalty.Rewards> rewardsList = new ArrayList<>();
                            for (DataSnapshot rewardsSnapshot : snapshot.child(position_id).getChildren()) {
                                rewardsList.add(rewardsSnapshot.getValue(Loyalty.Rewards.class));
                            }
                            if (snapshot.getKey() != null && !rewardsMap.containsKey(snapshot.getKey()))
                                rewardsMap.put(snapshot.getKey(), rewardsList);
                        }
                        view.onLoadLoyalty(loyaltyList, rewardsMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
