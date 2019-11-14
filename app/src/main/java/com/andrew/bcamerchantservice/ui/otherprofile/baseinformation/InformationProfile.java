package com.andrew.bcamerchantservice.ui.otherprofile.baseinformation;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.otherprofile.OtherProfile;
import com.andrew.bcamerchantservice.ui.profile.mystoreinformation.CatalogAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class InformationProfile extends Fragment implements IInformationProfileView, CatalogAdapter.onItemClick {
    public static final String GETTING_DATA = "getting_data";
    public static boolean isDescriptionClick;

    private View v;
    private Context mContext;
    private CatalogAdapter catalogAdapter;

    private IInformationProfilePresenter presenter;

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
        isDescriptionClick = false;
        mContext = v.getContext();

        TextView text_owner = v.findViewById(R.id.text_owner_name_information_profile);
        TextView text_phone_number = v.findViewById(R.id.text_phone_number_information_profile);
        TextView text_email = v.findViewById(R.id.text_email_information_profile);
        TextView text_address = v.findViewById(R.id.text_address_information_profile);
        RecyclerView recycler_information_profile = v.findViewById(R.id.recycler_information_profile);

        presenter = new InformationProfilePresenter(this);


        Bundle bundle = getArguments();
        if (bundle.getParcelable(GETTING_DATA) != null) {
            Merchant merchant = bundle.getParcelable(GETTING_DATA);

            text_owner.setText(" : " + merchant.getMerchant_owner_name());
            text_phone_number.setText(" : " + merchant.getMerchant_phone_number());
            text_email.setText(" : " + merchant.getMerchant_email());
            text_address.setText(" : " + merchant.getMerchant_address());

            presenter.onLoadCatalog(merchant.getMid());

            catalogAdapter = new CatalogAdapter(mContext, false, this);
            recycler_information_profile.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            recycler_information_profile.setAdapter(catalogAdapter);
        }
    }

    @Override
    public void onLoadCatalog(List<Merchant.MerchantCatalog> catalogList) {
        catalogAdapter.setCatalogList(catalogList);
        catalogAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(Merchant.MerchantCatalog merchantCatalog) {
        isDescriptionClick = true;
        OtherProfile.showDescriptionCatalog(merchantCatalog);
    }

    @Override
    public void onDelete(Merchant.MerchantCatalog merchantCatalog, int pos) {

    }
}
