package com.andrew.bcamerchantservice.ui.mainforum;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public interface IForumView {
    void onForumData(List<Forum> forums);

    void onMerchantProfile(Merchant merchant);

    void onMerchantStoryProfile(Merchant merchant);

    void onStoryData(List<Merchant.MerchantStory> stories);

    void onSuccessUpload();

    void onSuccessDeleteThread(int pos);

    void onSuccessUpdateViewCount(Forum forum, Merchant merchant);

    void onLoadStory(List<Merchant.MerchantStory> list);

    void onSuccessLoadCategory(List<Forum.ForumCategory> forumCategories);
}
