package com.andrew.bcamerchantservice.ui.profile.mystoreinformation.catalog;

import android.graphics.Bitmap;
import android.net.Uri;

import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CatalogPresenter implements ICatalogPresenter {

    private ICatalogView view;
    private DatabaseReference dbRef;
    private StorageReference storageReference;

    public CatalogPresenter(ICatalogView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }


    @Override
    public void sendCatalog(String MID, final String catalog_name, final String catalog_desc, final int price, Bitmap bitmap) {
        Random random = new Random();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        final String path = Constant.DB_REFERENCE_MERCHANT_CATALOG + "/" + MID;
        final int ran = random.nextInt(10000);
        final String key = dbRef.child(path).push().getKey();
        final String image_name = Constant.DB_REFERENCE_MERCHANT_CATALOG + "-" + MID + "-" + ran;

        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
        byte[] bytes = stream.toByteArray();

        UploadTask uploadTask = storageReference.child(path + "/" + image_name).putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(path + "/" + image_name)
                        .getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String URL = uri.toString();
                                Merchant.MerchantCatalog merchantCatalog =
                                        new Merchant.MerchantCatalog(key, catalog_name, catalog_desc
                                                , URL, Utils.getTime("dd/MM/yyyy HH:mm"), price);

                                dbRef.child(path + "/" + key)
                                        .setValue(merchantCatalog)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                view.onSuccessSendCatalog("Berhasil membuat katalog!");
                                            }
                                        });
                            }
                        });
            }
        });
    }

    @Override
    public void sendEditedCatalog(final String MID, String catalog_name, String catalog_desc, int price, Bitmap bitmap
            , final Merchant.MerchantCatalog merchantCatalog) {
        final String tree_path = Constant.DB_REFERENCE_MERCHANT_CATALOG + "/" + MID + "/" + merchantCatalog.getCid();
        if (bitmap != null) {
            Random random = new Random();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            final int ran = random.nextInt(10000);
            final String image_name = Constant.DB_REFERENCE_MERCHANT_CATALOG + "-" + MID + "-" + ran;

            String[] split = merchantCatalog.getCatalog_image().split("alt");
            String[] split2 = split[0].split("merchant_catalog");
            String final_name = "merchant_catalog" + split2[2].substring(0, split2[2].length() - 1);

            final String storage_path = Constant.DB_REFERENCE_MERCHANT_CATALOG + "/" + MID;

            storageReference.child(storage_path+"/"+final_name)
                    .delete();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
            byte[] bytes = stream.toByteArray();

            UploadTask uploadTask = storageReference.child(storage_path + "/" + image_name).putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child(storage_path + "/" + image_name)
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String URL = uri.toString();
                                    dbRef.child(tree_path + "/catalog_image")
                                            .setValue(URL);
                                }
                            });
                }
            });
        }

        Map<String, Object> map = new HashMap<>();
        map.put("catalog_date", Utils.getTime("dd/MM/yyyy HH:mm"));
        map.put("catalog_description", catalog_desc);
        map.put("catalog_name", catalog_name);
        map.put("catalog_price", price);

        dbRef.child(tree_path)
                .updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.onSuccessSendCatalog("Berhasil menyunting katalog!");
                    }
                });

    }
}
