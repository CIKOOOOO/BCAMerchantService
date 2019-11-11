package com.andrew.bcamerchantservice.ui.profile;


import com.andrew.bcamerchantservice.model.Merchant.MerchantStory;

import java.util.List;

public interface IProfileView {
    void onSuccessUpload(String pictureType, String URL);
}
