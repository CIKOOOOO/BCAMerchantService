package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.tncpromorequest;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.model.PromoRequest;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class TNCPromoRequestPresenter implements ITNCPromoRequestPresenter {

    private ITNCPromoRequestView view;
    private DatabaseReference dbRef;
    private StorageReference storageReference;

    TNCPromoRequestPresenter(ITNCPromoRequestView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void sendPromoRequest(final String MID, int MCC, PromoRequest promoRequest, final List<PromoRequest.Facilities> facilitiesList,
                                 final String specific_payment, final Uri attachment, final Context mContext,
                                 final List<ImagePicker> logoList, final List<ImagePicker> productList) {
        final String key = dbRef.push().getKey();
        final String path = Constant.DB_REFERENCE_PROMO_REQUEST + "/" + Constant.DB_REFERENCE_MERCHANT_PROMO_REQUEST
                + "/" + MCC + "/" + MID + "/" + key;

        promoRequest.setPromo_request_id(key);
        promoRequest.setPromo_status("promo_status_1");
        promoRequest.setPromo_request_date(Utils.getTime("dd/MM/yyyy HH:mm"));

        dbRef.child(path)
                .setValue(promoRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (facilitiesList.size() > 0) {
                            for (PromoRequest.Facilities facilities : facilitiesList) {
                                dbRef.child(path + "/" + Constant.DB_REFERENCE_MERCHANT_FACILITIES
                                        + "/" + Constant.DB_REFERENCE_FACILITIES + "/" + facilities.getFacilities_id())
                                        .setValue(facilities);
                            }
                        }

                        if (!specific_payment.isEmpty()) {
                            dbRef.child(path + "/" + Constant.DB_REFERENCE_MERCHANT_FACILITIES + "/specific_facilities")
                                    .setValue(specific_payment);
                        }

                        if (attachment != null) {
                            final String storage_key = Constant.DB_REFERENCE_MERCHANT_PROMO_REQUEST + "/"
                                    + MID + "/attachment-" + Utils.getFileName(attachment, mContext);
                            UploadTask uploadTask = storageReference
                                    .child(storage_key)
                                    .putFile(attachment);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    storageReference.child(storage_key)
                                            .getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String downloadUrl = uri.toString();
                                                    dbRef.child(path + "/promo_tnc").setValue(downloadUrl);
                                                }
                                            });
                                }
                            });
                        }

                        if (logoList.size() > 0) {
                            for (int i = 0; i < logoList.size(); i++) {
                                ImagePicker imagePicker = logoList.get(i);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                imagePicker.getImage_bitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
                                byte[] byteData = baos.toByteArray();

                                final String storage_key = Constant.DB_REFERENCE_MERCHANT_PROMO_REQUEST + "/"
                                        + MID + "/" + key + "/logo" + (i + 1);

                                UploadTask uploadTask = storageReference
                                        .child(storage_key)
                                        .putBytes(byteData);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        storageReference.child(storage_key)
                                                .getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String downloadUrl = uri.toString();
                                                        String logo_key = dbRef.child(path).push().getKey();
                                                        PromoRequest.Logo logo = new PromoRequest.Logo(logo_key, downloadUrl);
                                                        dbRef.child(path + "/" + Constant.DB_REFERENCE_PROMO_REQUEST_LOGO + "/" + logo_key)
                                                                .setValue(logo);
                                                    }
                                                });
                                    }
                                });
                            }
                        }

                        if (productList.size() > 0) {
                            for (int i = 0; i < productList.size(); i++) {
                                final int lastPos = i;
                                ImagePicker imagePicker = productList.get(i);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                imagePicker.getImage_bitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
                                byte[] byteData = baos.toByteArray();

                                final String storage_key = Constant.DB_REFERENCE_MERCHANT_PROMO_REQUEST + "/"
                                        + MID + "/" + key + "/product" + (i + 1);

                                UploadTask uploadTask = storageReference
                                        .child(storage_key)
                                        .putBytes(byteData);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        storageReference.child(storage_key)
                                                .getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String downloadUrl = uri.toString();
                                                        String product_key = dbRef.child(path).push().getKey();
                                                        PromoRequest.Product product = new PromoRequest.Product(product_key, downloadUrl);
                                                        dbRef.child(path + "/" + Constant.DB_REFERENCE_PROMO_REQUEST_PRODUCT + "/" + product_key)
                                                                .setValue(product)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (lastPos == productList.size() - 1) {
                                                                            view.onSuccessPromoRequest(key);
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
                });
    }
}
