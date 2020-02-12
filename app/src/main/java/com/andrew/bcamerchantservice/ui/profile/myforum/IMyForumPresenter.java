package com.andrew.bcamerchantservice.ui.profile.myforum;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;

import java.util.Map;

public interface IMyForumPresenter {
    void loadForum(String MID);

    void onUpdateViewCount(Map<String, Object> map, final Forum forum, final Merchant merchant);

    void onRemoveForum(String FID);

}
