package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.confirmationpromo;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.model.PromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.PromoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.logo.LogoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.product.ProductFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.tncrequest.TNCRequestFragment;
import com.andrew.bcamerchantservice.utils.ImageAdapter;
import com.andrew.bcamerchantservice.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmationPromoRequest extends Fragment implements IConfirmationPromoRequestView, View.OnClickListener {

    public static final String PRODUCT_REQUEST = "product_request";
    public static final String NORMAL_FLOW = "normal_flow"; // flow biasa
    public static final String NORMAL_EDIT_FLOW = "normal_edit_flow"; // flow edit untuk pengajuan baru
    public static final String CORRECTION_FLOW = "correction_flow"; // flow edit untuk pengajuan koreksi
    public static final String STATUS_FLOW = "status";

    private View v;
    private Context mContext;
    private EditText edit_text_title, edit_text_promo, edit_text_payment_other;
    private TextView text_start_date, text_end_date, text_promo_location, text_specific_location, text_specific_term_condition, text_doc_name, text_edit_title, text_edit_date, text_edit_promo, text_edit_payment, text_edit_location, text_edit_term_condition, text_edit_logo, text_edit_product;
    private LinearLayout linear_other_payment, linear_attachment;
    private Bundle init_bundle;
    private PromoRequest promoRequest;
    private PromoRequest.PromoType promoType;
    private PaymentTypeAdapter paymentTypeAdapter;
    private ImageAdapter logoAdapter, productAdapter;

    private IConfirmationPromoRequestPresenter presenter;

    private String STATUS;

    public ConfirmationPromoRequest() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_confirmation_promo_request, container, false);
        init_bundle = getArguments();
        if (init_bundle != null) {
            if (init_bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA) != null
                    && init_bundle.getParcelableArrayList(ProductFragment.GET_LOGO_REQUEST) != null
                    && init_bundle.getParcelableArrayList(PRODUCT_REQUEST) != null) {
                promoRequest = init_bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA);

                if (promoRequest != null)
//            if(init_bundle.getString(STATUS_FLOW) != null){
//                STATUS = init_bundle.getString(STATUS_FLOW);
//                if(STATUS != null){
//                    boolean isCorrection = STATUS.equals(CORRECTION_FLOW);
//                    if (isCorrection)
                    initVar();
//                }
//            }
            }
        }

        return v;
    }

    private void initVar() {
        mContext = v.getContext();

        ((TextView) v.findViewById(R.id.text_title_toolbar_back)).setText("Konfirmasi Pengajuan Promo");
        ((TextView) v.findViewById(R.id.text_title_toolbar_back)).setTextSize(20);

        edit_text_title = v.findViewById(R.id.edit_text_title_confirmation_proquest);
        edit_text_promo = v.findViewById(R.id.edit_text_promo_type_confirmation_proquest);
        edit_text_payment_other = v.findViewById(R.id.edit_text_payment_others_confirmation_proquest);
        text_start_date = v.findViewById(R.id.text_start_date_confirmation_proquest);
        text_end_date = v.findViewById(R.id.text_end_date_confirmation_proquest);
        text_promo_location = v.findViewById(R.id.text_promo_location_confirmation_proequest);
        text_specific_location = v.findViewById(R.id.text_specific_location_confirmation_proquest);
        text_specific_term_condition = v.findViewById(R.id.text_specific_term_confition_confirmation_proquest);
        text_doc_name = v.findViewById(R.id.text_document_name_confirmation_proquest);
        text_edit_title = v.findViewById(R.id.text_edit_title_confirmation_proquest);
        text_edit_date = v.findViewById(R.id.text_edit_date_confirmation_proquest);
        text_edit_promo = v.findViewById(R.id.text_edit_promo_type_confirmation_proquest);
        text_edit_payment = v.findViewById(R.id.text_edit_payment_type_confirmation_proquest);
        text_edit_location = v.findViewById(R.id.text_edit_location_confirmation_proquest);
        text_edit_term_condition = v.findViewById(R.id.text_edit_term_condition_confirmation_proquest);
        text_edit_logo = v.findViewById(R.id.text_edit_logo_confirmation_proquest);
        text_edit_product = v.findViewById(R.id.text_edit_product_confirmation_proquest);
        linear_other_payment = v.findViewById(R.id.linear_other_payment_confirmation_proquest);
        linear_attachment = v.findViewById(R.id.linear_attachment_confirmation_proquest);

        RecyclerView recycler_payment_type = v.findViewById(R.id.recycler_payment_type_confirmation_proquest);
        RecyclerView recycler_logo = v.findViewById(R.id.recycler_logo_confirmation_proquest);
        RecyclerView recycler_product = v.findViewById(R.id.reycler_product_confirmation_proquest);
        ImageButton img_back = v.findViewById(R.id.img_btn_back_toolbar_back);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        List<PromoRequest.Facilities> facilitiesList = new ArrayList<>();
        List<ImagePicker> logoList;
        List<ImagePicker> productList;

        presenter = new ConfirmationPromoRequestPresenter(this);
        if (init_bundle.getParcelableArrayList(TNCRequestFragment.GET_FACILITIES_LIST) != null)
            facilitiesList = init_bundle.getParcelableArrayList(TNCRequestFragment.GET_FACILITIES_LIST);

        paymentTypeAdapter = new PaymentTypeAdapter(mContext, facilitiesList);
        logoAdapter = new ImageAdapter(mContext);
        productAdapter = new ImageAdapter(mContext);

        recycler_payment_type.setLayoutManager(linearLayoutManager);
        recycler_logo.setLayoutManager(new LinearLayoutManager(mContext));
        recycler_product.setLayoutManager(new LinearLayoutManager(mContext));

        recycler_product.setAdapter(productAdapter);
        recycler_logo.setAdapter(logoAdapter);
        recycler_payment_type.setAdapter(paymentTypeAdapter);

        edit_text_title.setText(promoRequest.getPromo_title());

        try {
            text_start_date.setText(": " + Utils.formatDateFromDateString("dd/MM/yyyy", "EEEE, dd MMM yyyy", promoRequest.getPromo_start_date()));
            text_end_date.setText(": " + Utils.formatDateFromDateString("dd/MM/yyyy", "EEEE, dd MMM yyyy", promoRequest.getPromo_end_date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (init_bundle.getString(TNCRequestFragment.GET_SPECIFIC_PAYMENT) != null) {
            String specific_payment = init_bundle.getString(TNCRequestFragment.GET_SPECIFIC_PAYMENT);
            linear_other_payment.setVisibility(View.GONE);
            if (specific_payment != null) {
                if (!specific_payment.isEmpty()) {
                    linear_other_payment.setVisibility(View.VISIBLE);
                    edit_text_payment_other.setText(specific_payment);
                }
            }
        }

        if (promoRequest.getPromo_location().equals("Berlaku diseluruh outlet")) {
            text_promo_location.setText("\u2022 Berlaku diseluruh outlet");
            text_specific_location.setVisibility(View.GONE);
        } else {
            text_promo_location.setText("\u2022 Hanya berlaku pada :");
            text_specific_location.setVisibility(View.VISIBLE);
            text_specific_location.setText(promoRequest.getPromo_location());
        }

        if (promoRequest.getPromo_tnc().isEmpty()) {
            text_specific_term_condition.setVisibility(View.GONE);
            linear_attachment.setVisibility(View.VISIBLE);
            if (init_bundle.getString(LogoRequestFragment.GET_ATTACHMENT) != null) {
                Uri uri = Uri.parse(init_bundle.getString(LogoRequestFragment.GET_ATTACHMENT));
                text_doc_name.setText(Utils.getFileName(uri, mContext));
            }
        } else {
            linear_attachment.setVisibility(View.GONE);
            text_specific_term_condition.setVisibility(View.VISIBLE);
            text_specific_term_condition.setText(promoRequest.getPromo_tnc());
        }

        if (init_bundle.getParcelableArrayList(ProductFragment.GET_LOGO_REQUEST) != null) {
            logoList = init_bundle.getParcelableArrayList(ProductFragment.GET_LOGO_REQUEST);
            logoAdapter.setImagePickerList(logoList);
            logoAdapter.notifyDataSetChanged();
        }

        if (init_bundle.getParcelableArrayList(PRODUCT_REQUEST) != null) {
            productList = init_bundle.getParcelableArrayList(PRODUCT_REQUEST);
            productAdapter.setImagePickerList(productList);
            productAdapter.notifyDataSetChanged();
        }

        presenter.getPromoData(promoRequest.getPromo_type_id());

        img_back.setOnClickListener(this);

        text_edit_product.setOnClickListener(this);
        text_edit_logo.setOnClickListener(this);
        text_edit_term_condition.setOnClickListener(this);
        text_edit_location.setOnClickListener(this);
        text_edit_payment.setOnClickListener(this);
        text_edit_promo.setOnClickListener(this);
        text_edit_date.setOnClickListener(this);
        text_edit_title.setOnClickListener(this);
    }

    @Override
    public void onLoadPromoTypeData(PromoRequest.PromoType promoType) {
        this.promoType = promoType;
        edit_text_promo.setText(promoType.getPromo_name());
    }

    @Override
    public void onClick(View view) {
        AppCompatActivity activity = (AppCompatActivity) mContext;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.text_edit_product_confirmation_proquest:
            case R.id.img_btn_back_toolbar_back:
                ProductFragment productFragment = new ProductFragment();

                bundle.putParcelable(PromoRequestFragment.GET_PROMO_DATA, init_bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA));
                if (init_bundle.getString(LogoRequestFragment.GET_ATTACHMENT) != null) {
                    bundle.putString(LogoRequestFragment.GET_ATTACHMENT, init_bundle.getString(LogoRequestFragment.GET_ATTACHMENT));
                }
                if (init_bundle.getString(PromoRequestFragment.GET_SPECIFIC_PAYMENT) != null) {
                    bundle.putString(PromoRequestFragment.GET_SPECIFIC_PAYMENT, init_bundle.getString(PromoRequestFragment.GET_SPECIFIC_PAYMENT));
                }
                if (init_bundle.getParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST) != null) {
                    bundle.putParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST, init_bundle.getParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST));
                }

                bundle.putParcelableArrayList(ProductFragment.GET_LOGO_REQUEST, init_bundle.getParcelableArrayList(ProductFragment.GET_LOGO_REQUEST));
                bundle.putParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST, init_bundle.getParcelableArrayList(PRODUCT_REQUEST));

                productFragment.setArguments(bundle);

                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, productFragment);
                fragmentTransaction.commit();
                break;
            case R.id.text_edit_logo_confirmation_proquest:
                LogoRequestFragment logoRequestFragment = new LogoRequestFragment();

                changeEditFragment(bundle, logoRequestFragment);

                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, logoRequestFragment);
                fragmentTransaction.commit();
                break;
            case R.id.text_edit_term_condition_confirmation_proquest:
                TNCRequestFragment tncRequestFragment = new TNCRequestFragment();

                changeEditFragment(bundle, tncRequestFragment);

                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, tncRequestFragment);
                fragmentTransaction.commit();
                break;
            case R.id.text_edit_location_confirmation_proquest:
            case R.id.text_edit_payment_type_confirmation_proquest:
            case R.id.text_edit_promo_type_confirmation_proquest:
            case R.id.text_edit_date_confirmation_proquest:
            case R.id.text_edit_title_confirmation_proquest:
                PromoRequestFragment promoRequestFragment = new PromoRequestFragment();

                changeEditFragment(bundle, promoRequestFragment);

                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, promoRequestFragment);
                fragmentTransaction.commit();
                break;
        }
    }

    private void changeEditFragment(Bundle bundle, Fragment fragment) {
        bundle.putParcelable(PromoRequestFragment.GET_PROMO_DATA, init_bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA));
        if (init_bundle.getString(LogoRequestFragment.GET_ATTACHMENT) != null) {
            bundle.putString(LogoRequestFragment.GET_ATTACHMENT, init_bundle.getString(LogoRequestFragment.GET_ATTACHMENT));
        }
        if (init_bundle.getString(PromoRequestFragment.GET_SPECIFIC_PAYMENT) != null) {
            bundle.putString(PromoRequestFragment.GET_SPECIFIC_PAYMENT, init_bundle.getString(PromoRequestFragment.GET_SPECIFIC_PAYMENT));
        }
        if (init_bundle.getParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST) != null) {
            bundle.putParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST, init_bundle.getParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST));
        }

        bundle.putParcelableArrayList(ProductFragment.GET_LOGO_REQUEST, init_bundle.getParcelableArrayList(ProductFragment.GET_LOGO_REQUEST));
        bundle.putParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST, init_bundle.getParcelableArrayList(PRODUCT_REQUEST));
        bundle.putString(STATUS_FLOW, NORMAL_EDIT_FLOW);

        fragment.setArguments(bundle);
    }
}
