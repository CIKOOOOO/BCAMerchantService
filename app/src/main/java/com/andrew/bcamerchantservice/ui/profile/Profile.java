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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
public class Profile extends Fragment implements StoryAdapter.onImageClickListener
        , View.OnClickListener, ProfileAdapter.onClick, IProfileView {
    public static boolean showcase_condition;
    @SuppressLint("StaticFieldLeak")
    public static FrameLayout frame_add_showcase;

    private static final String TAG = Profile.class.getSimpleName();
    private static final int PERMISSION_READ_PROFILE = 1001;
    private static final int PERMISSION_READ_SHOW_CASE = 1002;
    private static final int PROFILE_REQUEST_CODE = 1003;
    private static final int HOME_REQUEST_CODE = 1004;

    private View v;
    private ImageView profilePic;
    private RoundedImageView homePic;
    private ImageView img_add_showcase, img_showcase;
    private AlertDialog codeAlert;
    private TextView tvError_AddShowCase;
    private EditText etInput_ShowCase;
    private ScaleDrawable scaleDrawable;
    private Context mContext;
    private Activity mActivity;
    private FrameLayout frame_loading;

    private IProfilePresenter presenter;

    private List<MerchantStory> showCaseList;
    private StoryAdapter storyAdapter;
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
        List<ProfileModel> profileModelList = new ArrayList<>(Constant.getProfileModels());

        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);
        scaleDrawable = DecodeBitmap.setScaleDrawable(mContext, R.drawable.placeholder);
        showcase_condition = false;

        presenter = new ProfilePresenter(this);

        ImageButton profileAdd = v.findViewById(R.id.btn_change_picture_profile);
        ImageButton homeAdd = v.findViewById(R.id.btn_change_home_profile);
        ImageButton file_download = v.findViewById(R.id.download_image_frame_profile);
        ImageButton btnClose = v.findViewById(R.id.btn_close_frame_profile);
        RecyclerView recyclerView = v.findViewById(R.id.recycler_profile);
        RecyclerView recycler_showcase = v.findViewById(R.id.recycler_profile_image);
        RelativeLayout relativeLayout = v.findViewById(R.id.rl_frame_main_profile);
        TextView text_name = v.findViewById(R.id.tv_merchant_name_profile);
        TextView text_position = v.findViewById(R.id.text_position_profile);
        TextView text_email = v.findViewById(R.id.tv_email_profile);
        TextView text_mid = v.findViewById(R.id.text_mid_profile);

        profilePic = v.findViewById(R.id.image_profile_picture_profile);
        homePic = v.findViewById(R.id.image_background_profile);
        frame_add_showcase = v.findViewById(R.id.picture_frame_profile);
        img_showcase = v.findViewById(R.id.image_frame_profile);
        frame_loading = v.findViewById(R.id.frame_loading_profile);

        showCaseList = new ArrayList<>();
        storyAdapter = new StoryAdapter(mContext, showCaseList, true, new HashMap<String, Merchant>(), this);

        Picasso.get()
                .load(prefConfig.getProfilePicture())
                .into(profilePic);

        if (!prefConfig.getBackgroundPicture().equals("0")) {
            Picasso.get()
                    .load(prefConfig.getBackgroundPicture())
                    .into(homePic);
        }

        text_name.setText(prefConfig.getName());
        text_position.setText(prefConfig.getPosition());
        text_email.setText(prefConfig.getEmail());
        text_mid.setText(prefConfig.getMID() + "");

        frame_loading.getBackground().setAlpha(Constant.MAX_ALPHA);

        recycler_showcase.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recycler_showcase.setAdapter(storyAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(new ProfileAdapter(mContext, profileModelList, this));

        profileAdd.setOnClickListener(this);
        homeAdd.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        file_download.setOnClickListener(this);

        img_showcase.setOnClickListener(this);
        frame_loading.setOnClickListener(this);

        presenter.loadStory(prefConfig.getMID());
    }

    @Override
    public void onImageClick(Context context, int pos) {
        if (pos == 0) {
            showAddShowCase();
        } else {
            MainActivity.bottomNavigationView.setVisibility(View.GONE);
            showcase_condition = true;
            frame_add_showcase.setVisibility(View.VISIBLE);
            frame_add_showcase.getBackground().setAlpha(Constant.MAX_ALPHA);
            Picasso.get()
                    .load(showCaseList.get(pos - 1).getStory_picture())
                    .into(img_showcase);
        }
    }

    private void showAddShowCase() {
        AlertDialog.Builder codeBuilder = new AlertDialog.Builder(mContext);
        @SuppressLint("InflateParams") View codeView = getLayoutInflater().inflate(R.layout.custom_add_show_case, null);
        img_add_showcase = codeView.findViewById(R.id.imgView_AddShowCase);
        etInput_ShowCase = codeView.findViewById(R.id.etInputName_AddShowCae);
        tvError_AddShowCase = codeView.findViewById(R.id.show_error_content_add_show_case);
        Button submit = codeView.findViewById(R.id.btnSubmit_AddShowCase);
        Button cancel = codeView.findViewById(R.id.btnCancel_AddShowCase);
        img_add_showcase.setOnClickListener(this);
        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);
        codeBuilder.setView(codeView);
        codeAlert = codeBuilder.create();
        codeAlert.show();
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
            case R.id.btnSubmit_AddShowCase:
                String checkName = etInput_ShowCase.getText().toString();
                tvError_AddShowCase.setVisibility(View.GONE);
//                if (checkName.isEmpty()) {
//                    etInput_ShowCase.setError(mContext.getResources().getString(R.string.dont_empty_this_edittext));
//                } else
                if (img_add_showcase.getDrawable() == null) {
                    tvError_AddShowCase.setVisibility(View.VISIBLE);
                } else {
                    frame_loading.setVisibility(View.VISIBLE);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ((BitmapDrawable) img_add_showcase.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    presenter.onUploadImage(prefConfig.getMID(), baos);
                    codeAlert.dismiss();
                }
                break;
            case R.id.btnCancel_AddShowCase:
                codeAlert.dismiss();
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
            case R.id.download_image_frame_profile:
                Bitmap bitmap = ((BitmapDrawable) img_showcase.getDrawable()).getBitmap();
                int position = storyAdapter.getAdapterPosition();
                if (ActivityCompat.checkSelfPermission(mActivity
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                            , Constant.PERMISSION_WRITE_EXTERNAL);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.download_success), Toast.LENGTH_SHORT).show();
                    MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bitmap
                            , showCaseList.get(position - 1).getSid() + "", "");
                }
                break;
            case R.id.btn_close_frame_profile:
            case R.id.rl_frame_main_profile:
                frame_add_showcase.setVisibility(View.GONE);
                MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onSettingClick(int pos) {
        switch (pos) {
            case 3:
                changeFragment(new TabPromoRequest());
                break;
            case 4:
                changeFragment(new MainForum());
                break;
            case 5:
//                changeFragment(new Loyalty());
                break;
            case 8:
                // Untuk kembali ke halaman login MID
                mActivity.finish();
                break;
        }
    }

    private void changeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
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
            case Constant.PERMISSION_WRITE_EXTERNAL:
                Bitmap bitmap = ((BitmapDrawable) img_showcase.getDrawable()).getBitmap();
                int position = storyAdapter.getAdapterPosition();
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.download_success), Toast.LENGTH_SHORT).show();
                    MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap
                            , showCaseList.get(position - 1).getSid() + "", "");
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
                case Constant.ACTIVITY_CHOOSE_IMAGE:
                    Uri uri_image = data.getData();
                    tvError_AddShowCase.setVisibility(View.GONE);
                    Glide.with(mContext)
                            .load(DecodeBitmap.decodeSampleBitmapFromUri(uri_image, img_add_showcase.getWidth(), img_add_showcase.getHeight(), mContext))
                            .placeholder(scaleDrawable)
                            .into(img_add_showcase);
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
    public void onGettingStoryData(List<MerchantStory> storyList) {
        showCaseList.clear();
        showCaseList.addAll(storyList);
        storyAdapter.setShowCases(showCaseList);
        storyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccessUpload() {
        Toast.makeText(mContext, mContext.getResources().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
        frame_loading.setVisibility(View.GONE);
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
