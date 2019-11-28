package com.andrew.bcamerchantservice.ui.loyalty.point_history.earn;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EarnFragment extends Fragment implements IEarnView {

    private View v;
    private Context mContext;
    private PrefConfig prefConfig;
    private EarnAdapter earnAdapter;

    private IEarnPresenter presenter;

    public EarnFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_earn, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);

        RecyclerView recycler_earn = v.findViewById(R.id.recycler_earn);

        earnAdapter = new EarnAdapter(mContext);

        presenter = new EarnPresenter(this);

        recycler_earn.setLayoutManager(new LinearLayoutManager(mContext));

        recycler_earn.setAdapter(earnAdapter);

        presenter.loadMerchantEarningListener(prefConfig.getMID());
    }

    @Override
    public void onLoadEarn(List<Loyalty.Earn> earnList) {
        earnAdapter.setEarnList(earnList);
        earnAdapter.notifyDataSetChanged();
    }
}
