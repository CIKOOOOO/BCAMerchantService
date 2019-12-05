package com.andrew.bcamerchantservice.ui.loyalty;

import android.support.annotation.NonNull;

import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
                        List<Merchant.Income> incomeList = new ArrayList<>();
                        List<Merchant.Mission> missionList = new ArrayList<>();
                        Merchant merchant = dataSnapshot.getValue(Merchant.class);
                        if (dataSnapshot.child("merchant_income" + "/"
                                + Utils.getTime("MM-yyyy")).getChildrenCount() > 0) {
                            for (DataSnapshot snapshot : dataSnapshot.child("merchant_income" + "/" + Utils.getTime("MM-yyyy")).getChildren()) {
                                incomeList.add(snapshot.getValue(Merchant.Income.class));
                            }
                        }

                        if (dataSnapshot.child(Constant.DB_REFERENCE_MERCHANT_MISSION
                                + "/" + Utils.getTime("MM-yyyy")).getChildrenCount() > 0) {
                            for (DataSnapshot snapshot : dataSnapshot.child(Constant.DB_REFERENCE_MERCHANT_MISSION
                                    + "/" + Utils.getTime("MM-yyyy")).getChildren()) {
                                missionList.add(snapshot.getValue(Merchant.Mission.class));
                            }
                        }
                        view.onMerchantListener(merchant, incomeList, missionList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void loadMission(final List<Merchant.Mission> missionMerchantList, String position_id) {
        dbRef.child(Constant.DB_REFERENCE_LOYALTY + "/" + Constant.DB_REFERENCE_MISSION + "/" + position_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Loyalty.Mission> missionLoyaltyList = new ArrayList<>();

                        List<Loyalty.Mission> collectedList = new ArrayList<>();
                        List<Loyalty.Mission> nonCollectedList = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Loyalty.Mission missionLoyalty = snapshot.getValue(Loyalty.Mission.class);
                            if (missionLoyalty != null) {
                                boolean c = false;
                                for (int i = 0; i < missionMerchantList.size(); i++) {
                                    Merchant.Mission missions = missionMerchantList.get(i);
                                    if (missions.getMission_id().equals(missionLoyalty.getMission_id())) {
                                        c = true;
                                        break;
                                    }
                                }

                                missionLoyalty.setCollected(c);

                                if (c)
                                    collectedList.add(missionLoyalty);
                                else
                                    nonCollectedList.add(missionLoyalty);
                            }
                        }

                        Collections.sort(nonCollectedList, new Comparator<Loyalty.Mission>() {
                            @Override
                            public int compare(Loyalty.Mission mission, Loyalty.Mission t1) {
                                return (int) mission.getMission_minimum_transaction() - (int) t1.getMission_minimum_transaction();
                            }
                        });

                        Collections.sort(collectedList, new Comparator<Loyalty.Mission>() {
                            @Override
                            public int compare(Loyalty.Mission mission, Loyalty.Mission t1) {
                                return (int) mission.getMission_minimum_transaction() - (int) t1.getMission_minimum_transaction();
                            }
                        });

                        missionLoyaltyList.addAll(nonCollectedList);
                        missionLoyaltyList.addAll(collectedList);

                        view.onLoadMission(missionLoyaltyList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public void sendMission(Loyalty.Mission mission, String MID, int point) {
        String path = Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID + "/"
                + Constant.DB_REFERENCE_MERCHANT_MISSION + "/" + Utils.getTime("MM-yyyy");

        String key = dbRef.child(path).push().getKey();

        Merchant.Mission merchant_mission = new Merchant.Mission(key, mission.getMission_id(), Utils.getTime("dd/MM/yyyy HH:mm"));

        dbRef.child(path + "/" + key)
                .setValue(merchant_mission);

        point += mission.getMission_prize();

        String income_path = Constant.DB_REFERENCE_LOYALTY + "/" + Constant.DB_REFERENCE_POINT_HISTORY
                + "/" + MID + "/" + Utils.getTime("MM-yyyy") + "/" + Constant.DB_REFERENCE_POINT_HISTORY_EARN;
        String income_key = dbRef.child(income_path).push().getKey();

        Loyalty.Earn earn = new Loyalty.Earn(income_key, Utils.getTime("dd/MM/yyyy")
                , "reward", mission.getMission_prize());

//        Map<String, Object> pointMap = new HashMap<>();
//        pointMap.put("earn_id", income_key);
//        pointMap.put("earn_date", Utils.getTime("dd/MM/yyyy HH:mm"));
//        pointMap.put("earn_point", mission.getMission_prize());
//        pointMap.put("earn_type", "reward");

        dbRef.child(income_path + "/" + income_key)
                .setValue(earn);

        dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID + "/merchant_point")
                .setValue(point)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }
}
