package com.andrew.bcamerchantservice.ui.tabpromorequest.promostatus;

import android.support.annotation.NonNull;

import com.andrew.bcamerchantservice.model.PromoRequest;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromoStatusPresenter implements IPromoStatusPresenter {

    private IPromoStatusView view;
    private DatabaseReference dbRef;

    PromoStatusPresenter(IPromoStatusView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onLoadData(String MID, int MCC) {
        final String path = Constant.DB_REFERENCE_PROMO_REQUEST + "/" + Constant.DB_REFERENCE_MERCHANT_PROMO_REQUEST + "/" + MCC + "/" + MID;
        dbRef.child(path)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<PromoRequest> promoRequestList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final PromoRequest promoRequest = snapshot.getValue(PromoRequest.class);
                            if (promoRequest != null) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    Date date1 = sdf.parse(Utils.getTime("dd/MM/yyyy"));
                                    Date date2 = sdf.parse(promoRequest.getPromo_start_date());

                                    /*
                                     * Promo tidak boleh sesudah tanggal mulai
                                     * Promo tidak boleh berada dihari yang sama dengan tanggal mulai
                                     * */

                                    if (date1.compareTo(date2) < 0 && date1.compareTo(date2) != 0) {
                                        dbRef.child(Constant.DB_REFERENCE_PROMO_REQUEST)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        PromoRequest.PromoType promoType = dataSnapshot.child(Constant.DB_REFERENCE_PROMO_REQUEST_TYPE
                                                                + "/" + promoRequest.getPromo_type_id()).getValue(PromoRequest.PromoType.class);

                                                        PromoRequest.PromoStatus promoStatus = dataSnapshot.child(Constant.DB_REFERENCE_PROMO_STATUS
                                                                + "/" + promoRequest.getPromo_status()).getValue(PromoRequest.PromoStatus.class);

                                                        if (promoType != null && promoStatus != null)
                                                            view.loadPromoType(promoType, promoStatus);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                        promoRequestList.add(0, promoRequest);
                                    } else if (promoRequest.getPromo_status().equals("promo_status_2") || promoRequest.getPromo_status().equals("promo_status_1")) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("promo_status", "Ditolak");

                                        dbRef.child(path + "/" + promoRequest.getPromo_request_id())
                                                .updateChildren(map);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
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
