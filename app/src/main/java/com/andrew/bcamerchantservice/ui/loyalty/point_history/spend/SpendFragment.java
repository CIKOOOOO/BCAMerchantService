package com.andrew.bcamerchantservice.ui.loyalty.point_history.spend;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.utils.PrefConfig;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpendFragment extends Fragment implements ISpendView {

    private View v;
    private Context mContext;
    private PrefConfig prefConfig;

    private ISpendPresenter presenter;

    public SpendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_spend, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();

        RecyclerView recycler_spend = v.findViewById(R.id.recycler_spend_point);

        prefConfig = new PrefConfig(mContext);
        presenter = new SpendPresenter(this);

        recycler_spend.setLayoutManager(new LinearLayoutManager(mContext));

        presenter.loadSpendListener(prefConfig.getMID());

    }
}
