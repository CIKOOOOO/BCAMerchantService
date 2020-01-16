package com.andrew.bcamerchantservice.ui.profile.mystoreinformation;

public interface IMyStoreInformationPresenter {

    void editProfile(String MID, String key, String value);

    void loadCatalog(String MID);

    void onDeleteCatalog(String MID, String CID, int pos, String catalog_name);

    void onUpdateInformationProfile(String MID, boolean isHide);
}
