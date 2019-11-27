package com.andrew.bcamerchantservice.ui.loyalty.rewards;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.ui.loyalty.LoyaltyFragment;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.allrewards.AllRewardsFragment;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.myrewards.MyRewardsFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabAdapter;
import com.andrew.bcamerchantservice.utils.PrefConfig;

/**
 * A simple {@link Fragment} subclass.
 */
public class RewardsFragment extends Fragment implements View.OnClickListener, IRewardsView {

    public static final String GET_DATA = "get_data";

    private View v;
    private Context mContext;
    private PrefConfig prefConfig;
    private TextView text_current_point;

    private IRewardsPresenter presenter;

    public RewardsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_rewards, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();

        prefConfig = new PrefConfig(mContext);

        ImageButton img_btn_back_rewards = v.findViewById(R.id.img_btn_back_toolbar_back);
        TextView text_title = v.findViewById(R.id.text_title_toolbar_back);
        TabLayout tabLayout = v.findViewById(R.id.tab_layout_rewards_fragment);
        ViewPager viewPager = v.findViewById(R.id.view_pager_rewards_fragment);

        text_current_point = v.findViewById(R.id.text_point_rewards_fragment);

        TabAdapter tabAdapter = new TabAdapter(getFragmentManager());

        presenter = new RewardsPresenter(this);

        tabAdapter.addTab(new AllRewardsFragment(), "All Rewards");
        tabAdapter.addTab(new MyRewardsFragment(), "My Rewards");

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Bundle bundle = getArguments();

        if (bundle != null) {
            viewPager.setCurrentItem(bundle.getInt(GET_DATA, 0));
        } else
            viewPager.setCurrentItem(0);

        presenter.merchantListener(prefConfig.getMID());

        text_title.setText("Rewards");

        img_btn_back_rewards.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_btn_back_toolbar_back) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, new LoyaltyFragment());
            fragmentTransaction.commit();
        }
    }

    @Override
    public void pointListener(int point) {
        prefConfig.insertPoint(point);
        text_current_point.setText(prefConfig.getPoint() + " Points");
    }
}
