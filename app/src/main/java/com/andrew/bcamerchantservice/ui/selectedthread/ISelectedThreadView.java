package com.andrew.bcamerchantservice.ui.selectedthread;


import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;
import java.util.Map;

public interface ISelectedThreadView {
    void onLoadTrendingList(List<Forum> forumList);

    void onLoadReply(boolean isLike, Forum forum, List<Forum.ForumReply> forumReplies
            , Map<String, List<Forum.ForumImageReply>> map);

    void onUpdateMerchant(Map<String, Merchant> merchantMap);

    void onLoadImageForum(List<Forum.ForumImage> imageList);

    void onSuccessReply();

    void onSuccessDelete(int pos);
}
