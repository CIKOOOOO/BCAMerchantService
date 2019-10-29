package com.andrew.bcamerchantservice.ui.otherprofile.forum;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.mainforum.ThreadAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForumOtherProfile extends Fragment implements ThreadAdapter.onItemClick, IForumOtherProfileView {

    public static final String GETTING_MERCHANT_DATA = "getting_merchant_data";

    private View v;
    private Context mContext;

    private ThreadAdapter threadAdapter;

    private IForumOtherPresenter presenter;

    private List<Forum> forumList;
    private Map<String, Merchant> merchantMap;

    public ForumOtherProfile() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_forum_other_profile, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        Bundle bundle = getArguments();

        mContext = v.getContext();
        presenter = new ForumOtherProfilePresenter(this);

        RecyclerView recyclerView = v.findViewById(R.id.recycler_forum_other_profile);

        forumList = new ArrayList<>();
        merchantMap = new HashMap<>();

        threadAdapter = new ThreadAdapter(mContext, forumList, merchantMap, this);

        if (bundle != null) {
            if (bundle.getString(GETTING_MERCHANT_DATA) != null) {
                String key = bundle.getString(GETTING_MERCHANT_DATA);
                presenter.onLoadForum(key);
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(threadAdapter);
    }

    @Override
    public void onClick(int pos) {

    }

    @Override
    public void onDelete(int pos, Forum forum) {

    }

    @Override
    public void profileOnClick(int pos, Merchant merchant) {

    }

    @Override
    public void onGettingData(List<Forum> forumList) {
        this.forumList.clear();
        this.forumList.addAll(forumList);
        threadAdapter.setForumList(forumList);
        threadAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMerchantProfile(Merchant merchant) {
        if (!merchantMap.containsKey(merchant.getMid())) {
            merchantMap.put(merchant.getMid(), merchant);
            threadAdapter.setMerchantMap(merchantMap);
            threadAdapter.notifyDataSetChanged();
        }
    }
}
