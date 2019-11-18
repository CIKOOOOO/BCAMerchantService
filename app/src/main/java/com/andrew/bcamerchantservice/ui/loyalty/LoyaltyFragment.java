package com.andrew.bcamerchantservice.ui.loyalty;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.profile.Profile;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.andrew.bcamerchantservice.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoyaltyFragment extends Fragment implements ILoyaltyView, LoyaltyAdapter.onItemClick, View.OnClickListener {


    private View v;
    private Context mContext;
    private PrefConfig prefConfig;
    private TextView text_point, text_expired_point, text_rank_name, text_rank_type_result, text_benefit_type;
    private LoyaltyAdapter loyaltyAdapter;
    private RoundCornerProgressBar progress_bar;
    private Loyalty loyalty;
    private ImageView image_check;
    private RelativeLayout rl_wrap_progress_bar;
    private LoyaltyBenefitAdapter loyaltyBenefitAdapter;
    private MissionAdapter missionAdapter;

    private ILoyaltyPresenter presenter;

    private List<Loyalty> loyaltyList;
    private List<Loyalty.Mission> missionList;

    public LoyaltyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_loyalty, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);

        presenter = new LoyaltyPresenter(this);

        RecyclerView recycler_loyalty = v.findViewById(R.id.recycler_merchant_rank_loyalty);
        RecyclerView recycler_benefit_loyalty = v.findViewById(R.id.recycler_benefit_loyalty);
        RecyclerView recycler_mission_loyalty = v.findViewById(R.id.recycler_mission_loyalty);
        ImageButton img_btn_back = v.findViewById(R.id.img_btn_back_toolbar_back);
        TextView text_title = v.findViewById(R.id.text_title_toolbar_back);

        text_point = v.findViewById(R.id.text_points_loyalty);
        text_expired_point = v.findViewById(R.id.text_expire_point_loyalty);
        text_rank_name = v.findViewById(R.id.text_rank_loyalty);
        text_rank_type_result = v.findViewById(R.id.text_rank_type_loyalty);
        progress_bar = v.findViewById(R.id.progress_loyalty);
        image_check = v.findViewById(R.id.image_check_loyalty);
        rl_wrap_progress_bar = v.findViewById(R.id.rl_dummy_00_loyalty);
        text_benefit_type = v.findViewById(R.id.text_rank_benefit_loyalty);

        loyaltyList = new ArrayList<>();
        missionList = new ArrayList<>();

        loyaltyAdapter = new LoyaltyAdapter(mContext, loyaltyList, prefConfig.getLoyaltyId(), this);
        loyaltyBenefitAdapter = new LoyaltyBenefitAdapter(mContext);
        missionAdapter = new MissionAdapter(mContext, missionList);

        progress_bar.setMax(100);
        image_check.setVisibility(View.VISIBLE);
        rl_wrap_progress_bar.setVisibility(View.GONE);

        text_title.setText("Membership Info");

        recycler_benefit_loyalty.setLayoutManager(new LinearLayoutManager(mContext));
        recycler_mission_loyalty.setLayoutManager(new LinearLayoutManager(mContext));

        recycler_loyalty.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });

        recycler_loyalty.setAdapter(loyaltyAdapter);
        recycler_benefit_loyalty.setAdapter(loyaltyBenefitAdapter);
        recycler_mission_loyalty.setAdapter(missionAdapter);

        presenter.loadMerchantLoyaltyListener(prefConfig.getMID());
        presenter.loadLoyaltyType();
        presenter.loadLoyalty(prefConfig.getLoyaltyId());
        presenter.loadMission();

        img_btn_back.setOnClickListener(this);
    }

    @Override
    public void onLoadRankType(List<Loyalty> loyaltyList) {
        this.loyaltyList = loyaltyList;
        loyaltyAdapter.setLoyaltyList(loyaltyList);
        loyaltyAdapter.notifyDataSetChanged();
    }

    @Override
    public void loyaltyListener(Loyalty loyalty) {
        int textSizeInSp = (int) getResources().getDimension(R.dimen.text_17);

        text_rank_name.setText(loyalty.getLoyalty_name());
        text_rank_type_result.setText("You have unlocked this rank.");
        text_rank_type_result.setTextColor(mContext.getResources().getColor(R.color.blackPalette));

        setTextSize(textSizeInSp);

        String[] loyaltySplit = loyalty.getLoyalty_benefit().split("##");
        loyaltyBenefitAdapter.setLoyaltyArray(loyaltySplit, loyalty.getLoyalty_id());
        loyaltyBenefitAdapter.notifyDataSetChanged();

        text_benefit_type.setText(loyalty.getLoyalty_name() + " Merchant Benefit");
    }

    @Override
    public void onMerchantListener(Merchant merchant) {
        prefConfig.insertMerchantData(merchant);
        text_point.setText(String.valueOf(prefConfig.getPoint()));
        text_expired_point.setText(prefConfig.getPoint() + " points will expire on 1 January " + (Integer.valueOf(Utils.getTime("yyyy")) + 1));
        if (loyaltyList != null && loyalty != null) {
            progressCondition();
        }
    }

    @Override
    public void onLoadMission(List<Loyalty.Mission> missionList) {
        this.missionList = missionList;
        missionAdapter.setMissionList(missionList);
        missionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(Loyalty loyalty, boolean isMyLoyalty) {
        this.loyalty = loyalty;
        text_rank_name.setText(loyalty.getLoyalty_name());
        text_benefit_type.setText(loyalty.getLoyalty_name() + " Merchant Benefit");

        String[] loyaltySplit = loyalty.getLoyalty_benefit().split("##");
        loyaltyBenefitAdapter.setLoyaltyArray(loyaltySplit, loyalty.getLoyalty_id());
        loyaltyBenefitAdapter.notifyDataSetChanged();

        if (isMyLoyalty) {
            image_check.setVisibility(View.VISIBLE);
            rl_wrap_progress_bar.setVisibility(View.GONE);

            int textSizeInSp = (int) getResources().getDimension(R.dimen.text_17);
            text_rank_type_result.setText("You have unlocked this rank.");
            text_rank_type_result.setTextColor(mContext.getResources().getColor(R.color.blackPalette));
            setTextSize(textSizeInSp);
        } else {
            rl_wrap_progress_bar.setVisibility(View.VISIBLE);
            image_check.setVisibility(View.GONE);
            progressCondition();
        }
    }

    private void progressCondition() {
        int totalExp = presenter.totalExpLoyalty(loyaltyList, loyalty);
        int totalEarning = loyalty.getLoyalty_exp() - (prefConfig.getExp() - totalExp);
        int textSizeInSp = (int) getResources().getDimension(R.dimen.text_12);
        text_rank_type_result.setText("Earn " + totalEarning + " points to unlock " + loyalty.getLoyalty_name());
        text_rank_type_result.setTextColor(mContext.getResources().getColor(R.color.silver_palette));

        float currentExp = (float) prefConfig.getExp() - (float) totalExp;
        float progress = (currentExp / (float) loyalty.getLoyalty_exp()) * 100;
        setTextSize(textSizeInSp);

        ObjectAnimator animation = ObjectAnimator.ofFloat(progress_bar, "progress", progress);
        animation.setDuration(1250);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private void setTextSize(int textSizeInSp) {
        text_rank_type_result.setTextSize(Utils.convertSpToPixels(textSizeInSp, mContext));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_btn_back_toolbar_back:
                Profile profile = new Profile();
                Bundle bundle = new Bundle();
                bundle.putInt(Profile.GET_CURRENT_ITEM_VIEW_PAGER, 2);
                profile.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, profile);
                fragmentTransaction.commit();
                break;
        }
    }
}
