package com.andrew.bcamerchantservice.ui.selectedthread;


import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.Report;

import java.util.List;
import java.util.Map;

public interface ISelectedThreadView {
    void onLoadReply(boolean isLike, Forum forum, List<Forum.ForumReply> forumReplies
            , Map<String, List<Forum.ForumImageReply>> map);

    void onFavorite(boolean isFavorite);

    void onUpdateMerchant(Map<String, Merchant> merchantMap);

    void onLoadImageForum(List<Forum.ForumImage> imageList);

    void onSuccessLoadReport(List<Report> reportList);

    void onSuccessSendReport();

    void onSuccessReply();

    void onSuccessDelete(int pos);

    void onGetForum(Forum.ForumCategory forumCategory);
}
