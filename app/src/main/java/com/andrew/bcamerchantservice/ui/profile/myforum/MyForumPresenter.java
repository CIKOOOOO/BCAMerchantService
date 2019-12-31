package com.andrew.bcamerchantservice.ui.profile.myforum;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyForumPresenter implements IMyForumPresenter {

    private DatabaseReference dbRef;
    private StorageReference storageRef;
    private IMyForumView view;

    public MyForumPresenter(IMyForumView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
    }


    @Override
    public void loadForum(final String MID) {
        dbRef.child(Constant.DB_REFERENCE_FORUM)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Forum> forumList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Forum forum = snapshot.getValue(Forum.class);
                            if (forum.getMid().equals(MID))
                                forumList.add(0, forum);
                        }
                        view.onLoadResult(forumList);
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
    public void onDelete(final String FID, final int pos) {
        dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + FID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.child(Constant.DB_REFERENCE_FORUM_IMAGE).getChildren()) {
                            Forum.ForumImage forumImage = snapshot.getValue(Forum.ForumImage.class);
                            if (forumImage != null) {
                                storageRef
                                        .child(Constant.DB_REFERENCE_FORUM_IMAGE + "/" + forumImage.getImage_name())
                                        .delete();
                            }
                        }

                        if (dataSnapshot.child(Constant.DB_REFERENCE_FORUM_REPLY).hasChildren()) {
                            for (DataSnapshot reply_snapshot : dataSnapshot.child(Constant.DB_REFERENCE_FORUM_REPLY).getChildren()) {
                                for (DataSnapshot snapshot : reply_snapshot.child(Constant.DB_REFERENCE_FORUM_IMAGE_REPLY).getChildren()) {
                                    Forum.ForumImageReply forumImageReply = snapshot.getValue(Forum.ForumImageReply.class);
                                    if (forumImageReply != null) {
                                        storageRef
                                                .child(Constant.DB_REFERENCE_FORUM_IMAGE_REPLY + "/" + forumImageReply.getImage_name())
                                                .delete();
                                    }
                                }
                            }
                        }

                        dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + FID)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        view.onSuccessDeleteThread(pos);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
