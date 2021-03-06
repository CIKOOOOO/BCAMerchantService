package com.andrew.bcamerchantservice.ui.mainforum;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.Report;

import java.util.List;

public interface IForumView {
    void onLoadLastKey(String last_key);

    void onForumData(List<Forum> forums);

    void onMaxData();

    void onMaxData(long max_data);

    void onMerchantProfile(Merchant merchant);

    void onMerchantStoryProfile(Merchant merchant);

    void onStoryData(List<Merchant.MerchantStory> stories);

    void onSuccessUpload();

    void onSuccessDeleteThread(int pos);

    void onSuccessUpdateViewCount(Forum forum, Merchant merchant);

    void onSuccessLoadCategory(List<Forum.ForumCategory> forumCategories);

    void onSuccessLoadReport(List<Report> reportList);

    void onSuccessSendReport();

    void onHide(int pos);
}
