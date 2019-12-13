package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.PromoRequest;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabPromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.confirmationpromo.ConfirmationPromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.logo.LogoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.product.ProductFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.tncrequest.TNCRequestFragment;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromoRequestFragment extends Fragment implements IPromoRequestView, PromoTypeAdapter.onClick, View.OnClickListener, PaymentTypeAdapter.onClick, RadioGroup.OnCheckedChangeListener, MainActivity.onBackPressFragment {

    public static final String GET_PROMO_DATA = "get_promo_data";
    public static final String GET_SPECIFIC_PAYMENT = "get_specific_payment";
    public static final String GET_FACILITIES_LIST = "get_facilities_list";

    private Context mContext;
    private View v;
    private PromoTypeAdapter promoTypeAdapter;
    private PromoRequest.PromoType promoType;
    private PaymentTypeAdapter paymentTypeAdapter;
    private EditText edit_text_payment, edit_text_address, edit_text_start_date, edit_text_end_date;
    private CheckBox check_payment;
    private RadioGroup radio_group_location;
    private PromoRequest promoRequest;
    private Bundle init_bundle;
    private ImageButton image_back;

    private IPromoRequestPresenter presenter;

    private List<PromoRequest.Facilities> facilitiesList;

    private String start_date, end_date, min_end_date, min_start_date, specific_payment, flow_status;

    public PromoRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_promo_request, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        start_date = "";
        end_date = "";
        min_end_date = "";
        min_start_date = "";
        flow_status = "";
        promoRequest = null;
        mContext = v.getContext();
        PrefConfig prefConfig = new PrefConfig(mContext);

        MainActivity.bottomNavigationView.setVisibility(View.GONE);

        ((TextView) v.findViewById(R.id.text_title_toolbar_back)).setText("Pengajuan Promo");

        RecyclerView recycler_information = v.findViewById(R.id.recycler_information_promo_request);
        RecyclerView recycler_promo_type = v.findViewById(R.id.recycler_type_promo_request);
        RecyclerView recycler_payment_type = v.findViewById(R.id.recycler_payment_type_promo_request);
        Button btn_next = v.findViewById(R.id.btn_next_promo_request);
        LinearLayout linear_start_date = v.findViewById(R.id.linear_start_date_promo_request);
        LinearLayout linear_end_date = v.findViewById(R.id.linear_end_date_promo_request);
        Button lazy_btn = v.findViewById(R.id.btn_tester_promo_request);

        image_back = v.findViewById(R.id.img_btn_back_toolbar_back);
        edit_text_payment = v.findViewById(R.id.edit_text_others_payment_promo_request);
        edit_text_address = v.findViewById(R.id.edit_text_address_promo_request);
        check_payment = v.findViewById(R.id.check_box_others_promo_request);
        radio_group_location = v.findViewById(R.id.radio_group_location_promo_request);
        edit_text_start_date = v.findViewById(R.id.edit_text_start_date_promo_request);
        edit_text_end_date = v.findViewById(R.id.edit_text_end_date_promo_request);

        InformationTextAdapter informationTextAdapter = new InformationTextAdapter(mContext);

        informationTextAdapter.setInformation_list(Constant.INFORMATION_PROMO_REQUEST.split("##"));

        presenter = new PromoRequestPresenter(this);
        promoTypeAdapter = new PromoTypeAdapter(mContext, this);
        paymentTypeAdapter = new PaymentTypeAdapter(mContext, this);
        facilitiesList = new ArrayList<>();

        init_bundle = getArguments();
        if (init_bundle != null) {
            if (init_bundle.getParcelable(GET_PROMO_DATA) != null) {
                promoRequest = init_bundle.getParcelable(GET_PROMO_DATA);
                if (promoRequest != null) {
                    ((EditText) v.findViewById(R.id.edit_title_promo_request)).setText(promoRequest.getPromo_title());
                    edit_text_start_date.setText(promoRequest.getPromo_start_date());
                    edit_text_end_date.setText(promoRequest.getPromo_end_date());
                    start_date = promoRequest.getPromo_start_date();
                    end_date = promoRequest.getPromo_end_date();

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MONTH, 1);
                    SimpleDateFormat sdfs = new SimpleDateFormat("dd/MM/yyyy");
                    min_start_date = sdfs.format(calendar.getTime());
                    calendar.add(Calendar.MONTH, 6);
                    min_end_date = sdfs.format(calendar.getTime());

                    RadioButton radio_all = v.findViewById(R.id.radio_button_all_promo_request);
                    RadioButton radio_specific = v.findViewById(R.id.radio_button_specific_promo_request);

                    if (promoRequest.getPromo_location().equals(radio_all.getText())) {
                        radio_all.setChecked(true);
                    } else {
                        radio_specific.setChecked(true);
                        edit_text_address.setText(promoRequest.getPromo_location());
                        edit_text_address.setEnabled(true);
                    }
                }
            }
            if (init_bundle.getString(GET_SPECIFIC_PAYMENT) != null) {
                specific_payment = init_bundle.getString(GET_SPECIFIC_PAYMENT);
                if (specific_payment != null) {
                    if (!specific_payment.isEmpty()) {
                        check_payment.setChecked(true);
                        edit_text_payment.setText(specific_payment);
                        edit_text_payment.setEnabled(true);
                    }
                }
            }
            if (init_bundle.getString(ConfirmationPromoRequest.STATUS_FLOW) != null) {
                flow_status = init_bundle.getString(ConfirmationPromoRequest.STATUS_FLOW);
                if (flow_status != null) {
                    int img_visible = flow_status.equals(ConfirmationPromoRequest.NORMAL_EDIT_FLOW) ? View.GONE : View.VISIBLE;
                    image_back.setVisibility(img_visible);
                }
            }
        }

        presenter.loadPromoType();
        presenter.loadPaymentType(prefConfig.getMID());

        recycler_information.setLayoutManager(new LinearLayoutManager(mContext));
        recycler_promo_type.setLayoutManager(new GridLayoutManager(mContext, 3));
        recycler_payment_type.setLayoutManager(new GridLayoutManager(mContext, 2));

        recycler_information.setAdapter(informationTextAdapter);
        recycler_promo_type.setAdapter(promoTypeAdapter);
        recycler_payment_type.setAdapter(paymentTypeAdapter);

        linear_start_date.setOnClickListener(this);
        linear_end_date.setOnClickListener(this);
        radio_group_location.setOnCheckedChangeListener(this);
        check_payment.setOnClickListener(this);
        image_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        lazy_btn.setOnClickListener(this);
    }

    private void changeFragment(Context context, Fragment fragment) {
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onLoadPromoType(List<PromoRequest.PromoType> promoTypes) {
        if (promoRequest != null) {
            for (int i = 0; i < promoTypes.size(); i++) {
                if (promoRequest.getPromo_type_id().equals(promoTypes.get(i).getPromo_type_id())) {
                    this.promoType = promoTypes.get(i);
                    promoTypeAdapter.setChosenPosition(i);
                    break;
                }
            }
        }
        promoTypeAdapter.setPromoTypeList(promoTypes);
        promoTypeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadPaymentType(PromoRequest.Facilities facilities) {
        if (init_bundle != null) {
            if (init_bundle.getParcelableArrayList(GET_FACILITIES_LIST) != null) {
                List<PromoRequest.Facilities> facilitiesList = new ArrayList<>(init_bundle.<PromoRequest.Facilities>getParcelableArrayList(GET_FACILITIES_LIST));
                if (facilitiesList.size() > 0) {
                    for (PromoRequest.Facilities facilities_loop : facilitiesList) {
                        if (facilities_loop.getFacilities_id().equals(facilities.getFacilities_id())) {
                            facilities.setCheck(true);
                            break;
                        }
                    }
                } else
                    facilities.setCheck(false);
            }
        }
        paymentTypeAdapter.addFacilities(facilities);
        facilitiesList.add(facilities);
    }

    @Override
    public void onPromoClick(PromoRequest.PromoType promoType) {
        this.promoType = promoType;
    }

    @Override
    public void onClick(View view) {
        EditText edit_title = v.findViewById(R.id.edit_title_promo_request);
        switch (view.getId()) {
            case R.id.img_btn_back_toolbar_back:
                changeFragment(mContext, new TabPromoRequest());
                break;
            case R.id.check_box_others_promo_request:
                if (check_payment.isChecked()) {
                    edit_text_payment.setEnabled(true);
                } else edit_text_payment.setEnabled(false);
                break;
            case R.id.linear_start_date_promo_request:
                Calendar calendar_min_start_date = Calendar.getInstance();
                calendar_min_start_date.add(Calendar.MONTH, 1);
                SimpleDateFormat sdfs = new SimpleDateFormat("dd/MM/yyyy");
                min_start_date = sdfs.format(calendar_min_start_date.getTime());
                Calendar calender_start_date = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                String s = start_date.isEmpty() ? min_start_date : start_date;
                try {
                    calender_start_date.setTime(sdfs.parse(s));
                    int mYear2 = calender_start_date.get(Calendar.YEAR);
                    int mMonth2 = calender_start_date.get(Calendar.MONTH);
                    int mDay2 = calender_start_date.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog date_start = new DatePickerDialog(mContext, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                            String day = selectedday + "";
                            String month = (selectedmonth + 1) + "";
                            if (selectedday < 10) {
                                day = "0" + selectedday;
                            }
                            if ((selectedmonth + 1) < 10) {
                                month = "0" + (selectedmonth + 1);
                            }
                            start_date = day + "/" + month + "/" + selectedyear;
                            edit_text_start_date.setText(start_date);
                            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            /*
                             * Min End Date untuk memberi tahu bahwa jarak berjalannya promo
                             * harus 1 bulan dengan pengajuan
                             * */
                            if (min_end_date.isEmpty())
                                try {
                                    cal.setTime(sdf.parse(start_date));
                                    cal.add(Calendar.MONTH, 6);
                                    end_date = min_end_date = sdf.format(cal.getTime());
                                    edit_text_end_date.setText(end_date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            else {
                                Calendar startCalendar = new GregorianCalendar();
                                Calendar endCalendar = new GregorianCalendar();
                                try {
                                    startCalendar.setTime(sdf.parse(start_date));
                                    endCalendar.setTime(sdf.parse(end_date));

                                    /*
                                     * Untuk membandingkan, apakah start date dengan end date memiliki
                                     * jarak 6 bulan atau tidak
                                     * */

                                    int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                                    int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
                                    cal.setTime(sdf.parse(start_date));
                                    cal.add(Calendar.MONTH, 6);
                                    if (diffMonth < 6) {
                                        end_date = min_end_date = sdf.format(cal.getTime());
                                        edit_text_end_date.setText(end_date);
                                    } else
                                        min_end_date = sdf.format(cal.getTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, mYear2, mMonth2, mDay2);
                    date_start.getDatePicker().setMinDate(calendar_min_start_date.getTimeInMillis());
                    date_start.show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.linear_end_date_promo_request:
                if (!end_date.isEmpty()) {
                    Calendar calender_end = Calendar.getInstance();
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        calender_end.setTime(sdf.parse(end_date));
                        int year_end = calender_end.get(Calendar.YEAR);
                        int month_end = calender_end.get(Calendar.MONTH);
                        int day_end = calender_end.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog date_end = new DatePickerDialog(mContext, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                String day = selectedday + "";
                                String month = (selectedmonth + 1) + "";
                                if (selectedday < 10) {
                                    day = "0" + selectedday;
                                }
                                if ((selectedmonth + 1) < 10) {
                                    month = "0" + (selectedmonth + 1);
                                }
                                end_date = day + "/" + month + "/" + selectedyear;
                                edit_text_end_date.setText(end_date);
                            }
                        }, year_end, month_end, day_end);

                        Calendar calendar_min_date = Calendar.getInstance();
                        calendar_min_date.setTime(sdf.parse(min_end_date));

                        date_end.getDatePicker().setMinDate(calendar_min_date.getTimeInMillis());
                        date_end.show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_tester_promo_request:
                edit_title.setText("Free Buy 1 Get 1 with BCA JCB Card");
                start_date = "15/01/2019";
                end_date = "17/07/2020";
                edit_text_start_date.setText("15/01/2019");
                edit_text_end_date.setText("17/07/2020");
                promoType = new PromoRequest.PromoType();
                promoType.setPromo_type_id("promo_type_1");
                promoTypeAdapter.setChosenPosition(0);
                promoTypeAdapter.notifyDataSetChanged();
                check_payment.setChecked(true);
                edit_text_payment.setText("Khusus pengguna BCA Card JCB Black");
                ((RadioButton) v.findViewById(R.id.radio_button_all_promo_request)).setChecked(true);
                break;
            case R.id.btn_next_promo_request:
                String title = edit_title.getText().toString();
                ((TextView) v.findViewById(R.id.show_error_title_promo_request)).setVisibility(View.GONE);
                ((TextView) v.findViewById(R.id.show_error_checkbox_promo_request)).setVisibility(View.GONE);
                ((TextView) v.findViewById(R.id.show_error_location_promo_request)).setVisibility(View.GONE);

                boolean facilitiesIsCheck = false;
                boolean isRadioChecked = radio_group_location.getCheckedRadioButtonId() != -1;

                for (PromoRequest.Facilities facilities : facilitiesList) {
                    if (facilities.isCheck()) {
                        facilitiesIsCheck = true;
                        break;
                    }
                }

                if (title.isEmpty() || title.trim().length() < 10) {
                    edit_title.setError("Format Judul tidak valid");
                    edit_title.requestFocus(edit_title.getLayoutDirection());
                } else if (promoTypeAdapter.getChosenPosition() == -1) {
                    ((TextView) v.findViewById(R.id.show_error_title_promo_request)).setVisibility(View.VISIBLE);
                } else if (!facilitiesIsCheck && !check_payment.isChecked()) {
                    ((TextView) v.findViewById(R.id.show_error_checkbox_promo_request)).setVisibility(View.VISIBLE);
                } else if (!isRadioChecked) {
                    ((TextView) v.findViewById(R.id.show_error_location_promo_request)).setVisibility(View.VISIBLE);
                } else {
                    if (check_payment.isChecked()) {
                        if (edit_text_payment.getText().toString().trim().length() < 5) {
                            edit_text_payment.setError("Format tidak valid");
                            break;
                        }
                    }

                    if (radio_group_location.getCheckedRadioButtonId() == R.id.radio_button_specific_promo_request) {
                        if (edit_text_address.getText().toString().trim().length() < 10) {
                            edit_text_address.setError("Format tidak valid");
                            break;
                        }
                    }

                    String location = radio_group_location.getCheckedRadioButtonId() == R.id.radio_button_all_promo_request
                            ? "Berlaku diseluruh outlet"
                            : edit_text_address.getText().toString();

                    List<PromoRequest.Facilities> tempFacilitiesList = new ArrayList<>();

                    for (PromoRequest.Facilities facilities : facilitiesList) {
                        if (facilities.isCheck())
                            tempFacilitiesList.add(facilities);
                    }

                    if (promoRequest == null)
                        promoRequest = new PromoRequest();

                    promoRequest.setPromo_title(title);
                    promoRequest.setPromo_start_date(start_date);
                    promoRequest.setPromo_end_date(end_date);
                    promoRequest.setPromo_type_id(promoType.getPromo_type_id());
                    promoRequest.setPromo_location(location);

                    Bundle bundle = new Bundle();

                    bundle.putParcelable(TNCRequestFragment.GET_PROMO_DATA, promoRequest);
                    if (check_payment.isChecked())
                        bundle.putString(TNCRequestFragment.GET_SPECIFIC_PAYMENT, edit_text_payment.getText().toString());
                    else
                        bundle.putString(TNCRequestFragment.GET_SPECIFIC_PAYMENT, "");
                    if (tempFacilitiesList.size() > 0)
                        bundle.putParcelableArrayList(TNCRequestFragment.GET_FACILITIES_LIST, (ArrayList<? extends Parcelable>) tempFacilitiesList);

                    AppCompatActivity activity = (AppCompatActivity) mContext;
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    if (flow_status.isEmpty()) {
                        TNCRequestFragment tncRequestFragment = new TNCRequestFragment();
                        tncRequestFragment.setArguments(bundle);

                        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        fragmentTransaction.replace(R.id.main_frame, tncRequestFragment);
                        fragmentTransaction.commit();
                    } else {
                        ConfirmationPromoRequest confirmationPromoRequest = new ConfirmationPromoRequest();

                        bundle.putString(LogoRequestFragment.GET_ATTACHMENT, init_bundle.getString(LogoRequestFragment.GET_ATTACHMENT));
                        bundle.putParcelableArrayList(ProductFragment.GET_LOGO_REQUEST, init_bundle.getParcelableArrayList(ProductFragment.GET_LOGO_REQUEST));
                        bundle.putParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST, init_bundle.getParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST));

                        confirmationPromoRequest.setArguments(bundle);

                        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        fragmentTransaction.replace(R.id.main_frame, confirmationPromoRequest);
                        fragmentTransaction.commit();
                    }


                }
                break;
        }
    }

    @Override
    public void checkboxIsChecked(int pos, boolean check) {
        PromoRequest.Facilities facilities = facilitiesList.get(pos);
        facilities.setCheck(check);
        facilitiesList.set(pos, facilities);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_button_all_promo_request:
                edit_text_address.setEnabled(false);
                break;
            case R.id.radio_button_specific_promo_request:
                edit_text_address.setEnabled(true);
                break;
        }
    }

    @Override
    public void onBackPress(boolean check, Context context) {

    }
}
