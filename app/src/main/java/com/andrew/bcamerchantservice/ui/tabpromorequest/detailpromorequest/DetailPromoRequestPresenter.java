package com.andrew.bcamerchantservice.ui.tabpromorequest.detailpromorequest;

import android.support.annotation.NonNull;

import com.andrew.bcamerchantservice.model.PromoRequest;
import com.andrew.bcamerchantservice.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailPromoRequestPresenter implements IDetailPromoRequestPresenter {

    private IDetailPromoRequestView view;
    private DatabaseReference dbRef;

    public DetailPromoRequestPresenter(IDetailPromoRequestView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public void loadPromoRequest(String MID, final String promo_request) {
        final String path = Constant.DB_REFERENCE_PROMO_REQUEST + "/" + Constant.DB_REFERENCE_MERCHANT_PROMO_REQUEST
                + "/" + MID + "/" + promo_request;
        dbRef.child(path)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(PromoRequest.class) != null) {
                            final PromoRequest promoRequest = dataSnapshot.getValue(PromoRequest.class);
                            if (promoRequest != null) {
                                view.onLoadPromoData(promoRequest);
                                dbRef.child(Constant.DB_REFERENCE_PROMO_REQUEST)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                PromoRequest.PromoType promoType = dataSnapshot.child(Constant.DB_REFERENCE_PROMO_REQUEST_TYPE
                                                        + "/" + promoRequest.getPromo_type_id()).getValue(PromoRequest.PromoType.class);

                                                PromoRequest.PromoStatus promoStatus = dataSnapshot.child(Constant.DB_REFERENCE_PROMO_STATUS
                                                        + "/" + promoRequest.getPromo_status()).getValue(PromoRequest.PromoStatus.class);

                                                if (promoType != null && promoStatus != null)
                                                    view.onLoadPromoType(promoType, promoStatus);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }

                            List<PromoRequest.Facilities> facilitiesList = new ArrayList<>();
                            List<PromoRequest.Logo> logoList = new ArrayList<>();
                            List<PromoRequest.Product> productList = new ArrayList<>();
                            String special_facilities = "";

                            for (DataSnapshot snapshot : dataSnapshot.child(Constant.DB_REFERENCE_MERCHANT_FACILITIES
                                    + "/" + Constant.DB_REFERENCE_FACILITIES).getChildren()) {
                                facilitiesList.add(snapshot.getValue(PromoRequest.Facilities.class));
                            }

                            if (dataSnapshot.child(Constant.DB_REFERENCE_MERCHANT_FACILITIES + "/specific_facilities").getValue() != null) {
                                special_facilities = dataSnapshot.child(Constant.DB_REFERENCE_MERCHANT_FACILITIES + "/specific_facilities").getValue(String.class);
                            }

                            for (DataSnapshot snapshot : dataSnapshot.child(Constant.DB_REFERENCE_PROMO_REQUEST_LOGO).getChildren()) {
                                logoList.add(snapshot.getValue(PromoRequest.Logo.class));
                            }

                            for (DataSnapshot snapshot : dataSnapshot.child(Constant.DB_REFERENCE_PROMO_REQUEST_PRODUCT).getChildren()) {
                                productList.add(snapshot.getValue(PromoRequest.Product.class));
                            }

                            view.onLoadRestData(special_facilities, facilitiesList, logoList, productList);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void loadStatusPromoTypeRequest(final String promo_status_id, final String promo_type_id) {
        dbRef.child(Constant.DB_REFERENCE_PROMO_REQUEST)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        PromoRequest.PromoType promoType = dataSnapshot.child(Constant.DB_REFERENCE_PROMO_REQUEST_TYPE
                                + "/" + promo_type_id).getValue(PromoRequest.PromoType.class);

                        PromoRequest.PromoStatus promoStatus = dataSnapshot.child(Constant.DB_REFERENCE_PROMO_STATUS
                                + "/" + promo_status_id).getValue(PromoRequest.PromoStatus.class);

                        if (promoType != null && promoStatus != null)
                            view.onLoadPromoType(promoType, promoStatus);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
