package com.andrew.bcamerchantservice.ui.loyalty.point_history.earn;

import android.support.annotation.NonNull;

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

public class EarnPresenter implements IEarnPresenter {

    private IEarnView view;
    private DatabaseReference dbRef;

    EarnPresenter(IEarnView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void loadMerchantEarningListener(String MID) {
        dbRef.child(Constant.DB_REFERENCE_LOYALTY + "/" + Constant.DB_REFERENCE_POINT_HISTORY + "/"
                + MID + "/" + Utils.getTime("MM-yyyy") + "/" + Constant.DB_REFERENCE_POINT_HISTORY_EARN)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Loyalty.Earn> earnList = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Loyalty.Earn earn = snapshot.getValue(Loyalty.Earn.class);
                            /*
                             * Search for the same date, if exist then add it
                             * If false, then add new parent with data in it
                             * */
                            boolean c = false;
                            for (int i = 0; i < earnList.size(); i++) {
                                if (earn.getEarn_date().equals(earnList.get(i).getEarn_date())) {
                                    earnList.add(earn);
                                    c = true;
                                    break;
                                }
                            }

                            if (!c) {
                                earnList.add(new Loyalty.Earn("parent", earn.getEarn_date(), "", 0));
                                earnList.add(earn);
                            }
                        }

                        Collections.sort(earnList, new Comparator<Loyalty.Earn>() {
                            @Override
                            public int compare(Loyalty.Earn spend, Loyalty.Earn t1) {
                                return t1.getEarn_date().compareTo(spend.getEarn_date());
                            }
                        });

                        view.onLoadEarn(earnList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
