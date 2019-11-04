package com.andrew.bcamerchantservice.ui.mainforum.favorite;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;

import java.util.Map;

public interface IFavoritePresenter {
    void onGetFavoriteList(String MID);

    void onUpdateViewCount(Map<String, Object> map, final Forum forum, final Merchant merchant);

    void onRemoveThread(String FID, final int pos);
}
