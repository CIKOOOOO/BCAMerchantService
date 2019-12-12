package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.tnc;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.PromoRequest;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.PromoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.logo.LogoRequestFragment;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class TNCFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, TextWatcher, MainActivity.onBackPressFragment {
    public static final String GET_PROMO_DATA = "get_promo_data";
    public static final String GET_SPECIFIC_PAYMENT = "get_specific_payment";
    public static final String GET_FACILITIES_LIST = "get_facilities_list";

    private static Bundle init_bundle;

    private View v;
    private Context mContext;
    private RadioGroup radioGroup;
    private EditText edit_text_description;
    private TextView text_choose_file, text_doc_name;
    private Activity mActivity;
    private ImageButton img_btn_clear_doc;

    private Uri uri_selected_data;

    public TNCFragment() {
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
        v = inflater.inflate(R.layout.fragment_tnc, container, false);
        init_bundle = getArguments();
        if (init_bundle != null) {
            if (init_bundle.getParcelable(GET_PROMO_DATA) != null) {
                initVar();
            }
        }
        return v;
    }

    private void initVar() {
        uri_selected_data = null;
        mContext = v.getContext();

        Button btn_next = v.findViewById(R.id.btn_next_tnc);
        ImageButton img_btn_back = v.findViewById(R.id.img_btn_back_toolbar_back);

        radioGroup = v.findViewById(R.id.radio_group_tnc);
        edit_text_description = v.findViewById(R.id.edit_text_description_tnc);
        text_choose_file = v.findViewById(R.id.text_choose_file_tnc);
        text_doc_name = v.findViewById(R.id.text_document_name_tnc);
        img_btn_clear_doc = v.findViewById(R.id.img_btn_clear_doc_tnc);

        ((TextView) v.findViewById(R.id.text_title_toolbar_back)).setText("Pengajuan Promo");
        if (init_bundle.getString(LogoRequestFragment.GET_ATTACHMENT) != null) {
            ((RadioButton) v.findViewById(R.id.radio_button_attachment_tnc)).setChecked(true);
            Uri uri = Uri.parse(init_bundle.getString(LogoRequestFragment.GET_ATTACHMENT));
            text_doc_name.setText(Utils.getFileName(uri, mContext));
            img_btn_clear_doc.setVisibility(View.VISIBLE);
        } else {
            PromoRequest promoRequest = init_bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA);
            if (promoRequest != null && promoRequest.getPromo_tnc() != null) {
                ((TextView) v.findViewById(R.id.text_max_character_description_tnc)).setText(promoRequest.getPromo_tnc().length() + "/500");
                edit_text_description.setEnabled(true);
                edit_text_description.setText(promoRequest.getPromo_tnc());
                ((RadioButton) v.findViewById(R.id.radio_button_description_tnc)).setChecked(true);
            }
        }

        edit_text_description.addTextChangedListener(this);

        radioGroup.setOnCheckedChangeListener(this);
        text_choose_file.setOnClickListener(this);
        img_btn_clear_doc.setOnClickListener(this);
        img_btn_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        AppCompatActivity activity = (AppCompatActivity) mContext;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.img_btn_back_toolbar_back:
                PromoRequestFragment promoRequestFragment = new PromoRequestFragment();
                bundle.putParcelable(PromoRequestFragment.GET_PROMO_DATA, init_bundle.getParcelable(GET_PROMO_DATA));
                if (init_bundle.getString(GET_SPECIFIC_PAYMENT) != null) {
                    bundle.putString(PromoRequestFragment.GET_SPECIFIC_PAYMENT, init_bundle.getString(GET_SPECIFIC_PAYMENT));
                }
                if (init_bundle.getParcelableArrayList(GET_FACILITIES_LIST) != null) {
                    bundle.putParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST, init_bundle.getParcelableArrayList(GET_FACILITIES_LIST));
                }
                promoRequestFragment.setArguments(bundle);
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, promoRequestFragment);
                fragmentTransaction.commit();
                break;
            case R.id.text_choose_file_tnc:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.PERMISSION_READ_FILE_EXTERNAL);
                } else {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select a file"), Constant.ACTIVITY_CHOOSE_FILE);
                }
                break;
            case R.id.img_btn_clear_doc_tnc:
                img_btn_clear_doc.setVisibility(View.GONE);
                text_doc_name.setText("");
                if (uri_selected_data != null)
                    uri_selected_data = null;
                break;
            case R.id.btn_next_tnc:
                ((TextView) v.findViewById(R.id.show_error_tnc)).setVisibility(View.GONE);
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    ((TextView) v.findViewById(R.id.show_error_tnc)).setVisibility(View.VISIBLE);
                } else {
                    LogoRequestFragment logoRequestFragment = new LogoRequestFragment();
                    PromoRequest promoRequest = init_bundle.getParcelable(GET_PROMO_DATA);
                    if (init_bundle.getString(GET_SPECIFIC_PAYMENT) != null) {
                        bundle.putString(PromoRequestFragment.GET_SPECIFIC_PAYMENT, init_bundle.getString(GET_SPECIFIC_PAYMENT));
                    }
                    if (init_bundle.getParcelableArrayList(GET_FACILITIES_LIST) != null) {
                        bundle.putParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST, init_bundle.getParcelableArrayList(GET_FACILITIES_LIST));
                    }
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.radio_button_description_tnc:
                            if (edit_text_description.getText().toString().isEmpty()) {
                                edit_text_description.setError("Deskripsi tidak boleh kosong");
                                edit_text_description.requestFocus(edit_text_description.getLayoutDirection());
                            } else if (edit_text_description.getText().toString().length() > 500) {
                                edit_text_description.setError("Format deskripsi salah");
                                edit_text_description.requestFocus(edit_text_description.getLayoutDirection());
                            } else {
                                if (promoRequest != null) {
                                    promoRequest.setPromo_tnc(edit_text_description.getText().toString());
                                }
                                bundle.putParcelable(PromoRequestFragment.GET_PROMO_DATA, promoRequest);
                                logoRequestFragment.setArguments(bundle);
                                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                                fragmentTransaction.replace(R.id.main_frame, logoRequestFragment);
                                fragmentTransaction.commit();
                            }
                            break;
                        case R.id.radio_button_attachment_tnc:
                            /*
                             * Handle tnc attachment
                             * */
                            promoRequest.setPromo_tnc("");
                            bundle.putString(LogoRequestFragment.GET_ATTACHMENT, uri_selected_data.toString());
                            bundle.putParcelable(PromoRequestFragment.GET_PROMO_DATA, promoRequest);
                            logoRequestFragment.setArguments(bundle);
                            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            fragmentTransaction.replace(R.id.main_frame, logoRequestFragment);
                            fragmentTransaction.commit();
                            break;
                    }

                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_button_description_tnc:
                edit_text_description.setEnabled(true);
                text_choose_file.setEnabled(false);
                break;
            case R.id.radio_button_attachment_tnc:
                edit_text_description.setEnabled(false);
                text_choose_file.setEnabled(true);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.PERMISSION_READ_FILE_EXTERNAL:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent()
                            .setType("*/*")
                            .setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select a file"), Constant.ACTIVITY_CHOOSE_FILE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ACTIVITY_CHOOSE_FILE) {
            if (data != null && data.getData() != null) {
                if (getFileName(data.getData()).contains(".docx")) {
                    uri_selected_data = data.getData();
                    text_doc_name.setText(getFileName(uri_selected_data));
                    img_btn_clear_doc.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(mContext, "File ekstensi harus .docx!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        ((TextView) v.findViewById(R.id.text_max_character_description_tnc)).setText(charSequence.toString().length() + "/500");
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private String getFileName(Uri uri) throws IllegalArgumentException {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        if (cursor.getCount() <= 0) {
            cursor.close();
            throw new IllegalArgumentException("Can't obtain file name, cursor is empty");
        }
        cursor.moveToFirst();
        String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        cursor.close();
        return fileName;
    }

    @Override
    public void onBackPress(boolean check, Context context) {
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        PromoRequestFragment promoRequestFragment = new PromoRequestFragment();
        bundle.putParcelable(PromoRequestFragment.GET_PROMO_DATA, init_bundle.getParcelable(GET_PROMO_DATA));
        if (init_bundle.getString(GET_SPECIFIC_PAYMENT) != null) {
            bundle.putString(PromoRequestFragment.GET_SPECIFIC_PAYMENT, init_bundle.getString(GET_SPECIFIC_PAYMENT));
        }
        if (init_bundle.getParcelableArrayList(GET_FACILITIES_LIST) != null) {
            bundle.putParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST, init_bundle.getParcelableArrayList(GET_FACILITIES_LIST));
        }
        promoRequestFragment.setArguments(bundle);
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, promoRequestFragment);
        fragmentTransaction.commit();
    }
}
