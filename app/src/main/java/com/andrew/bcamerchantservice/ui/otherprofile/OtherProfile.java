package com.andrew.bcamerchantservice.ui.otherprofile;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.otherprofile.baseinformation.InformationProfile;
import com.andrew.bcamerchantservice.ui.otherprofile.forum.ForumOtherProfile;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabAdapter;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherProfile extends Fragment {

    public static final String GET_DATA_PROFILE = "get_data_profile";

    private View v;

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
    }
}
