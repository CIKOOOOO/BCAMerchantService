package com.andrew.bcamerchantservice.ui.tabpromorequest.activepromo;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.ui.main.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActivePromo extends Fragment implements MainActivity.onBackPressFragment {


    public ActivePromo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_promo, container, false);
    }

    @Override
    public void onBackPress(boolean check, Context context) {

    }
}
