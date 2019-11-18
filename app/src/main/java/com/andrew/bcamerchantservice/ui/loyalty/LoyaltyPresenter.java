package com.andrew.bcamerchantservice.ui.loyalty;

import android.support.annotation.NonNull;

import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoyaltyPresenter implements ILoyaltyPresenter {

    private ILoyaltyView view;
    private DatabaseReference dbRef;

    public LoyaltyPresenter(ILoyaltyView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public void loadLoyaltyType() {
        String path = Constant.DB_REFERENCE_LOYALTY + "/" + Constant.DB_REFERENCE_LOYALTY_TYPE;

        dbRef.child(path)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Loyalty> loyaltyList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            loyaltyList.add(snapshot.getValue(Loyalty.class));
                        }
                        view.onLoadRankType(loyaltyList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void loadLoyalty(String LID) {
        String path = Constant.DB_REFERENCE_LOYALTY + "/" + Constant.DB_REFERENCE_LOYALTY_TYPE + "/" + LID;
        dbRef.child(path)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Loyalty loyalty = dataSnapshot.getValue(Loyalty.class);
                        view.loyaltyListener(loyalty);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int totalExpLoyalty(List<Loyalty> loyaltyList, Loyalty loyalty) {
        int totalExp = 0;
        for (int i = 0; i < loyaltyList.size(); i++) {
            Loyalty loyalty1 = loyaltyList.get(i);

            if (loyalty1.getLoyalty_id().equals(loyalty.getLoyalty_id()))
                break;

            totalExp += loyalty1.getLoyalty_exp();
        }
        return totalExp;
    }

    @Override
    public void loadMerchantLoyaltyListener(String MID) {
        String path = Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID;
        dbRef.child(path)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Merchant merchant = dataSnapshot.getValue(Merchant.class);
                        view.onMerchantListener(merchant);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void loadMission() {
        dbRef.child(Constant.DB_REFERENCE_LOYALTY + "/" + Constant.DB_REFERENCE_MISSION)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Loyalty.Mission> missionList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            missionList.add(snapshot.getValue(Loyalty.Mission.class));
                        }
                        view.onLoadMission(missionList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
