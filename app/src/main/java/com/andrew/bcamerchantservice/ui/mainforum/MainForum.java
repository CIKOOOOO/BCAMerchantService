package com.andrew.bcamerchantservice.ui.mainforum;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.MerchantStory;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.newthread.NewThread;
import com.andrew.bcamerchantservice.ui.otherprofile.OtherProfile;
import com.andrew.bcamerchantservice.ui.selectedthread.SelectedThread;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.DecodeBitmap;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.andrew.bcamerchantservice.utils.Utils;
import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.developers.coolprogressviews.ColorfulProgress;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainForum extends Fragment implements ThreadAdapter.onItemClick
        , IForumView, ShowcaseAdapter.onImageClickListener, TrendingAdapter.itemClickListener, View.OnClickListener
        , View.OnTouchListener, PullRefreshLayout.OnRefreshListener, TextWatcher, View.OnKeyListener, MainActivity.onBackPressFragment {
    public static final String BUNDLE_SEARCH = "BUNDLE_SEARCH";

    public static FrameLayout frame_showcase;
    public static boolean trendingIsVisible, showcase_condition, isSearch;

    private static final String TAG = MainForum.class.getSimpleName();

    private static RecyclerView showcase_recycler_view, trending_recycler_view;
    private static ShowcaseAdapter showcaseAdapter;
    private static TrendingAdapter trendingAdapter;
    private static List<Forum> trendingArrayList, tempList, searchList, forumLists;
    private static LinearLayout trending_linear;
    private static List<MerchantStory> showCaseList;

    private View v;
    private RecyclerView thread_recycler_view;
    private ThreadAdapter threadAdapter;
    private EditText etSearch;
    private TextView tvResult, tvError_AddShowCase;
    private PullRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager threadLayoutManager;
    private ColorfulProgress colorfulProgress;
    private ImageView img_showcase, img_add_showcase;
    private AlertDialog codeAlert;
    private Context mContext;
    private Activity mActivity;
    private ScaleDrawable scaleDrawable;
    private StorageReference storageStory;
    private PrefConfig prefConfig;
    private FrameLayout frame_loading;
    private Bundle bundle;

    private IForumPresenter presenter;

    private Map<String, Merchant> merchantMap, merchantStoryMap;
    private boolean c;
    private int page_number;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main_forum, container, false);
        initVar();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initVar() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        mContext = v.getContext();
        presenter = new ForumPresenter(this);

        trendingIsVisible = false;
        showcase_condition = false;
        isSearch = false;

        c = false;
        page_number = 1;
//        isLoad = true;
//        previous_total = 0;

        prefConfig = new PrefConfig(mContext);
        storageStory = storage.getReference(Constant.DB_REFERENCE_MERCHANT_STORY);
        scaleDrawable = DecodeBitmap.setScaleDrawable(mContext, R.drawable.placeholder);

        LinearLayout linearLayout = v.findViewById(R.id.parentll_main_forum);
        ImageButton search = v.findViewById(R.id.imgBtn_Search);
        ImageButton new_thread = v.findViewById(R.id.imgBtn_AddThread);
        ImageButton close = v.findViewById(R.id.btn_close_frame);
        ImageButton file_download = v.findViewById(R.id.download_image_frame);
        RelativeLayout relativeLayout = v.findViewById(R.id.rl_frame_main);
        NestedScrollView scrollView = v.findViewById(R.id.nestedScrollView_MainForum);

        showcase_recycler_view = v.findViewById(R.id.recycler_story_main_forum);
        trending_recycler_view = v.findViewById(R.id.recyclerTrending_MainForum);
        thread_recycler_view = v.findViewById(R.id.recycler_thread_main_forum);
        trending_linear = v.findViewById(R.id.linear_trending);
        frame_showcase = v.findViewById(R.id.picture_frame);

        etSearch = v.findViewById(R.id.etSearch);
        swipeRefreshLayout = v.findViewById(R.id.swipe);
        tvResult = v.findViewById(R.id.tv_result);
        colorfulProgress = v.findViewById(R.id.colorfulProgress);
        img_showcase = v.findViewById(R.id.image_frame);
        frame_loading = v.findViewById(R.id.frame_loading_main_forum);

        trendingArrayList = new ArrayList<>();
        tempList = new ArrayList<>();
        searchList = new ArrayList<>();
        showCaseList = new ArrayList<>();
        forumLists = new ArrayList<>();

        merchantMap = new HashMap<>();
        merchantStoryMap = new HashMap<>();

        threadLayoutManager = new LinearLayoutManager(mContext);
        swipeRefreshLayout.setDurations(0, 3);

        tvResult.setVisibility(View.GONE);

        setAdapter();

        presenter.loadForum(isSearch);
        presenter.loadShowCase();

        bundle = getArguments();
        if (bundle != null) {
            /*
             * Untuk mendapatkan hasil search dari Selected Forum
             * */
            if (bundle.getString(BUNDLE_SEARCH) != null) {
                final String result = bundle.getString(BUNDLE_SEARCH);
                tvResult.setVisibility(View.VISIBLE);
                tvResult.setText("Hasil dari \'" + result + "\'");
                presenter.onLoadSearch(merchantMap, result);
            } else {
                tvResult.setVisibility(View.GONE);
                isSearch = false;
            }
        }

        removeTrending(mContext);

        etSearch.addTextChangedListener(this);
        etSearch.setOnClickListener(this);
        etSearch.setOnKeyListener(this);

        file_download.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
        close.setOnClickListener(this);
        linearLayout.setOnClickListener(this);
        new_thread.setOnClickListener(this);
        search.setOnClickListener(this);
        img_showcase.setOnClickListener(this);
        frame_showcase.setOnClickListener(this);

        scrollView.setOnTouchListener(this);

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setAdapter() {
        trendingAdapter = new TrendingAdapter(mContext, trendingArrayList, this);
        trending_recycler_view.setLayoutManager(new LinearLayoutManager(mContext));
        trending_recycler_view.setAdapter(trendingAdapter);

        showcaseAdapter = new ShowcaseAdapter(mContext, showCaseList, true, merchantStoryMap, this);
        showcase_recycler_view.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        showcase_recycler_view.setAdapter(showcaseAdapter);

        threadAdapter = new ThreadAdapter(mContext, forumLists, merchantMap, this);
        thread_recycler_view.setLayoutManager(threadLayoutManager);
        thread_recycler_view.setAdapter(threadAdapter);
    }

    private void makeTrendingVisible() {
        /*
         * Merupakan function yang berfungsi untuk memunculkan trending list pada search box
         * */
        etSearch.setCursorVisible(true);
        Animation animation2 = AnimationUtils.loadAnimation(mContext, R.anim.slide_down);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) trending_linear.getLayoutParams();

        lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;

        trendingIsVisible = true;
        trending_linear.setVisibility(View.VISIBLE);
        trending_recycler_view.setVisibility(View.VISIBLE);
        trending_linear.setAnimation(animation2);
    }

    private void removeTrending(Context context) {
        etSearch.setCursorVisible(false);
        trendingIsVisible = false;
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        trending_linear.setAnimation(animation);
        Utils.hideSoftKeyboard(mActivity);
        trending_linear.setVisibility(View.GONE);
        trending_recycler_view.setVisibility(View.GONE);
    }

    private void search() {
        String result = etSearch.getText().toString();
        if (!result.trim().isEmpty()) {
            tvResult.setVisibility(View.VISIBLE);
            tvResult.setText("Hasil dari \'" + result + "\'");
            searchList.clear();
            searchList.addAll(presenter.onSearchTrending(forumLists, result));
            threadAdapter.setForumMerchantMap(searchList, merchantMap);
            removeTrending(mContext);
            isSearch = true;
            threadAdapter.notifyDataSetChanged();
        }
    }

    private void addShowCase() {
        AlertDialog.Builder codeBuilder = new AlertDialog.Builder(mContext);
        View codeView = getLayoutInflater().inflate(R.layout.custom_add_show_case, null);
        img_add_showcase = codeView.findViewById(R.id.imgView_AddShowCase);
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
    public void onClick(int pos) {
        if (thread_recycler_view.isEnabled()) {
            final Forum forumThread = forumLists.get(pos);
            final Merchant merchant = merchantMap.get(forumThread.getMid());
            Map<String, Object> map = new HashMap<>();
            map.put(forumThread.getFid() + "/view_count", forumThread.getView_count() + 1);
            presenter.onUpdateViewCount(map, forumThread, merchant);
        }
    }

    @Override
    public void onDelete(final int pos, final Forum forum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Apa Anda yakin untuk menghapus thread berjudul " + forum.getForum_title() + " ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        presenter.onRemoveThread(forum.getFid(), pos);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    @Override
    public void profileOnClick(int pos, Merchant merchant) {
        OtherProfile otherProfile = new OtherProfile();
        Bundle bundle = new Bundle();
        FragmentManager fragmentManager = getFragmentManager();

        bundle.putParcelable(OtherProfile.GET_DATA_PROFILE, merchant);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, otherProfile);

        otherProfile.setArguments(bundle);
        fragmentTransaction.commit();
    }

    @Override
    public void onForumData(List<Forum> forums) {
        forumLists.clear();
        forumLists.addAll(forums);
        threadAdapter.setForumList(forums);
        threadAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMerchantProfile(Merchant merchant) {
        if (!merchantMap.containsKey(merchant.getMid())) {
            merchantMap.put(merchant.getMid(), merchant);
            threadAdapter.setMerchantMap(merchantMap);
            threadAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMerchantStoryProfile(Merchant merchant) {
        if (!merchantStoryMap.containsKey(merchant.getMid())) {
            merchantStoryMap.put(merchant.getMid(), merchant);
            showcaseAdapter.setMerchantMap(merchantStoryMap);
            showcaseAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onTrendingList(List<Forum> trendingList) {
        trendingArrayList.clear();
        trendingArrayList.addAll(trendingList);
        trendingAdapter.setTrendingList(trendingArrayList);
        trendingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStoryData(List<MerchantStory> stories) {
        showCaseList.clear();
        showCaseList.addAll(stories);
        showcaseAdapter.setShowCases(stories);
        showcaseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadSearch(List<Forum> list) {
        forumLists.clear();
        forumLists.addAll(list);
        threadAdapter.setForumList(list);
        threadAdapter.notifyDataSetChanged();
        isSearch = true;
        bundle.remove(BUNDLE_SEARCH);
    }

    @Override
    public void onSuccessUpload() {
        Toast.makeText(mContext, mContext.getResources().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
        frame_loading.setVisibility(View.GONE);
        MainActivity.floatingActionButton.show();
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccessDeleteThread(int pos) {
        /*
         * This syntax below is no need, because we use add value event listener
         * So the data will keep be update all time even the data is deleted
         * */
//        threadAdapter.deleteList(pos);
//        threadAdapter.notifyDataSetChanged();
        Toast.makeText(mContext, mContext.getResources().getString(R.string.thread_deleted), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessUpdateViewCount(Forum forum, Merchant merchant) {
        SelectedThread selectedThread = new SelectedThread();
        Bundle bundle = new Bundle();
        FragmentManager fragmentManager = getFragmentManager();

        bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, forum);
        bundle.putParcelable(SelectedThread.GET_MERCHANT, merchant);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, selectedThread);

        selectedThread.setArguments(bundle);
        fragmentTransaction.commit();
    }

    @Override
    public void onImageClick(Context context, int pos) {
        // For story list
        if (trendingIsVisible) {
            removeTrending(context);
        } else {
            if (pos == 0) {
                addShowCase();
            } else {
//                TextView textView = v.findViewById(R.id.merchantName_MainForum);
//                if (showCaseList.get(pos - 1).getMid().equals(prefConfig.getMID())) {
//                    textView.setText("Merchant Name : " + prefConfig.getName());
//                } else {
//                    textView.setText("Merchant Name : " + showCaseList.get(pos - 1).getMid());
//                }
//                showcase_condition = true;
//                frame_showcase.setVisibility(View.VISIBLE);
//                frame_showcase.getBackground().setAlpha(230);
//                Picasso.get().load(showCaseList.get(pos - 1).getStory_picture()).into(img_showcase);
//                MainActivity.bottomNavigationView.setVisibility(View.GONE);
//                MainActivity.floatingActionButton.hide();
            }
        }
    }

    @Override
    public void onItemClick(int pos, List<Forum> forumThreads) {
        // For trending list
        if (forumThreads.size() == 0) {
            Log.e("HEHE", "PENCARIAN");
        } else {
            Forum thread = forumThreads.get(pos);
            SelectedThread selectedThread = new SelectedThread();
            Bundle bundle = new Bundle();
            FragmentManager fragmentManager = getFragmentManager();

            bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, thread);
            bundle.putParcelable(SelectedThread.GET_MERCHANT, merchantMap.get(thread.getMid()));

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, selectedThread);

            selectedThread.setArguments(bundle);
            fragmentTransaction.commit();
            Utils.hideSoftKeyboard(mActivity);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtn_Search:
                search();
                break;
            case R.id.etSearch:
                /*
                 * Jika kolom search di klik maka akan masuk kedalam kondisi dibawah
                 * */
                if (trendingIsVisible) {
                    removeTrending(mContext);
                } else {
                    makeTrendingVisible();
                }
                break;
            case R.id.parentll_main_forum:
                removeTrending(view.getContext());
                break;
            case R.id.recycler_story_main_forum:
                removeTrending(view.getContext());
                break;
            case R.id.imgBtn_AddThread:
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, new NewThread());
                fragmentTransaction.commit();
                break;
            case R.id.btn_close_frame:
                frame_showcase.setVisibility(View.GONE);
                MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                MainActivity.floatingActionButton.show();
                break;
            case R.id.rl_frame_main:
                frame_showcase.setVisibility(View.GONE);
                MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                MainActivity.floatingActionButton.hide();
                break;
            case R.id.download_image_frame:
                Bitmap bitmap = ((BitmapDrawable) img_showcase.getDrawable()).getBitmap();
                int position = showcaseAdapter.getAdapterPosition();
                if (ActivityCompat.checkSelfPermission(mActivity
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_WRITE_EXTERNAL);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.download_success), Toast.LENGTH_SHORT).show();
                    MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bitmap
                            , showCaseList.get(position).getSid(), "");
                }
                break;
            case R.id.btnSubmit_AddShowCase:
                tvError_AddShowCase.setVisibility(View.GONE);
                if (img_add_showcase.getDrawable() == null) {
                    tvError_AddShowCase.setVisibility(View.VISIBLE);
                } else {
                    frame_loading.setVisibility(View.VISIBLE);
                    Random random = new Random();
                    final int ran = random.nextInt(10000);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ((BitmapDrawable) img_add_showcase.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    byte[] byteData = baos.toByteArray();

                    presenter.onUploadShowCase(prefConfig.getMID(), ran, byteData);
                    codeAlert.dismiss();
                    MainActivity.floatingActionButton.hide();
                    MainActivity.bottomNavigationView.setVisibility(View.GONE);
                }
                break;
            case R.id.btnCancel_AddShowCase:
                codeAlert.dismiss();
                break;
            case R.id.imgView_AddShowCase:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.PERMISSION_READ_GALLERY_EXTERNAL);
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
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.nestedScrollView_MainForum:
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE)
                    if (trendingIsVisible) removeTrending(mContext);
                break;
        }
        return false;
    }

    @Override
    public void onRefresh() {
//        pastVisibleItems = 0;
        page_number = 1;
//        totalItemCount = 1;
//        previous_total = 0;
//        visibleItemCounts = 0;
        etSearch.setText("");
        if (trendingIsVisible)
            removeTrending(mContext);
        initVar();

        // use when data is loaded
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!trendingIsVisible) {
            makeTrendingVisible();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        tempList.clear();
        if (editable.toString().isEmpty()) {
            tempList.addAll(trendingArrayList);
            trendingAdapter.setTrendingList(trendingArrayList);
        } else {
            tempList.addAll(presenter.onSearchTrending(trendingArrayList, editable.toString()));
            trendingAdapter.setTrendingList(tempList);
        }
        trendingAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        switch (view.getId()) {
            case R.id.etSearch:
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (i) {
                        case KeyEvent.KEYCODE_ENTER:
                            search();
                            return true;
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public void onBackPress(boolean check, Context context) {
        removeTrending(context);
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
            case Constant.PERMISSION_WRITE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Bitmap bitmap = ((BitmapDrawable) img_showcase.getDrawable()).getBitmap();
                    int position = showcaseAdapter.getAdapterPosition();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.download_success), Toast.LENGTH_SHORT).show();
                    MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bitmap
                            , showCaseList.get(position).getSid(), "");
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.ACTIVITY_CHOOSE_IMAGE:
                    if (data.getData() != null) {
                        Uri targetUri = data.getData();
                        File f = new File("" + targetUri);
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(targetUri));
                            Glide.with(mContext)
                                    .load(DecodeBitmap.compressBitmap(bitmap))
                                    .placeholder(scaleDrawable)
                                    .into(img_add_showcase);
                            tvError_AddShowCase.setVisibility(View.GONE);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }
}
