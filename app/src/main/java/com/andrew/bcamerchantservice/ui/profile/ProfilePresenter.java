package com.andrew.bcamerchantservice.ui.profile;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.andrew.bcamerchantservice.model.Merchant.MerchantStory;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ProfilePresenter implements IProfilePresenter {
    private DatabaseReference dbRef;
    private StorageReference storageReference;
    private IProfileView view;

    ProfilePresenter(IProfileView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void loadStory(String MID) {
        dbRef.child(Constant.DB_REFERENCE_MERCHANT_STORY + "/" + MID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<MerchantStory> storyList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MerchantStory merchantStory = snapshot.getValue(MerchantStory.class);
                    storyList.add(0, merchantStory);
                }
                view.onGettingStoryData(storyList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onUploadImage(final String MID, ByteArrayOutputStream baos) {
        Random random = new Random();
        final int ran = random.nextInt(10000);
        byte[] byteData = baos.toByteArray();

        final UploadTask uploadTask = storageReference.child(Constant.DB_REFERENCE_MERCHANT_STORY + "/" + MID)
                .child(Constant.DB_REFERENCE_MERCHANT_STORY + "story-" + MID + "-" + ran)
                .putBytes(byteData);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(Constant.DB_REFERENCE_MERCHANT_STORY + "/" + MID).child(Constant.DB_REFERENCE_MERCHANT_STORY + "story-" + MID + "-" + ran)
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
                                map.put("SID", key);
                                map.put("story_picture", uri.toString());
                                map.put("story_date", Utils.getTime("yyyy-MM-dd HH:mm"));

                                dbRef.child(Constant.DB_REFERENCE_MERCHANT_STORY + "/" + MID)
                                        .child(key)
                                        .setValue(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                view.onSuccessUpload();
                                            }
                                        });
                            }
                        });
            }
        });
    }

    @Override
    public void onUploadImage(final String name, final String MID, final String child, byte[] byteData) {
        final UploadTask uploadTask = storageReference.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID + "/" + name)
                .putBytes(byteData);
        storageReference.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID + "/" + name)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        /*
                         * Merupakan kondisi saat image ada dan berhasil dihapus
                         * */
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID + "/" + name)
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(final Uri uri) {
                                                /*
                                                 * Apabila data tersebut telah dihapus, maka sekarang mencari URL dr foto yang diupload
                                                 * Data merchant akan diupdate sesuai dengan URL terbaru
                                                 * */
                                                dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID)
                                                        .child(child)
                                                        .setValue(uri.toString())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                /*
                                                                 * Kondisi dibawah ini akan berjalan jika value sudah berhasil diUpdate
                                                                 * */
                                                                view.onSuccessUpload(child, uri.toString());
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        /*
                         * Merupakan kondisi saat image tidak ditemukan, maka langsung upload saja
                         * */
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID + "/" + name)
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(final Uri uri) {
                                                /*
                                                 * Setelah foto selesai diupload, saat ini mencari URL foto tersebut
                                                 * */
                                                dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID)
                                                        .child(child)
                                                        .setValue(uri.toString())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                view.onSuccessUpload(child, uri.toString());
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
                    }
                });
    }
}
