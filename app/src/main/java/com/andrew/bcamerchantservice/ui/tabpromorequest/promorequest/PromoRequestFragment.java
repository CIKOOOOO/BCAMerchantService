package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.PromoRequest;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabPromoRequest;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromoRequestFragment extends Fragment implements IPromoRequestView, PromoTypeAdapter.onClick, View.OnClickListener {
    private PrefConfig prefConfig;

    private Context mContext;
    private Activity mActivity;
    private View v;
    private PromoTypeAdapter promoTypeAdapter;
    private PromoRequest.PromoType promoType;

    private IPromoRequestPresenter presenter;

    public PromoRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_promo_request, container, false);
        initVar();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    private void initVar() {
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);

        MainActivity.bottomNavigationView.setVisibility(View.GONE);

        ((TextView) v.findViewById(R.id.text_title_toolbar_back)).setText("Pengajuan Promo");

        RecyclerView recycler_information = v.findViewById(R.id.recycler_information_promo_request);
        RecyclerView recycler_promo_type = v.findViewById(R.id.recycler_type_promo_request);
        ImageButton image_back = v.findViewById(R.id.img_btn_back_toolbar_back);

        InformationTextAdapter informationTextAdapter = new InformationTextAdapter(mContext);

        presenter = new PromoRequestPresenter(this);
        promoTypeAdapter = new PromoTypeAdapter(mContext, this);

        presenter.loadPromoType();
        presenter.loadPaymentType(prefConfig.getMID());

        recycler_information.setLayoutManager(new LinearLayoutManager(mContext));
        recycler_promo_type.setLayoutManager(new GridLayoutManager(mContext, 3));

        recycler_information.setAdapter(informationTextAdapter);
        recycler_promo_type.setAdapter(promoTypeAdapter);

        image_back.setOnClickListener(this);
    }

    private void changeFragment(Context context, Fragment fragment) {
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onLoadPromoType(List<PromoRequest.PromoType> promoTypes) {
        promoTypeAdapter.setPromoTypeList(promoTypes);
        promoTypeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPromoClick(PromoRequest.PromoType promoType) {
        this.promoType = promoType;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_btn_back_toolbar_back:
                changeFragment(mContext, new TabPromoRequest());
                break;
        }
    }
}
