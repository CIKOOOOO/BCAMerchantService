package com.andrew.bcamerchantservice.ui.selectedthread;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.Map;

public interface ISelectedThreadPresenter {
    void onLoadData(DatabaseReference reference);

    void onLoadReplyData(DatabaseReference dbRef, Forum forum, String MID);

    void onUpdateLike(String path, Map<String, Object> map);

    void onRemove(String path);

    void onRemove(String path, int pos);

    void onReply(String path, Forum.ForumReply reply);

    void onReply(String path, String FID, String key, String MID, Forum.ForumReply reply, List<ImagePicker> list);
}
