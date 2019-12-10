package com.andrew.bcamerchantservice.ui.loyalty.point_history;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.ui.loyalty.LoyaltyFragment;
import com.andrew.bcamerchantservice.ui.loyalty.point_history.earn.EarnFragment;
import com.andrew.bcamerchantservice.ui.loyalty.point_history.spend.SpendFragment;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.utils.TabAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PointHistoryFragment extends Fragment implements View.OnClickListener {

    private View v;
    private Context mContext;

    public PointHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_point_history, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        MainActivity.bottomNavigationView.setVisibility(View.GONE);

        TabLayout tabLayout = v.findViewById(R.id.tab_layout_point_history);
        ViewPager viewPager = v.findViewById(R.id.view_pager_point_history);
        TextView text_title = v.findViewById(R.id.text_title_toolbar_back);
        ImageButton image_btn_back = v.findViewById(R.id.img_btn_back_toolbar_back);

        TabAdapter tabAdapter = new TabAdapter(getFragmentManager());

        tabAdapter.addTab(new EarnFragment(), "Earn");
        tabAdapter.addTab(new SpendFragment(), "Spend");

        text_title.setText("Point History");

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        image_btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_btn_back_toolbar_back:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, new LoyaltyFragment());
                fragmentTransaction.commit();
                break;
        }
    }
}
