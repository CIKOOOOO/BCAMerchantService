package com.andrew.bcamerchantservice.ui.otherprofile.forum;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.Report;

import java.util.List;
import java.util.Map;

public interface IForumOtherPresenter {
    void onLoadForum(String MID, String merchant_owner_id);

    void loadReportList();

    void onSendReport(String content, List<Report> reportList, final String FID, final String MID);

    void onHide(String FID, String MID);

    void onUpdateViewCount(Map<String, Object> map, final Forum forum, final Merchant merchant);
}
