package com.andrew.bcamerchantservice.ui.otherprofile;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.otherprofile.baseinformation.InformationProfile;
import com.andrew.bcamerchantservice.ui.otherprofile.forum.ForumOtherProfile;
import com.andrew.bcamerchantservice.ui.profile.Profile;
import com.andrew.bcamerchantservice.ui.profile.mystoreinformation.MyStoreInformation;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabAdapter;
import com.andrew.bcamerchantservice.utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherProfile extends Fragment implements MainActivity.onBackPressFragment, View.OnClickListener {

    public static final String GET_DATA_PROFILE = "get_data_profile";

    private static View view_description, v;

    public OtherProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_other_profile, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        ImageView background = v.findViewById(R.id.image_background_other_profile);
        RoundedImageView profile_picture = v.findViewById(R.id.image_profile_picture_other_profile);
        TextView text_merchant = v.findViewById(R.id.text_name_other_profile);
        TabLayout tabLayout = v.findViewById(R.id.tab_other_profile);
        ViewPager viewPager = v.findViewById(R.id.view_pager_other_profile);
        FrameLayout frame_catalog = v.findViewById(R.id.frame_description_catalog);
        LinearLayout linear_catalog = v.findViewById(R.id.linear_description_catalog);

        view_description = v.findViewById(R.id.custom_description_other_profile);

        InformationProfile informationProfile = new InformationProfile();
        ForumOtherProfile forumOtherProfile = new ForumOtherProfile();

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getParcelable(GET_DATA_PROFILE) != null) {
                Merchant merchant = bundle.getParcelable(GET_DATA_PROFILE);
                Picasso.get()
                        .load(merchant.getMerchant_background_picture())
                        .placeholder(R.color.cornflower_blue_palette)
                        .into(background);

                Picasso.get()
                        .load(merchant.getMerchant_profile_picture())
                        .into(profile_picture);

                text_merchant.setText(merchant.getMerchant_name());

                Bundle bundle1 = new Bundle();
                bundle1.putParcelable(InformationProfile.GETTING_DATA, merchant);
                informationProfile.setArguments(bundle1);

                TabAdapter tabAdapter = new TabAdapter(getFragmentManager());

                Bundle bundle2 = new Bundle();
                bundle2.putString(ForumOtherProfile.GETTING_MERCHANT_DATA, merchant.getMid());
                forumOtherProfile.setArguments(bundle2);

                tabAdapter.addTab(informationProfile, "Informasi Toko");
                tabAdapter.addTab(forumOtherProfile, "Forum");

                viewPager.setAdapter(tabAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }
        }

        frame_catalog.setOnClickListener(this);
        linear_catalog.setOnClickListener(this);
        view_description.setOnClickListener(this);
    }

    public static void showDescriptionCatalog(Merchant.MerchantCatalog merchantCatalog) {
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
        OtherProfile.view_description.setVisibility(View.VISIBLE);
        TextView text_title, text_price, text_description;
        ImageView image_catalog;

        text_title = v.findViewById(R.id.text_title_catalog_custom);
        text_price = v.findViewById(R.id.text_price_catalog_custom);
        text_description = v.findViewById(R.id.text_description_catalog_custom);
        image_catalog = v.findViewById(R.id.image_catalog_custom);

        text_title.setText(merchantCatalog.getCatalog_name());
        text_price.setText("Price: Rp " + Utils.priceFormat(merchantCatalog.getCatalog_price()));
        text_description.setText(merchantCatalog.getCatalog_description());
        Picasso.get()
                .load(merchantCatalog.getCatalog_image())
                .into(image_catalog);
    }

    @Override
    public void onBackPress(boolean check, Context context) {
        if (InformationProfile.isDescriptionClick) {
            InformationProfile.isDescriptionClick = false;
            view_description.setVisibility(View.GONE);
            MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.frame_description_catalog) {
            view_description.setVisibility(View.GONE);
            MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}
