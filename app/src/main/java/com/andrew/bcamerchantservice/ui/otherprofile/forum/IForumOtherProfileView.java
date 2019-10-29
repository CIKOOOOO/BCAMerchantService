package com.andrew.bcamerchantservice.ui.otherprofile.forum;


import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public interface IForumOtherProfileView {
    void onGettingData(List<Forum> forumList);

    void onMerchantProfile(Merchant merchant);
}
