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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.Merchant.MerchantStory;
import com.andrew.bcamerchantservice.model.Report;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.mainforum.favorite.FavoriteFragment;
import com.andrew.bcamerchantservice.ui.mainforum.search.SearchFragment;
import com.andrew.bcamerchantservice.ui.mainforum.story.StoryFragment;
import com.andrew.bcamerchantservice.ui.newthread.NewThread;
import com.andrew.bcamerchantservice.ui.otherprofile.OtherProfile;
import com.andrew.bcamerchantservice.ui.profile.Profile;
import com.andrew.bcamerchantservice.ui.selectedthread.SelectedThread;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.DecodeBitmap;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
        , IForumView, StoryAdapter.onImageClickListener, View.OnClickListener
        , PullRefreshLayout.OnRefreshListener, MainActivity.onBackPressFragment
        , CategoryAdapter.onCategoryClick {

    public static boolean isStoryVisible, isCategoryBottomSheetVisible;
    public static ViewPager story_pager;

    private static final String TAG = MainForum.class.getSimpleName();

    private static RecyclerView showcase_recycler_view;
    private static StoryAdapter storyAdapter;

    private static List<Forum> forumLists;
    private static List<MerchantStory> storyList;

    private int ITEM_LOAD_COUNT;

    private View v, codeView;
    private RecyclerView recycler_forum;
    private ThreadAdapter threadAdapter;
    private TextView tvError_AddShowCase;
    private PullRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager threadLayoutManager;
    private ImageView img_add_showcase;
    private AlertDialog codeAlert, dialog_report;
    private Context mContext;
    private Activity mActivity;
    private ScaleDrawable scaleDrawable;
    private PrefConfig prefConfig;
    private FrameLayout frame_loading;
    private CategoryAdapter categoryAdapter;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView text_category;
    private ReportAdapter reportAdapter;

    private IForumPresenter presenter;

    private List<Forum.ForumCategory> categoryList;
    private List<Report> reportList;
    private Map<String, Merchant> merchantMap, merchantStoryMap;

    private int total_item, last_visible_item;
    private boolean isLoading, isMaxData, check;
    private String last_node, last_key, FCID;
    private long MAX_DATA;

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
        recycler_forum.destroyDrawingCache();
        showcase_recycler_view.destroyDrawingCache();
        super.onDestroyView();
    }

    private void initVar() {
        total_item = 0;
        last_visible_item = 0;
        isLoading = false;
        isMaxData = false;
        last_node = "";
        last_key = "";
        FCID = "0";
        MAX_DATA = 0;
        ITEM_LOAD_COUNT = 4;
        mContext = v.getContext();
        presenter = new ForumPresenter(this);
        check = false;

        isStoryVisible = false;
        isCategoryBottomSheetVisible = false;

        prefConfig = new PrefConfig(mContext);
        scaleDrawable = DecodeBitmap.setScaleDrawable(mContext, R.drawable.placeholder);

        LinearLayout linearLayout = v.findViewById(R.id.parentll_main_forum);
        ImageButton search = v.findViewById(R.id.image_button_search_main_forum);
        ImageButton new_thread = v.findViewById(R.id.imgBtn_AddThread);
        ImageButton favorite = v.findViewById(R.id.image_button_favorite_main_forum);
        TextView text_change = v.findViewById(R.id.text_change_category_main_forum);
        CoordinatorLayout coordinatorLayout = v.findViewById(R.id.bottom_sheet_main);

        showcase_recycler_view = v.findViewById(R.id.recycler_story_main_forum);
        recycler_forum = v.findViewById(R.id.recycler_thread_main_forum);
        swipeRefreshLayout = v.findViewById(R.id.swipe);
        frame_loading = v.findViewById(R.id.frame_loading_main_forum);
        text_category = v.findViewById(R.id.text_category_main_forum);
        story_pager = v.findViewById(R.id.view_pager_main_forum);
        bottomSheetBehavior = BottomSheetBehavior.from(coordinatorLayout);

        storyList = new ArrayList<>();
        forumLists = new ArrayList<>();
        categoryList = new ArrayList<>();
        reportList = new ArrayList<>();

        merchantMap = new HashMap<>();
        merchantStoryMap = new HashMap<>();

        codeView = LayoutInflater.from(mContext).inflate(R.layout.custom_report, null);

        categoryAdapter = new CategoryAdapter(mContext, categoryList, this);
        reportAdapter = new ReportAdapter(reportList, codeView.getContext());
        threadLayoutManager = new LinearLayoutManager(mContext);

        swipeRefreshLayout.setDurations(0, 3);

        setAdapter();

        text_category.setText("Category: All Category");

        presenter.getLastKey();

        getData();

//        presenter.loadShowCase();

        linearLayout.setOnClickListener(this);
        new_thread.setOnClickListener(this);
        search.setOnClickListener(this);
        favorite.setOnClickListener(this);
        text_change.setOnClickListener(this);

        frame_loading.setOnClickListener(this);

        recycler_forum.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                MainActivity.floatingActionButton.show();

                total_item = threadLayoutManager.getItemCount();
                last_visible_item = threadLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && total_item <= (last_visible_item + 4)) {
                    getData();
                    isLoading = true;
                }
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        ((View) v.findViewById(R.id.view_blur_main_forum)).setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_HIDDEN:
                        ((View) v.findViewById(R.id.view_blur_main_forum)).setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void getData() {
        if (!isMaxData) {
            Query query;
            if (last_node.isEmpty())
                query = FirebaseDatabase.getInstance().getReference()
                        .child(Constant.DB_REFERENCE_FORUM)
                        .limitToLast(ITEM_LOAD_COUNT);
            else
                query = FirebaseDatabase.getInstance().getReference()
                        .child(Constant.DB_REFERENCE_FORUM)
                        .limitToLast(ITEM_LOAD_COUNT += 4);

            /*
             * Firebase doesn't support getting data with descending operation
             * So we have to manipulate it.
             * When recycler view is already goes down
             * We query it with + 4, not base on the last node
             * Well yes, it's barbarian, but it's work :)
             * */

            presenter.loadForum(prefConfig.getMID(), FCID, query);
        }
    }

    private void setAdapter() {
        storyAdapter = new StoryAdapter(mContext, storyList, true, merchantStoryMap, this);
        showcase_recycler_view.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        showcase_recycler_view.setAdapter(storyAdapter);

        threadAdapter = new ThreadAdapter(mContext, forumLists, merchantMap, this);
        recycler_forum.setLayoutManager(threadLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recycler_forum.getContext(), threadLayoutManager.getOrientation());
        recycler_forum.addItemDecoration(dividerItemDecoration);
        recycler_forum.setAdapter(threadAdapter);
    }

    private void addShowCase() {
        AlertDialog.Builder codeBuilder = new AlertDialog.Builder(mContext);
        View codeView = getLayoutInflater().inflate(R.layout.custom_add_show_case, null);
        Button submit = codeView.findViewById(R.id.btnSubmit_AddShowCase);
        Button cancel = codeView.findViewById(R.id.btnCancel_AddShowCase);
        img_add_showcase = codeView.findViewById(R.id.imgView_AddShowCase);
        tvError_AddShowCase = codeView.findViewById(R.id.show_error_content_add_show_case);
        img_add_showcase.setOnClickListener(this);
        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);
        codeBuilder.setView(codeView);
        codeAlert = codeBuilder.create();
        codeAlert.show();
    }

    private void changeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransactions = getFragmentManager().beginTransaction();
        fragmentTransactions.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransactions.replace(R.id.main_frame, fragment);
        fragmentTransactions.commit();
    }

    private boolean isChecked(List<Report> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isReport_is_checked()) return true;
        }
        return false;
    }

    @Override
    public void onClick(int pos) {
        final Forum forumThread = forumLists.get(pos);
        final Merchant merchant = merchantMap.get(forumThread.getMid());
        Map<String, Object> map = new HashMap<>();
        map.put(forumThread.getFid() + "/view_count", forumThread.getView_count() + 1);
        presenter.onUpdateViewCount(map, forumThread, merchant);
    }

    @Override
    public void onDelete(final int pos, final Forum forum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Apa Anda yakin untuk menghapus forum berjudul " + forum.getForum_title() + " ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        frame_loading.setVisibility(View.VISIBLE);
                        presenter.onRemoveForum(forum.getFid(), pos);
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
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        Bundle bundle = new Bundle();
        if (!merchant.getMid().equals(prefConfig.getMID())) {
            OtherProfile otherProfile = new OtherProfile();
            bundle.putParcelable(OtherProfile.GET_DATA_PROFILE, merchant);
            otherProfile.setArguments(bundle);
            fragmentTransaction.replace(R.id.main_frame, otherProfile);
        } else {
            fragmentTransaction.replace(R.id.main_frame, new Profile());
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onHide(final String FID, String title, final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Apa Anda yakin untuk menyembunyikan forum berjudul " + title + " ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        presenter.hideForum(FID, prefConfig.getMID(), pos);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    @Override
    public void onShowReport(final Merchant merchant, final Forum forum) {
        AlertDialog.Builder codeBuilder = new AlertDialog.Builder(mContext);
        TextView name = codeView.findViewById(R.id.report_name);
        TextView thread = codeView.findViewById(R.id.report_title);
        final TextView error = codeView.findViewById(R.id.show_error_content_report);
        final EditText content = codeView.findViewById(R.id.etOther_Report);
        Button send = codeView.findViewById(R.id.btnSubmit_Report);
        Button cancel = codeView.findViewById(R.id.btnCancel_Report);
        RecyclerView recyclerView = codeView.findViewById(R.id.recycler_checkbox_report);
        final CheckBox checkBox = codeView.findViewById(R.id.check_other);

        recyclerView.setLayoutManager(new GridLayoutManager(codeView.getContext(), 2));

        codeBuilder.setView(codeView);

        if (codeView.getParent() == null) {
            dialog_report = codeBuilder.create();
        }

        name.setText(": " + merchant.getMerchant_name());
        thread.setText(": " + forum.getForum_title());

        recyclerView.setAdapter(reportAdapter);

        presenter.loadReportList();

        content.setEnabled(false);
        content.setText("");
        check = false;
        content.setBackground(codeView.getContext().getDrawable(R.drawable.background_grey));
        checkBox.setChecked(false);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!check) {
                    content.setBackground(codeView.getContext().getDrawable(R.drawable.background_stroke_white));
                    check = true;
                    content.setEnabled(true);
                } else {
                    content.setBackground(codeView.getContext().getDrawable(R.drawable.background_grey));
                    check = false;
                    content.setEnabled(false);
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setVisibility(View.GONE);
                if (check) {
                    if (content.getText().toString().isEmpty())
                        error.setVisibility(View.VISIBLE);
                    else {
                        presenter.onSendReport(content.getText().toString(), reportList, forum.getFid(), prefConfig.getMID());
                    }
                } else if (isChecked(reportList)) {
                    presenter.onSendReport(content.getText().toString(), reportList, forum.getFid(), prefConfig.getMID());
                } else {
                    error.setVisibility(View.VISIBLE);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_report.dismiss();
            }
        });

        dialog_report.show();
    }

    @Override
    public void onEditThread(Forum forum) {
        NewThread newThread = new NewThread();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(NewThread.EDIT_THREAD, forum);

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, newThread);

        newThread.setArguments(bundle);
        fragmentTransaction.commit();
    }

    @Override
    public void onLoadLastKey(String last_key) {
        this.last_key = last_key;
    }

    @Override
    public void onForumData(List<Forum> forums) {
        Log.e("asd", "Load - load");
        if (forums != null && !last_node.equals("end")) {
            if (forums.size() > 0) {
                forumLists = forums;
                last_node = forums.get(forums.size() - 1).getFid();
            }

            if (!last_node.equals(last_key)) {
                if (forums.size() < 4 && ITEM_LOAD_COUNT < MAX_DATA && !FCID.equals("0")) {
                    /*
                     * Is a condition where merchant categorized and the result is zero
                     * So it should make a recursion until the last data
                     * If result of query is less then 5, it will recursion. Five is the minimum number of to take data.
                     * If the number of recursion is over the limit, it will stop.
                     * Recursion will occur if category is not general
                     * */
                    isLoading = false;
                    ITEM_LOAD_COUNT += 4;
                    getData();
                    Log.e("asd", ITEM_LOAD_COUNT + " item load count..");
                }
                if (forumLists.size() > 3)
                    forumLists.remove(forumLists.size() - 1);
            } else
                last_node = "end";
            Log.e("asd", last_node);
            isLoading = false;
            threadAdapter.setForumList(forumLists);
            threadAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onMaxData() {
        isLoading = false;
        isMaxData = true;
        Log.e("asd", "List is getting max!");
    }

    @Override
    public void onMaxData(long max_data) {
        this.MAX_DATA = max_data;
        Log.e("asd", "This is max of data : " + MAX_DATA);
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
            storyAdapter.setMerchantMap(merchantStoryMap);
            storyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStoryData(List<MerchantStory> stories) {
        storyList.clear();
        storyList.addAll(stories);
        storyAdapter.setShowCases(stories);
        storyAdapter.notifyDataSetChanged();
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
        frame_loading.setVisibility(View.GONE);
        forumLists.remove(pos);
        threadAdapter.setForumList(forumLists);
        threadAdapter.notifyItemChanged(pos);
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
    public void onSuccessLoadCategory(List<Forum.ForumCategory> forumCategories) {
        categoryList.clear();
        categoryList.addAll(forumCategories);
        categoryAdapter.setCategoryList(forumCategories);
        categoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccessLoadReport(List<Report> reportList) {
        this.reportList.clear();
        this.reportList.addAll(reportList);
        reportAdapter.setReportList(reportList);
        reportAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccessSendReport() {
        Toast.makeText(mContext, mContext.getResources().getString(R.string.report_sent)
                , Toast.LENGTH_SHORT).show();
        dialog_report.dismiss();
    }

    @Override
    public void onHide(int pos) {
        forumLists.remove(pos);
        threadAdapter.setForumList(forumLists);
        threadAdapter.notifyItemChanged(pos);
    }

    @Override
    public void onImageClick(Context context, final int pos) {
        // For story list
        if (pos == 0) {
            addShowCase();
        } else {
            MainActivity.bottomNavigationView.setVisibility(View.GONE);
            MainActivity.floatingActionButton.hide();
            isStoryVisible = true;
            story_pager.setVisibility(View.VISIBLE);

            PagerAdapter pagerAdapter = new FragmentPagerAdapter(getFragmentManager()) {
                @Override
                public Fragment getItem(int i) {
                    Fragment fragment = new StoryFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(StoryFragment.GET_MID, storyList.get(i).getMid());
                    bundle.putInt(StoryFragment.GET_MAX_STORY, storyList.size());
                    fragment.setArguments(bundle);
                    return fragment;
                }

                @Override
                public int getCount() {
                    return storyList.size();
                }
            };

//            PagerAdapter adapter = new InfinitePagerAdapter(pagerAdapter);
//            if (story_pager.getParent() != null) {
//                story_pager = null;
//            }
            story_pager.setAdapter(pagerAdapter);
            story_pager.setPageTransformer(false, new RotateUpTransformer());
            story_pager.setCurrentItem(pos - 1, true);
//            story_pager.setCurrentItem(pos - 1);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_button_search_main_forum:
                changeFragment(new SearchFragment());
                break;
            case R.id.imgBtn_AddThread:
                changeFragment(new NewThread());
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
            case R.id.image_button_favorite_main_forum:
                changeFragment(new FavoriteFragment());
                break;
            case R.id.text_change_category_main_forum:
                MainActivity.floatingActionButton.hide();
                MainActivity.bottomNavigationView.setVisibility(View.GONE);
                isCategoryBottomSheetVisible = true;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                RecyclerView recycler_category = v.findViewById(R.id.recycler_category_bottom_sheet);
                recycler_category.setLayoutManager(new GridLayoutManager(mContext, 3));

                recycler_category.setAdapter(categoryAdapter);
                presenter.loadCategory();

                break;
        }
    }

    @Override
    public void onRefresh() {
        initVar();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPress(boolean check, Context context) {
        if (isStoryVisible) {
            isStoryVisible = false;
//            StoryFragment.storiesProgressView.destroy();
//            story_pager.setVisibility(View.GONE);
            MainActivity.floatingActionButton.show();
            MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
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

    @Override
    public void onClickCategory(int pos) {
        String category;
        if (pos == 0) {
            category = "All Category";
            FCID = "0";
        } else {
            category = categoryList.get(pos - 1).getCategory_name();
            FCID = categoryList.get(pos - 1).getFcid();
        }
        forumLists.clear();
        threadAdapter.setForumList(forumLists);
        threadAdapter.notifyDataSetChanged();
        isMaxData = false;
        isLoading = false;
        ITEM_LOAD_COUNT = 5;
        last_node = "";
        getData();
        categoryAdapter.setPosition(pos);
        text_category.setText("Category: " + category);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
