package com.andrew.bcamerchantservice.ui.newthread;


import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.ImagePicker;

import java.util.List;

public interface INewThreadView {
    void onThreadSuccessUpload(Forum forum);

    void onLoadImage(List<ImagePicker> list, List<Forum.ForumImage> imageList);

    void onEditSuccess();

    void onEditReplySuccess();

    void onLoadCategory(List<Forum.ForumCategory> forumCategories);
}
