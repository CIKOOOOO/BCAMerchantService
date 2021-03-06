package com.andrew.bcamerchantservice.ui.tabpromorequest.detailpromorequest;


import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.model.PromoRequest;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabPromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.InformationTextAdapter;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.PromoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.confirmationpromo.ConfirmationPromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.confirmationpromo.PaymentTypeAdapter;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.logo.LogoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.product.ProductFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.tncrequest.TNCRequestFragment;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.ImageBitmapAdapter;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.andrew.bcamerchantservice.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailPromoRequestFragment extends Fragment implements IDetailPromoRequestView, View.OnClickListener, MainActivity.onBackPressFragment {

    public static final String PROMO_REQUEST_ID = "promo_request_id";
    public static final String CORRECTION_FLOW = "correction_flow"; // flow edit untuk pengajuan koreksi

    private View v;
    private Context mContext;
    private Activity mActivity;
    private PrefConfig prefConfig;
    private TextView text_edit_title, text_edit_date, text_edit_promo, text_edit_payment, text_edit_location, text_edit_term_condition, text_edit_logo, text_edit_product, text_other_payment;
    private LinearLayout linear_other_payment;
    private PromoRequest promoRequest;

    private IDetailPromoRequestPresenter presenter;

    private List<ImagePicker> logoPickerList, productPickerList;
    private List<PromoRequest.Product> productList;
    private List<PromoRequest.Logo> logoList;
    private List<PromoRequest.Facilities> facilitiesList;
    private String attachment_url, attachment_name, special_facilities, flow_status;
    private long downloadID;
    private int tab_page;
    private Uri attachment_URI;

    public DetailPromoRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(onDownloadComplete);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_detail_promo_request, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        tab_page = -1;
        downloadID = -1;
        special_facilities = "";
        flow_status = "";
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);

        ((TextView) v.findViewById(R.id.text_title_toolbar_back)).setText("Detail Pengajuan Promo");

        ImageButton img_back = v.findViewById(R.id.img_btn_back_toolbar_back);

        linear_other_payment = v.findViewById(R.id.linear_other_payment_confirmation_proquest);
        text_edit_title = v.findViewById(R.id.text_edit_title_confirmation_proquest);
        text_edit_date = v.findViewById(R.id.text_edit_date_confirmation_proquest);
        text_edit_promo = v.findViewById(R.id.text_edit_promo_type_confirmation_proquest);
        text_edit_payment = v.findViewById(R.id.text_edit_payment_type_confirmation_proquest);
        text_edit_location = v.findViewById(R.id.text_edit_location_confirmation_proquest);
        text_edit_term_condition = v.findViewById(R.id.text_edit_term_condition_confirmation_proquest);
        text_edit_logo = v.findViewById(R.id.text_edit_logo_confirmation_proquest);
        text_edit_product = v.findViewById(R.id.text_edit_product_confirmation_proquest);
        text_other_payment = v.findViewById(R.id.edit_text_payment_others_confirmation_proquest);

        presenter = new DetailPromoRequestPresenter(this);

        logoPickerList = new ArrayList<>();
        productPickerList = new ArrayList<>();
        productList = new ArrayList<>();
        logoList = new ArrayList<>();
        facilitiesList = new ArrayList<>();

        mContext.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        MainActivity.bottomNavigationView.setVisibility(View.GONE);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getString(ConfirmationPromoRequest.STATUS_FLOW) != null) {
                flow_status = bundle.getString(ConfirmationPromoRequest.STATUS_FLOW);
                if (flow_status != null) {
                    if (flow_status.isEmpty()) {
                        if (bundle.getString(PROMO_REQUEST_ID) != null) {
                            /*
                             * For normal Flow
                             * */
                            String promo_request_id = bundle.getString(PROMO_REQUEST_ID);
                            if (promo_request_id != null) {
                                presenter.loadPromoRequest(prefConfig.getMID(), prefConfig.getMCC(), promo_request_id);
                            }
                        }
                    } else if (flow_status.equals(DetailPromoRequestFragment.CORRECTION_FLOW)) {
                        /*
                         *For Correction Flow
                         * */
                        if (bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA) != null) {
                            promoRequest = bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA);
                            if (promoRequest != null) {
                                presenter.loadStatusPromoTypeRequest(promoRequest.getPromo_status(), promoRequest.getPromo_type_id());
                                onLoadPromoData(promoRequest);
                            }
                        }

                        if (bundle.getString(LogoRequestFragment.GET_ATTACHMENT) != null) {
                            attachment_URI = Uri.parse(bundle.getString(LogoRequestFragment.GET_ATTACHMENT));
                            if (attachment_URI != null) {
                                LinearLayout linear_attachment = v.findViewById(R.id.linear_attachment_confirmation_proquest);
                                TextView text_attachment = v.findViewById(R.id.text_document_name_confirmation_proquest);
                                TextView text_tnc = v.findViewById(R.id.text_specific_term_confition_confirmation_proquest);
                                text_tnc.setVisibility(View.GONE);
                                linear_attachment.setVisibility(View.VISIBLE);
                                text_attachment.setText(Utils.getFileName(attachment_URI, mContext));
                            }
                        }

                        if (bundle.getParcelableArrayList(TNCRequestFragment.GET_FACILITIES_LIST) != null) {
                            this.facilitiesList = bundle.getParcelableArrayList(TNCRequestFragment.GET_FACILITIES_LIST);
                            PaymentTypeAdapter paymentTypeAdapter = new PaymentTypeAdapter(mContext, facilitiesList);
                            RecyclerView recycler_facilities = v.findViewById(R.id.recycler_payment_type_confirmation_proquest);
                            recycler_facilities.setLayoutManager(new GridLayoutManager(mContext, 2));
                            recycler_facilities.setAdapter(paymentTypeAdapter);
                        }
                        if (bundle.getParcelableArrayList(ProductFragment.GET_LOGO_REQUEST) != null) {
                            this.logoPickerList = bundle.getParcelableArrayList(ProductFragment.GET_LOGO_REQUEST);
                            if (logoPickerList != null) {
                                if (logoPickerList.size() > 0) {
                                    RecyclerView recycler_logo = v.findViewById(R.id.recycler_logo_confirmation_proquest);
                                    recycler_logo.setLayoutManager(new LinearLayoutManager(mContext));
                                    String check_adapter = "";
                                    try {
                                        ImagePicker imagePicker = logoPickerList.get(0);
                                        check_adapter = "image_picker";

                                    } catch (ClassCastException e) {
                                        check_adapter = "logo_list";
                                    }
                                    if (check_adapter.equals("image_picker")) {
                                        ImageBitmapAdapter imageBitmapAdapter = new ImageBitmapAdapter(mContext);
                                        imageBitmapAdapter.setImagePickerList(logoPickerList);
                                        recycler_logo.setAdapter(imageBitmapAdapter);
                                    } else {
                                        logoList = bundle.getParcelableArrayList(ProductFragment.GET_LOGO_REQUEST);
                                        LogoAdapter logoAdapter = new LogoAdapter(mContext, logoList);
                                        recycler_logo.setAdapter(logoAdapter);
                                    }
                                }
                            }
                        }

                        if (bundle.getParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST) != null) {
                            this.productPickerList = bundle.getParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST);
                            if (productPickerList != null) {
                                if (productPickerList.size() > 0) {
                                    RecyclerView recycler_product = v.findViewById(R.id.recycler_product_confirmation_proquest);
                                    recycler_product.setLayoutManager(new LinearLayoutManager(mContext));
                                    String check_adapter = "";
                                    try {
                                        ImagePicker imagePicker = productPickerList.get(0);
                                        check_adapter = "image_picker";

                                    } catch (ClassCastException e) {
                                        check_adapter = "logo_list";
                                    }

                                    if (check_adapter.equals("image_picker")) {
                                        ImageBitmapAdapter productAdapter = new ImageBitmapAdapter(mContext);
                                        productAdapter.setImagePickerList(this.productPickerList);
                                        recycler_product.setAdapter(productAdapter);
                                    } else {
                                        productList = bundle.getParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST);
                                        ProductAdapter productAdapter = new ProductAdapter(mContext, productList);
                                        recycler_product.setAdapter(productAdapter);
                                    }
                                }
                            }
                        }

                        if (bundle.getString(TNCRequestFragment.GET_SPECIFIC_FACILITIES) != null) {
                            special_facilities = bundle.getString(TNCRequestFragment.GET_SPECIFIC_FACILITIES);
                            if (special_facilities == null || special_facilities.isEmpty()) {
                                linear_other_payment.setVisibility(View.GONE);
                            } else {
                                text_other_payment.setText(special_facilities);
                                linear_other_payment.setVisibility(View.VISIBLE);
                            }
                        } else {
                            linear_other_payment.setVisibility(View.GONE);
                        }
                    }
                }
            } else if (bundle.getString(PROMO_REQUEST_ID) != null) {
                String promo_request_id = bundle.getString(PROMO_REQUEST_ID);
                if (promo_request_id != null) {
                    presenter.loadPromoRequest(prefConfig.getMID(), prefConfig.getMCC(), promo_request_id);
                }
            }
            tab_page = bundle.getInt(TabPromoRequest.TAB_PAGE, 0);
        }

        img_back.setOnClickListener(this);
    }

    @Override
    public void onLoadPromoData(PromoRequest promoRequest) {
        this.promoRequest = promoRequest;
        EditText text_title = v.findViewById(R.id.edit_text_title_confirmation_proquest);
        TextView text_start_date = v.findViewById(R.id.text_start_date_confirmation_proquest);
        TextView text_end_date = v.findViewById(R.id.text_end_date_confirmation_proquest);
        TextView text_location = v.findViewById(R.id.text_promo_location_confirmation_proequest);
        TextView text_specific_location = v.findViewById(R.id.text_specific_location_confirmation_proquest);
        LinearLayout linear_attachment = v.findViewById(R.id.linear_attachment_confirmation_proquest);
        TextView text_tnc = v.findViewById(R.id.text_specific_term_confition_confirmation_proquest);
        TextView text_attachment = v.findViewById(R.id.text_document_name_confirmation_proquest);
        LinearLayout linear_correction = v.findViewById(R.id.linear_correction_detail_promo_request);
        RecyclerView recycler_correction = v.findViewById(R.id.recycler_correction_detail_promo_request);
        Button btn_next_confirmation = v.findViewById(R.id.btn_next_confirmation_proquest);

        text_edit_title.setVisibility(View.GONE);
        text_edit_date.setVisibility(View.GONE);
        text_edit_promo.setVisibility(View.GONE);
        text_edit_payment.setVisibility(View.GONE);
        text_edit_location.setVisibility(View.GONE);
        text_edit_term_condition.setVisibility(View.GONE);
        text_edit_logo.setVisibility(View.GONE);
        text_edit_product.setVisibility(View.GONE);

        linear_correction.setVisibility(View.GONE);
        btn_next_confirmation.setVisibility(View.GONE);

        if (promoRequest.getPromo_status().equals("promo_status_2")) {
            linear_correction.setVisibility(View.VISIBLE);
            recycler_correction.setLayoutManager(new LinearLayoutManager(mContext));
            btn_next_confirmation.setVisibility(View.VISIBLE);

            String[] correction_menu = promoRequest.getPromo_correction_menu().split("##");
            String[] correction_reason = promoRequest.getPromo_correction_reason().split("##");

            InformationTextAdapter informationTextAdapter = new InformationTextAdapter(mContext);
            informationTextAdapter.setInformation_list(correction_reason);
            recycler_correction.setAdapter(informationTextAdapter);

            for (String s : correction_menu) {
                switch (s) {
                    case "Judul Promo":
                        text_edit_title.setVisibility(View.VISIBLE);
                        text_edit_title.setOnClickListener(this);
                        break;
                    case "Jenis Payment":
                        text_edit_payment.setVisibility(View.VISIBLE);
                        text_edit_payment.setOnClickListener(this);
                        break;
                    case "Jenis Promo":
                        text_edit_promo.setVisibility(View.VISIBLE);
                        text_edit_promo.setOnClickListener(this);
                        break;
                    case "Syarat & Ketentuan":
                        text_edit_term_condition.setVisibility(View.VISIBLE);
                        text_edit_term_condition.setOnClickListener(this);
                        break;
                    case "Logo Perusahaan":
                        text_edit_logo.setVisibility(View.VISIBLE);
                        text_edit_logo.setOnClickListener(this);
                        break;
                    case "Produk Perusahaan":
                        text_edit_product.setVisibility(View.VISIBLE);
                        text_edit_product.setOnClickListener(this);
                        break;
                }
            }
        } else if (promoRequest.getPromo_status().equals("promo_status_3")) {
            linear_correction.setVisibility(View.VISIBLE);
            recycler_correction.setLayoutManager(new LinearLayoutManager(mContext));

            String[] correction_reason = promoRequest.getPromo_correction_reason().split("##");

            InformationTextAdapter informationTextAdapter = new InformationTextAdapter(mContext);
            informationTextAdapter.setInformation_list(correction_reason);
            recycler_correction.setAdapter(informationTextAdapter);
        }

        text_title.setText(promoRequest.getPromo_title());

        try {
            text_start_date.setText(": " + Utils.formatDateFromDateString("dd/MM/yyyy", "EEEE, dd MMM yyyy", promoRequest.getPromo_start_date()));
            text_end_date.setText(": " + Utils.formatDateFromDateString("dd/MM/yyyy", "EEEE, dd MMM yyyy", promoRequest.getPromo_end_date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (promoRequest.getPromo_location().equals("Berlaku diseluruh outlet")) {
            text_location.setText("\u2022 " + promoRequest.getPromo_location());
            text_specific_location.setVisibility(View.GONE);
        } else {
            text_location.setText("\u2022 Hanya berlaku pada");
            text_specific_location.setVisibility(View.VISIBLE);
            text_specific_location.setText(promoRequest.getPromo_location());
        }

        if (promoRequest.getPromo_tnc().contains("firebase") && promoRequest.getPromo_tnc().contains("attachment")) {
            String[] split_name = promoRequest.getPromo_tnc().split("attachment");
            String[] split_name_2 = split_name[1].split("alt");
            String final_name = split_name_2[0].substring(1, split_name_2[0].length() - 1).replace("%20", " ");

            this.attachment_name = final_name;
            this.attachment_url = promoRequest.getPromo_tnc();

            text_attachment.setText(final_name);
            linear_attachment.setVisibility(View.VISIBLE);
            text_tnc.setVisibility(View.GONE);
            text_attachment.setOnClickListener(this);
        } else if (!promoRequest.getPromo_tnc().isEmpty()) {
            text_tnc.setText(promoRequest.getPromo_tnc());
            text_tnc.setVisibility(View.VISIBLE);
            linear_attachment.setVisibility(View.GONE);
        }

        btn_next_confirmation.setOnClickListener(this);
    }

    @Override
    public void onLoadPromoType(PromoRequest.PromoType promoType, PromoRequest.PromoStatus promoStatus) {
        EditText text_promo_type = v.findViewById(R.id.edit_text_promo_type_confirmation_proquest);
        TextView text_status = v.findViewById(R.id.text_status_detail_promo_request);
        text_promo_type.setText(promoType.getPromo_name());
        text_status.setText(promoStatus.getPromo_status_name());
    }

    @Override
    public void onLoadRestData(String special_facilities, List<PromoRequest.Facilities> facilitiesList
            , List<PromoRequest.Logo> logoList, List<PromoRequest.Product> productList) {

        if (facilitiesList.size() > 0) {
            this.facilitiesList = facilitiesList;
            PaymentTypeAdapter paymentTypeAdapter = new PaymentTypeAdapter(mContext, facilitiesList);
            RecyclerView recycler_facilities = v.findViewById(R.id.recycler_payment_type_confirmation_proquest);
            recycler_facilities.setLayoutManager(new GridLayoutManager(mContext, 2));
            recycler_facilities.setAdapter(paymentTypeAdapter);
        }

        if (logoList.size() > 0) {
            this.logoList = logoList;
            RecyclerView recycler_logo = v.findViewById(R.id.recycler_logo_confirmation_proquest);
            LogoAdapter logo_adapter = new LogoAdapter(mContext, logoList);
            recycler_logo.setLayoutManager(new LinearLayoutManager(mContext));
            recycler_logo.setAdapter(logo_adapter);
        }

        if (productList.size() > 0) {
            this.productList = productList;
            RecyclerView recycler_product = v.findViewById(R.id.recycler_product_confirmation_proquest);
            ProductAdapter productAdapter = new ProductAdapter(mContext, productList);
            recycler_product.setLayoutManager(new LinearLayoutManager(mContext));
            recycler_product.setAdapter(productAdapter);
        }

        if (special_facilities != null && !special_facilities.isEmpty()) {
            text_other_payment.setText(special_facilities);
            linear_other_payment.setVisibility(View.VISIBLE);
        } else {
            linear_other_payment.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        AppCompatActivity activity = (AppCompatActivity) mContext;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();

        switch (view.getId()) {
            case R.id.btn_next_confirmation_proquest:

                break;
            case R.id.text_document_name_confirmation_proquest:
                if (attachment_url != null && !attachment_url.isEmpty()) {
                    if (ActivityCompat.checkSelfPermission(mActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                Constant.PERMISSION_WRITE_EXTERNAL);
                    } else {
                        beginDownload();
                    }
                }
                break;
            case R.id.img_btn_back_toolbar_back:
                TabPromoRequest tabPromoRequest = new TabPromoRequest();
                bundle.putInt(TabPromoRequest.TAB_PAGE, tab_page);
                tabPromoRequest.setArguments(bundle);
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, tabPromoRequest);
                fragmentTransaction.commit();
                break;
            case R.id.text_edit_title_confirmation_proquest:
                break;
            case R.id.text_edit_promo_type_confirmation_proquest:
                break;
            case R.id.text_edit_payment_type_confirmation_proquest:
                break;
            case R.id.text_edit_location_confirmation_proquest:
                break;
            case R.id.text_edit_term_condition_confirmation_proquest:
                changeCorrectionFragment(new TNCRequestFragment());
                break;
            case R.id.text_edit_logo_confirmation_proquest:
                break;
            case R.id.text_edit_product_confirmation_proquest:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.PERMISSION_WRITE_EXTERNAL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                beginDownload();
            } else {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void beginDownload() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(attachment_url));
        request.setTitle(attachment_name);
        request.setDescription("Downloading..");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, attachment_name);

        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadID = manager.enqueue(request);
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                Toast.makeText(mContext, "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void changeCorrectionFragment(Fragment fragment) {
        AppCompatActivity activity = (AppCompatActivity) mContext;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PromoRequestFragment.GET_PROMO_DATA, promoRequest);
        bundle.putString(ConfirmationPromoRequest.STATUS_FLOW, DetailPromoRequestFragment.CORRECTION_FLOW);
        if (productPickerList.size() > 0)
            bundle.putParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST, (ArrayList<? extends Parcelable>) productPickerList);
        else
            bundle.putParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST, (ArrayList<? extends Parcelable>) productList);
        if (linear_other_payment.getVisibility() == View.VISIBLE) {
            bundle.putString(TNCRequestFragment.GET_SPECIFIC_FACILITIES, text_other_payment.getText().toString());
        }
        if (logoPickerList.size() > 0) {
            bundle.putParcelableArrayList(ProductFragment.GET_LOGO_REQUEST, (ArrayList<? extends Parcelable>) logoPickerList);
        } else {
            bundle.putParcelableArrayList(ProductFragment.GET_LOGO_REQUEST, (ArrayList<? extends Parcelable>) logoList);
        }
        if (facilitiesList.size() > 0) {
            bundle.putParcelableArrayList(TNCRequestFragment.GET_FACILITIES_LIST, (ArrayList<? extends Parcelable>) facilitiesList);
        }

        if (attachment_URI != null) {
            bundle.putString(LogoRequestFragment.GET_ATTACHMENT, attachment_URI.toString());
        }

        fragment.setArguments(bundle);

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPress(boolean check, Context context) {
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TabPromoRequest tabPromoRequest = new TabPromoRequest();
        Bundle bundle = new Bundle();
        bundle.putInt(TabPromoRequest.TAB_PAGE, 1);
        tabPromoRequest.setArguments(bundle);
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, tabPromoRequest);
        fragmentTransaction.commit();
    }
}
