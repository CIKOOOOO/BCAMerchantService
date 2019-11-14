package com.andrew.bcamerchantservice.ui.profile.mystoreinformation;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.profile.Profile;
import com.andrew.bcamerchantservice.ui.profile.mystoreinformation.catalog.CatalogFragment;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.andrew.bcamerchantservice.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyStoreInformation extends Fragment implements View.OnClickListener
        , IMyStoreInformationView, CatalogAdapter.onItemClick {

    public static boolean isDescriptionClick;

    private View v;
    private EditText edit_text_owner, edit_text_phone_number, edit_text_email, edit_text_address, edit_text_description;
    private ImageButton image_btn_check_owner, image_btn_check_phone_number, image_btn_check_email, image_btn_check_address, image_btn_check_description;
    private ImageButton img_btn_owner, img_btn_phone_number, img_btn_email, img_btn_address, img_btn_description;
    private Context mContext;
    private Activity mActivity;
    private PrefConfig prefConfig;
    private CatalogAdapter adapter;

    private IMyStoreInformationPresenter presenter;

    private static final String MERCHANT_OWNER = "merchant_owner_name", MERCHANT_PHONE_NUMBER = "merchant_phone_number", MERCHANT_DESCRIPTION = "merchant_description", MERCHANT_EMAIL = "merchant_email", MERCHANT_ADDRESS = "merchant_address";

    public MyStoreInformation() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_my_store_information, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        isDescriptionClick = false;
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);
        presenter = new MyStoreInformationPresenter(this);

        RecyclerView recycler_catalog = v.findViewById(R.id.recycler_store_information);
        FloatingActionButton fab = v.findViewById(R.id.fab_add_store_information);
//        CoordinatorLayout.LayoutParams layoutParams2 = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();

        edit_text_owner = v.findViewById(R.id.edit_text_owner_store_information);
        edit_text_phone_number = v.findViewById(R.id.edit_text_phone_number_store_information);
        edit_text_email = v.findViewById(R.id.edit_text_email_store_information);
        edit_text_address = v.findViewById(R.id.edit_text_address_store_information);
        edit_text_description = v.findViewById(R.id.edit_text_description_store_information);

        image_btn_check_owner = v.findViewById(R.id.image_button_check_owner_store_information);
        image_btn_check_phone_number = v.findViewById(R.id.image_button_check_phone_number_store_information);
        image_btn_check_email = v.findViewById(R.id.image_button_check_email_store_information);
        image_btn_check_address = v.findViewById(R.id.image_button_check_address_store_information);
        image_btn_check_description = v.findViewById(R.id.image_button_check_description_store_information);

        img_btn_owner = v.findViewById(R.id.image_button_owner_store_information);
        img_btn_phone_number = v.findViewById(R.id.image_button_phone_number_store_information);
        img_btn_email = v.findViewById(R.id.image_button_email_store_information);
        img_btn_address = v.findViewById(R.id.image_button_address_store_information);
        img_btn_description = v.findViewById(R.id.image_button_description_store_information);

        recycler_catalog.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new CatalogAdapter(mContext, true, this);
//        layoutParams2.setBehavior(new FloatingActionButtonBehavior());

        presenter.onLoadCatalog(prefConfig.getMID());

        fab.setOnClickListener(this);

        recycler_catalog.setAdapter(adapter);

        edit_text_owner.setText(prefConfig.getOwnerName());
        edit_text_phone_number.setText(prefConfig.getPhoneNumber());
        edit_text_email.setText(prefConfig.getEmail());
        edit_text_address.setText(prefConfig.getStoreAddress());
        edit_text_description.setText(prefConfig.getDescription());

        img_btn_owner.setOnClickListener(this);
        img_btn_phone_number.setOnClickListener(this);
        img_btn_email.setOnClickListener(this);
        img_btn_address.setOnClickListener(this);
        img_btn_description.setOnClickListener(this);

        image_btn_check_owner.setOnClickListener(this);
        image_btn_check_phone_number.setOnClickListener(this);
        image_btn_check_email.setOnClickListener(this);
        image_btn_check_address.setOnClickListener(this);
        image_btn_check_description.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_button_owner_store_information:
                editCondition(true, edit_text_owner, image_btn_check_owner, img_btn_owner);
                break;
            case R.id.image_button_check_owner_store_information:
                if (edit_text_owner.getText().toString().isEmpty()) {
                    edit_text_owner.setError("This field cannot be empty");
                    break;
                }
                presenter.editProfile(prefConfig.getMID(), MERCHANT_OWNER, edit_text_owner.getText().toString());
                editCondition(false, edit_text_owner, image_btn_check_owner, img_btn_owner);
                break;
            case R.id.image_button_phone_number_store_information:
                editCondition(true, edit_text_phone_number, image_btn_check_phone_number, img_btn_phone_number);
                break;
            case R.id.image_button_check_phone_number_store_information:
                if (edit_text_phone_number.getText().toString().length() < 10 || edit_text_phone_number.getText().toString().length() > 13) {
                    edit_text_phone_number.setError("Invalid phone number");
                    break;
                }
                presenter.editProfile(prefConfig.getMID(), MERCHANT_PHONE_NUMBER, edit_text_phone_number.getText().toString());
                editCondition(false, edit_text_phone_number, image_btn_check_phone_number, img_btn_phone_number);
                break;
            case R.id.image_button_email_store_information:
                editCondition(true, edit_text_email, image_btn_check_email, img_btn_email);
                break;
            case R.id.image_button_check_email_store_information:
                if (!Utils.isValidEmail(edit_text_email.getText().toString())) {
                    edit_text_email.setError("Invalid email format");
                    break;
                }
                presenter.editProfile(prefConfig.getMID(), MERCHANT_EMAIL, edit_text_email.getText().toString());
                editCondition(false, edit_text_email, image_btn_check_email, img_btn_email);
                break;
            case R.id.image_button_address_store_information:
                editCondition(true, edit_text_address, image_btn_check_address, img_btn_address);
                break;
            case R.id.image_button_check_address_store_information:
                if (edit_text_address.getText().toString().length() < 5) {
                    edit_text_address.setError("Invalid address");
                    break;
                }
                presenter.editProfile(prefConfig.getMID(), MERCHANT_ADDRESS, edit_text_address.getText().toString());
                editCondition(false, edit_text_address, image_btn_check_address, img_btn_address);
                break;
            case R.id.image_button_description_store_information:
                editCondition(true, edit_text_description, image_btn_check_description, img_btn_description);
                break;
            case R.id.image_button_check_description_store_information:
                presenter.editProfile(prefConfig.getMID(), MERCHANT_DESCRIPTION, edit_text_description.getText().toString());
                editCondition(false, edit_text_description, image_btn_check_description, img_btn_description);
                break;
            case R.id.fab_add_store_information:
                MainActivity.bottomNavigationView.setVisibility(View.GONE);
                FragmentTransaction fragmentTransactions = getFragmentManager().beginTransaction();
                fragmentTransactions.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransactions.replace(R.id.main_frame, new CatalogFragment());
                fragmentTransactions.commit();
                break;
        }
    }

    private void editCondition(boolean isEdit, EditText editText, ImageButton imageButton, ImageButton img) {
        if (isEdit) {
            editText.requestFocus(editText.getLayoutDirection());
            editText.setBackground(mContext.getDrawable(R.drawable.rectangle_rounded_stroke_blue));
            editText.setEnabled(true);
            editText.setCursorVisible(true);

            img.setVisibility(View.GONE);
            imageButton.setVisibility(View.VISIBLE);
        } else {
            editText.setBackground(null);
            editText.setCursorVisible(false);
            editText.setEnabled(false);
            Utils.hideSoftKeyboard(mActivity);

            img.setVisibility(View.VISIBLE);
            imageButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccessEditProfile(String value, String KEY) {
        switch (KEY) {
            case MERCHANT_ADDRESS:
                prefConfig.insertAddress(value);
                break;
            case MERCHANT_DESCRIPTION:
                prefConfig.insertDescription(value);
                break;
            case MERCHANT_EMAIL:
                prefConfig.insertEmail(value);
                break;
            case MERCHANT_OWNER:
                prefConfig.insertOwnerName(value);
                break;
            case MERCHANT_PHONE_NUMBER:
                prefConfig.insertPhoneNumber(value);
                break;
        }
    }

    @Override
    public void onLoadCatalog(List<Merchant.MerchantCatalog> catalogList) {
        adapter.setCatalogList(catalogList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccessDeleteCatalog(int pos) {
        adapter.setLastPosition(-1);
        adapter.notifyItemChanged(pos);
    }

    @Override
    public void onClick(Merchant.MerchantCatalog merchantCatalog) {
        isDescriptionClick = true;
        Profile.showDescriptionCatalog(merchantCatalog);
    }

    @Override
    public void onDelete(Merchant.MerchantCatalog merchantCatalog, int pos) {
        presenter.onDeleteCatalog(prefConfig.getMID(), merchantCatalog.getCid(), pos);
    }
}
