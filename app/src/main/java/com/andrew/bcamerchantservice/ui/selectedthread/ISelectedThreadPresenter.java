package com.andrew.bcamerchantservice.ui.selectedthread;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.model.Report;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.Map;

public interface ISelectedThreadPresenter {
    void onLoadReplyData(DatabaseReference dbRef, Forum forum, String MID);

    void onFavoriteThread(String MID, String FID);

    void onRemoveFavoriteThread(String MID, String FID);

    void onCheckFavorite(String MID, String FID);

    void onUpdateLike(String path, Map<String, Object> map);

    void onRemove(String path);

    void onRemove(String fid, String frid, int pos);

    void onReply(String path, Forum.ForumReply reply);

    void onReply(String path, String FID, String key, String MID, Forum.ForumReply reply, List<ImagePicker> list);

    void onLoadReportList();

    void onSendReport(String path, String content, List<Report> reportList, String FID, String MID);

    void onSendReplyReport();

    void onHideForum(String FID, String MID);

    void getCategoryName(String FCID);
}
