package com.andrew.bcamerchantservice.ui.newthread.examplethread;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.ui.newthread.ImagePickerAdapter;
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
public class ExampleThreadFragment extends Fragment implements ImagePickerAdapter.onItemClick {

    public static final String GET_NEW_THREAD = "get_new_thread";
    public static final String GET_ARRAY_OF_IMAGE = "get_array_of_image";

    private View v;
    private PrefConfig prefConfig;
    private Context mContext;

    private Forum forum;

    private List<ImagePicker> imageList;

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

                Picasso.get()
                        .load(prefConfig.getProfilePicture())
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(roundedImageView);

                text_merchant_name.setText(prefConfig.getName());
                text_merchant_location.setText(prefConfig.getLocation());
                text_time.setText(Utils.getTime("EEEE, dd/MM/yyyy HH:mm") + " WIB");
                text_title.setText(forum.getForum_title());
                text_content.setText(forum.getForum_content());

                if (bundle.getParcelableArrayList(GET_ARRAY_OF_IMAGE) != null) {
                    RecyclerView recycler_image_preview = v.findViewById(R.id.recycler_preview_example_new_thread);

                    imageList.addAll(bundle.<ImagePicker>getParcelableArrayList(GET_ARRAY_OF_IMAGE));
                    ImagePickerAdapter imagePickerAdapter = new ImagePickerAdapter(mContext, imageList, this, ImagePickerAdapter.STATES_NO_BUTTON);
                    recycler_image_preview.setLayoutManager(new GridLayoutManager(mContext, 2));
                    recycler_image_preview.setAdapter(imagePickerAdapter);
                }
            }
        }

    }

    @Override
    public void onItemClicked(int pos, String states) {

    }
}
