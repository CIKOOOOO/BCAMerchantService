package com.andrew.bcamerchantservice.ui.profile;


import com.andrew.bcamerchantservice.model.MerchantStory;

import java.util.List;

public interface IProfileView {
    void onGettingStoryData(List<MerchantStory> storyList);

    void onSuccessUpload();

    void onSuccessUpload(String pictureType, String URL);
}
