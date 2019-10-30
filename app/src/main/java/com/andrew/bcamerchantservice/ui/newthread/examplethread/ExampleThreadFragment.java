package com.andrew.bcamerchantservice.ui.newthread.examplethread;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.ui.newthread.ImagePickerAdapter;
import com.andrew.bcamerchantservice.ui.newthread.NewThread;
import com.andrew.bcamerchantservice.ui.selectedthread.SelectedThread;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.andrew.bcamerchantservice.utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExampleThreadFragment extends Fragment implements ImagePickerAdapter.onItemClick, View.OnClickListener, IExampleView {

    public static final String GET_NEW_THREAD = "get_new_thread";
    public static final String GET_ARRAY_OF_IMAGE = "get_array_of_image";
    public static final String GET_CATEGORY = "get_category";
    public static final String GET_BITMAP_THUMBNAIL = "get_bitmap_thumbnail";

    private View v;
    private PrefConfig prefConfig;
    private Context mContext;
    private FrameLayout frame_loading;

    private IExampleThreadPresenter presenter;

    private Forum forum;
    private Forum.ForumCategory category;

    private List<ImagePicker> imageList;

    private Bitmap thumbnail_bitmap;

    public ExampleThreadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_example_thread, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);
        presenter = new ExampleThreadPresenter(this);

        Button btn_back = v.findViewById(R.id.btn_back_example_thread);
        Button btn_send = v.findViewById(R.id.btn_send_example_thread);

        frame_loading = v.findViewById(R.id.frame_loading_example_new_thread);

        imageList = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getParcelable(GET_NEW_THREAD) != null) {
                /*
                 * Passing data category, thumbnail
                 * */
                forum = bundle.getParcelable(GET_NEW_THREAD);
                RoundedImageView roundedImageView = v.findViewById(R.id.image_profile_example_new_thread);
                TextView text_merchant_name = v.findViewById(R.id.text_merchant_name);
                TextView text_merchant_location = v.findViewById(R.id.text_merchant_location);
                TextView text_title = v.findViewById(R.id.text_title_example_new_thread);
                TextView text_time = v.findViewById(R.id.text_time_example_new_thread);
                TextView text_content = v.findViewById(R.id.text_content_example_new_thread);
                TextView text_category = v.findViewById(R.id.text_category_example_new_thread);

                Picasso.get()
                        .load(prefConfig.getProfilePicture())
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(roundedImageView);

                text_merchant_name.setText(prefConfig.getName());
                text_merchant_location.setText(prefConfig.getLocation());
                text_time.setText(Utils.getTime("EEEE, dd/MM/yyyy HH:mm") + " WIB");
                text_title.setText(forum.getForum_title());
                text_content.setText(forum.getForum_content());

                if (bundle.getParcelable(GET_BITMAP_THUMBNAIL) != null) {
                    thumbnail_bitmap = bundle.getParcelable(GET_BITMAP_THUMBNAIL);
                }

                if (bundle.getParcelable(GET_CATEGORY) != null) {
                    category = bundle.getParcelable(GET_CATEGORY);
                    text_category.setText("Kategori : " + category.getCategory_name());
                }

                if (bundle.getParcelableArrayList(GET_ARRAY_OF_IMAGE) != null) {
                    RecyclerView recycler_image_preview = v.findViewById(R.id.recycler_preview_example_new_thread);
                    imageList.addAll(bundle.<ImagePicker>getParcelableArrayList(GET_ARRAY_OF_IMAGE));
                    ImagePickerAdapter imagePickerAdapter = new ImagePickerAdapter(mContext, imageList, this, ImagePickerAdapter.STATES_NO_BUTTON);
                    recycler_image_preview.setLayoutManager(new GridLayoutManager(mContext, 2));
                    recycler_image_preview.setAdapter(imagePickerAdapter);
                }
            }
        }
        btn_back.setOnClickListener(this);
        btn_send.setOnClickListener(this);
    }

    @Override
    public void onItemClicked(int pos, String states) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back_example_thread:
                AppCompatActivity activity = (AppCompatActivity) mContext;

                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                NewThread newThread = new NewThread();
                Bundle bundle = new Bundle();

                bundle.putParcelable(GET_NEW_THREAD, forum);
                bundle.putParcelableArrayList(GET_ARRAY_OF_IMAGE, (ArrayList<? extends Parcelable>) imageList);
                bundle.putParcelable(GET_CATEGORY, category);
                bundle.putParcelable(GET_BITMAP_THUMBNAIL, thumbnail_bitmap);

                newThread.setArguments(bundle);

                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, newThread);

                fragmentTransaction.commit();
                break;
            case R.id.btn_send_example_thread:
                NewThread.categoryPosition = -1;
                frame_loading.setVisibility(View.VISIBLE);
                if (imageList.size() == 0) {
                    presenter.onSendNewThread(forum, prefConfig, thumbnail_bitmap);
                } else {
                    presenter.onSendNewThread(forum, prefConfig, imageList, thumbnail_bitmap);
                }
                break;
        }
    }

    @Override
    public void onSuccessUpload(Forum forum) {
        frame_loading.setVisibility(View.GONE);
        SelectedThread selectedThread = new SelectedThread();

        AppCompatActivity activity = (AppCompatActivity) mContext;

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, forum);
        bundle.putParcelable(SelectedThread.GET_MERCHANT, prefConfig.getMerchantData());

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, selectedThread);

        selectedThread.setArguments(bundle);
        fragmentTransaction.commit();
    }
}
