package com.andrew.bcamerchantservice.ui.otherprofile.baseinformation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Merchant;


/**
 * A simple {@link Fragment} subclass.
 */
public class InformationProfile extends Fragment {

    public static final String GETTING_DATA = "getting_data";

    private View v;

    public InformationProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_information_profile, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        TextView text_owner = v.findViewById(R.id.text_owner_name_information_profile);
        TextView text_phone_number = v.findViewById(R.id.text_phone_number_information_profile);
        TextView text_email = v.findViewById(R.id.text_email_information_profile);
        TextView text_address = v.findViewById(R.id.text_address_information_profile);

        Bundle bundle = getArguments();
        if (bundle.getParcelable(GETTING_DATA) != null) {
            Merchant merchant = bundle.getParcelable(GETTING_DATA);
            text_owner.setText(" : " + merchant.getMerchant_owner_name());
            text_phone_number.setText(" : " + merchant.getMerchant_phone_number());
            text_email.setText(" : " + merchant.getMerchant_email());
            text_address.setText(" : " + merchant.getMerchant_address());
        }
    }
}
