package com.andrew.bcamerchantservice.ui.profile.profilesetting;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.ProfileModel;
import com.andrew.bcamerchantservice.ui.loyalty.LoyaltyFragment;
import com.andrew.bcamerchantservice.ui.mainforum.MainForum;
import com.andrew.bcamerchantservice.ui.profile.ProfileAdapter;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabPromoRequest;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment implements ProfileAdapter.onClick {
    private View v;
    private PrefConfig prefConfig;
    private Context mContext;
    private Activity mActivity;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_setting, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();

        prefConfig = new PrefConfig(mContext);

        List<ProfileModel> profileList = new ArrayList<>(Constant.getProfileModels());

        TextView text_email = v.findViewById(R.id.text_email_setting);
        TextView text_mid = v.findViewById(R.id.text_mid_setting);
        TextView text_position = v.findViewById(R.id.text_position_setting);
        RecyclerView recycler_setting = v.findViewById(R.id.recycler_setting);

        ProfileAdapter profileAdapter = new ProfileAdapter(mContext, profileList, this);

        text_email.setText(prefConfig.getEmail());
        text_mid.setText(": " + prefConfig.getMID());
        text_position.setText(": " + prefConfig.getMerchantPosition().getPosition_name());

        recycler_setting.setLayoutManager(new LinearLayoutManager(mContext));

        recycler_setting.setAdapter(profileAdapter);
    }

    @Override
    public void onSettingClick(int pos) {
        switch (pos) {
            case 0:
                changeFragment(new LoyaltyFragment());
                break;
            case 4:
                if (prefConfig.getMerchantPosition().getPosition_id().equals("position_2"))
                changeFragment(new TabPromoRequest());
                break;
            case 5:
                changeFragment(new MainForum());
                break;
            case 8:
                // Untuk kembali ke halaman login MID
                mActivity.finish();
                break;
        }
    }

    private void changeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
