package com.andrew.bcamerchantservice.ui.mainforum;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.MerchantStory;

import java.util.List;

public interface IForumView {
    void onForumData(List<Forum> forums);

    void onMerchantProfile(Merchant merchant);

    void onMerchantStoryProfile(Merchant merchant);

    void onStoryData(List<MerchantStory> stories);

    void onSuccessUpload();

    void onSuccessDeleteThread(int pos);

    void onSuccessUpdateViewCount(Forum forum, Merchant merchant);
}
