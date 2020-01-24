package com.andrew.bcamerchantservice.ui.profile;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.andrew.bcamerchantservice.utils.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
