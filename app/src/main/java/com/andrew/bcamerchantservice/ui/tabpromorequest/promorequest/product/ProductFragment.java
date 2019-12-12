package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.product;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.model.PromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.PromoRequestFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductFragment extends Fragment {

    public static final String GET_LOGO_REQUEST = "get_logo_request";

    private View v;
    private Context mContext;
    private Bundle init_bundle;

    public ProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_product, container, false);
//        init_bundle = getArguments();
//        if (init_bundle != null) {
//            if (init_bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA) != null
//                    && init_bundle.getParcelableArrayList(GET_LOGO_REQUEST) != null) {
                initVar();
//            }
//        }
        return v;
    }

    private void initVar() {
        mContext = v.getContext();

        ((TextView) v.findViewById(R.id.text_title_toolbar_back)).setText("Pengajuan Promo");
    }
}
