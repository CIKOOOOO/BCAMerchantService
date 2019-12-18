package com.andrew.bcamerchantservice.ui.profile.mystoreinformation;

import android.support.annotation.NonNull;
import android.util.Log;

import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Constant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyStoreInformationPresenter implements IMyStoreInformationPresenter {
    private static final String TAG = MyStoreInformationPresenter.class.getSimpleName();
    private DatabaseReference dbRef;
    private IMyStoreInformationView view;

    public MyStoreInformationPresenter(IMyStoreInformationView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void editProfile(String MID, final String key, final String value) {
        dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID + "/" + key)
                .setValue(value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.onSuccessEditProfile(value, key);
                    }
                });
    }

    @Override
    public void onLoadCatalog(String MID) {
        dbRef.child(Constant.DB_REFERENCE_MERCHANT_CATALOG + "/" + MID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Merchant.MerchantCatalog> catalogList = new ArrayList<>();
                        if (dataSnapshot.getChildrenCount() == 0)
                            Log.e(TAG, "MID doesn't have any catalog");
                        else {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                catalogList.add(0, snapshot.getValue(Merchant.MerchantCatalog.class));
                            }
                        }
                        view.onLoadCatalog(catalogList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onDeleteCatalog(String MID, String CID, final int pos) {
        dbRef.child(Constant.DB_REFERENCE_MERCHANT_CATALOG + "/" + MID + "/" + CID)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.onSuccessDeleteCatalog(pos);
                    }
                });
    }

    @Override
    public void onUpdateInformationProfile(String MID, final boolean isHide) {
        Map<String, Object> map = new HashMap<>();
        map.put("information_hide", isHide);
        dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID)
                .updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.onUpdateInformationProfile(isHide);
                    }
                });
    }
}
