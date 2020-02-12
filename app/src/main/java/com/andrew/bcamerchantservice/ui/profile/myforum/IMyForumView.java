package com.andrew.bcamerchantservice.ui.profile.myforum;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public interface IMyForumView {
    void onLoadResult(List<Forum> forums);

    void onSuccessUpdateViewCount(Forum forum, Merchant merchant);

    void onSuccessDeleteThread();
}
