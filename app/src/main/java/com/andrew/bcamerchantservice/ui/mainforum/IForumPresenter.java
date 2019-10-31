package com.andrew.bcamerchantservice.ui.mainforum;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;

public interface IForumPresenter {
    void loadForum(String MID);

    void loadShowCase();

    void onUploadShowCase(String MID, int randomNumber, byte[] byteData);

    void onRemoveThread(String FID, int pos);

    void onUpdateViewCount(Map<String, Object> map, Forum forum, Merchant merchant);

    void onHide(String FID, String MID);
}
