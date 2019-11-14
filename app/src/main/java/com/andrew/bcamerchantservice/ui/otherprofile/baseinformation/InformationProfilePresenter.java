package com.andrew.bcamerchantservice.ui.otherprofile.baseinformation;

import android.support.annotation.NonNull;
import android.util.Log;

import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InformationProfilePresenter implements IInformationProfilePresenter {
    private static final String TAG = InformationProfilePresenter.class.getSimpleName();
    private IInformationProfileView view;
    private DatabaseReference dbRef;

    public InformationProfilePresenter(IInformationProfileView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
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
                                catalogList.add(0,snapshot.getValue(Merchant.MerchantCatalog.class));
                            }
                        }
                        view.onLoadCatalog(catalogList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
