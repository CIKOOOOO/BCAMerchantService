package com.andrew.bcamerchantservice.ui.profile.mystoreinformation.catalog;

import android.graphics.Bitmap;

import com.andrew.bcamerchantservice.model.Merchant;

public interface ICatalogPresenter {

    void sendCatalog(String MID, String catalog_name, String catalog_desc, int price, Bitmap bitmap);

    void sendEditedCatalog(String MID, String catalog_name, String catalog_desc, int price, Bitmap bitmap
            , Merchant.MerchantCatalog merchantCatalog);
}
