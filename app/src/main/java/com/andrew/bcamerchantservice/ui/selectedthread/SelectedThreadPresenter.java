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

    private ISelectedThreadView view;
    private DatabaseReference dbRef;
    private StorageReference storageReference;
    private FirebaseStorage storage;

    SelectedThreadPresenter(ISelectedThreadView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @Override
    public void onLoadReplyData(final DatabaseReference dbRef, final Forum forum, final String MID) {
        dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Forum.ForumReply> forums = new ArrayList<>();
                        List<Forum.ForumImage> images = new ArrayList<>();
                        Map<String, List<Forum.ForumImageReply>> map = new HashMap<>();
                        final Map<String, Merchant> merchantMap = new HashMap<>();

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
                                            view.onUpdateMerchant(merchantMap);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }

                        boolean isLike = false;

                        for (DataSnapshot a : dataSnapshot.child("forum_like_by").getChildren()) {
                            if (a.getKey().equals(MID)) {
                                isLike = true;
                                break;
                            }
                        }

                        forum.setLike(isLike);

                        for (DataSnapshot s : dataSnapshot.child(Constant.DB_REFERENCE_FORUM_IMAGE).getChildren()) {
                            /*
                             * untuk mendapatkan image dari Thread Utamanya
                             * */
                            Forum.ForumImage forumImage = new Forum.ForumImage(s.child("fiid").getValue().toString()
                                    , s.child("image_name").getValue().toString(), s.child("image_url").getValue().toString());
                            images.add(forumImage);
                        }

                        if (images.size() > 0) {
                            view.onLoadImageForum(images);
                        }

                        view.onLoadReply(forum, forums, map);
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
                        view.onFavorite(true);
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
                        view.onFavorite(false);
                    }
                });
    }

    @Override
    public void onCheckFavorite(final String MID, String FID) {
        dbRef.child(Constant.DB_REFERENCE_FORUM_FAVORITE + "/" + MID + "/" + FID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            view.onFavorite(false);
                        } else {
                            view.onFavorite(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        String path = Constant.DB_REFERENCE_FORUM + "/" + FID + "/forum_like_by";

        dbRef.child(path)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isLike = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getKey().equals(MID)) {
                                isLike = true;
                                break;
                            }
                        }
                        view.onLike(isLike, (int) dataSnapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onUpdateLike(String path, Map<String, Object> map) {
        dbRef.child(path)
                .updateChildren(map);
    }

    @Override
    public void onRemoveLike(String path) {
        dbRef.child(path)
                .removeValue();
    }

    @Override
    public void onRemoveForum(final String FID) {
        dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + FID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.child(Constant.DB_REFERENCE_FORUM_IMAGE).getChildren()) {
                            Forum.ForumImage forumImage = snapshot.getValue(Forum.ForumImage.class);
                            if (forumImage != null) {
                                storageReference
                                        .child(Constant.DB_REFERENCE_FORUM_IMAGE + "/" + forumImage.getImage_name())
                                        .delete();
                            }
                        }

                        if (dataSnapshot.child(Constant.DB_REFERENCE_FORUM_REPLY).hasChildren()) {
                            for (DataSnapshot reply_snapshot : dataSnapshot.child(Constant.DB_REFERENCE_FORUM_REPLY).getChildren()) {
                                for (DataSnapshot snapshot : reply_snapshot.child(Constant.DB_REFERENCE_FORUM_IMAGE_REPLY).getChildren()) {
                                    Forum.ForumImageReply forumImageReply = snapshot.getValue(Forum.ForumImageReply.class);
                                    if (forumImageReply != null) {
                                        storageReference
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
                                        view.onSuccessDeleteThread();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onRemove(String fid, String frid, final int pos) {
        String path = Constant.DB_REFERENCE_FORUM + "/" + fid + "/"
                + Constant.DB_REFERENCE_FORUM_REPLY + "/" + frid;

        dbRef.child(path)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Constant.DB_REFERENCE_FORUM_IMAGE_REPLY).hasChildren()) {
                            for (DataSnapshot snapshot : dataSnapshot.child(Constant.DB_REFERENCE_FORUM_IMAGE_REPLY).getChildren()) {
                                Forum.ForumImageReply forumImageReply = snapshot.getValue(Forum.ForumImageReply.class);
                                if (forumImageReply != null) {
                                    storageReference.child(Constant.DB_REFERENCE_FORUM_IMAGE_REPLY + "/" + forumImageReply.getImage_name())
                                            .delete();
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        dbRef.child(path)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.onSuccessDelete(pos);
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
                        view.onSuccessReply();
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
                                                                        view.onSuccessReply();
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
                        view.onSuccessLoadReport(reportList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onSendReport(final String path, String content, final List<Report> reportList, final String FID, final String MID) {
        boolean isReportListAvailable = false;
        final String key = dbRef.child(path).push().getKey();
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
            dbRef.child(path + "/" + key)
                    .setValue(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            for (int i = 0; i < reportList.size(); i++) {
                                Report report = reportList.get(i);
                                if (report.isReport_is_checked()) {
                                    dbRef.child(path + "/" + key + "/" + Constant.DB_REFERENCE_FORUM_REPORT_LIST + "/" + report.getFrlid())
                                            .setValue(report);
                                }

                                if (i == reportList.size() - 1)
                                    view.onSuccessSendReport();
                            }
                        }
                    });
        else
            dbRef.child(path + "/" + key)
                    .setValue(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            view.onSuccessSendReport();
                        }
                    });
    }

    @Override
    public void onSendReplyReport() {

    }

    @Override
    public void onHideForum(final String FID, final String MID) {
        final String key = dbRef.push().getKey();
        final Forum.ForumHidden forumHidden = new Forum.ForumHidden(key, FID
                , Utils.getTime("dd/MM/yyyy HH:mm"));

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
                        final String forum_favorite_path = Constant.DB_REFERENCE_FORUM + "/" + FID
                                + "/" + Constant.DB_REFERENCE_FORUM_FAVORITE + "/" + MID;
                        dbRef.child(forum_favorite_path)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            dbRef.child(Constant.DB_REFERENCE_FORUM_FAVORITE + "/"
                                                    + MID + "/" + FID).removeValue();
                                            dbRef.child(forum_favorite_path).removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                        view.onSuccessHide();
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
                        view.onGetForum(forumCategory);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
