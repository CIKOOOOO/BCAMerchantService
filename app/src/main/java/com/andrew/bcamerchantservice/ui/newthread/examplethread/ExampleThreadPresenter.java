package com.andrew.bcamerchantservice.ui.newthread.examplethread;

import android.graphics.Bitmap;
import android.net.Uri;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ExampleThreadPresenter implements IExampleThreadPresenter {
    private IExampleView view;
    private DatabaseReference dbRef;
    private StorageReference storageRef;

    public ExampleThreadPresenter(IExampleView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
    }


    @Override
    public void onSendNewThread(final Forum forum, final PrefConfig prefConfig, Bitmap thumbnail) {
        ByteArrayOutputStream thumbnail_baos = new ByteArrayOutputStream();
        if (thumbnail == null) {
            dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid())
                    .setValue(forum)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            view.onSuccessUpload(forum);
                        }
                    });
        } else {
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 30, thumbnail_baos);
            byte[] byte_thumbnail = thumbnail_baos.toByteArray();

            Random randoms = new Random();
            final int rans = randoms.nextInt(10000);

            final String thumbnail_name = Constant.DB_REFERENCE_FORUM_THUMBNAIL + "-" + prefConfig.getMID() + "-" + rans;

            UploadTask thumbnail_upload = storageRef.child(Constant.DB_REFERENCE_FORUM_THUMBNAIL
                    + "/" + prefConfig.getMID() + "/" + thumbnail_name).putBytes(byte_thumbnail);

            thumbnail_upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.child(Constant.DB_REFERENCE_FORUM_THUMBNAIL
                            + "/" + prefConfig.getMID() + "/" + thumbnail_name)
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    forum.setForum_thumbnail(uri.toString());
                                    dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid())
                                            .setValue(forum)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    view.onSuccessUpload(forum);
                                                }
                                            });
                                }
                            });
                }
            });
        }
    }

    @Override
    public void onSendNewThread(final Forum forum, final PrefConfig prefConfig, final List<ImagePicker> imageList
            , final Bitmap thumbnail) {
        if (thumbnail == null) {
            dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid()).setValue(forum)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            for (int i = 0; i < imageList.size(); i++) {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                imageList.get(i).getImage_bitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
                                byte[] byteData = baos.toByteArray();

                                Random random = new Random();
                                final int ran = random.nextInt(10000);
                                final String imgName = "forum-first-" + prefConfig.getMID() + "-" + forum.getFid() + "-" + ran;

                                UploadTask uploadTask = storageRef.child(Constant.DB_REFERENCE_FORUM_IMAGE + "/" + imgName).putBytes(byteData);
                                // it will make image name become unique
                                final int finalI = i;
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        storageRef.child(Constant.DB_REFERENCE_FORUM_IMAGE + "/" + imgName)
                                                .getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        HashMap<String, String> imgMap = new HashMap<>();
                                                        imgMap.put("image_name", imgName);
                                                        imgMap.put("image_url", uri.toString());

                                                        dbRef.child("forum_image");

                                                        String key1 = dbRef.push().getKey();

                                                        imgMap.put("fiid", key1);

                                                        dbRef.child(Constant.DB_REFERENCE_FORUM).child(forum.getFid() + "/forum_image/" + key1)
                                                                .setValue(imgMap)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        if (finalI == imageList.size() - 1) {
                                                                            view.onSuccessUpload(forum);
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
        } else {
            ByteArrayOutputStream thumbnail_baos = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 30, thumbnail_baos);
            byte[] byte_thumbnail = thumbnail_baos.toByteArray();

            Random randoms = new Random();
            final int rans = randoms.nextInt(10000);

            final String thumbnail_name = Constant.DB_REFERENCE_FORUM_THUMBNAIL + "-" + prefConfig.getMID() + "-" + rans;

            UploadTask thumbnail_upload = storageRef.child(Constant.DB_REFERENCE_FORUM_THUMBNAIL
                    + "/" + prefConfig.getMID() + "/" + thumbnail_name).putBytes(byte_thumbnail);

            thumbnail_upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.child(Constant.DB_REFERENCE_FORUM_THUMBNAIL
                            + "/" + prefConfig.getMID() + "/" + thumbnail_name)
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    forum.setForum_thumbnail(uri.toString());
                                    dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid()).setValue(forum)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    for (int i = 0; i < imageList.size(); i++) {
                                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                        imageList.get(i).getImage_bitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
                                                        byte[] byteData = baos.toByteArray();

                                                        Random random = new Random();
                                                        final int ran = random.nextInt(10000);
                                                        final String imgName = "forum-first-" + prefConfig.getMID() + "-" + forum.getFid() + "-" + ran;

                                                        UploadTask uploadTask = storageRef.child(Constant.DB_REFERENCE_FORUM_IMAGE + "/" + imgName).putBytes(byteData);
                                                        // it will make image name become unique
                                                        final int finalI = i;
                                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                storageRef.child(Constant.DB_REFERENCE_FORUM_IMAGE + "/" + imgName)
                                                                        .getDownloadUrl()
                                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                            @Override
                                                                            public void onSuccess(Uri uri) {
                                                                                HashMap<String, String> imgMap = new HashMap<>();
                                                                                imgMap.put("image_name", imgName);
                                                                                imgMap.put("image_url", uri.toString());

                                                                                dbRef.child("forum_image");

                                                                                String key1 = dbRef.push().getKey();

                                                                                imgMap.put("fiid", key1);

                                                                                dbRef.child(Constant.DB_REFERENCE_FORUM).child(forum.getFid() + "/forum_image/" + key1)
                                                                                        .setValue(imgMap)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                if (finalI == imageList.size() - 1) {
                                                                                                    view.onSuccessUpload(forum);
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
                            });
                }
            });
        }
    }
}
