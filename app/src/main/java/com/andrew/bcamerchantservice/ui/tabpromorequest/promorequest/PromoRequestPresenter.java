package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest;

import android.support.annotation.NonNull;

import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.PromoRequest;
import com.andrew.bcamerchantservice.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PromoRequestPresenter implements IPromoRequestPresenter {

    private IPromoRequestView view;
    private DatabaseReference dbRef;

    public PromoRequestPresenter(IPromoRequestView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public void loadPromoType() {
        dbRef.child(Constant.DB_REFERENCE_PROMO_REQUEST + "/" + Constant.DB_REFERENCE_PROMO_REQUEST_TYPE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<PromoRequest.PromoType> promoTypeList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            promoTypeList.add(snapshot.getValue(PromoRequest.PromoType.class));
                        }
                        view.onLoadPromoType(promoTypeList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void facilities(String facilities_id) {
        dbRef.child(Constant.DB_REFERENCE_FACILITIES + "/" + facilities_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(PromoRequest.Facilities.class) != null)
                            view.onLoadPaymentType(dataSnapshot.getValue(PromoRequest.Facilities.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void loadPaymentType(String MID) {
        dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID + "/" + Constant.DB_REFERENCE_MERCHANT_FACILITIES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            facilities(snapshot.getValue(Merchant.Facilities.class).getFacilities_id());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
