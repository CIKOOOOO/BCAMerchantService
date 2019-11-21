package com.andrew.bcamerchantservice.ui.loyalty.rewards.allrewards;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrew.bcamerchantservice.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllRewardsFragment extends Fragment {


    public AllRewardsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_rewards, container, false);
    }

}
