package com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards;


import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.RewardsFragment;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards.about.AboutDetailRewardsFragment;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards.termcondition.TermConditionDetailRewards;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabAdapter;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.andrew.bcamerchantservice.utils.Utils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailRewardsFragment extends Fragment implements View.OnClickListener, IDetailView {

    public static final String GET_REWARDS_DATA = "get_rewards_data";
    public static final String GET_MERCHANT_REWARDS_DATA = "get_merchant_rewards_data";
    public static final String CONDITION = "condition";
    public static final String REDEEM_CONDITION = "redeem";
    public static final String USE_CONDITION = "use";
    public static final String IS_USED_CONDITION = "is_used";

    private View v, custom_loading;
    private Context mContext;
    private Loyalty.Rewards rewards;
    private TextView text_conditional_status, text_voucher_use, text_voucher_date, text_voucher;
    private PrefConfig prefConfig;
    private RelativeLayout custom_relative_redeem_point;
    private FrameLayout custom_frame_redeem_point;
    private LinearLayout linear_custom_redeem_point, linear_voucher_code;
    private Merchant.Rewards merchant_rewards;

    private DetailRewardsPresenter presenter;

    private String condition;
    private boolean isVoucherVisible;

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
        isVoucherVisible = false;
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);

        ImageButton img_btn_back = v.findViewById(R.id.img_btn_back_toolbar_back);
        TextView text_toolbar = v.findViewById(R.id.text_title_toolbar_back);
        TabLayout tabLayout = v.findViewById(R.id.tab_layout_detail_rewards);
        ViewPager viewPager = v.findViewById(R.id.view_pager_detail_rewards);
        LinearLayout linear_redeem = v.findViewById(R.id.ll_dummy_00_detail_rewards);
        ImageButton img_btn_eye = v.findViewById(R.id.img_btn_eye_detail_rewards);
        ImageButton img_btn_copy = v.findViewById(R.id.img_btn_copy_voucher_detail_rewards);

        text_conditional_status = v.findViewById(R.id.text_conditional_redeem_status_detail_rewards);
        custom_relative_redeem_point = v.findViewById(R.id.custom_redeem_point);
        custom_frame_redeem_point = v.findViewById(R.id.custom_frame_00_dummy);
        linear_custom_redeem_point = v.findViewById(R.id.custom_ll_dummy_00);
        custom_loading = v.findViewById(R.id.custom_loading_detail_rewards);
        linear_voucher_code = v.findViewById(R.id.ll_dummy_01_detail_rewards);
        text_voucher_use = v.findViewById(R.id.text_voucher_use_on_detail_rewards);
        text_voucher_date = v.findViewById(R.id.text_voucher_using_date_detail_rewards);
        text_voucher = v.findViewById(R.id.text_voucher_detail_rewards);

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

                    if (rewards.getRewards_thumbnail().isEmpty())
                        Picasso.get()
                                .load(R.color.iron_palette)
                                .into(img_detail_rewards);
                    else
                        Picasso.get()
                                .load(rewards.getRewards_thumbnail())
                                .into(img_detail_rewards);

                    String status_redeem = "";
                    Drawable drawable_status_redeem = null;
                    int color_status = -1;
                    boolean isRedeemAble = true;

                    if (bundle.getString(CONDITION) != null) {
                        condition = bundle.getString(CONDITION);
                        if (condition != null) {
                            text_conditional_status.setVisibility(View.VISIBLE);
                            switch (condition) {
                                case REDEEM_CONDITION:
                                    linear_redeem.setVisibility(View.VISIBLE);
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
                                    if (bundle.getParcelable(GET_MERCHANT_REWARDS_DATA) != null) {
                                        merchant_rewards = bundle.getParcelable(GET_MERCHANT_REWARDS_DATA);
                                    }
                                    status_redeem = "Use now";
                                    drawable_status_redeem = mContext.getDrawable(R.drawable.background_reply);
                                    color_status = mContext.getResources().getColor(R.color.white_color);
                                    break;
                                case IS_USED_CONDITION:
                                    if (bundle.getParcelable(GET_MERCHANT_REWARDS_DATA) != null) {
                                        merchant_rewards = bundle.getParcelable(GET_MERCHANT_REWARDS_DATA);
                                    }
                                    text_conditional_status.setVisibility(View.GONE);
                                    text_voucher_date.setVisibility(View.VISIBLE);
                                    text_voucher_use.setVisibility(View.VISIBLE);
                                    linear_voucher_code.setVisibility(View.VISIBLE);

                                    text_voucher.setText("Voucher code: xxxxxxxxxxxxxxx");

                                    try {
                                        text_voucher_date.setText(Utils.formatDateFromDateString(
                                                "dd/MM/yyyy HH:mm", "EEEE, dd MMM yyyy HH:mm"
                                                , merchant_rewards.getMerchant_rewards_date_collect()) + " WIB");
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
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
        img_btn_copy.setOnClickListener(this);
        img_btn_eye.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_btn_back_toolbar_back:
                RewardsFragment rewardsFragment = new RewardsFragment();
                Bundle bundle = new Bundle();
                FragmentManager fragmentManager = getFragmentManager();

                bundle.putInt(RewardsFragment.GET_DATA, 1);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, rewardsFragment);

                rewardsFragment.setArguments(bundle);
                fragmentTransaction.commit();
                break;
            case R.id.text_conditional_redeem_status_detail_rewards:
                if (!condition.isEmpty())
                    switch (condition) {
                        case REDEEM_CONDITION:
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
                        case IS_USED_CONDITION:
                            break;
                        case USE_CONDITION:
                            presenter.useReward(prefConfig.getMID(), merchant_rewards.getMerchant_rewards_id());
                            break;
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
            case R.id.img_btn_eye_detail_rewards:
                ImageButton imageButton = v.findViewById(R.id.img_btn_eye_detail_rewards);
                Drawable drawable = null;
                if (isVoucherVisible) {
                    text_voucher.setText("Voucher code: xxxxxxxxxxxxxxx");
                    isVoucherVisible = false;
                    drawable = mContext.getDrawable(R.drawable.ic_closed_eye);
                } else {
                    text_voucher.setText("Voucher code: " + merchant_rewards.getMerchant_rewards_code());
                    isVoucherVisible = true;
                    drawable = mContext.getDrawable(R.drawable.ic_eye);
                }
                imageButton.setBackground(drawable);
                break;
            case R.id.img_btn_copy_voucher_detail_rewards:
                Toast.makeText(mContext, "Kode telah disalin", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Voucher code:", merchant_rewards.getMerchant_rewards_code());
                clipboard.setPrimaryClip(clip);
                break;
        }
    }

    @Override
    public void onRedeemSuccess(Merchant.Rewards merchant_rewards, Loyalty.Rewards loyalty_rewards) {
        custom_frame_redeem_point.setVisibility(View.GONE);
        linear_custom_redeem_point.setVisibility(View.GONE);
        custom_loading.setVisibility(View.GONE);

        condition = USE_CONDITION;
        text_conditional_status.setText("Use now");
    }

    @Override
    public void onUseSuccess(String date) {
        condition = IS_USED_CONDITION;
        merchant_rewards.setRewards_is_used(true);
        merchant_rewards.setMerchant_rewards_date_collect(date);
        text_conditional_status.setVisibility(View.GONE);
        text_voucher_date.setVisibility(View.VISIBLE);
        text_voucher_use.setVisibility(View.VISIBLE);
        linear_voucher_code.setVisibility(View.VISIBLE);
        text_voucher.setText("Voucher code: xxxxxxxxxxxxxxx");
        try {
            text_voucher_date.setText(Utils.formatDateFromDateString("dd/MM/yyyy HH:mm"
                    , "EEEE, dd MMM yyyy HH:mm", date) + " WIB");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
