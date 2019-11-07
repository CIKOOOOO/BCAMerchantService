package com.andrew.bcamerchantservice.ui.otherprofile.forum;


import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.Report;

import java.util.List;

public interface IForumOtherProfileView {
    void onGettingData(List<Forum> forumList);

    void onMerchantProfile(Merchant merchant);

    void onSuccessSendReport();

    void onSuccessLoadReport(List<Report> reportList);

    void onSuccessUpdateViewCount(Forum forum, Merchant merchant);
}
