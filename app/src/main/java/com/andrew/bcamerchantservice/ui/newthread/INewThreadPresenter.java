package com.andrew.bcamerchantservice.ui.newthread;


import android.graphics.Bitmap;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Map;

public interface INewThreadPresenter {
    void onLoadCategory();

    void onFirebaseRemoveValue(String path);

    void onStorageDelete(StorageReference reference, String path);

    void onSendNewThread(String path, Forum forum);

    void onSendNewThread(String key, StorageReference reference, Forum forum
            , List<ImagePicker> imagePickers, PrefConfig prefConfig);

    void loadImage(String path);

    void onEditThreadReply(Map<String, Object> map);

    void onEditThreadReply(Map<String, Object> map, List<ImagePicker> list, String MID, String FID, String key, String FRID);

    void onUpdateThread(Map<String, Object> map, Bitmap bitmap, String MID, final String FID);

    void onUpdateThread(Map<String, Object> map, List<ImagePicker> list, Forum forum, String MID
            , String content, String title, Bitmap bitmap);
}
