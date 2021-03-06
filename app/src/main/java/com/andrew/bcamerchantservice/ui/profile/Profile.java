package com.andrew.bcamerchantservice.ui.profile;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.profile.myforum.MyForumFragment;
import com.andrew.bcamerchantservice.ui.profile.mystoreinformation.MyStoreInformation;
import com.andrew.bcamerchantservice.ui.profile.mystoreinformation.catalog.CatalogFragment;
import com.andrew.bcamerchantservice.ui.profile.profilesetting.SettingFragment;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.DecodeBitmap;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.andrew.bcamerchantservice.utils.TabAdapter;
import com.andrew.bcamerchantservice.utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment implements
        View.OnClickListener, IProfileView, MainActivity.onBackPressFragment {
    public static final String GET_CURRENT_ITEM_VIEW_PAGER = "get_current_item_view_pager";

    private static View view_description, v;

    private static final String TAG = Profile.class.getSimpleName();
    private static final int PERMISSION_READ_PROFILE = 1001;
    private static final int PERMISSION_READ_SHOW_CASE = 1002;
    private static final int PROFILE_REQUEST_CODE = 1003;
    private static final int HOME_REQUEST_CODE = 1004;

    private static FrameLayout frame_loading;

    private static IProfilePresenter presenter;

    private static PrefConfig prefConfig;

    private ImageView profilePic;
    private RoundedImageView homePic;
    private ScaleDrawable scaleDrawable;
    private Context mContext;
    private Activity mActivity;

    public Profile() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);
        scaleDrawable = DecodeBitmap.setScaleDrawable(mContext, R.drawable.placeholder);

        presenter = new ProfilePresenter(this);

        ImageButton profileAdd = v.findViewById(R.id.btn_change_picture_profile);
        Button homeAdd = v.findViewById(R.id.btn_change_home_profile);
        TextView text_name = v.findViewById(R.id.tv_merchant_name_profile);
        ViewPager viewPager = v.findViewById(R.id.view_pager_profile);
        TabLayout tabLayout = v.findViewById(R.id.tab_layout_profile);
        FrameLayout frame_catalog = v.findViewById(R.id.frame_description_catalog);
        LinearLayout linear_catalog = v.findViewById(R.id.linear_description_catalog);

        TabAdapter tabAdapter = new TabAdapter(getFragmentManager());

        view_description = v.findViewById(R.id.custom_description_catalog);

        profilePic = v.findViewById(R.id.image_profile_picture_profile);
        homePic = v.findViewById(R.id.image_background_profile);
        frame_loading = v.findViewById(R.id.frame_loading_profile);

        tabAdapter.addTab(new MyStoreInformation(), "Informasi Toko");
        tabAdapter.addTab(new MyForumFragment(), "My Forum");
        tabAdapter.addTab(new SettingFragment(), "Pengaturan");

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Bundle bundle = getArguments();
        if (bundle != null) {
            viewPager.setCurrentItem(bundle.getInt(GET_CURRENT_ITEM_VIEW_PAGER, 0));
        }

        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);

        Picasso.get()
                .load(prefConfig.getProfilePicture())
                .into(profilePic);

        if (!prefConfig.getBackgroundPicture().equals("0")) {
            Picasso.get()
                    .load(prefConfig.getBackgroundPicture())
                    .into(homePic);
        }

        text_name.setText(prefConfig.getName());

        view_description.setOnClickListener(this);
        frame_catalog.setOnClickListener(this);
        linear_catalog.setOnClickListener(this);
        profileAdd.setOnClickListener(this);
        homeAdd.setOnClickListener(this);
    }

    public static void showDescriptionCatalog(final Merchant.MerchantCatalog merchantCatalog) {
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
        Profile.view_description.setVisibility(View.VISIBLE);
        TextView text_title, text_price, text_description;
        ImageView image_catalog;
        ImageButton edit, delete;

        edit = v.findViewById(R.id.image_button_edit_catalog_custom);
        delete = v.findViewById(R.id.image_button_delete_catalog_custom);
        text_title = v.findViewById(R.id.text_title_catalog_custom);
        text_price = v.findViewById(R.id.text_price_catalog_custom);
        text_description = v.findViewById(R.id.text_description_catalog_custom);
        image_catalog = v.findViewById(R.id.image_catalog_custom);

        edit.setVisibility(View.VISIBLE);
        delete.setVisibility(View.VISIBLE);

        text_title.setText(merchantCatalog.getCatalog_name());
        text_price.setText("Price: Rp " + Utils.priceFormat(merchantCatalog.getCatalog_price()));
        text_description.setText(merchantCatalog.getCatalog_description());
        Picasso.get()
                .load(merchantCatalog.getCatalog_image())
                .into(image_catalog);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CatalogFragment catalogFragment = new CatalogFragment();
                FragmentActivity activity = (FragmentActivity) v.getContext();
                FragmentManager manager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                Bundle bundle = new Bundle();
                bundle.putParcelable(CatalogFragment.GET_DATA, merchantCatalog);
                catalogFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.main_frame, catalogFragment);
                fragmentTransaction.commit();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Apa Anda yakin untuk menghapus katalog " + merchantCatalog.getCatalog_name() + " ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int a) {
                                frame_loading.setVisibility(View.VISIBLE);
                                String[] split = merchantCatalog.getCatalog_image().split("alt");
                                String[] split2 = split[0].split("merchant_catalog");
                                String final_name = "merchant_catalog" + split2[2].substring(0, split2[2].length() - 1);
                                presenter.onDeleteCatalog(prefConfig.getMID(), merchantCatalog.getCid(), final_name);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_home_profile:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.PERMISSION_READ_GALLERY_EXTERNAL);
                } else {
                    Intent intent1 = new Intent();
                    intent1.setType("image/*");
                    intent1.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent1, "Select Home Pic"), HOME_REQUEST_CODE);
                }
                break;
            case R.id.btn_change_picture_profile:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            , PERMISSION_READ_PROFILE);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Profile Pic"), PROFILE_REQUEST_CODE);
                }
                break;
            case R.id.imgView_AddShowCase:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            , PERMISSION_READ_SHOW_CASE);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                }
                break;
            case R.id.frame_description_catalog:
                view_description.setVisibility(View.GONE);
                MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.PERMISSION_READ_GALLERY_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent1 = new Intent();
                    intent1.setType("image/*");
                    intent1.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent1, "Select Home Pic"), HOME_REQUEST_CODE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_READ_PROFILE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Profile Pic"), PROFILE_REQUEST_CODE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_READ_SHOW_CASE:
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
    public void onActivityResult(int requestCode, int resultCode, Intent data   ) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK && data.getData() != null) {
            switch (requestCode) {
                case PROFILE_REQUEST_CODE:
                    final Uri uris = data.getData();
                    frame_loading.setVisibility(View.VISIBLE);
                    sendImage(uris, profilePic, "merchant_profile_picture", "profile-picture-" + prefConfig.getMID());
                    break;
                case HOME_REQUEST_CODE:
                    Uri uri = data.getData();
                    frame_loading.setVisibility(View.VISIBLE);
                    sendImage(uri, homePic, "merchant_background_picture", "background-picture-" + prefConfig.getMID());
                    break;
            }
        }
    }

    private void sendImage(final Uri uris, final ImageView imageView, final String child, final String name) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DecodeBitmap.decodeSampleBitmapFromUri(uris, imageView.getWidth(), imageView.getHeight(), mContext).compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] byteData = baos.toByteArray();
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
        presenter.onUploadImage(name, prefConfig.getMID(), child, byteData);
    }

    @Override
    public void onSuccessUpload(String pictureType, String URL) {
        ImageView imageView = null;

        switch (pictureType) {
            case "merchant_profile_picture":
                prefConfig.insertProfilePic(URL);
                imageView = profilePic;
                break;
            case "merchant_background_picture":
                prefConfig.insertBackgroundPic(URL);
                imageView = homePic;
                break;
        }

        try {
            Picasso.get()
                    .load(URL)
                    .placeholder(scaleDrawable)
                    .into(imageView);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        frame_loading.setVisibility(View.GONE);
    }

    @Override
    public void onSuccessDeleteCatalog() {
        view_description.setVisibility(View.GONE);
        frame_loading.setVisibility(View.GONE);
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        Toast.makeText(mContext, "Katalog berhasil dihapus!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPress(boolean check, Context context) {
        if (MyStoreInformation.isDescriptionClick) {
            MyStoreInformation.isDescriptionClick = false;
            view_description.setVisibility(View.GONE);
            MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}
