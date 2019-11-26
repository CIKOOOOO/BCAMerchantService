package com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards.about;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards.termcondition.TermConditionAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutDetailRewardsFragment extends Fragment {

    public static final String GET_ABOUT = "get_about";
    public static final String GET_TUTORIAL = "get_tutorial";

    private View v;
    private Context mContext;

    public AboutDetailRewardsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_about_detail_rewards, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getString(GET_TUTORIAL) != null) {

                RecyclerView recycler_tutorial = v.findViewById(R.id.recycler_tutorial_about);
                recycler_tutorial.setLayoutManager(new LinearLayoutManager(mContext));

                String[] tutorial_array = new String[100];
                tutorial_array = bundle.getString(GET_TUTORIAL).split("##");

                TermConditionAdapter termConditionAdapter = new TermConditionAdapter(tutorial_array, mContext);
                recycler_tutorial.setAdapter(termConditionAdapter);
            }
            if (bundle.getString(GET_ABOUT) != null) {
                TextView text_description = v.findViewById(R.id.text_description_about);
                text_description.setText(bundle.getString(GET_ABOUT));
            }
        }
    }
}
