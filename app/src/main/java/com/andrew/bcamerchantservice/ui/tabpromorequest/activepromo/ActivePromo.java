package com.andrew.bcamerchantservice.ui.tabpromorequest.activepromo;


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
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabPromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.detailpromorequest.DetailPromoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promostatus.PromoStatusAdapter;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActivePromo extends Fragment implements MainActivity.onBackPressFragment, IActivePromoView, PromoStatusAdapter.onClick {

    private View v;
    private Context mContext;
    private PrefConfig prefConfig;
    private PromoStatusAdapter promoStatusAdapter;

    private IActivePromoPresenter presenter;

    private Map<String, PromoRequest.PromoStatus> promoStatusMap;
    private Map<String, PromoRequest.PromoType> promoTypeMap;

    public ActivePromo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_active_promo, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);

        RecyclerView recycler_active_promo = v.findViewById(R.id.recycler_active_promo);

        promoStatusMap = new HashMap<>();
        promoTypeMap = new HashMap<>();

        promoStatusAdapter = new PromoStatusAdapter(mContext, this);

        presenter = new ActivePromoPresenter(this);

        recycler_active_promo.setLayoutManager(new LinearLayoutManager(mContext));
        recycler_active_promo.setAdapter(promoStatusAdapter);

        presenter.loadActivePromo(prefConfig.getMID(), prefConfig.getMCC());

        recycler_active_promo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                final int visible = newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE ? View.VISIBLE : View.GONE;
                TabPromoRequest.linear_new_promo.setVisibility(visible);
            }
        });
    }

    @Override
    public void onBackPress(boolean check, Context context) {

    }

    @Override
    public void onPromoClick(String promo_request_id) {
        DetailPromoRequestFragment detailPromoRequestFragment = new DetailPromoRequestFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DetailPromoRequestFragment.PROMO_REQUEST_ID, promo_request_id);
        bundle.putInt(TabPromoRequest.TAB_PAGE, 0);
        detailPromoRequestFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, detailPromoRequestFragment);
        fragmentTransaction.commit();
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
}
