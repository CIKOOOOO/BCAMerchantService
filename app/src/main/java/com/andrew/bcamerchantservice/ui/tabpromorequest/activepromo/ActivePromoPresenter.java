package com.andrew.bcamerchantservice.ui.tabpromorequest.activepromo;

import android.support.annotation.NonNull;

import com.andrew.bcamerchantservice.model.PromoRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivePromoPresenter implements IActivePromoPresenter {

    private IActivePromoView view;
    private DatabaseReference dbRef;

    public ActivePromoPresenter(IActivePromoView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public void loadActivePromo(final String MID, final int MCC) {
        dbRef.child(Constant.DB_REFERENCE_PROMO_REQUEST)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<PromoRequest> promoRequestList = new ArrayList<>();
                            String promo_request_path = Constant.DB_REFERENCE_MERCHANT_PROMO_REQUEST + "/" + MCC + "/" + MID;
                        for (DataSnapshot snapshot : dataSnapshot.child(promo_request_path).getChildren()) {
                            PromoRequest promoRequest = snapshot.getValue(PromoRequest.class);
                            if (promoRequest != null) {
                                if (promoRequest.getPromo_status().equals("promo_status_4")) {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                        Date date1 = sdf.parse(Utils.getTime("dd/MM/yyyy"));
                                        Date date2 = sdf.parse(promoRequest.getPromo_start_date());
                                        if (date1.compareTo(date2) >= 0) {
//                                             || date1.compareTo(date2) == 0
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("promo_status", "promo_status_5");

                                            final String path = Constant.DB_REFERENCE_PROMO_REQUEST
                                                    + "/" + Constant.DB_REFERENCE_MERCHANT_PROMO_REQUEST
                                                    + "/" + MCC + "/" + MID + "/" + promoRequest.getPromo_request_id();

                                            dbRef.child(path)
                                                    .updateChildren(map);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (promoRequest.getPromo_status().equals("promo_status_5")
                                        || promoRequest.getPromo_status().equals("promo_status_6")) {

                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                        Date date1 = sdf.parse(Utils.getTime("dd/MM/yyyy"));
                                        Date date2 = sdf.parse(promoRequest.getPromo_end_date());
                                        if (date1.compareTo(date2) > 0) {
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("promo_status", "promo_status_6");

                                            final String path = Constant.DB_REFERENCE_PROMO_REQUEST
                                                    + "/" + Constant.DB_REFERENCE_MERCHANT_PROMO_REQUEST
                                                    + "/" + MCC + "/" + MID + "/" + promoRequest.getPromo_request_id();

                                            dbRef.child(path)
                                                    .updateChildren(map);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    promoRequestList.add(0, promoRequest);

                                    String promo_status_path = Constant.DB_REFERENCE_PROMO_STATUS + "/" + promoRequest.getPromo_status();
                                    PromoRequest.PromoStatus promoStatus = dataSnapshot.child(promo_status_path)
                                            .getValue(PromoRequest.PromoStatus.class);

                                    String promo_type_path = Constant.DB_REFERENCE_PROMO_REQUEST_TYPE + "/" + promoRequest.getPromo_type_id();
                                    PromoRequest.PromoType promoType = dataSnapshot.child(promo_type_path)
                                            .getValue(PromoRequest.PromoType.class);

                                    if (promoStatus != null && promoType != null)
                                        view.loadPromoType(promoType, promoStatus);
                                }
                            }
                        }
                        view.loadData(promoRequestList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
