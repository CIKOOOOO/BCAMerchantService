package com.andrew.bcamerchantservice.ui.mainforum;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;

public interface IForumPresenter {
    void onGetData();

    void loadForum(boolean isSearch);

    void loadShowCase();

    List<Forum> onSearchTrending(List<Forum> list, String search);

    void onLoadSearch(Map<String, Merchant> map, String searchResult);

    void onUploadShowCase(String MID, int randomNumber, byte[] byteData);

    void onRemoveThread(String FID, int pos);

    void onUpdateViewCount(Map<String, Object> map, Forum forum, Merchant merchant);
}
