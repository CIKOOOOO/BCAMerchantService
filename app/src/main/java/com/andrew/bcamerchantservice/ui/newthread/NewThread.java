package com.andrew.bcamerchantservice.ui.newthread;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.mainforum.MainForum;
import com.andrew.bcamerchantservice.ui.newthread.examplethread.ExampleThreadFragment;
import com.andrew.bcamerchantservice.ui.selectedthread.SelectedThread;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.DecodeBitmap;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.andrew.bcamerchantservice.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewThread extends Fragment implements View.OnClickListener, View.OnTouchListener
        , ImagePickerAdapter.onItemClick, INewThreadView, MainActivity.onBackPressFragment
        , CategoryAdapter.onCategoryClick {
    public static final String EDIT_THREAD = "EDIT_THREAD";
    public static final String EDIT_THREAD_SELECTED = "EDIT_THREAD_SELECTED";
    public static final String EDIT_THREAD_REPLY = "EDIT_THREAD_REPLY";
    public static final String EDIT_THREAD_REPLY_BACK = "EDIT_THREAD_REPLY_BACK";
    public static final String EDIT_THREAD_MERCHANT = "EDIT_THREAD_MERCHANT";

    private static final String TAG = NewThread.class.getSimpleName();
    private static final int CAMERA_THUMBNAIL = 191, GALLERY_THUMBNAIL = 192;
    private static final int REQUEST_CAMERA_THUMBNAIL = 193, REQUEST_GALLERY_THUMBNAIL = 194;

    public static String THREAD_CONDITION;
    public static int categoryPosition;

    private static PrefConfig prefConfig;
    private static Forum forum;
    private static Forum.ForumReply forumReply;
    private static Forum.ForumCategory forumCategory;

    private View v;
    private RecyclerView recyclerView, recycler_category;
    private EditText title, content;
    private TextView text_delete_thumbnail;
    private ImagePickerAdapter imagePickerAdapter;
    private Context mContext;
    private Activity mActivity;
    private DatabaseReference dbRef;
    private StorageReference storageReference, strRef;
    private FrameLayout frame_loading;
    private Merchant merchant;
    private CategoryAdapter categoryAdapter;
    private RoundedImageView image_thumbnail;
    private ImageButton question_mark;
    private ToolTipsManager toolTipsManager;

    private INewThreadPresenter presenter;

    private List<ImagePicker> imageList;
    private List<Forum.ForumImage> forumImageList;
    private List<Forum.ForumCategory> categoryList;

    private Bitmap thumbnail_bitmap;
    private boolean isTooltipAppear;

    public NewThread() {
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
        v = inflater.inflate(R.layout.fragment_new_thread, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        isTooltipAppear = false;
        THREAD_CONDITION = "";
        thumbnail_bitmap = null;
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);
        presenter = new NewThreadPresenter(this);
        dbRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference(Constant.DB_REFERENCE_FORUM_IMAGE);
        strRef = FirebaseStorage.getInstance().getReference(Constant.DB_REFERENCE_FORUM_IMAGE_REPLY);
        toolTipsManager = new ToolTipsManager();
        MainActivity.bottomNavigationView.setVisibility(View.GONE);

        TextView tvTitle = v.findViewById(R.id.title_new_thread);
        TextView title_only = v.findViewById(R.id.tvTitle_NewThread);
        Button submit = v.findViewById(R.id.btnSubmit_NewThread);
        ImageButton photo = v.findViewById(R.id.photo_taker_new_thread);
//        ImageButton file = v.findViewById(R.id.file_taker_new_thread);
        ImageButton camera = v.findViewById(R.id.camera_taker_new_thread);
        ImageButton camera_thumbnail = v.findViewById(R.id.camera_thumbnail_new_thread);
        ImageButton gallery_thumbnail = v.findViewById(R.id.gallery_thumbnail_new_thread);
        NestedScrollView nested_scroll = v.findViewById(R.id.nested_scroll_new_thread);
        LinearLayout linear_category = v.findViewById(R.id.ll_dummy_00_new_thread);
        LinearLayout linear_thumbnail_title = v.findViewById(R.id.ll_dummy_01_new_thread);
        LinearLayout linear_thumbnail_content = v.findViewById(R.id.ll_dummy_02_new_thread);

        question_mark = v.findViewById(R.id.image_button_question_mark_new_thread);
        recycler_category = v.findViewById(R.id.recycler_category_new_thread);
        recyclerView = v.findViewById(R.id.recycler_image_new_thread);
        title = v.findViewById(R.id.edit_title_new_thread);
        content = v.findViewById(R.id.edit_text_content_new_thread);
        frame_loading = v.findViewById(R.id.frame_loading_new_thread);
        image_thumbnail = v.findViewById(R.id.image_thumbnail_new_thread);
        text_delete_thumbnail = v.findViewById(R.id.text_delete_thumbnail_new_thread);

        imageList = new ArrayList<>();
        forumImageList = new ArrayList<>();
        categoryList = new ArrayList<>();

        setRecyclerView();

        presenter.onLoadCategory();

        submit.setOnClickListener(this);
        photo.setOnClickListener(this);
        camera_thumbnail.setOnClickListener(this);
        gallery_thumbnail.setOnClickListener(this);
        text_delete_thumbnail.setOnClickListener(this);
        question_mark.setOnClickListener(this);
//        it will appear when we know how to upload pdf file to firebase storage
//        file.setOnClickListener(this);
        camera.setOnClickListener(this);
        frame_loading.setOnClickListener(this);
        content.setOnTouchListener(this);
        nested_scroll.setOnTouchListener(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getParcelable(EDIT_THREAD) != null || bundle.getParcelable(EDIT_THREAD_SELECTED) != null) {
                if (bundle.getParcelable(EDIT_THREAD) != null) {
                    THREAD_CONDITION = EDIT_THREAD;
                } else if (bundle.getParcelable(EDIT_THREAD_SELECTED) != null) {
                    THREAD_CONDITION = EDIT_THREAD_SELECTED;
                }
//                frame_loading.setVisibility(View.VISIBLE);
                forum = bundle.getParcelable(THREAD_CONDITION);
                if (forum != null) {
                    title.setText(forum.getForum_title());
                    content.setText(forum.getForum_content());
                    String URL = "";

                    if (forum.getForum_thumbnail().isEmpty())
                        URL = Constant.SOLID_COLOR;
                    else
                        URL = forum.getForum_thumbnail();

                    Picasso.get()
                            .load(URL)
                            .into(image_thumbnail);

                    String path = Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/" + Constant.DB_REFERENCE_FORUM_IMAGE;
                    presenter.onLoadImage(path);
                }

                tvTitle.setText(mContext.getResources().getString(R.string.edit_thread));
            } else if (bundle.getParcelable(EDIT_THREAD_REPLY) != null) {
                THREAD_CONDITION = EDIT_THREAD_REPLY;
                title.setVisibility(View.GONE);
                title_only.setVisibility(View.GONE);
                linear_category.setVisibility(View.GONE);
                linear_thumbnail_content.setVisibility(View.GONE);
                linear_thumbnail_title.setVisibility(View.GONE);
                forumReply = bundle.getParcelable(EDIT_THREAD_REPLY);
                forum = bundle.getParcelable(EDIT_THREAD_REPLY_BACK);
                merchant = bundle.getParcelable(EDIT_THREAD_MERCHANT);
                content.setText(forumReply.getForum_content());
                String path = Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/"
                        + Constant.DB_REFERENCE_FORUM_REPLY + "/" + forumReply.getFrid() + "/"
                        + Constant.DB_REFERENCE_FORUM_IMAGE_REPLY;
                presenter.onLoadImage(path);
                tvTitle.setText(mContext.getResources().getString(R.string.edit_thread));
            } else if (bundle.getParcelable(ExampleThreadFragment.GET_NEW_THREAD) != null) {
                forum = bundle.getParcelable(ExampleThreadFragment.GET_NEW_THREAD);

                title.setText(forum.getForum_title());
                content.setText(forum.getForum_content());

                if (bundle.getParcelable(ExampleThreadFragment.GET_BITMAP_THUMBNAIL) != null) {
                    thumbnail_bitmap = bundle.getParcelable(ExampleThreadFragment.GET_BITMAP_THUMBNAIL);
                    Glide.with(mContext)
                            .load(thumbnail_bitmap)
                            .into(image_thumbnail);
                }
                if (bundle.getParcelable(ExampleThreadFragment.GET_CATEGORY) != null) {
                    forumCategory = bundle.getParcelable(ExampleThreadFragment.GET_CATEGORY);
                    categoryAdapter.setLastPosition(categoryPosition);
                    categoryAdapter.notifyItemChanged(categoryPosition);
                }
                if (bundle.getParcelableArrayList(ExampleThreadFragment.GET_ARRAY_OF_IMAGE) != null) {
                    imageList.addAll(bundle.<ImagePicker>getParcelableArrayList(ExampleThreadFragment.GET_ARRAY_OF_IMAGE));
                    imagePickerAdapter.setImageList(imageList);
                }
            }
        }
    }

    private void setRecyclerView() {
        categoryAdapter = new CategoryAdapter(mContext, categoryList, this);
        recycler_category.setLayoutManager(new GridLayoutManager(mContext, 3));
        recycler_category.setAdapter(categoryAdapter);

        imagePickerAdapter = new ImagePickerAdapter(mContext, imageList, this, "");
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(imagePickerAdapter);
    }

    @Override
    public void onThreadSuccessUpload(Forum forum) {
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
        Toast.makeText(mContext, "Submit Sukses!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadImage(List<ImagePicker> list, List<Forum.ForumImage> imageList) {
        frame_loading.setVisibility(View.GONE);
        if (list.size() > 0) {
            forumImageList.clear();
            this.imageList.clear();
            this.imageList.addAll(list);
            forumImageList.addAll(imageList);
            imagePickerAdapter.setImageList(list);
            imagePickerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onEditSuccess() {
        frame_loading.setVisibility(View.GONE);
        Forum forums = new Forum(forum.getFid(), String.valueOf(prefConfig.getMID())
                , content.getText().toString(), forum.getForum_date()
                , title.getText().toString(), forumCategory.getFcid(), "", forum.getForum_like()
                , forum.getView_count(), forum.isLike());
        SelectedThread selectedThread = new SelectedThread();

        AppCompatActivity activity = (AppCompatActivity) mContext;

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, forums);
        bundle.putParcelable(SelectedThread.GET_MERCHANT, prefConfig.getMerchantData());

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, selectedThread);

        selectedThread.setArguments(bundle);
        fragmentTransaction.commit();
        Toast.makeText(mContext, getResources().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditReplySuccess() {
        SelectedThread selectedThread = new SelectedThread();

        AppCompatActivity activity = (AppCompatActivity) mContext;

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, forum);
        bundle.putParcelable(SelectedThread.GET_MERCHANT, merchant);

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, selectedThread);

        selectedThread.setArguments(bundle);
        fragmentTransaction.commit();
        Toast.makeText(mContext, getResources().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadCategory(List<Forum.ForumCategory> forumCategories) {
        categoryList.clear();
        categoryList.addAll(forumCategories);
        categoryAdapter.setForumCategoryList(forumCategories);
        categoryAdapter.notifyDataSetChanged();
        if (THREAD_CONDITION.equals(EDIT_THREAD) || THREAD_CONDITION.equals(EDIT_THREAD_SELECTED) || THREAD_CONDITION.equals(ExampleThreadFragment.GET_NEW_THREAD))
            for (int i = 0; i < categoryList.size(); i++) {
                if (forum.getFcid().equals(categoryList.get(i).getFcid())) {
                    forumCategory = categoryList.get(i);
                    categoryAdapter.setLastPosition(i);
                    break;
                }
            }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit_NewThread:
                final String titles = title.getText().toString();
                final String contents = content.getText().toString();

                if (titles.isEmpty() && !THREAD_CONDITION.equals(EDIT_THREAD_REPLY)) {
                    title.setError("This cannot be empty");
                    title.requestFocus(title.getLayoutDirection());
                } else if (categoryAdapter.getLastPosition() == -1 && !THREAD_CONDITION.equals(EDIT_THREAD_REPLY)) {
                    recycler_category.requestFocus(recycler_category.getLayoutDirection());
                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(500); //You can manage the blinking time with this parameter
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(5);
                    recycler_category.startAnimation(anim);
                } else if (contents.isEmpty()) {
                    content.setError("This cannot be empty");
                    content.requestFocus(content.getLayoutDirection());
                } else {
                    Map<String, Object> map = new HashMap<>();
                    switch (THREAD_CONDITION) {
                        case EDIT_THREAD_REPLY:
                            frame_loading.setVisibility(View.VISIBLE);
                            if (forumImageList.size() > 0) {
                                for (int i = 0; i < forumImageList.size(); i++) {
                                    presenter.onStorageDelete(storageReference, forumImageList.get(i).getImage_name());
                                }
                                String path = Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/" + Constant.DB_REFERENCE_FORUM_REPLY + "/"
                                        + forumReply.getFrid() + "/" + Constant.DB_REFERENCE_FORUM_IMAGE_REPLY;
                                presenter.onFirebaseRemoveValue(path);
                            }
                            if (imageList.size() == 0) {
                                map.put(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid()
                                        + "/forum_reply/" + forumReply.getFrid() + "/forum_content", content.getText().toString());
                                presenter.onEditThreadReply(map);
                            } else {
                                final String key = dbRef.push().getKey();
                                map.put(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/forum_reply/"
                                        + forumReply.getFrid() + "/forum_content", content.getText().toString());
                                presenter.onEditThreadReply(map, imageList, prefConfig.getMID(), forum.getFid(), key, forumReply.getFrid());
                            }
                            break;
                        case "":
                            final String key = dbRef.push().getKey();

                            Forum forum = new Forum(key, prefConfig.getMID(), content.getText().toString()
                                    , Utils.getTime("EEEE, dd/MM/yyyy HH:mm"), title.getText().toString()
                                    , forumCategory.getFcid(), "", 0, 1, false);
                            ExampleThreadFragment exampleThreadFragment = new ExampleThreadFragment();
                            Bundle bundle = new Bundle();
                            FragmentManager fragmentManager = getFragmentManager();

                            bundle.putParcelable(ExampleThreadFragment.GET_NEW_THREAD, forum);
                            bundle.putParcelable(ExampleThreadFragment.GET_CATEGORY, forumCategory);
                            bundle.putParcelable(ExampleThreadFragment.GET_BITMAP_THUMBNAIL, thumbnail_bitmap);
                            bundle.putParcelableArrayList(ExampleThreadFragment.GET_ARRAY_OF_IMAGE, (ArrayList<? extends Parcelable>) imageList);

                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            fragmentTransaction.replace(R.id.main_frame, exampleThreadFragment);

                            exampleThreadFragment.setArguments(bundle);
                            fragmentTransaction.commit();
                            break;
                        case EDIT_THREAD:
                        case EDIT_THREAD_SELECTED:
                            frame_loading.setVisibility(View.VISIBLE);
                            map.put(Constant.DB_REFERENCE_FORUM + "/" + NewThread.forum.getFid() + "/forum_content", content.getText().toString());
                            map.put(Constant.DB_REFERENCE_FORUM + "/" + NewThread.forum.getFid() + "/forum_title/", title.getText().toString());
                            map.put(Constant.DB_REFERENCE_FORUM + "/" + NewThread.forum.getFid() + "/fcid/", forumCategory.getFcid());

                            if (forumImageList.size() > 0) {
                                for (int i = 0; i < forumImageList.size(); i++) {
                                    presenter.onStorageDelete(storageReference, forumImageList.get(i).getImage_name());
                                }
                                presenter.onFirebaseRemoveValue(Constant.DB_REFERENCE_FORUM + "/"
                                        + NewThread.forum.getFid() + "/" + Constant.DB_REFERENCE_FORUM_IMAGE);
                            }

                            if (imageList.size() == 0) {
                                presenter.onUpdateThread(map, thumbnail_bitmap, prefConfig.getMID(), NewThread.forum.getFid());
                            } else {
                                presenter.onUpdateThread(map, imageList, NewThread.forum, prefConfig.getMID()
                                        , content.getText().toString(), title.getText().toString(), thumbnail_bitmap);
                            }
                            break;
                    }
                }
                break;
            case R.id.photo_taker_new_thread:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.PERMISSION_READ_GALLERY_EXTERNAL);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                }
                break;
            case R.id.gallery_thumbnail_new_thread:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_GALLERY_THUMBNAIL);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, GALLERY_THUMBNAIL);
                }
                break;

//            case R.id.file_taker_new_thread:
//                if (ActivityCompat.checkSelfPermission(mActivity,
//                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                            Constant.PERMISSION_READ_FILE_EXTERNAL);
//                } else {
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.setType("application/pdf");
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_FILE);
//                }
//                break;

            case R.id.camera_taker_new_thread:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                        || ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                            , Constant.PERMISSION_CAMERA_TAKER);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, Constant.ACTIVITY_TAKE_IMAGE);
                }
                break;
            case R.id.camera_thumbnail_new_thread:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                        || ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_THUMBNAIL);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_THUMBNAIL);
                }
                break;
            case R.id.image_button_question_mark_new_thread:
                if (!isTooltipAppear) {
                    isTooltipAppear = true;
                    ToolTip.Builder builder = new ToolTip.Builder(mContext, question_mark
                            , (CoordinatorLayout) v.findViewById(R.id.coordinator_new_thread)
                            , "Thumbnail akan muncul\ndihalaman forum utama", ToolTip.POSITION_BELOW);
                    builder.setBackgroundColor(mContext.getResources().getColor(R.color.blue_palette));
                    builder.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/sniglet_reguler.ttf"));
                    toolTipsManager.show(builder.build());
                }
                break;
            case R.id.text_delete_thumbnail_new_thread:
                if (thumbnail_bitmap != null) {
                    thumbnail_bitmap.recycle();
                    thumbnail_bitmap = null;
                    Picasso.get()
                            .load(Constant.SOLID_COLOR)
                            .memoryPolicy(MemoryPolicy.NO_STORE)
                            .into(image_thumbnail);
                }
                break;
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
//        if (view.getId() == R.id.edit_text_content_new_thread) {
//            view.getParent().requestDisallowInterceptTouchEvent(true);
//            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
//                case MotionEvent.ACTION_UP:
//                    view.getParent().requestDisallowInterceptTouchEvent(false);
//                    break;
//            }
//        }
        if (view.getId() == R.id.nested_scroll_new_thread) {
            isTooltipAppear = false;
            toolTipsManager.dismissAll();
        }
        return false;
    }

    @Override
    public void onItemClicked(int pos, String states) {
        imageList.remove(pos);
        imagePickerAdapter.setImageList(imageList);
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000, true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (mContext.getContentResolver() != null) {
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.PERMISSION_CAMERA_TAKER:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, Constant.ACTIVITY_TAKE_IMAGE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.PERMISSION_READ_FILE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_FILE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.PERMISSION_READ_GALLERY_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CAMERA_THUMBNAIL:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_THUMBNAIL);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_GALLERY_THUMBNAIL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, GALLERY_THUMBNAIL);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.ACTIVITY_CHOOSE_IMAGE:
                    Bitmap bitmap;
                    if (data.getData() != null) {
                        Uri targetUri = data.getData();
                        File f = new File("" + targetUri);
                        try {
                            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(targetUri));
                            imageList.add(new ImagePicker(DecodeBitmap.compressBitmap(bitmap), "IMG", f.getName()));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (data.getClipData() != null) {
                        ClipData clipData = data.getClipData();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();
                            File f = new File("" + uri);
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                                imageList.add(new ImagePicker(DecodeBitmap.compressBitmap(bitmap), "IMG", f.getName()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    imagePickerAdapter.setImageList(imageList);
                    break;
                case Constant.ACTIVITY_CHOOSE_FILE:
                    if (data.getData() != null) {
                        Uri targetUri = data.getData();
                        String filePath = getFileName(targetUri);
                        imageList.add(new ImagePicker(null, ImagePickerAdapter.STATES_PDF, filePath));
                    } else if (data.getClipData() != null) {
                        ClipData clipData = data.getClipData();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri targetUri = item.getUri();
                            String filePath = getFileName(targetUri);
                            imageList.add(new ImagePicker(null, ImagePickerAdapter.STATES_PDF, filePath));
                        }
                    }
                    imagePickerAdapter.setImageList(imageList);
                    break;
                case Constant.ACTIVITY_TAKE_IMAGE:
                    Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    Uri tempUri = getImageUri(mContext, photo);
                    File finalFile = new File(getRealPathFromURI(tempUri));
                    imageList.add(new ImagePicker(photo, "IMG", finalFile.getName()));
                    imagePickerAdapter.setImageList(imageList);
                    break;
                case CAMERA_THUMBNAIL:
                    thumbnail_bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    Glide.with(mContext)
                            .load(DecodeBitmap.compressBitmap(thumbnail_bitmap))
                            .into(image_thumbnail);
                    break;
                case GALLERY_THUMBNAIL:
                    Uri targetUri = data.getData();
                    try {
                        thumbnail_bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(targetUri));
                        Glide.with(mContext)
                                .load(DecodeBitmap.compressBitmap(thumbnail_bitmap))
                                .into(image_thumbnail);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            recyclerView.smoothScrollToPosition(imageList.size());
        }
    }

    @Override
    public void onBackPress(boolean check, Context context) {
        if (THREAD_CONDITION.equals(EDIT_THREAD) || THREAD_CONDITION.isEmpty()) {
            MainForum mainForum = new MainForum();

            AppCompatActivity activity = (AppCompatActivity) context;

            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, mainForum);

            fragmentTransaction.commit();
        } else {
            SelectedThread selectedThread = new SelectedThread();

            AppCompatActivity activity = (AppCompatActivity) context;

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

    @Override
    public void onClick(Forum.ForumCategory forumCategory, int pos) {
        NewThread.forumCategory = forumCategory;
        categoryPosition = pos;
    }
}
