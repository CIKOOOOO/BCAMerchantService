package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.tncpromorequest;

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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.model.PromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.detailpromorequest.DetailPromoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.InformationTextAdapter;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.PromoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.confirmationpromo.ConfirmationPromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.logo.LogoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.product.ProductFragment;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.util.ArrayList;
import java.util.List;

public class TNCPromoRequestFragment extends Fragment implements AgreementAdapter.onClick, View.OnClickListener, ITNCPromoRequestView {

    private View v;
    private Context mContext;
    private InformationTextAdapter informationTextAdapter;
    private AgreementAdapter agreementAdapter;
    private List<PromoRequest.Agreement> agreementList;
    private Bundle init_bundle;
    private FrameLayout custom_loading;
    private PromoRequest promoRequest;
    private PrefConfig prefConfig;
    private RelativeLayout relative_custom_success;

    private ITNCPromoRequestPresenter presenter;

    private String promo_request_id;

    public TNCPromoRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_tncpromo_request, container, false);
        init_bundle = getArguments();
        if (init_bundle != null) {
            if (init_bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA) != null &&
                    init_bundle.getParcelableArrayList(ProductFragment.GET_LOGO_REQUEST) != null &&
                    init_bundle.getParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST) != null)
                initVar();
        }
        return v;
    }

    private void initVar() {
        String[] terms_split = Constant.TERM_CONDITION_PROMO_REQUEST.split("##");

        promo_request_id = "";
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);

        RecyclerView recycler_term_condition = v.findViewById(R.id.recycler_term_condition_promo_request);
        RecyclerView recycler_agreement = v.findViewById(R.id.recycler_agreement_tnc_promo_request);
        Button btn_send = v.findViewById(R.id.btn_send_tnc_promo_request);
        Button btn_cancel = v.findViewById(R.id.btn_cancel_tnc_promo_request);

        custom_loading = v.findViewById(R.id.custom_loading_tnc_promo_request);
        relative_custom_success = v.findViewById(R.id.custom_success_promo_request);

        agreementList = getAgreementList();

        informationTextAdapter = new InformationTextAdapter(mContext);
        agreementAdapter = new AgreementAdapter(mContext, agreementList, this);

        presenter = new TNCPromoRequestPresenter(this);

        promoRequest = init_bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA);

        informationTextAdapter.setInformation_list(terms_split);

        recycler_term_condition.setLayoutManager(new LinearLayoutManager(mContext));
        recycler_agreement.setLayoutManager(new LinearLayoutManager(mContext));

        recycler_term_condition.setAdapter(informationTextAdapter);
        recycler_agreement.setAdapter(agreementAdapter);

        btn_send.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onCheckboxClick(int pos, boolean check) {
        PromoRequest.Agreement agreement = agreementList.get(pos);
        agreement.setCheck(check);
        agreementList.set(pos, agreement);
        agreementAdapter.setAgreementList(agreementList);
        agreementAdapter.notifyItemChanged(pos);
    }

    private List<PromoRequest.Agreement> getAgreementList() {
        List<PromoRequest.Agreement> agreements = new ArrayList<>();
        agreements.add(new PromoRequest.Agreement("Saya mengisi data dengan sebenar-benarnya.", false));
        agreements.add(new PromoRequest.Agreement("Saya menyetujui Syarat dan Ketentuan untuk menjadi Merchant BCA.", false));
        agreements.add(new PromoRequest.Agreement("Saya menyetujui bahwa promo yang telah diajukan tidakdapat disunting atau diberhentikan.", false));
        return agreements;
    }

    @Override
    public void onClick(View view) {
        AppCompatActivity activity = (AppCompatActivity) mContext;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.btn_send_tnc_promo_request:
                boolean isCheck = true;
                for (PromoRequest.Agreement agreement : agreementList) {
                    if (!agreement.isCheck()) {
                        isCheck = false;
                        break;
                    }
                }

                if (isCheck) {
                    if (promoRequest != null) {
                        custom_loading.setVisibility(View.VISIBLE);

                        List<PromoRequest.Facilities> facilitiesList = new ArrayList<>();
                        List<ImagePicker> logoList = new ArrayList<>();
                        List<ImagePicker> productList = new ArrayList<>();
                        String specific_payment = "";
                        Uri attachment_uri = null;

                        if (init_bundle.getParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST) != null) {
                            facilitiesList = init_bundle.getParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST);
                        }
                        if (init_bundle.getString(PromoRequestFragment.GET_SPECIFIC_PAYMENT) != null) {
                            specific_payment = init_bundle.getString(PromoRequestFragment.GET_SPECIFIC_PAYMENT);
                        }
                        if (init_bundle.getString(LogoRequestFragment.GET_ATTACHMENT) != null) {
                            String attachment_string = init_bundle.getString(LogoRequestFragment.GET_ATTACHMENT);
                            if (attachment_string != null) {
                                attachment_uri = Uri.parse(attachment_string);
                            }
                        }
                        if (init_bundle.getParcelableArrayList(ProductFragment.GET_LOGO_REQUEST) != null) {
                            logoList = init_bundle.getParcelableArrayList(ProductFragment.GET_LOGO_REQUEST);
                        }

                        if (init_bundle.getParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST) != null) {
                            productList = init_bundle.getParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST);
                        }

                        presenter.sendPromoRequest(prefConfig.getMID(), prefConfig.getMCC(), promoRequest, facilitiesList
                                , specific_payment, attachment_uri, mContext, logoList, productList);
                    }
                } else {
                    Toast.makeText(mContext, "Anda harus centang seluruh persetujuan", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_cancel_tnc_promo_request:
                ConfirmationPromoRequest confirmationPromoRequest = new ConfirmationPromoRequest();
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
                bundle.putParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST, init_bundle.getParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST));
                confirmationPromoRequest.setArguments(bundle);
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, confirmationPromoRequest);
                fragmentTransaction.commit();
                break;
            case R.id.btn_ok_custom_success_promo_request:
                DetailPromoRequestFragment detailPromoRequestFragment = new DetailPromoRequestFragment();

                bundle.putString(DetailPromoRequestFragment.PROMO_REQUEST_ID, promo_request_id);

                detailPromoRequestFragment.setArguments(bundle);

                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, detailPromoRequestFragment);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onSuccessPromoRequest(String promo_request_id) {
        custom_loading.setVisibility(View.GONE);
        relative_custom_success.setVisibility(View.VISIBLE);

        this.promo_request_id = promo_request_id;

        Button btn_oke = v.findViewById(R.id.btn_ok_custom_success_promo_request);

        btn_oke.setOnClickListener(this);
    }
}
