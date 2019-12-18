package com.andrew.bcamerchantservice.ui.tabpromorequest.promostatus;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.PromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabPromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.detailpromorequest.DetailPromoRequestFragment;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromoStatusFragment extends Fragment implements IPromoStatusView, PromoStatusAdapter.onClick {

    private View v;
    private IPromoStatusPresenter presenter;
    private PrefConfig prefConfig;
    private Context mContext;
    private PromoStatusAdapter promoStatusAdapter;
    private Map<String, PromoRequest.PromoType> promoTypeMap;
    private Map<String, PromoRequest.PromoStatus> promoStatusMap;

    public PromoStatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_promo_status, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);

        final RecyclerView recycler_promo_status = v.findViewById(R.id.recycler_promo_status);

        promoStatusAdapter = new PromoStatusAdapter(mContext, this);

        promoTypeMap = new HashMap<>();
        promoStatusMap = new HashMap<>();

        presenter = new PromoStatusPresenter(this);

        recycler_promo_status.setLayoutManager(new LinearLayoutManager(mContext));

        presenter.onLoadData(prefConfig.getMID());

        recycler_promo_status.setAdapter(promoStatusAdapter);

        recycler_promo_status.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                final int visible = newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE ? View.VISIBLE : View.GONE;
                TabPromoRequest.linear_new_promo.setVisibility(visible);
            }
        });
    }

    @Override
    public void loadPromoType(PromoRequest.PromoType promoType, PromoRequest.PromoStatus promoStatus) {
        if (!promoTypeMap.containsKey(promoType.getPromo_type_id())) {
            promoTypeMap.put(promoType.getPromo_type_id(), promoType);
            promoStatusAdapter.setPromoTypeMap(promoTypeMap);
            promoStatusAdapter.notifyDataSetChanged();
        }

        if (!promoStatusMap.containsKey(promoStatus.getPromo_status_id())) {
            promoStatusMap.put(promoStatus.getPromo_status_id(), promoStatus);
            promoStatusAdapter.setPromoStatusMap(promoStatusMap);
            promoStatusAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void loadData(List<PromoRequest> promoRequests) {
        promoStatusAdapter.setPromoRequestList(promoRequests);
        promoStatusAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPromoClick(String promo_request_id) {
        DetailPromoRequestFragment detailPromoRequestFragment = new DetailPromoRequestFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DetailPromoRequestFragment.PROMO_REQUEST_ID, promo_request_id);
        detailPromoRequestFragment.setArguments(bundle);
        bundle.putInt(TabPromoRequest.TAB_PAGE, 1);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, detailPromoRequestFragment);
        fragmentTransaction.commit();
    }
}
