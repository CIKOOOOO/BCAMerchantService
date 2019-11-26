package com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards.termcondition;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrew.bcamerchantservice.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TermConditionDetailRewards extends Fragment {

    public static final String GET_TNC = "get_tnc";

    private View v;

    public TermConditionDetailRewards() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_term_condition_detail_rewards, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        Context mContext = v.getContext();

        RecyclerView recycler_tnc = v.findViewById(R.id.recycler_term_condition);
        recycler_tnc.setLayoutManager(new LinearLayoutManager(mContext));

        Bundle bundle = getArguments();

        if (bundle != null) {
            if (bundle.getString(GET_TNC) != null) {
                String[] tnc_array = new String[100];
                String tnc = bundle.getString(GET_TNC);

                if (tnc != null) {
                    tnc_array = tnc.split("##");
                    TermConditionAdapter termConditionAdapter = new TermConditionAdapter(tnc_array, mContext);
                    recycler_tnc.setAdapter(termConditionAdapter);
                }
            }
        }
    }
}
