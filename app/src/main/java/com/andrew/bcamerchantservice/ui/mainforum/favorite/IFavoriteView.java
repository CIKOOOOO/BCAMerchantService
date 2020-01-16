package com.andrew.bcamerchantservice.ui.mainforum.favorite;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public interface IFavoriteView {
    void onMerchantProfile(Merchant merchant);

    void onLoadForum(List<Forum> forumList);

    void onSuccessUpdateViewCount(Forum forum, Merchant merchant);
}
