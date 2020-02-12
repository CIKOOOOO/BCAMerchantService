package com.andrew.bcamerchantservice.ui.main;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.andrew.bcamerchantservice.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainPresenter implements IMainPresenter {

    private IMainView iMainView;
    private DatabaseReference dbRef;

    MainPresenter(IMainView iMainView) {
        this.iMainView = iMainView;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onBackPressFragment() {


    }

    @Override
    public void changeFragment(Fragment fragment, FragmentTransaction fragmentTransaction) {
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void checkConnectionInternet() {
        dbRef.child(".info/connected")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        iMainView.onConnectedToInternet(dataSnapshot.getValue(Boolean.class));
                        Log.e("asd", dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
