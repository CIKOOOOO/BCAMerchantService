package com.andrew.bcamerchantservice.ui.login;

import android.support.annotation.NonNull;
import android.util.Log;

import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Constant;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginPresenter implements ILoginPresenter {

    private ILoginView iLoginView;
    private DatabaseReference dbLogin;

    LoginPresenter(ILoginView iLoginView) {
        this.iLoginView = iLoginView;
        dbLogin = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onLogin(String MID) {
        if (MID.isEmpty()) {
            iLoginView.onLoginFailed("Cannot be empty");
        } else {
            dbLogin.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        iLoginView.onLoginFailed("MID is not found");
                        return;
                    }
                    Merchant merchant = dataSnapshot.getValue(Merchant.class);
                    iLoginView.onLoginResult(merchant, "Login Success!");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onLoadData() {
        dbLogin.child(Constant.DB_REFERENCE_MERCHANT_PROFILE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Merchant> merchantList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            merchantList.add(snapshot.getValue(Merchant.class));
                        }
                        iLoginView.onLoadData(merchantList);
//                        if (dataSnapshot.getValue() == null) {
//                            iLoginView.onLoginFailed("MID is not found");
//                            return;
//                        }
//                        Merchant merchant = dataSnapshot.getValue(Merchant.class);
//                        iLoginView.onLoginResult(merchant, "Login Success!");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
