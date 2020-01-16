package com.andrew.bcamerchantservice.ui.profile.myforum.hiddenforum;

public interface IHiddenForumPresenter {
    void loadHiddenForum(String MID);

    void onRemoveHideForum(String MID, String FHID, String FID);
}
