package com.andrew.bcamerchantservice.ui.profile;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.Merchant.MerchantStory;
import com.andrew.bcamerchantservice.model.ProfileModel;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.mainforum.MainForum;
import com.andrew.bcamerchantservice.ui.mainforum.StoryAdapter;
import com.andrew.bcamerchantservice.ui.profile.myforum.MyForumFragment;
import com.andrew.bcamerchantservice.ui.profile.mystoreinformation.MyStoreInformation;
import com.andrew.bcamerchantservice.ui.profile.profilesetting.SettingFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabAdapter;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabPromoRequest;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.DecodeBitmap;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment implements
        View.OnClickListener, IProfileView {
    private static final String TAG = Profile.class.getSimpleName();
    private static final int PERMISSION_READ_PROFILE = 1001;
    private static final int PERMISSION_READ_SHOW_CASE = 1002;
    private static final int PROFILE_REQUEST_CODE = 1003;
    private static final int HOME_REQUEST_CODE = 1004;

    private View v;
    private ImageView profilePic;
    private RoundedImageView homePic;
    private ScaleDrawable scaleDrawable;
    private Context mContext;
    private Activity mActivity;
    private FrameLayout frame_loading;

    private IProfilePresenter presenter;

    private PrefConfig prefConfig;

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

        TabAdapter tabAdapter = new TabAdapter(getFragmentManager());

        profilePic = v.findViewById(R.id.image_profile_picture_profile);
        homePic = v.findViewById(R.id.image_background_profile);
        frame_loading = v.findViewById(R.id.frame_loading_profile);

        tabAdapter.addTab(new MyStoreInformation(), "Informasi Toko");
        tabAdapter.addTab(new MyForumFragment(), "My Forum");
        tabAdapter.addTab(new SettingFragment(), "Pengaturan");

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Picasso.get()
                .load(prefConfig.getProfilePicture())
                .into(profilePic);

        if (!prefConfig.getBackgroundPicture().equals("0")) {
            Picasso.get()
                    .load(prefConfig.getBackgroundPicture())
                    .into(homePic);
        }

        text_name.setText(prefConfig.getName());

        profileAdd.setOnClickListener(this);
        homeAdd.setOnClickListener(this);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
}
