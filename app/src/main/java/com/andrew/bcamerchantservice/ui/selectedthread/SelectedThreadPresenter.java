package com.andrew.bcamerchantservice.ui.selectedthread;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.Report;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SelectedThreadPresenter implements ISelectedThreadPresenter {

    private ISelectedThreadView iSelectedThreadView;
    private DatabaseReference dbRef;
    private StorageReference storageReference;
    private FirebaseStorage storage;

    SelectedThreadPresenter(ISelectedThreadView iSelectedThreadView) {
        this.iSelectedThreadView = iSelectedThreadView;
        dbRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @Override
    public void onLoadReplyData(final DatabaseReference dbRef, final Forum forum, final String MID) {
        dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Forum.ForumReply> forums = new ArrayList<>();
                List<Forum.ForumImage> images = new ArrayList<>();
                Map<String, List<Forum.ForumImageReply>> map = new HashMap<>();
                final Map<String, Merchant> merchantMap = new HashMap<>();
                boolean isLike = false;

                if (dataSnapshot.child("forum_like").getValue() != null)
                    forum.setForum_like(Integer.parseInt(dataSnapshot.child("forum_like").getValue().toString()));

                for (DataSnapshot snapshot : dataSnapshot.child(Constant.DB_REFERENCE_FORUM_REPLY).getChildren()) {
                    /*
                     * Untuk mendapatkan setiap reply dari forum yang diklik oleh user
                     * */
                    final Forum.ForumReply forumReply = snapshot.getValue(Forum.ForumReply.class);

                    forumReply.setLike(false);

                    for (DataSnapshot likeSnapshot : snapshot.child("forum_like_by").getChildren()) {
                        if (likeSnapshot.getKey().equals(MID)) {
                            forumReply.setLike(true);
                            break;
                        }
                    }

                    forums.add(forumReply);

                    for (DataSnapshot ss : snapshot.getChildren()) {
                        // Forum Image Reply
                        List<Forum.ForumImageReply> list = new ArrayList<>();
                        for (DataSnapshot snapImageReply : ss.getChildren()) {
                            Forum.ForumImageReply forumImageReply = snapImageReply.getValue(Forum.ForumImageReply.class);
                            if (forumImageReply.getImage_url() != null)
                                list.add(forumImageReply);
                        }
                        if (list.size() > 0) {
                            map.put(forumReply.getFrid(), list);
                        }
                    }

                    dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + forumReply.getMid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot s) {
                                    /*
                                     * Untuk mendapatkan data merchant dari tree merchant_profile
                                     * Menggunakan Map untuk tidak membuang-buang resource jika menggunakan array
                                     * */
                                    if (s.getValue(Merchant.class) == null || merchantMap.containsKey(forumReply.getMid())) {
                                        return;
                                    }
                                    merchantMap.put(forumReply.getMid(), s.getValue(Merchant.class));
                                    iSelectedThreadView.onUpdateMerchant(merchantMap);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }

                for (DataSnapshot a : dataSnapshot.child("forum_like_by").getChildren()) {
                    if (a.getKey().equals(MID)) {
                        isLike = true;
                        break;
                    }
                }

                for (DataSnapshot s : dataSnapshot.child(Constant.DB_REFERENCE_FORUM_IMAGE).getChildren()) {
                    /*
                     * untuk mendapatkan image dari Thread Utamanya
                     * */
                    Forum.ForumImage forumImage = new Forum.ForumImage(s.child("fiid").getValue().toString()
                            , s.child("image_name").getValue().toString(), s.child("image_url").getValue().toString());
                    images.add(forumImage);
                }

                if (images.size() > 0) {
                    iSelectedThreadView.onLoadImageForum(images);
                }

                iSelectedThreadView.onLoadReply(isLike, forum, forums, map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onFavoriteThread(String MID, String FID) {
        Forum.ForumFavorite forumFavorite = new Forum.ForumFavorite(FID, Utils.getTime("dd/MM/yyyy"));
        String path = Constant.DB_REFERENCE_FORUM + "/" + FID + "/" + Constant.DB_REFERENCE_FORUM_FAVORITE + "/" + MID;
        dbRef.child(path).setValue(forumFavorite);
        dbRef.child(Constant.DB_REFERENCE_FORUM_FAVORITE + "/" + MID + "/" + FID)
                .setValue(forumFavorite)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        iSelectedThreadView.onFavorite(true);
                    }
                });
    }

    @Override
    public void onRemoveFavoriteThread(String MID, String FID) {
        String path = Constant.DB_REFERENCE_FORUM + "/" + FID + "/" + Constant.DB_REFERENCE_FORUM_FAVORITE + "/" + MID;
        dbRef.child(path).removeValue();
        dbRef.child(Constant.DB_REFERENCE_FORUM_FAVORITE + "/" + MID + "/" + FID)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        iSelectedThreadView.onFavorite(false);
                    }
                });
    }

    @Override
    public void onCheckFavorite(String MID, String FID) {
        dbRef.child(Constant.DB_REFERENCE_FORUM_FAVORITE + "/" + MID + "/" + FID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            iSelectedThreadView.onFavorite(false);
                        } else {
                            iSelectedThreadView.onFavorite(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onUpdateLike(String path, Map<String, Object> map) {
        dbRef.child(path).updateChildren(map);
    }

    @Override
    public void onRemove(String path) {
        dbRef.child(path)
                .removeValue();
    }

    @Override
    public void onRemove(String path, final int pos) {
        dbRef.child(path)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        iSelectedThreadView.onSuccessDelete(pos);
                    }
                });
    }

    @Override
    public void onReply(String path, Forum.ForumReply reply) {
        dbRef.child(path)
                .setValue(reply)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        iSelectedThreadView.onSuccessReply();
                    }
                });
    }

    @Override
    public void onReply(final String path, final String FID, final String key, final String MID
            , Forum.ForumReply reply, final List<ImagePicker> imageReply) {
        dbRef.child(path)
                .setValue(reply)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        /*
                         * Berfungsi untuk mereply sebuah thread
                         * Jika sudah berhasil maka akan melooping gambar yang ada untuk diupload
                         * dan diupdate ke dalam tree forum reply
                         * */
                        for (int i = 0; i < imageReply.size(); i++) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            imageReply.get(i).getImage_bitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
                            byte[] byteData = baos.toByteArray();

                            final String keys = dbRef.child(FID).child("forum_reply").child("forum_image_reply").push().getKey();

                            Random random = new Random();
                            final int ran = random.nextInt(10000);
                            final String imgName = "forum-reply-" + MID + "-" + key + "-" + ran;

                            UploadTask uploadTask = storageReference.child(Constant.DB_REFERENCE_FORUM_IMAGE_REPLY + "/" + imgName).putBytes(byteData);
                            /*
                             * Untuk upload gambar ke firebase
                             * */
                            final int finalI = i;
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    storageReference.child(Constant.DB_REFERENCE_FORUM_IMAGE_REPLY + "/" + imgName)
                                            .getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Forum.ForumImageReply forumImageReply = new Forum.ForumImageReply(keys, imgName, uri.toString());
                                                    dbRef.child(path + "/" + Constant.DB_REFERENCE_FORUM_IMAGE_REPLY)
                                                            .child(keys)
                                                            .setValue(forumImageReply)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    if (finalI == imageReply.size() - 1) {
                                                                        iSelectedThreadView.onSuccessReply();
                                                                    }
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void onLoadReportList() {
        dbRef.child(Constant.DB_REFERENCE_FORUM_REPORT_LIST)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Report> reportList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.e("asd", snapshot.getValue().toString());
                            reportList.add(snapshot.getValue(Report.class));
                        }
                        iSelectedThreadView.onSuccessLoadReport(reportList);
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

        if (isReportListAvailable)
            dbRef.child(Constant.DB_REFERENCE_FORUM_REPORT + "/" + FID + "/" + MID + "/" + key)
                    .setValue(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            for (int i = 0; i < reportList.size(); i++) {
                                Report report = reportList.get(i);
                                if (report.isReport_is_checked()) {
                                    dbRef.child(Constant.DB_REFERENCE_FORUM_REPORT + "/" + FID + "/" + MID + "/"
                                            + key + "/" + Constant.DB_REFERENCE_FORUM_REPORT_LIST + "/" + report.getFrlid())
                                            .setValue(report);
                                }

                                if (i == reportList.size() - 1)
                                    iSelectedThreadView.onSuccessSendReport();
                            }
                        }
                    });
        else
            dbRef.child(Constant.DB_REFERENCE_FORUM_REPORT + "/" + FID + "/" + MID + "/" + key)
                    .setValue(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            iSelectedThreadView.onSuccessSendReport();
                        }
                    });
    }

    @Override
    public void getCategoryName(String FCID) {
        dbRef.child(Constant.DB_REFERENCE_FORUM_CATEGORY + "/" + FCID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Forum.ForumCategory forumCategory = dataSnapshot.getValue(Forum.ForumCategory.class);
                        iSelectedThreadView.onGetForum(forumCategory);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
