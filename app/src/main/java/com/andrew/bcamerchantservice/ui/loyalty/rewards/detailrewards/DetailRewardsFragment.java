package com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.RewardsFragment;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards.about.AboutDetailRewardsFragment;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards.termcondition.TermConditionDetailRewards;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabAdapter;
import com.andrew.bcamerchantservice.utils.CustomViewPager;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailRewardsFragment extends Fragment implements View.OnClickListener {

    public static final String GET_REWARDS_DATA = "get_rewards_data";

    private View v;
    private Context mContext;
    private Loyalty.Rewards rewards;
    private TextView text_conditional_status;


    public DetailRewardsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_detail_rewards, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();

        ImageButton img_btn_back = v.findViewById(R.id.img_btn_back_toolbar_back);
        TextView text_toolbar = v.findViewById(R.id.text_title_toolbar_back);
        TabLayout tabLayout = v.findViewById(R.id.tab_layout_detail_rewards);
        ViewPager viewPager = v.findViewById(R.id.view_pager_detail_rewards);

        text_conditional_status = v.findViewById(R.id.text_conditional_redeem_status_detail_rewards);

        TabAdapter tabAdapter = new TabAdapter(getFragmentManager());
        Bundle bundle = getArguments();
        Bundle bundle_about = new Bundle();
        Bundle bundle_tnc = new Bundle();

        AboutDetailRewardsFragment aboutDetailRewardsFragment = new AboutDetailRewardsFragment();
        TermConditionDetailRewards termConditionDetailRewards = new TermConditionDetailRewards();

        text_toolbar.setText("Redeem Voucher");

        if (bundle != null) {
            if (bundle.getParcelable(GET_REWARDS_DATA) != null) {
                rewards = bundle.getParcelable(GET_REWARDS_DATA);
                if (rewards != null) {
                    ImageView img_detail_rewards = v.findViewById(R.id.image_detail_rewards);
                    TextView text_title = v.findViewById(R.id.text_title_detail_rewards);
                    TextView text_amount_point = v.findViewById(R.id.text_amount_point_detail_rewards);

                    if (rewards.getRewards_image().isEmpty())
                        Picasso.get()
                                .load(R.color.iron_palette)
                                .into(img_detail_rewards);
                    else
                        Picasso.get()
                                .load(rewards.getRewards_image())
                                .into(img_detail_rewards);

                    text_title.setText(rewards.getRewards_name());
                    text_amount_point.setText(rewards.getRewards_point() + " Points");

                    bundle_tnc.putString(TermConditionDetailRewards.GET_TNC, rewards.getRewards_tnc());

                    bundle_about.putString(AboutDetailRewardsFragment.GET_ABOUT, rewards.getRewards_description());
                    bundle_about.putString(AboutDetailRewardsFragment.GET_TUTORIAL, rewards.getRewards_tutorial());

                    aboutDetailRewardsFragment.setArguments(bundle_about);
                    termConditionDetailRewards.setArguments(bundle_tnc);

                    tabAdapter.addTab(aboutDetailRewardsFragment, "About");
                    tabAdapter.addTab(termConditionDetailRewards, "Terms & Conditions");

                    viewPager.setAdapter(tabAdapter);
                    tabLayout.setupWithViewPager(viewPager);
                    viewPager.setCurrentItem(1);
                }
            }
        }

        img_btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_btn_back_toolbar_back:
                RewardsFragment rewardsFragment = new RewardsFragment();
                Bundle bundle = new Bundle();
                FragmentManager fragmentManager = getFragmentManager();

//                bundle.putInt(AllRewardsFragment.GET_DATA, 1);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, rewardsFragment);

//                allRewardsFragment.setArguments(bundle);
                fragmentTransaction.commit();
                break;
        }
    }
}
