package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.logo;


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
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.PromoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.confirmationpromo.ConfirmationPromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.product.ProductFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.tncrequest.TNCRequestFragment;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogoRequestFragment extends Fragment implements LogoRequestAdapter.onClick, View.OnClickListener, MainActivity.onBackPressFragment {

    public static final String GET_ATTACHMENT = "get_attachment";

    private static Bundle init_bundle;

    private View v;
    private Context mContext;
    private LogoRequestAdapter logoRequestAdapter;
    private Activity mActivity;
    private LinearLayout linear_add;
    private ImageButton img_back;

    private List<ImagePicker> imagePickerList;

    private String flow_status;

    public LogoRequestFragment() {
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
        v = inflater.inflate(R.layout.fragment_logo_request, container, false);
        init_bundle = getArguments();
        if (init_bundle != null) {
            if (init_bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA) != null) {
                initVar();
                if (init_bundle.getParcelableArrayList(ProductFragment.GET_LOGO_REQUEST) != null) {
                    imagePickerList = init_bundle.getParcelableArrayList(ProductFragment.GET_LOGO_REQUEST);
                    if (imagePickerList != null && imagePickerList.size() > 0) {
                        if (imagePickerList.size() < 3)
                            linear_add.setVisibility(View.VISIBLE);
                        logoRequestAdapter.setImagePickerList(imagePickerList);
                        logoRequestAdapter.notifyDataSetChanged();
                    }
                }
            }
            if (init_bundle.getString(ConfirmationPromoRequest.STATUS_FLOW) != null) {
                flow_status = init_bundle.getString(ConfirmationPromoRequest.STATUS_FLOW);
                if (flow_status != null) {
                    int visibility_img_back = flow_status.equals(ConfirmationPromoRequest.NORMAL_EDIT_FLOW) ? View.GONE : View.VISIBLE;
                    img_back.setVisibility(visibility_img_back);
                }
            }
        }
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        flow_status = "";

        ((TextView) v.findViewById(R.id.text_title_toolbar_back)).setText("Pengajuan Promo");

        RecyclerView recycler_logo_request = v.findViewById(R.id.recycler_logo_request);
        img_back = v.findViewById(R.id.img_btn_back_toolbar_back);
        Button btn_next = v.findViewById(R.id.btn_next_logo_request);

        linear_add = v.findViewById(R.id.linear_add_logo_request);

        logoRequestAdapter = new LogoRequestAdapter(mContext, this);

        imagePickerList = new ArrayList<>();

        recycler_logo_request.setLayoutManager(new LinearLayoutManager(mContext));

        recycler_logo_request.setAdapter(logoRequestAdapter);

        linear_add.setOnClickListener(this);
        img_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
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
            case R.id.linear_add_logo_request:
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
                TNCRequestFragment tncRequestFragment = new TNCRequestFragment();
                bundle.putParcelable(PromoRequestFragment.GET_PROMO_DATA, init_bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA));
                if (init_bundle.getString(GET_ATTACHMENT) != null) {
                    bundle.putString(GET_ATTACHMENT, init_bundle.getString(GET_ATTACHMENT));
                }
                if (init_bundle.getString(PromoRequestFragment.GET_SPECIFIC_PAYMENT) != null) {
                    bundle.putString(PromoRequestFragment.GET_SPECIFIC_PAYMENT, init_bundle.getString(PromoRequestFragment.GET_SPECIFIC_PAYMENT));
                }
                if (init_bundle.getParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST) != null) {
                    bundle.putParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST, init_bundle.getParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST));
                }
                tncRequestFragment.setArguments(bundle);
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, tncRequestFragment);
                fragmentTransaction.commit();
                break;
            case R.id.btn_next_logo_request:
                if (imagePickerList.size() == 0) break;

                if (flow_status.isEmpty()) {
                    ProductFragment productFragment = new ProductFragment();

                    bundle.putParcelable(PromoRequestFragment.GET_PROMO_DATA, init_bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA));
                    if (init_bundle.getString(GET_ATTACHMENT) != null) {
                        bundle.putString(GET_ATTACHMENT, init_bundle.getString(GET_ATTACHMENT));
                    }
                    if (init_bundle.getString(PromoRequestFragment.GET_SPECIFIC_PAYMENT) != null) {
                        bundle.putString(PromoRequestFragment.GET_SPECIFIC_PAYMENT, init_bundle.getString(PromoRequestFragment.GET_SPECIFIC_PAYMENT));
                    }
                    if (init_bundle.getParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST) != null) {
                        bundle.putParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST, init_bundle.getParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST));
                    }

                    bundle.putParcelableArrayList(ProductFragment.GET_LOGO_REQUEST, (ArrayList<? extends Parcelable>) imagePickerList);

                    productFragment.setArguments(bundle);

                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.replace(R.id.main_frame, productFragment);
                    fragmentTransaction.commit();
                } else {
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

                    bundle.putParcelableArrayList(ProductFragment.GET_LOGO_REQUEST, (ArrayList<? extends Parcelable>) imagePickerList);
                    bundle.putParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST, init_bundle.getParcelableArrayList(ConfirmationPromoRequest.PRODUCT_REQUEST));

                    confirmationPromoRequest.setArguments(bundle);

                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.replace(R.id.main_frame, confirmationPromoRequest);
                    fragmentTransaction.commit();
                }
                break;
        }
    }

    @Override
    public void onBackPress(boolean check, Context context) {
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        TNCRequestFragment tncRequestFragment = new TNCRequestFragment();
        bundle.putParcelable(PromoRequestFragment.GET_PROMO_DATA, init_bundle.getParcelable(PromoRequestFragment.GET_PROMO_DATA));
        if (init_bundle.getString(GET_ATTACHMENT) != null) {
            bundle.putString(GET_ATTACHMENT, init_bundle.getString(GET_ATTACHMENT));
        }
        if (init_bundle.getString(PromoRequestFragment.GET_SPECIFIC_PAYMENT) != null) {
            bundle.putString(PromoRequestFragment.GET_SPECIFIC_PAYMENT, init_bundle.getString(PromoRequestFragment.GET_SPECIFIC_PAYMENT));
        }
        if (init_bundle.getParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST) != null) {
            bundle.putParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST, init_bundle.getParcelableArrayList(PromoRequestFragment.GET_FACILITIES_LIST));
        }
        tncRequestFragment.setArguments(bundle);
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, tncRequestFragment);
        fragmentTransaction.commit();
    }
}
