package com.andrew.bcamerchantservice.ui.loyalty.rewards.myrewards;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyRewardsFragment extends Fragment implements IMyRewardsView, MyRewardsAdapter.onClick {

    private View v;
    private Context mContext;
    private MyRewardsAdapter myRewardsAdapter;
    private PrefConfig prefConfig;

    private IMyRewardsPresenter presenter;

    private Map<String, Loyalty.Rewards> rewardsMap;

    public MyRewardsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_my_rewards, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);

        RecyclerView recycler_my_rewards = v.findViewById(R.id.recycler_my_rewards);

        rewardsMap = new HashMap<>();

        presenter = new MyRewardsPresenter(this);
        myRewardsAdapter = new MyRewardsAdapter(mContext, this);

        presenter.loadRewards(prefConfig.getMID());
        recycler_my_rewards.setLayoutManager(new LinearLayoutManager(mContext));

        recycler_my_rewards.setAdapter(myRewardsAdapter);
    }

    @Override
    public void onLoadMerchantRewards(List<Merchant.Rewards> rewards) {
        myRewardsAdapter.setMerchantRewardList(rewards);
        myRewardsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadRewards(Loyalty.Rewards rewards) {
        if (!rewardsMap.containsKey(rewards.getRewards_id())) {
            rewardsMap.put(rewards.getRewards_id(), rewards);
            myRewardsAdapter.setRewardsMap(rewardsMap);
            myRewardsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(Merchant.Rewards merchant_rewards, Loyalty.Rewards loyalty_rewards) {

    }
}
