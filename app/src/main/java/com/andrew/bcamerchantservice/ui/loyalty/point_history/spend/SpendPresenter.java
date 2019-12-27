package com.andrew.bcamerchantservice.ui.loyalty.point_history.spend;

import android.support.annotation.NonNull;
import android.util.Log;

import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SpendPresenter implements ISpendPresenter {

    private ISpendView view;
    private DatabaseReference dbRef;

    public SpendPresenter(ISpendView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    private void gettingRewardsData(final String rewards_id) {
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
    public void loadSpendListener(String MID) {
        dbRef.child(Constant.DB_REFERENCE_LOYALTY + "/" + Constant.DB_REFERENCE_POINT_HISTORY + "/"
                + MID + "/" + Utils.getTime("MM-yyyy") + "/" + Constant.DB_REFERENCE_POINT_HISTORY_SPEND)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Loyalty.Spend> spendList = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Loyalty.Spend spend = snapshot.getValue(Loyalty.Spend.class);

                            /*
                             * Search for the same date, if exist then add it
                             * If false, then add new parent with data in it
                             * */
                            boolean c = false;
                            for (int i = 0; i < spendList.size(); i++) {
                                if (spend.getSpend_date().equals(spendList.get(i).getSpend_date())) {
                                    spendList.add(spend);
                                    c = true;
                                    break;
                                }
                            }

                            if (!c) {
                                spendList.add(spendList.size(), new Loyalty.Spend("parent", "", spend.getSpend_date(), 0));
                                spendList.add(spend);
                            }
                            gettingRewardsData(spend.getRewards_id());
                        }

                        Collections.sort(spendList, new Comparator<Loyalty.Spend>() {
                            @Override
                            public int compare(Loyalty.Spend spend, Loyalty.Spend t1) {
                                return t1.getSpend_date().compareTo(spend.getSpend_date());
                            }
                        });

                        view.onLoadSpendList(spendList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
