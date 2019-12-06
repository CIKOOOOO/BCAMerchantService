package com.andrew.bcamerchantservice.ui.loyalty.rewards.allrewards;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.ui.loyalty.LoyaltyAdapter;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards.DetailRewardsFragment;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllRewardsFragment extends Fragment implements LoyaltyAdapter.onItemClick, IAllRewardsView, AllRewardsAdapter.onClick {

    public static final String GET_DATA = "get_data";

    private View v;
    private Context mContext;
    private LoyaltyAdapter loyaltyAdapter;
    private PrefConfig prefConfig;
    private AllRewardsAdapter rewardsAdapter;

    private IAllRewardsPresenter presenter;

    private List<Loyalty> loyaltyList;
    private Map<String, List<Loyalty.Rewards>> rewardsMap;

    public AllRewardsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_all_rewards, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);

        RecyclerView recycler_rank = v.findViewById(R.id.recycler_rank_all_rewards);
        RecyclerView recycler_rewards = v.findViewById(R.id.recycler_rewards_all_rewards);

        loyaltyList = new ArrayList<>();
        rewardsMap = new HashMap<>();

        loyaltyAdapter = new LoyaltyAdapter(mContext, loyaltyList, prefConfig.getLoyaltyId(), this);
        rewardsAdapter = new AllRewardsAdapter(mContext, prefConfig.getPoint(), true, this);

        presenter = new AllRewardsPresenter(this);

        recycler_rank.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recycler_rewards.setLayoutManager(new LinearLayoutManager(mContext));

        recycler_rank.setAdapter(loyaltyAdapter);
        recycler_rewards.setAdapter(rewardsAdapter);

//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            if (bundle.getParcelable(GET_DATA) == null) {
//                presenter.loadRewardsLoyalty();
//            } else {
//
//            }
//        } else {
            presenter.loadRewardsLoyalty(prefConfig.getMerchantPosition().getPosition_id());
//        }
    }

    @Override
    public void onClick(Loyalty loyalty, boolean isMyLoyalty) {
        List<Loyalty.Rewards> rewardsList = new ArrayList<>(Objects.requireNonNull(rewardsMap.get(loyalty.getLoyalty_id())));
        rewardsAdapter.setRankEnough(isMyLoyalty);
        rewardsAdapter.setRewardsList(rewardsList);
        rewardsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadLoyalty(List<Loyalty> loyaltyList, Map<String, List<Loyalty.Rewards>> map) {
        this.rewardsMap = map;
        loyaltyAdapter.setLoyaltyList(loyaltyList);
        loyaltyAdapter.notifyDataSetChanged();
        String loyalty_type = loyaltyList.get(loyaltyAdapter.getLastPosition()).getLoyalty_id();
        List<Loyalty.Rewards> rewardsList = new ArrayList<>(Objects.requireNonNull(map.get(loyalty_type)));
        rewardsAdapter.setRewardsList(rewardsList);
        rewardsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRewardsClick(Loyalty.Rewards rewards) {
        DetailRewardsFragment detailRewardsFragment = new DetailRewardsFragment();
        Bundle bundle = new Bundle();
        FragmentManager fragmentManager = getFragmentManager();

        bundle.putParcelable(DetailRewardsFragment.GET_REWARDS_DATA, rewards);
        bundle.putString(DetailRewardsFragment.CONDITION, DetailRewardsFragment.REDEEM_CONDITION);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, detailRewardsFragment);

        detailRewardsFragment.setArguments(bundle);
        fragmentTransaction.commit();
    }
}
