package com.andrew.bcamerchantservice.ui.profile.mystoreinformation.catalog;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.ui.profile.Profile;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;

/**
 * A simple {@link Fragment} subclass.
 */
public class CatalogFragment extends Fragment implements View.OnClickListener, ICatalogView {

    private View v;
    private Context mContext;
    private Activity mActivity;
    private ImageView image_catalog;
    private EditText edit_name, edit_price, edit_description;
    private TextView text_counter;
    private FrameLayout frame_loading;
    private PrefConfig prefConfig;

    private ICatalogPresenter presenter;

    private Bitmap catalog_bitmap;

    public CatalogFragment() {
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
        v = inflater.inflate(R.layout.fragment_catalog, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        catalog_bitmap = null;
        presenter = new CatalogPresenter(this);
        prefConfig = new PrefConfig(mContext);

        ImageButton image_add = v.findViewById(R.id.image_button_add_catalog);
        Button btn_send = v.findViewById(R.id.btn_send_catalog);
        Button btn_cancel = v.findViewById(R.id.btn_cancel_catalog);

        image_catalog = v.findViewById(R.id.image_new_catalog);
        edit_name = v.findViewById(R.id.edit_text_name_catalog);
        edit_price = v.findViewById(R.id.edit_text_price_catalog);
        edit_description = v.findViewById(R.id.edit_text_description_catalog);
        text_counter = v.findViewById(R.id.text_counter_catalog);
        frame_loading = v.findViewById(R.id.frame_loading_catalog);

        btn_cancel.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        image_add.setOnClickListener(this);

        frame_loading.setOnClickListener(this);

        edit_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                text_counter.setText(charSequence.toString().length() + "/160");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_button_add_catalog:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            , Constant.PERMISSION_READ_FILE_EXTERNAL);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                }
                break;
            case R.id.btn_send_catalog:
                String numberOnlyRegex = "^[0-9]*$";
                String name = edit_name.getText().toString();
                String desc = edit_description.getText().toString();
                TextView text_error = v.findViewById(R.id.text_error_catalog);
                text_error.setVisibility(View.GONE);

                if (name.isEmpty()) {
                    edit_name.setError("This field cannot be empty");
                } else if (!edit_price.getText().toString().matches(numberOnlyRegex)) {
                    edit_price.setError("Invalid Price");
                } else if (desc.isEmpty()) {
                    edit_description.setError("This field cannot be empty");
                } else if (catalog_bitmap == null) {
                    text_error.setVisibility(View.VISIBLE);
                } else {
                    frame_loading.setVisibility(View.VISIBLE);
                    int price = edit_price.getText().toString().isEmpty() ? 0 : Integer.parseInt(edit_price.getText().toString());
                    presenter.sendCatalog(prefConfig.getMID(), name, desc, price, catalog_bitmap);
                }
                break;
            case R.id.btn_cancel_catalog:
                FragmentTransaction fragmentTransactions = getFragmentManager().beginTransaction();
                fragmentTransactions.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransactions.replace(R.id.main_frame, new Profile());
                fragmentTransactions.commit();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.PERMISSION_READ_FILE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent1 = new Intent();
                    intent1.setType("image/*");
                    intent1.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent1, Constant.ACTIVITY_CHOOSE_IMAGE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.ACTIVITY_CHOOSE_IMAGE:
                if (data == null || data.getData() == null)
                    break;

                Uri targetUri = data.getData();
                if (targetUri == null)
                    break;
                try {
                    catalog_bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(targetUri));
                    Glide.with(mContext)
                            .load(catalog_bitmap)
                            .centerCrop()
                            .into(image_catalog);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onSuccessSendCatalog() {
        frame_loading.setVisibility(View.GONE);
        Toast.makeText(mContext, "Upload catalog success", Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, new Profile());

        fragmentTransaction.commit();
    }
}
