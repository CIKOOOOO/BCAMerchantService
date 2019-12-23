package com.andrew.bcamerchantservice.ui.mainforum.search;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public interface ISearchView {

    void onLoadSearchResult(List<Forum> forumList, List<Merchant> merchantList);

    void onMerchantProfile(Merchant merchant);
}
