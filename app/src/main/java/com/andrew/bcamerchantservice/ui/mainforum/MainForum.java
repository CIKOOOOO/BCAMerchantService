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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private View v, codeView;
    private RecyclerView thread_recycler_view;
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
    private NestedScrollView nestedScrollView;
    private ReportAdapter reportAdapter;

    private IForumPresenter presenter;

    private List<Forum.ForumCategory> categoryList;
    private List<Report> reportList;
    private Map<String, Merchant> merchantMap, merchantStoryMap;

    private boolean check;

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
        thread_recycler_view.destroyDrawingCache();
        showcase_recycler_view.destroyDrawingCache();
        super.onDestroyView();
    }

    private void initVar() {
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
        thread_recycler_view = v.findViewById(R.id.recycler_thread_main_forum);
        swipeRefreshLayout = v.findViewById(R.id.swipe);
        frame_loading = v.findViewById(R.id.frame_loading_main_forum);
        text_category = v.findViewById(R.id.text_category_main_forum);
        nestedScrollView = v.findViewById(R.id.nested_scroll_main_forum);
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

        presenter.loadForum(prefConfig.getMID(), "0");
        presenter.loadShowCase();

        linearLayout.setOnClickListener(this);
        new_thread.setOnClickListener(this);
        search.setOnClickListener(this);
        favorite.setOnClickListener(this);
        text_change.setOnClickListener(this);

        frame_loading.setOnClickListener(this);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                MainActivity.floatingActionButton.show();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setAdapter() {
        storyAdapter = new StoryAdapter(mContext, storyList, true, merchantStoryMap, this);
        showcase_recycler_view.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        showcase_recycler_view.setAdapter(storyAdapter);

        threadAdapter = new ThreadAdapter(mContext, forumLists, merchantMap, this);
        thread_recycler_view.setLayoutManager(threadLayoutManager);
        thread_recycler_view.setAdapter(threadAdapter);
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
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        Bundle bundle = new Bundle();
        if (!merchant.getMid().equals(prefConfig.getMID())) {
            OtherProfile otherProfile = new OtherProfile();
            otherProfile.setArguments(bundle);
            bundle.putParcelable(OtherProfile.GET_DATA_PROFILE, merchant);
            fragmentTransaction.replace(R.id.main_frame, otherProfile);
        } else {
            fragmentTransaction.replace(R.id.main_frame, new Profile());
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onHide(final String FID, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Apa Anda yakin untuk menyembunyikan forum berjudul " + title + " ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        presenter.onHide(FID, prefConfig.getMID());

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
            presenter.loadForum(prefConfig.getMID(), "0");
        } else {
            category = categoryList.get(pos - 1).getCategory_name();
            presenter.loadForum(prefConfig.getMID(), categoryList.get(pos - 1).getFcid());
        }
        categoryAdapter.setPosition(pos);
        text_category.setText("Category: " + category);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
