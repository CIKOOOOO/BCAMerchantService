package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.confirmationpromo;

import android.support.annotation.NonNull;

import com.andrew.bcamerchantservice.model.PromoRequest;
import com.andrew.bcamerchantservice.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConfirmationPromoRequestPresenter implements IConfirmationPromoRequestPresenter {

    private IConfirmationPromoRequestView view;
    private DatabaseReference dbRef;

    public ConfirmationPromoRequestPresenter(IConfirmationPromoRequestView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public void getPromoData(String promo_type_id) {
        dbRef.child(Constant.DB_REFERENCE_PROMO_REQUEST + "/" + Constant.DB_REFERENCE_PROMO_REQUEST_TYPE + "/" + promo_type_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(PromoRequest.PromoType.class) != null) {
                            view.onLoadPromoTypeData(dataSnapshot.getValue(PromoRequest.PromoType.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
