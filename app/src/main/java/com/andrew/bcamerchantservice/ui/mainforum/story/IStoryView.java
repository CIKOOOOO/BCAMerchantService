package com.andrew.bcamerchantservice.ui.mainforum.story;

import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public interface IStoryView {
    void onLoadStory(List<Merchant.MerchantStory> merchantStoryList);
}
