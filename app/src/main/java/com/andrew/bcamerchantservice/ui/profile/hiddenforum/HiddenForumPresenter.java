package com.andrew.bcamerchantservice.ui.profile.hiddenforum;

import android.support.annotation.NonNull;

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

public class HiddenForumPresenter implements IHiddenForumPresenter {
    private DatabaseReference dbRef;
    private IHiddenForumView view;

    public HiddenForumPresenter(IHiddenForumView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public void loadHiddenForum(final String MID) {
        dbRef.child(Constant.DB_REFERENCE_FORUM_HIDDEN + "/" + MID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final List<Forum> forumList = new ArrayList<>();
                        if (dataSnapshot.getChildrenCount() == 0) {
                            // Data is null
                            view.onLoadHiddenForum(forumList, "");
                        } else {
                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + snapshot.child("fid").getValue().toString())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() == null) {
                                                    /*
                                                     * Kondisi ini akan masuk, jika thread yang dicari ternyata sudah tidak ada / ter-delete
                                                     * Jadi list dr hidden list akan dihapus
                                                     * */
                                                    dbRef.child(Constant.DB_REFERENCE_FORUM_HIDDEN + "/" + MID + "/"
                                                            + snapshot.child("fhid").getValue().toString())
                                                            .removeValue();
                                                } else {
                                                    String mid = dataSnapshot.child("mid").getValue().toString();
                                                    dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + mid)
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    view.onMerchantProfile(dataSnapshot.getValue(Merchant.class));
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                    Forum forum = dataSnapshot.getValue(Forum.class);
                                                    forumList.add(0, forum);

                                                    view.onLoadHiddenForum(forumList, snapshot.child("fhid").getValue().toString());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onRemoveHideForum(String MID, String FHID, String FID) {
        dbRef.child(Constant.DB_REFERENCE_FORUM_HIDDEN + "/" + MID + "/" + FHID)
                .removeValue();

        dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + FID + "/" + Constant.DB_REFERENCE_FORUM_HIDDEN + "/" + MID)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.onSuccessUnHideForum();
                    }
                });
    }
}
