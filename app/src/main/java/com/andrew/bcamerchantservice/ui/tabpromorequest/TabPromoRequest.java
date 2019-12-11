package com.andrew.bcamerchantservice.ui.tabpromorequest;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.tabpromorequest.activepromo.ActivePromo;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.PromoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promostatus.PromoStatus;
import com.andrew.bcamerchantservice.utils.TabAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabPromoRequest extends Fragment implements View.OnClickListener {
    public static int PAGE;

    private View v;

    public TabPromoRequest() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_tab_promo_request, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        PAGE = 0;

        ViewPager viewPager = v.findViewById(R.id.view_pager_promo_request);
        TabLayout tabLayout = v.findViewById(R.id.tab_promo_request);
        LinearLayout linear_new_promo = v.findViewById(R.id.linear_new_promo_request);
        TabAdapter tabAdapter = new TabAdapter(getFragmentManager());

        tabAdapter.addTab(new ActivePromo(), "Promo Berjalan");
        tabAdapter.addTab(new PromoStatus(), "Status Pengajuan");

        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

//        viewPager.setCurrentItem(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float vs, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                PAGE = i;
//                if (i == 0)
//                    StatusPromo.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        linear_new_promo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_new_promo_request:
                changeFragment(v.getContext(), new PromoRequestFragment());
                break;
        }
    }

    private void changeFragment(Context context, Fragment fragment) {
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
