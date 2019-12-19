package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.product;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.tabpromorequest.detailpromorequest.DetailPromoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.PromoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.confirmationpromo.ConfirmationPromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.logo.LogoRequestAdapter;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.logo.LogoRequestFragment;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductFragment extends Fragment implements LogoRequestAdapter.onClick, View.OnClickListener, MainActivity.onBackPressFragment {

    public static final String GET_LOGO_REQUEST = "get_logo_request";

    private static Bundle init_bundle;

    private View v;
    private Context mContext;
    private LogoRequestAdapter logoRequestAdapter;
    private Activity mActivity;
    private LinearLayout linear_add;

    private List<ImagePicker> imagePickerList;
    private String flow_status;

    public ProductFragment() {
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
        v = inflater.inflate(R.layout.fragment_product, container, false);
        init_bundle = getArguments();
        if (init_bundle != null) {
            if (init_bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA) != null
                    && init_bundle.getParcelableArrayList(GET_LOGO_REQUEST) != null) {
                initVar();
            }
        }
        return v;
    }

    private void initVar() {
        mContext = v.getContext();

        ((TextView) v.findViewById(R.id.text_title_toolbar_back)).setText("Pengajuan Promo");

        RecyclerView recycler_product = v.findViewById(R.id.recycler_product_request);
        ImageButton img_btn_back = v.findViewById(R.id.img_btn_back_toolbar_back);
        Button btn_next = v.findViewById(R.id.btn_next_product_request);

        linear_add = v.findViewById(R.id.linear_add_product_request);

        imagePickerList = new ArrayList<>();
        logoRequestAdapter = new LogoRequestAdapter(mContext, this);

        recycler_product.setLayoutManager(new LinearLayoutManager(mContext));

        recycler_product.setAdapter(logoRequestAdapter);

        if (init_bundle.getString(ConfirmationPromoRequest.STATUS_FLOW) != null) {
            flow_status = init_bundle.getString(ConfirmationPromoRequest.STATUS_FLOW);
            if (flow_status != null) {
                if (flow_status.equals(DetailPromoRequestFragment.CORRECTION_FLOW)) {

                } else if (flow_status.equals(ConfirmationPromoRequest.NORMAL_EDIT_FLOW)) {
                    if (init_bundle.getParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST) != null) {
                        imagePickerList = init_bundle.getParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST);
                        logoRequestAdapter.setImagePickerList(imagePickerList);
                        logoRequestAdapter.notifyDataSetChanged();
                        if (imagePickerList.size() != 0 && imagePickerList.size() != 3)
                            linear_add.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        linear_add.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        img_btn_back.setOnClickListener(this);
    }

    @Override
    public void onImageClick() {
        if (ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constant.PERMISSION_READ_GALLERY_EXTERNAL);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
        }
    }

    @Override
    public void onImageDelete(int pos) {
        if (imagePickerList.size() > 0) {
            imagePickerList.remove(pos);
            logoRequestAdapter.setImagePickerList(imagePickerList);
            logoRequestAdapter.notifyDataSetChanged();

            int visible_linear = imagePickerList.size() == 0 ? View.GONE : View.VISIBLE;
            linear_add.setVisibility(visible_linear);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.ACTIVITY_CHOOSE_IMAGE) {
                if (data != null) {
                    if (data.getData() != null) {
                        Uri targetUri = data.getData();
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(targetUri));
                            imagePickerList.add(new ImagePicker(bitmap, "", Utils.getFileName(targetUri, mContext)));
                            logoRequestAdapter.setImagePickerList(imagePickerList);
                            logoRequestAdapter.notifyDataSetChanged();
                            int plus_condition = imagePickerList.size() == 3 ? View.GONE : View.VISIBLE;
                            linear_add.setVisibility(plus_condition);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.PERMISSION_READ_GALLERY_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        AppCompatActivity activity = (AppCompatActivity) mContext;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.linear_add_product_request:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.PERMISSION_READ_GALLERY_EXTERNAL);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                }
                break;
            case R.id.img_btn_back_toolbar_back:
                LogoRequestFragment logoRequestFragment = new LogoRequestFragment();

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

                bundle.putParcelableArrayList(ProductFragment.GET_LOGO_REQUEST, init_bundle.getParcelableArrayList(GET_LOGO_REQUEST));

                logoRequestFragment.setArguments(bundle);

                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, logoRequestFragment);
                fragmentTransaction.commit();
                break;
            case R.id.btn_next_product_request:
                if (imagePickerList.size() == 0) break;
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

                bundle.putParcelableArrayList(ProductFragment.GET_LOGO_REQUEST, init_bundle.getParcelableArrayList(GET_LOGO_REQUEST));
                bundle.putParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST, (ArrayList<? extends Parcelable>) imagePickerList);

                confirmationPromoRequest.setArguments(bundle);

                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, confirmationPromoRequest);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onBackPress(boolean check, Context context) {
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        LogoRequestFragment logoRequestFragment = new LogoRequestFragment();

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

        bundle.putParcelableArrayList(ProductFragment.GET_LOGO_REQUEST, init_bundle.getParcelableArrayList(GET_LOGO_REQUEST));

        logoRequestFragment.setArguments(bundle);

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, logoRequestFragment);
        fragmentTransaction.commit();
    }
}
