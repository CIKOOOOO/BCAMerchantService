package com.andrew.bcamerchantservice.ui.mainforum.search;

import android.support.annotation.NonNull;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private ISearchView view;
    private DatabaseReference dbRef;

    public SearchPresenter(ISearchView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onSearch(final String search, final String MID) {
        if (search.isEmpty()) {
            view.onLoadSearchResult(new ArrayList<Forum>());
            return;
        }
        dbRef.child(Constant.DB_REFERENCE_FORUM)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Forum> forumList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Forum forum = snapshot.getValue(Forum.class);
                            if (snapshot.child(Constant.DB_REFERENCE_FORUM_HIDDEN).getChildrenCount() > 0) {
                                boolean isHidden = false;
                                for (DataSnapshot hiddenSnapshot : snapshot.child(Constant.DB_REFERENCE_FORUM_HIDDEN).getChildren()) {
                                    final String hiddenMID = hiddenSnapshot.getKey();
                                    if (hiddenMID.equals(MID)) {
                                        isHidden = true;
                                        break;
                                    }
                                }
                                if (!isHidden && forum.getForum_title().toLowerCase().trim().contains(search.toLowerCase().trim())) {
                                    dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE)
                                            .child(forum.getMid())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                                                    if (dataSnapshots.getValue(Merchant.class) != null)
                                                        view.onMerchantProfile(dataSnapshots.getValue(Merchant.class));
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                    forumList.add(0, forum);
                                }
                            } else {
                                if (forum.getForum_title().toLowerCase().trim().contains(search.toLowerCase().trim())) {
                                    dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE)
                                            .child(forum.getMid())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                                                    if (dataSnapshots.getValue(Merchant.class) != null)
                                                        view.onMerchantProfile(dataSnapshots.getValue(Merchant.class));
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                    forumList.add(0, forum);
                                }
                            }
                        }
                        view.onLoadSearchResult(forumList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
