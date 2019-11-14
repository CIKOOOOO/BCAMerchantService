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
                                                view.onSuccessSendCatalog();
                                            }
                                        });
                            }
                        });
            }
        });
    }
}
