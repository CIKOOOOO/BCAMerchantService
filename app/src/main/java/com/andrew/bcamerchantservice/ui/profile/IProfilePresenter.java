package com.andrew.bcamerchantservice.ui.profile;

public interface IProfilePresenter {
    void onUploadImage(final String name, final String MID, final String child, byte[] byteData);

    void onDeleteCatalog(String MID, String CID, String catalog_name);
}
