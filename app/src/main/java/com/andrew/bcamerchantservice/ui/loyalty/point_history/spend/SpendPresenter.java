package com.andrew.bcamerchantservice.ui.loyalty.point_history.spend;

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
import java.util.List;

public class SpendPresenter implements ISpendPresenter {

    private ISpendView view;
    private DatabaseReference dbRef;

    public SpendPresenter(ISpendView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void loadSpendListener(String MID) {
        dbRef.child(Constant.DB_REFERENCE_LOYALTY + "/" + Constant.DB_REFERENCE_POINT_HISTORY + "/"
                + MID + "/" + Utils.getTime("MM-yyyy") + "/" + Constant.DB_REFERENCE_POINT_HISTORY_SPEND)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Loyalty.Spend> spendList = new ArrayList<>();

                        int i = 0;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String date = "";
                            Loyalty.Spend spend = snapshot.getValue(Loyalty.Spend.class);
                            if (i == 0) {
                                date = spend.getSpend_date();
                            } else if (!date.equals(spend.getSpend_date())) {
                                date = spend.getSpend_date();
                            }
//                            spendList.add();
                            i++;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
