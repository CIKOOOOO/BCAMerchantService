package com.andrew.bcamerchantservice.ui.newthread;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NewThreadPresenter implements INewThreadPresenter {

    private INewThreadView iNewThreadView;
    private DatabaseReference dbRef;
    private StorageReference storageReference;

    NewThreadPresenter(INewThreadView iNewThreadView) {
        this.iNewThreadView = iNewThreadView;
        dbRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void onLoadCategory() {
        dbRef.child(Constant.DB_REFERENCE_FORUM_CATEGORY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Forum.ForumCategory> list = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            list.add(snapshot.getValue(Forum.ForumCategory.class));
                        }
                        iNewThreadView.onLoadCategory(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onFirebaseRemoveValue(String path) {
        dbRef.child(path).removeValue();
    }

    @Override
    public void onStorageDelete(StorageReference reference, String path) {
        reference.child(path).delete();
    }

    @Override
    public void onSendNewThread(String path, final Forum forum) {
        dbRef.child(path)
                .setValue(forum)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        iNewThreadView.onThreadSuccessUpload(forum);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onSendNewThread(final String key, final StorageReference reference
            , final Forum forum, final List<ImagePicker> imageList, final PrefConfig prefConfig) {
        dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + key).setValue(forum)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for (int i = 0; i < imageList.size(); i++) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            imageList.get(i).getImage_bitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
                            byte[] byteData = baos.toByteArray();

                            Random random = new Random();
                            final int ran = random.nextInt(10000);
                            final String imgName = "forum-first-" + prefConfig.getMID() + "-" + key + "-" + ran;

                            UploadTask uploadTask = reference.child(imgName).putBytes(byteData);
                            // it will make image name become unique
                            final int finalI = i;
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    reference
                                            .child(imgName)
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

                                                    dbRef.child(Constant.DB_REFERENCE_FORUM).child(key + "/forum_image/" + key1)
                                                            .setValue(imgMap)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    if (finalI == imageList.size() - 1) {
                                                                        iNewThreadView.onThreadSuccessUpload(forum);
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
    public void loadImage(String path) {
        dbRef.child(path)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        /*
                         * Harus menggunakan single value event listener, agar saat diupload data tidak refresh
                         * */
                        List<Forum.ForumImage> forumImageList = new ArrayList<>();
                        final List<ImagePicker> imageList = new ArrayList<>();

                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            forumImageList.add(snapshot.getValue(Forum.ForumImage.class));
                            Picasso.get().load(snapshot.child("image_url").getValue().toString()).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    imageList.add(new ImagePicker(bitmap, ImagePickerAdapter.STATES
                                            , snapshot.child("image_name").getValue().toString()));
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                        }
                        iNewThreadView.onLoadImage(imageList, forumImageList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onEditThreadReply(Map<String, Object> map) {
        dbRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                iNewThreadView.onEditReplySuccess();
            }
        });
    }

    @Override
    public void onEditThreadReply(Map<String, Object> map, final List<ImagePicker> list, String MID
            , final String FID, final String key, final String FRID) {
        dbRef.updateChildren(map);

        for (int i = 0; i < list.size(); i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            list.get(i).getImage_bitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] byteData = baos.toByteArray();

            Random random = new Random();
            final int ran = random.nextInt(10000);
            final String imgName = "forum-reply-" + MID + "-" + key + "-" + ran;

            UploadTask uploadTask = storageReference.child(Constant.DB_REFERENCE_FORUM_IMAGE_REPLY + "/" + imgName).putBytes(byteData);

            final int finalI = i;
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child(Constant.DB_REFERENCE_FORUM_IMAGE_REPLY + "/" + imgName)
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String frid = dbRef.child(Constant.DB_REFERENCE_FORUM_REPLY).push().getKey();

                                    HashMap<String, String> imgMap = new HashMap<>();
                                    imgMap.put("image_name", imgName);
                                    imgMap.put("image_url", uri.toString());
                                    imgMap.put("frid", frid);

                                    dbRef.child(Constant.DB_REFERENCE_FORUM + "/"
                                            + FID + "/" + Constant.DB_REFERENCE_FORUM_REPLY
                                            + "/" + FRID + "/" + Constant.DB_REFERENCE_FORUM_IMAGE_REPLY + "/" + frid)
                                            .setValue(imgMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    if (finalI == list.size() - 1) {
                                                        iNewThreadView.onEditReplySuccess();
                                                    }
                                                }
                                            });
                                }
                            });
                }
            });
        }
    }

    @Override
    public void onUpdateThread(final Map<String, Object> map, Bitmap bitmap, final String MID, final String FID) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();

            Random randoms = new Random();
            final int rans = randoms.nextInt(10000);

            final String thumbnail_name = Constant.DB_REFERENCE_FORUM_THUMBNAIL + "-" + MID + "-" + rans;

            UploadTask uploadTask = storageReference.child(Constant.DB_REFERENCE_FORUM_THUMBNAIL
                    + "/" + MID + "/" + thumbnail_name).putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child(Constant.DB_REFERENCE_FORUM_THUMBNAIL
                            + "/" + MID + "/" + thumbnail_name)
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    map.put(Constant.DB_REFERENCE_FORUM + "/" + FID + "/forum_thumbnail", uri.toString());
                                    dbRef.updateChildren(map)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    iNewThreadView.onEditSuccess();
                                                }
                                            });
                                }
                            });
                }
            });
        } else dbRef.updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        iNewThreadView.onEditSuccess();
                    }
                });
    }

    @Override
    public void onUpdateThread(final Map<String, Object> map, final List<ImagePicker> imageList
            , final Forum forum, final String MID, final String content, final String title, Bitmap bitmap) {

        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();

            Random randoms = new Random();
            final int rans = randoms.nextInt(10000);

            final String thumbnail_name = Constant.DB_REFERENCE_FORUM_THUMBNAIL + "-" + MID + "-" + rans;

            UploadTask uploadTask = storageReference.child(Constant.DB_REFERENCE_FORUM_THUMBNAIL
                    + "/" + MID + "/" + thumbnail_name).putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child(Constant.DB_REFERENCE_FORUM_THUMBNAIL
                            + "/" + MID + "/" + thumbnail_name)
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    map.put(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/forum_thumbnail", uri.toString());
                                    dbRef.updateChildren(map);
                                }
                            });
                }
            });
        } else dbRef.updateChildren(map);

        for (int i = 0; i < imageList.size(); i++) {
            final String key = dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/" + Constant.DB_REFERENCE_FORUM_IMAGE).push().getKey();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageList.get(i).getImage_bitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] byteData = baos.toByteArray();

            Random random = new Random();
            final int ran = random.nextInt(10000);
            final String imgName = "forum-first-" + MID + "-" + key + "-" + ran;

            UploadTask uploadTask = storageReference.child(imgName).putBytes(byteData);
            // it will make image name become unique
            final int finalI = i;
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference
                            .child(imgName)
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

                                    dbRef.child("forum").child(forum.getFid() + "/forum_image/" + key1)
                                            .setValue(imgMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    if (finalI == imageList.size() - 1) {
                                                        iNewThreadView.onEditSuccess();
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