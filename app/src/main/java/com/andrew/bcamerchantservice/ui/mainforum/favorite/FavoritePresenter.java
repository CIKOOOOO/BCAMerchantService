package com.andrew.bcamerchantservice.ui.mainforum.favorite;

import android.support.annotation.NonNull;
import android.util.Log;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Constant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoritePresenter implements IFavoritePresenter {

    private DatabaseReference dbRef;
    private IFavoriteView view;

    public FavoritePresenter(IFavoriteView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public void onGetFavoriteList(final String MID) {
        dbRef.child(Constant.DB_REFERENCE_FORUM_FAVORITE + "/" + MID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final List<Forum> forumList = new ArrayList<>();
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + snapshot.getKey())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Forum forum = dataSnapshot.getValue(Forum.class);
                                            if (dataSnapshot.child(Constant.DB_REFERENCE_FORUM_HIDDEN).getChildrenCount() == 0) {
                                                dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE)
                                                        .child(forum.getMid())
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                                                                if (dataSnapshots.getValue(Merchant.class) == null)
                                                                    return;
                                                                view.onMerchantProfile(dataSnapshots.getValue(Merchant.class));
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                forumList.add(0, forum);
                                            } else {
                                                boolean isHide = false;
                                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                    if (snapshot1.getKey().equals(MID)) {
                                                        isHide = true;
                                                        break;
                                                    }
                                                }

                                                if (!isHide) {
                                                    dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE)
                                                            .child(forum.getMid())
                                                            .addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                                                                    if (dataSnapshots.getValue(Merchant.class) == null)
                                                                        return;
                                                                    view.onMerchantProfile(dataSnapshots.getValue(Merchant.class));
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                    forumList.add(0, forum);
                                                }
                                            }
                                            view.onLoadForum(forumList);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onUpdateViewCount(Map<String, Object> map, final Forum forum, final Merchant merchant) {
        dbRef.child(Constant.DB_REFERENCE_FORUM).updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.onSuccessUpdateViewCount(forum, merchant);
                    }
                });
    }

    @Override
    public void onRemoveThread(String FID, final int pos) {
        dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + FID)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.onSuccessDeleteThread(pos);
                    }
                });
    }
}
