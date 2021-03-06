package com.andrew.bcamerchantservice.ui.mainforum;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.Report;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumPresenter implements IForumPresenter {

    private IForumView view;
    private DatabaseReference dbRef;
    private StorageReference storageRef;
    private FirebaseStorage firebaseStorage;

    ForumPresenter(IForumView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();
    }

    @Override
    public void loadForum(final String MID, final String FCID, Query query) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            List<Forum> forums = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                final Forum forum = snapshot.getValue(Forum.class);
                                if (forum != null) {
                                    if (FCID.equals("0") || FCID.equals(forum.getFcid())) {
                                        if (snapshot.child(Constant.DB_REFERENCE_FORUM_HIDDEN).getChildrenCount() == 0) {
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
                                            forums.add(0, forum);
                                        } else {
                                            boolean isHide = false;
                                            for (DataSnapshot hiddenSnapshot : snapshot.child(Constant.DB_REFERENCE_FORUM_HIDDEN).getChildren()) {
                                                if (hiddenSnapshot.getKey().equals(MID)) {
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
                                                forums.add(0, forum);
                                            }
                                        }
                                    }
                                }
                            }
                            view.onForumData(forums);
                        } else {
                            view.onMaxData();
                        }
                    }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getLastKey() {
        Query query = dbRef.child(Constant.DB_REFERENCE_FORUM)
                .orderByKey()
                .limitToFirst(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    view.onLoadLastKey(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dbRef.child(Constant.DB_REFERENCE_FORUM)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        view.onMaxData(dataSnapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void loadShowCase() {
        dbRef.child(Constant.DB_REFERENCE_MERCHANT_STORY)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            List<Merchant.MerchantStory> showCaseList = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String URL = "";
                                String MID = "";
                                String date = "";
                                String SID = "";
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    SID = snapshot1.child("sid").getValue(String.class);
                                    date = snapshot1.child("story_date").getValue(String.class);
                                    URL = snapshot1.child("story_picture").getValue(String.class);
                                    MID = snapshot.getRef().getKey();
                                }
                                showCaseList.add(0, new Merchant.MerchantStory(URL, SID, MID, date));
                            }

                            Collections.sort(showCaseList, new Comparator<Merchant.MerchantStory>() {
                                @Override
                                public int compare(Merchant.MerchantStory t2, Merchant.MerchantStory t1) {
                                    return t1.getStory_date().compareTo(t2.getStory_date());
                                }
                            });

                            for (int i = 0; i < showCaseList.size(); i++) {
                                dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE)
                                        .child(showCaseList.get(i).getMid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                                                if (dataSnapshots.getValue(Merchant.class) == null)
                                                    return;
                                                view.onMerchantStoryProfile(dataSnapshots.getValue(Merchant.class));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                            view.onStoryData(showCaseList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void loadCategory() {
        dbRef.child(Constant.DB_REFERENCE_FORUM_CATEGORY)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Forum.ForumCategory> forumCategoryList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            forumCategoryList.add(snapshot.getValue(Forum.ForumCategory.class));
                        }
                        view.onSuccessLoadCategory(forumCategoryList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void loadReportList() {
        dbRef.child(Constant.DB_REFERENCE_FORUM_REPORT_LIST)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Report> reportList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            reportList.add(snapshot.getValue(Report.class));
                        }
                        view.onSuccessLoadReport(reportList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onSendReport(String content, final List<Report> reportList, final String FID, final String MID) {
        boolean isReportListAvailable = false;
        final String key = dbRef.child(Constant.DB_REFERENCE_FORUM_REPORT + "/" + FID + "/" + MID).push().getKey();
        Map<String, String> map = new HashMap<>();
        map.put("report_id", key);
        map.put("report_date", Utils.getTime("dd/MM/yyyy HH:mm"));
        map.put("report_mid", MID);

        map.put("report_content", content);

        for (Report report : reportList) {
            if (report.isReport_is_checked()) {
                isReportListAvailable = true;
                break;
            }
        }

        String path = Constant.DB_REFERENCE_FORUM_REPORT + "/" + FID + "/" + Constant.DB_REFERENCE_FORUM_REPORTER + "/" + MID + "/" + key;

        if (isReportListAvailable)
            dbRef.child(path)
                    .setValue(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            for (int i = 0; i < reportList.size(); i++) {
                                Report report = reportList.get(i);
                                if (report.isReport_is_checked()) {
                                    dbRef.child(Constant.DB_REFERENCE_FORUM_REPORT + "/" + FID + "/"
                                            + Constant.DB_REFERENCE_FORUM_REPORTER + "/" + MID + "/"
                                            + key + "/" + Constant.DB_REFERENCE_FORUM_REPORT_LIST + "/" + report.getFrlid())
                                            .setValue(report);
                                }

                                if (i == reportList.size() - 1)
                                    view.onSuccessSendReport();
                            }
                        }
                    });
        else
            dbRef.child(path)
                    .setValue(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            view.onSuccessSendReport();
                        }
                    });
    }

    @Override
    public void onUploadShowCase(final String MID, final int randomNumber, byte[] byteData) {
        final UploadTask uploadTask = storageRef.child(Constant.DB_REFERENCE_MERCHANT_STORY).child("story-" + MID + "-" + randomNumber).putBytes(byteData);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.child(Constant.DB_REFERENCE_MERCHANT_STORY).child("story-" + MID + "-" + randomNumber)
                        .getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                /*
                                 * Apabila data tersebut telah dihapus, maka sekarang mencari URL dr foto yang diupload
                                 * Data merchant akan diupdate sesuai dengan URL terbaru
                                 * */
                                String key = dbRef.child(Constant.DB_REFERENCE_MERCHANT_STORY + "/" + MID).push().getKey();

                                HashMap<String, String> map = new HashMap<>();
                                map.put("sid", key);
                                map.put("story_picture", uri.toString());
                                map.put("story_date", Utils.getTime("yyyy-MM-dd HH:mm"));

                                dbRef.child(Constant.DB_REFERENCE_MERCHANT_STORY + "/" + MID).child(key)
                                        .setValue(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                /*
                                                 * Kondisi dibawah ini akan berjalan jika value sudah berhasil diUpdate
                                                 * */
                                                view.onSuccessUpload();
                                            }
                                        });
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onRemoveForum(final String FID, final int pos) {
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
    public void hideForum(final String FID, final String MID, final int pos) {
        final String key = dbRef.push().getKey();
        final Forum.ForumHidden forumHidden = new Forum.ForumHidden(key, FID, Utils.getTime("dd/MM/yyyy HH:mm"));

        dbRef.child(Constant.DB_REFERENCE_FORUM_HIDDEN + "/" + MID + "/" + key)
                .setValue(forumHidden)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

        dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + FID + "/" + Constant.DB_REFERENCE_FORUM_HIDDEN + "/" + MID)
                .setValue(forumHidden)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.onHide(pos);
                        final String forum_favorite_path = Constant.DB_REFERENCE_FORUM + "/" + FID
                                + "/" + Constant.DB_REFERENCE_FORUM_FAVORITE + "/" + MID;
                        dbRef.child(forum_favorite_path)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            dbRef.child( forum_favorite_path).removeValue();
                                            dbRef.child(Constant.DB_REFERENCE_FORUM_FAVORITE + "/" + MID + "/" + FID).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                });
//        dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + FID)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.child(Constant.DB_REFERENCE_FORUM_HIDDEN).getChildrenCount() == 0) {
//                            dbRef.child(Constant.DB_REFERENCE_FORUM_HIDDEN + "/" + MID + "/" + key)
//                                    .setValue(forumHidden)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//
//                                        }
//                                    });
//
//                            dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + FID + "/" + Constant.DB_REFERENCE_FORUM_HIDDEN + "/" + MID)
//                                    .setValue(forumHidden)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//
//                                        }
//                                    });
//                        } else {
//                            boolean isHide = false;
//                            for (DataSnapshot snapshot : dataSnapshot.child(Constant.DB_REFERENCE_FORUM_HIDDEN).getChildren()) {
//                                if (snapshot.getKey().equals(MID)) {
//                                    isHide = true;
//                                    break;
//                                }
//                            }
//                            if (!isHide) {
//                                dbRef.child(Constant.DB_REFERENCE_FORUM_HIDDEN + "/" + MID + "/" + key)
//                                        .setValue(forumHidden)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//
//                                            }
//                                        });
//
//                                dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + FID + "/" + Constant.DB_REFERENCE_FORUM_HIDDEN + "/" + MID)
//                                        .setValue(forumHidden)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//
//                                            }
//                                        });
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });


    }

}
