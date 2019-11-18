package com.andrew.bcamerchantservice.ui.loyalty;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.profile.Profile;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.andrew.bcamerchantservice.utils.Utils;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoyaltyFragment extends Fragment implements ILoyaltyView, LoyaltyAdapter.onItemClick, View.OnClickListener {


    private View v;
    private Context mContext;
    private PrefConfig prefConfig;
    private TextView text_point, text_expired_point, text_rank_name, text_rank_type_result;
    private LoyaltyAdapter loyaltyAdapter;

    private ILoyaltyPresenter presenter;

    private List<Loyalty> loyaltyList;

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
        ImageButton img_btn_back = v.findViewById(R.id.img_btn_back_toolbar_back);
        TextView text_title = v.findViewById(R.id.text_title_toolbar_back);

        text_point = v.findViewById(R.id.text_points_loyalty);
        text_expired_point = v.findViewById(R.id.text_expire_point_loyalty);
        text_rank_name = v.findViewById(R.id.text_rank_loyalty);
        text_rank_type_result = v.findViewById(R.id.text_rank_type_loyalty);

        loyaltyList = new ArrayList<>();

        loyaltyAdapter = new LoyaltyAdapter(mContext, loyaltyList, prefConfig.getLoyaltyId(), this);

        text_title.setText("Membership Info");

        recycler_loyalty.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });

//        SnappingRecyclerView snappingRecyclerView = new SnappingRecyclerView(mContext);
//        snappingRecyclerView.
//        snapHelper.attachToRecyclerView(recycler_loyalty);

//        GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
//        snapHelper.attachToRecyclerView(recycler_loyalty);
        recycler_loyalty.setAdapter(loyaltyAdapter);

        presenter.loadMerchantLoyaltyListener(prefConfig.getMID());
        presenter.loadLoyaltyType();
        presenter.loadLoyalty(prefConfig.getLoyaltyId());

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
        text_rank_name.setText(loyalty.getLoyalty_name());
        int textSizeInSp = (int) getResources().getDimension(R.dimen.text_17);
        text_rank_type_result.setText("You have unlocked this rank.");
        text_rank_type_result.setTextColor(mContext.getResources().getColor(R.color.blackPalette));
        setTextSize(textSizeInSp);
    }

    @Override
    public void onMerchantListener(Merchant merchant) {
        prefConfig.insertMerchantData(merchant);
        text_point.setText(String.valueOf(prefConfig.getPoint()));
        text_expired_point.setText(prefConfig.getPoint() + " points will expire on 1 January " + (Integer.valueOf(Utils.getTime("yyyy")) + 1));

    }

    @Override
    public void onClick(Loyalty loyalty, boolean isMyLoyalty, Loyalty nextLoyalty) {
        text_rank_name.setText(loyalty.getLoyalty_name());
        int textSizeInSp = 0;
        if (isMyLoyalty) {
            textSizeInSp = (int) getResources().getDimension(R.dimen.text_17);
            text_rank_type_result.setText("You have unlocked this rank.");
            text_rank_type_result.setTextColor(mContext.getResources().getColor(R.color.blackPalette));
        } else {
            int totalExp = presenter.totalExpLoyalty(loyaltyList, loyalty);
            int totalEarning = loyalty.getLoyalty_exp() - (prefConfig.getExp() - totalExp);
            textSizeInSp = (int) getResources().getDimension(R.dimen.text_12);
            text_rank_type_result.setText("Earn " + totalEarning + " points to unlock " + loyalty.getLoyalty_name());
            text_rank_type_result.setTextColor(mContext.getResources().getColor(R.color.silver_palette));
        }
        setTextSize(textSizeInSp);
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
