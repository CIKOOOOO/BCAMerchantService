package com.andrew.bcamerchantservice.ui.profile.myforum.hiddenforum;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public interface IHiddenForumView {
    void onMerchantProfile(Merchant merchant);

    void onLoadHiddenForum(List<Forum> forumList, String FHID);

    void onSuccessUnHideForum();
}
