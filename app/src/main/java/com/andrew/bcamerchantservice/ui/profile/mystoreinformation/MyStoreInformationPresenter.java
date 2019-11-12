package com.andrew.bcamerchantservice.ui.profile.mystoreinformation;

import com.andrew.bcamerchantservice.utils.Constant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyStoreInformationPresenter implements IMyStoreInformationPresenter {

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
}
