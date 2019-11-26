package com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.RewardsFragment;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards.about.AboutDetailRewardsFragment;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards.termcondition.TermConditionDetailRewards;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabAdapter;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailRewardsFragment extends Fragment implements View.OnClickListener, IDetailView {

    public static final String GET_REWARDS_DATA = "get_rewards_data";
    public static final String CONDITION = "condition";
    public static final String REDEEM_CONDITION = "redeem";
    public static final String USE_CONDITION = "use";
    public static final String IS_USED_CONDITION = "is_used";

    private View v, custom_loading;
    private Context mContext;
    private Loyalty.Rewards rewards;
    private TextView text_conditional_status;
    private PrefConfig prefConfig;
    private RelativeLayout custom_relative_redeem_point;
    private FrameLayout custom_frame_redeem_point;
    private LinearLayout linear_custom_redeem_point;

    private DetailRewardsPresenter presenter;

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
        prefConfig = new PrefConfig(mContext);

        ImageButton img_btn_back = v.findViewById(R.id.img_btn_back_toolbar_back);
        TextView text_toolbar = v.findViewById(R.id.text_title_toolbar_back);
        TabLayout tabLayout = v.findViewById(R.id.tab_layout_detail_rewards);
        ViewPager viewPager = v.findViewById(R.id.view_pager_detail_rewards);

        text_conditional_status = v.findViewById(R.id.text_conditional_redeem_status_detail_rewards);
        custom_relative_redeem_point = v.findViewById(R.id.custom_redeem_point);
        custom_frame_redeem_point = v.findViewById(R.id.custom_frame_00_dummy);
        linear_custom_redeem_point = v.findViewById(R.id.custom_ll_dummy_00);
        custom_loading = v.findViewById(R.id.custom_loading_detail_rewards);

        TabAdapter tabAdapter = new TabAdapter(getFragmentManager());
        Bundle bundle = getArguments();
        Bundle bundle_about = new Bundle();
        Bundle bundle_tnc = new Bundle();

        AboutDetailRewardsFragment aboutDetailRewardsFragment = new AboutDetailRewardsFragment();
        TermConditionDetailRewards termConditionDetailRewards = new TermConditionDetailRewards();

        presenter = new DetailRewardsPresenter(this);

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

                    String status_redeem = "";
                    Drawable drawable_status_redeem = null;
                    int color_status = -1;
                    boolean isRedeemAble = true;

                    if (bundle.getString(CONDITION) != null) {
                        String condition = bundle.getString(CONDITION);
                        if (condition != null) {
                            switch (condition) {
                                case REDEEM_CONDITION:
                                    if (prefConfig.getPoint() >= rewards.getRewards_point()) {
                                        status_redeem = "Redeem now";
                                        drawable_status_redeem = mContext.getDrawable(R.drawable.background_reply);
                                        color_status = mContext.getResources().getColor(R.color.white_color);
                                    } else {
                                        isRedeemAble = false;
                                        status_redeem = "Point not enough";
                                        drawable_status_redeem = mContext.getDrawable(R.drawable.rectangle_rounded_stroke_iron);
                                        color_status = mContext.getResources().getColor(R.color.iron_palette);
                                    }
                                    break;
                                case USE_CONDITION:
                                    status_redeem = "Use now";
                                    drawable_status_redeem = mContext.getDrawable(R.drawable.background_reply);
                                    color_status = mContext.getResources().getColor(R.color.white_color);
                                    break;
                                case IS_USED_CONDITION:
                                    text_conditional_status.setVisibility(View.GONE);
                                    break;
                            }
                        }
                    }

                    text_conditional_status.setEnabled(isRedeemAble);
                    text_conditional_status.setText(status_redeem);
                    text_conditional_status.setBackground(drawable_status_redeem);
                    text_conditional_status.setTextColor(color_status);

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

                    custom_frame_redeem_point.setOnClickListener(this);

                    linear_custom_redeem_point.setOnClickListener(this);
                }
            }
        }

        text_conditional_status.setOnClickListener(this);
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
            case R.id.text_conditional_redeem_status_detail_rewards:
                if (rewards != null) {
                    TextView text = v.findViewById(R.id.text_warning_custom_redeem_point);
                    Button btn_cancel = v.findViewById(R.id.btn_cancel_custom_redeem_point);
                    Button btn_send = v.findViewById(R.id.btn_yes_custom_redeem_point);

                    custom_relative_redeem_point.setVisibility(View.VISIBLE);

                    text.setText("Apakah Anda yakin untuk me-redeem voucher ini? Point Anda akan berkurang sebesar " + rewards.getRewards_point() + " points");

                    btn_cancel.setOnClickListener(this);
                    btn_send.setOnClickListener(this);
                }
                break;
            case R.id.btn_cancel_custom_redeem_point:
            case R.id.custom_frame_00_dummy:
                custom_frame_redeem_point.setVisibility(View.GONE);
                linear_custom_redeem_point.setVisibility(View.GONE);
                break;
            case R.id.btn_yes_custom_redeem_point:
                custom_loading.setVisibility(View.VISIBLE);
                int point = prefConfig.getPoint() - rewards.getRewards_point();
                presenter.sendReward(prefConfig.getMID(), rewards, point);
                break;
        }
    }

    @Override
    public void onRedeemSuccess(Merchant.Rewards merchant_rewards, Loyalty.Rewards loyalty_rewards) {
        custom_frame_redeem_point.setVisibility(View.GONE);
        linear_custom_redeem_point.setVisibility(View.GONE);
        custom_loading.setVisibility(View.GONE);


    }
}
