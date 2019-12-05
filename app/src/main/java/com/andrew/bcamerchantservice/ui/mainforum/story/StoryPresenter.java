package com.andrew.bcamerchantservice.ui.mainforum.story;

import android.support.annotation.NonNull;

import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StoryPresenter implements IStoryPresenter {

    private IStoryView view;
    private DatabaseReference dbRef;

    public StoryPresenter(IStoryView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClickStory(String MID) {
        dbRef.child(Constant.DB_REFERENCE_MERCHANT_STORY + "/" + MID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Merchant.MerchantStory> storyList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            storyList.add(0, snapshot.getValue(Merchant.MerchantStory.class));
                        }
                        view.onLoadStory(storyList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}

