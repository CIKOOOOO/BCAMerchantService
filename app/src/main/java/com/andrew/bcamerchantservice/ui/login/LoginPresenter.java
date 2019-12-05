package com.andrew.bcamerchantservice.ui.login;

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

public class LoginPresenter implements ILoginPresenter {

    private ILoginView iLoginView;
    private DatabaseReference dbLogin;

    LoginPresenter(ILoginView iLoginView) {
        this.iLoginView = iLoginView;
        dbLogin = FirebaseDatabase.getInstance().getReference();
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

    @Override
    public void onGetPositionDetail(String position_id) {
        dbLogin.child(Constant.DB_REFERENCE_MERCHANT_POSITION + "/" + position_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(Merchant.Position.class) != null) {
                            Merchant.Position position = dataSnapshot.getValue(Merchant.Position.class);
                            iLoginView.onLoadPositionData(position);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
