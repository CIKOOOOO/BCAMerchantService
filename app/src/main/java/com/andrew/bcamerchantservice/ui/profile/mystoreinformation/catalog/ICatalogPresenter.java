package com.andrew.bcamerchantservice.ui.profile.mystoreinformation.catalog;

import android.graphics.Bitmap;

public interface ICatalogPresenter {

    void sendCatalog(String MID, String catalog_name, String catalog_desc, int price, Bitmap bitmap);

}
