package com.andrew.bcamerchantservice.ui.profile.mystoreinformation;

public interface IMyStoreInformationPresenter {

    void editProfile(String MID, String key, String value);

    void onLoadCatalog(String MID);

    void onDeleteCatalog(String MID, String CID, int pos);
}
