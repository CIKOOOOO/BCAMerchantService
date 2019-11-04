package com.andrew.bcamerchantservice.ui.selectedthread;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.Report;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.mainforum.MainForum;
import com.andrew.bcamerchantservice.ui.mainforum.ReportAdapter;
import com.andrew.bcamerchantservice.ui.newthread.NewThread;
import com.andrew.bcamerchantservice.ui.otherprofile.OtherProfile;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.DecodeBitmap;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.andrew.bcamerchantservice.utils.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mlsdev.animatedrv.AnimatedRecyclerView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedThread extends Fragment implements ISelectedThreadView, View.OnClickListener
        , ReplyAdapter.onEdit, ReplyAdapter.onReplyDelete, ReplyAdapter.onReplyClick
        , PageNumberAdapter.pageNumber, MainActivity.onBackPressFragment, ImageGridAdapter.imageOnClick
        , ImagePickerAdapter.onItemClick, PopupMenu.OnMenuItemClickListener {

    public static final String GET_THREAD_OBJECT = "thread_object";
    public static final String GET_MERCHANT = "merchant_profile";

    public static int PAGE_NUMBER_STATE = 0;
    public static boolean trendingIsVisible, frameIsVisible;
    public static FrameLayout frameLayout;

    private static Map<String, List<Forum.ForumImageReply>> forumImageReplyMap;

    private static final int STATE_LINEAR_VERTICAL = 1;
    private static final int STATE_GRID = 2;
    private static final int STATE_LINEAR_HORIZONTAL = 3;
    private static final int AMOUNT_REPLY = 5;

    @SuppressLint("StaticFieldLeak")
    private static List<ImagePicker> imageList, imageReply;

    private View v;
    private LinearLayout reply_linear;
    private RecyclerView recycler_main_forum, recycler_img_reply, recycler_page_number, recycler_page_number2;
    private AnimatedRecyclerView recycler_reply;
    private TextView amount_like, merchant_name, text_category;
    private ImageButton img_more, frame_close, img_download, img_like, img_favorite;
    private NestedScrollView scrollView;
    private ReplyAdapter replyAdapter;
    private ImagePickerAdapter pickerAdapter;
    private EditText etReply;
    private PrefConfig prefConfig;
    private PageNumberAdapter pageNumberAdapter;
    private Context mContext;
    private Activity mActivity;
    private ImageView img_frame_selected, img_profile;
    private Forum forum;
    private Merchant merchant;
    private DatabaseReference dbRef;
    private ImageGridAdapter imageGridAdapter;
    private RelativeLayout relative_page_number, relative_page_number2;
    private FrameLayout frame_loading;

    private ISelectedThreadPresenter presenter;

    private List<Forum> threadList;
    private List<Forum.ForumImage> forumImageList;
    private List<Forum.ForumReply> replyList;
    private Map<String, Merchant> merchantMap;

    private boolean isLike, isCheck, isReply, isFavorite;

    public SelectedThread() {
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
        v = inflater.inflate(R.layout.fragment_selected_thread, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        trendingIsVisible = false;
        isFavorite = false;
        frameIsVisible = false;
        isCheck = false;
        isReply = false;
        PAGE_NUMBER_STATE = 0;
        prefConfig = new PrefConfig(mContext);
        isLike = false;
        dbRef = FirebaseDatabase.getInstance().getReference();
        presenter = new SelectedThreadPresenter(this);

        ImageButton gallery_opener = v.findViewById(R.id.gallery_opener_reply);
        ImageButton after = v.findViewById(R.id.btn_after_reply);
        ImageButton after2 = v.findViewById(R.id.btn_after_reply2);
        ImageButton before = v.findViewById(R.id.btn_before_reply);
        ImageButton before2 = v.findViewById(R.id.btn_before_reply2);
        ImageButton favorite = v.findViewById(R.id.image_button_favorite_selected_thread);
        ImageButton back = v.findViewById(R.id.img_btn_back_selected_thread);
        Button reply = v.findViewById(R.id.btn_reply_thread);
        Button send = v.findViewById(R.id.btn_send_selected);
        TextView first_page = v.findViewById(R.id.btn_first_page_reply);
        TextView first_page2 = v.findViewById(R.id.btn_first_page_reply2);
        TextView last_page = v.findViewById(R.id.btn_end_reply);
        TextView last_page2 = v.findViewById(R.id.btn_end_reply2);

        img_like = v.findViewById(R.id.img_smile_thread);
        scrollView = v.findViewById(R.id.scrollView_SelectedThread);
        etReply = v.findViewById(R.id.edit_reply_selected_thread);
        img_more = v.findViewById(R.id.thread_more_selected);
        reply_linear = v.findViewById(R.id.linear_reply);
        recycler_main_forum = v.findViewById(R.id.recycler_img_selected);
        recycler_reply = v.findViewById(R.id.recycler_reply);
        recycler_img_reply = v.findViewById(R.id.recycler_img_reply);
        recycler_page_number = v.findViewById(R.id.recycler_page_number);
        recycler_page_number2 = v.findViewById(R.id.recycler_page_number2);
        frameLayout = v.findViewById(R.id.frame_selected_thread);
        frame_close = v.findViewById(R.id.btn_close_frame_selected);
        img_download = v.findViewById(R.id.download_image_frame_selected);
        img_frame_selected = v.findViewById(R.id.image_frame_selected);
        merchant_name = v.findViewById(R.id.name_frame_selected_thread);
        amount_like = v.findViewById(R.id.smile_amount_thread);
        img_profile = v.findViewById(R.id.image_profile_selected_thread);
        relative_page_number = v.findViewById(R.id.relative_page_number);
        relative_page_number2 = v.findViewById(R.id.relative_page_number2);
        frame_loading = v.findViewById(R.id.frame_loading_selected_thread);
        text_category = v.findViewById(R.id.text_category_selected_thread);
        img_favorite = v.findViewById(R.id.image_button_favorite_selected_thread);

        frameLayout.getBackground().setAlpha(Constant.MAX_ALPHA);
        frame_loading.getBackground().setAlpha(Constant.MAX_ALPHA);

        threadList = new ArrayList<>();
        imageList = new ArrayList<>();
        imageReply = new ArrayList<>();
        forumImageList = new ArrayList<>();
        replyList = new ArrayList<>();

        merchantMap = new HashMap<>();
        forumImageReplyMap = new HashMap<>();

        setAdapter();

        reply.setOnClickListener(this);
        send.setOnClickListener(this);
        gallery_opener.setOnClickListener(this);
        favorite.setOnClickListener(this);
        back.setOnClickListener(this);

        frameLayout.setOnClickListener(this);
        frame_close.setOnClickListener(this);
        img_download.setOnClickListener(this);
        img_like.setOnClickListener(this);
        img_more.setOnClickListener(this);
        img_favorite.setOnClickListener(this);

        scrollView.setSmoothScrollingEnabled(true);
        scrollView.setNestedScrollingEnabled(false);

        after.setOnClickListener(this);
        before.setOnClickListener(this);
        first_page.setOnClickListener(this);
        last_page.setOnClickListener(this);

        first_page2.setOnClickListener(this);
        before2.setOnClickListener(this);
        after2.setOnClickListener(this);
        last_page2.setOnClickListener(this);

        new AsyncTasks().doInBackground();
    }

    @Override
    public void onUpdateMerchant(Map<String, Merchant> merchantMap) {
        this.merchantMap.clear();
        this.merchantMap.putAll(merchantMap);
        replyAdapter.setMerchantMap(merchantMap);
        replyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadImageForum(List<Forum.ForumImage> imageList) {
        forumImageList.clear();
        forumImageList.addAll(imageList);
        imageGridAdapter.setForumImageList(imageList);
        imageGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccessReply() {
        frame_loading.setVisibility(View.GONE);
        etReply.setText("");
        if (imageReply.size() > 0) {
            imageReply.clear();
            pickerAdapter.setImageList(imageReply);
        }
        setList(getLastPosition());
    }

    @Override
    public void onSuccessDelete(int pos) {
        int PAGE_POSITION = pos / AMOUNT_REPLY;
        if (replyList.size() > pos) setDeletedList(PAGE_POSITION);
        else if (PAGE_POSITION - 1 < 0) setDeletedList(PAGE_POSITION);
        else setDeletedList(PAGE_POSITION - 1);
    }

    @Override
    public void onGetForum(Forum.ForumCategory forumCategory) {
        text_category.setText("Kategori : " + forumCategory.getCategory_name());
    }

    @Override
    public void onLoadReply(boolean isLike, Forum forum, List<Forum.ForumReply> forumReplies, Map<String, List<Forum.ForumImageReply>> map) {
        this.forum = forum;
        amount_like.setText(forum.getForum_like() + "");
        replyList.clear();
        forumImageReplyMap.clear();

        if (isLike) {
            img_like.setBackground(mContext.getResources().getDrawable(R.drawable.smile_press));
        } else {
            img_like.setBackground(mContext.getResources().getDrawable(R.drawable.smile));
        }

        forumImageReplyMap.putAll(map);
        replyList.addAll(forumReplies);
        presenter.getCategoryName(forum.getFcid());

        replyAdapter.setList(forumReplies, map);
        replyAdapter.notifyDataSetChanged();

        if (replyList.size() > 0) {
            relative_page_number.setVisibility(View.VISIBLE);
            relative_page_number2.setVisibility(View.VISIBLE);
            if (isReply) {
                isReply = false;
            } else setList(PAGE_NUMBER_STATE);
        } else {
            relative_page_number.setVisibility(View.GONE);
            relative_page_number2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
        if (isFavorite) {
            img_favorite.setBackground(mContext.getResources().getDrawable(R.drawable.icon_love_fill));
        } else
            img_favorite.setBackground(mContext.getResources().getDrawable(R.drawable.icon_love));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_smile_thread:
                Map<String, Object> map = new HashMap<>();
                if (!isLike) {
                    img_like.setBackground(mContext.getResources().getDrawable(R.drawable.smile_press));
                    amount_like.setText(Integer.parseInt(amount_like.getText().toString()) + 1 + "");
                    isLike = true;

                    map.put("like", true);
                    map.put("forum_like", forum.getForum_like() + 1);

                    Map<String, Object> maps = new HashMap<>();
                    maps.put("forum_like_time", Utils.getTime("EEEE, dd-MM-yyyy HH:mm"));
                    maps.put("forum_like_mid", prefConfig.getMID());

                    String path = Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/forum_like_by/" + prefConfig.getMID();
                    presenter.onUpdateLike(path, maps);
                } else {
                    img_like.setBackground(mContext.getResources().getDrawable(R.drawable.smile));
                    amount_like.setText(Integer.parseInt(amount_like.getText().toString()) - 1 + "");
                    isLike = false;
                    map.put("like", false);
                    map.put("forum_like", forum.getForum_like() - 1);

                    String path = Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/forum_like_by/" + prefConfig.getMID();
                    presenter.onRemove(path);
                }
                presenter.onUpdateLike(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid(), map);
                break;
            case R.id.thread_more_selected:
                Context option_menu = new ContextThemeWrapper(view.getContext(), R.style.PopupMenu);
                PopupMenu popupMenu = new PopupMenu(option_menu, img_more);
                if (merchant.getMid().equals(prefConfig.getMID())) {
                    popupMenu.inflate(R.menu.option_menu_forum_owner);
                } else popupMenu.inflate(R.menu.option_menu_forum_general);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
            case R.id.btn_reply_thread:
                scrollView.smoothScrollTo(0, reply_linear.getBottom());
                break;
            case R.id.gallery_opener_reply:
                if (ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            , Constant.PERMISSION_READ_GALLERY_EXTERNAL);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                }
                break;
            case R.id.btn_send_selected:
                if (etReply.getText().toString().trim().isEmpty()) {
                    scrollView.smoothScrollTo(scrollView.getScrollX(), reply_linear.getTop());
                    etReply.requestFocus();
                    etReply.setError("This field cannot be empty");
                } else {
                    frame_loading.setVisibility(View.VISIBLE);
                    final String key = dbRef.push().getKey();
                    Forum.ForumReply forumReply = new Forum.ForumReply(key, prefConfig.getMID()
                            , etReply.getText().toString(), Utils.getTime("EEEE, dd/MM/yyyy HH:mm")
                            , 0, false);

                    String path = Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/" + Constant.DB_REFERENCE_FORUM_REPLY + "/" + key;
                    if (imageReply.size() == 0) {
                        /*
                         * Untuk mengirim reply ke firebase
                         * Dikirim menurut urutan tree nya, berdasarkan Forum -> Forum ID -> Forum Reply -> Forum Reply ID -> Value
                         * */
                        presenter.onReply(path, forumReply);
                    } else {
                        presenter.onReply(path, forum.getFid(), key, prefConfig.getMID(), forumReply, imageReply);
                    }
                }
                break;
            case R.id.btn_first_page_reply:
            case R.id.btn_first_page_reply2:
                setList(0);
                scrollView.smoothScrollTo(0, recycler_reply.getTop());
                break;
            case R.id.btn_end_reply:
            case R.id.btn_end_reply2:
                setList(pageNumberAdapter.getPageList() - 1);
                scrollView.smoothScrollTo(0, recycler_reply.getTop());
                break;
            case R.id.btn_before_reply:
            case R.id.btn_before_reply2:
                if (pageNumberAdapter.getPage_active() - 1 < 0) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.start_of_thread), Toast.LENGTH_SHORT).show();
                    break;
                }
                setList(pageNumberAdapter.getPage_active() - 1);
                break;
            case R.id.btn_after_reply:
            case R.id.btn_after_reply2:
                if (pageNumberAdapter.getPage_active() + 1 >= pageNumberAdapter.getPageList()) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.end_of_thread), Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    setList(pageNumberAdapter.getPage_active() + 1);
                    scrollView.smoothScrollTo(0, recycler_reply.getTop());
                }
                break;
            case R.id.frame_selected_thread:
            case R.id.btn_close_frame_selected:
                frameLayout.setVisibility(View.GONE);
                frameIsVisible = false;
                MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                break;
            case R.id.download_image_frame_selected:
                if (ActivityCompat.checkSelfPermission(mContext
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                            , Constant.PERMISSION_WRITE_EXTERNAL);
                } else {
                    Bitmap bitmap = ((BitmapDrawable) img_frame_selected.getDrawable()).getBitmap();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.download_success), Toast.LENGTH_SHORT).show();
                    MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bitmap
                            , merchant_name.getText().toString(), "");
                }
                break;
            case R.id.image_button_favorite_selected_thread:
                if (!isFavorite)
                    presenter.onFavoriteThread(prefConfig.getMID(), forum.getFid());
                else
                    presenter.onRemoveFavoriteThread(prefConfig.getMID(), forum.getFid());
                break;
            case R.id.img_btn_back_selected_thread:
                FragmentManager fragmentManager = getFragmentManager();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.main_frame, new MainForum());

                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onThreadEdit(int pos) {
        Forum.ForumReply thread = replyList.get(pos);
        NewThread newThread = new NewThread();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(NewThread.EDIT_THREAD_REPLY, thread);
        bundle.putParcelable(NewThread.EDIT_THREAD_REPLY_BACK, forum);
        bundle.putParcelable(NewThread.EDIT_THREAD_MERCHANT, merchant);

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, newThread);

        newThread.setArguments(bundle);
        fragmentTransaction.commit();
    }

    @Override
    public void onDelete(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Apa Anda yakin untuk menghapus balasan thread ini?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        Forum.ForumReply forumReply = replyList.get(pos);
                        String path = Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/"
                                + Constant.DB_REFERENCE_FORUM_REPLY + "/" + forumReply.getFrid();
                        presenter.onRemove(path, pos);
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.thread_deleted), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    @Override
    public void onReply(int pos) {
        scrollView.smoothScrollTo(0, reply_linear.getBottom());
    }

    @Override
    public void onReplyLike(int pos) {
        int position;
        if (pageNumberAdapter.getPage_active() == 0) {
            position = pos;
        } else {
            position = (pageNumberAdapter.getPage_active() * AMOUNT_REPLY) + pos;
        }
        Forum.ForumReply forumReply = replyList.get(position);
        Map<String, Object> map = new HashMap<>();
        if (!forumReply.isLike()) {
            map.put("like", true);
            map.put("forum_like_amount", forumReply.getForum_like_amount() + 1);
            String path = Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/forum_reply/" + forumReply.getFrid();
            presenter.onUpdateLike(path, map);

            Map<String, Object> maps = new HashMap<>();
            maps.put("forum_like_time", Utils.getTime("EEEE, dd-MM-yyyy HH:mm"));
            maps.put("forum_like_mid", prefConfig.getMID());

            String secondPath = Constant.DB_REFERENCE_FORUM + "/" + forum.getFid() + "/forum_reply/"
                    + forumReply.getFrid() + "/forum_like_by/" + prefConfig.getMID();
            presenter.onUpdateLike(secondPath, maps);
        } else {
            map.put("like", false);
            map.put("forum_like_amount", forumReply.getForum_like_amount() - 1);
            presenter.onUpdateLike(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid()
                    + "/forum_reply/" + forumReply.getFrid(), map);
            presenter.onRemove(Constant.DB_REFERENCE_FORUM + "/" + forum.getFid()
                    + "/forum_reply/" + forumReply.getFrid() + "/forum_like_by/" + prefConfig.getMID());
        }
    }

    @Override
    public void onClick(int pos) {
        setList(pos);
    }

    @Override
    public void onBackPress(boolean check, Context context) {
    }

    @Override
    public void imageOnClick(int pos) {
        frameIsVisible = true;
        frameLayout.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(forumImageList.get(pos).getImage_url())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(img_frame_selected);
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    public void onItemClicked(int pos, String states) {
        if (states.equals(ImagePickerAdapter.STATES_CLICKED_DELETE)) {
            imageReply.remove(pos);
            pickerAdapter.setImageList(imageReply);
        } else {
            frameLayout.setVisibility(View.VISIBLE);
            frameIsVisible = true;
            merchant_name.setText("Merchant Name : " + merchant.getMerchant_name());
            DecodeBitmap.setScaledImageView(img_frame_selected, imageList.get(pos).getImg_static(), mContext);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Apa anda yakin untuk menghapus thread ini?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String path = Constant.DB_REFERENCE_FORUM + "/" + forum.getFid();
                        presenter.onRemove(path);
                        MainForum mainForum = new MainForum();

                        FragmentManager manager = getFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();

                        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.replace(R.id.main_frame, mainForum);

                        transaction.commit();
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.thread_deleted), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

                break;
            case R.id.menu_edit:
                if (forum != null) {
                    NewThread newThread = new NewThread();

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(NewThread.EDIT_THREAD_SELECTED, forum);

                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.replace(R.id.main_frame, newThread);

                    newThread.setArguments(bundle);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.menu_report:
                final List<Report> reportList = new ArrayList<>();
                reportList.addAll(Constant.getReport());

                AlertDialog.Builder codeBuilder = new AlertDialog.Builder(mContext);
                final View codeView = LayoutInflater.from(mContext).inflate(R.layout.custom_report, null);

                final TextView error = codeView.findViewById(R.id.show_error_content_report);
                final EditText content = codeView.findViewById(R.id.etOther_Report);
                final CheckBox checkBox = codeView.findViewById(R.id.check_other);
                ReportAdapter reportAdapter = new ReportAdapter(reportList, codeView.getContext());

                TextView name = codeView.findViewById(R.id.report_name);
                TextView thread = codeView.findViewById(R.id.report_title);
                TextView threadTitle = codeView.findViewById(R.id.report_tv_title);
                Button send = codeView.findViewById(R.id.btnSubmit_Report);
                Button cancel = codeView.findViewById(R.id.btnCancel_Report);
                RecyclerView recyclerView = codeView.findViewById(R.id.recycler_checkbox_report);

                recyclerView.setLayoutManager(new GridLayoutManager(codeView.getContext(), 2));

                codeBuilder.setView(codeView);
                final AlertDialog codeAlert = codeBuilder.create();

                name.setText(merchant.getMerchant_name());
                thread.setText(forum.getForum_title());

                recyclerView.setAdapter(reportAdapter);

                thread.setVisibility(View.GONE);
                threadTitle.setVisibility(View.GONE);
                content.setEnabled(false);

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isCheck) {
                            content.setBackground(codeView.getContext().getDrawable(R.drawable.background_stroke_white));
                            isCheck = true;
                            content.setEnabled(true);
                        } else {
                            content.setBackground(codeView.getContext().getDrawable(R.drawable.background_grey));
                            isCheck = false;
                            content.setEnabled(false);
                        }
                    }
                });

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        error.setVisibility(View.GONE);
                        if (isCheck) {
                            if (content.getText().toString().isEmpty())
                                error.setVisibility(View.VISIBLE);
                            else {
                                Toast.makeText(codeView.getContext(), codeView.getContext().getResources().getString(R.string.report_sent)
                                        , Toast.LENGTH_SHORT).show();
                                codeAlert.dismiss();
                            }
                        } else if (isChecked(reportList)) {
                            Toast.makeText(codeView.getContext(), codeView.getContext().getResources().getString(R.string.report_sent)
                                    , Toast.LENGTH_SHORT).show();
                            codeAlert.dismiss();
                        } else {
                            error.setVisibility(View.VISIBLE);
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        codeAlert.dismiss();
                    }
                });

                codeAlert.show();
                break;
        }
        return false;
    }

    private class AsyncTasks extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            TextView merchant_name = v.findViewById(R.id.merchantName_Selected);
            TextView merchant_loc = v.findViewById(R.id.merchantLoc_Selected);
            TextView thread_title = v.findViewById(R.id.title_selected);
            TextView thread_time = v.findViewById(R.id.time_selected);
            TextView reply_to = v.findViewById(R.id.reply_to);
            TextView content = v.findViewById(R.id.content_selected);

            Bundle bundles = getArguments();
            if (bundles != null) {
                if (bundles.getParcelable(GET_THREAD_OBJECT) != null) {
                    /*
                     * Disini kita mendapatkan object yang dibungkus oleh parcelable dari Main Forum
                     * Agar data yang diklik sebelumnya bisa ter-show
                     * */
                    forum = bundles.getParcelable(GET_THREAD_OBJECT);
                    thread_title.setText(forum.getForum_title());
                    content.setText(forum.getForum_content());
                    thread_time.setText(forum.getForum_date() + " WIB");
                    amount_like.setText(forum.getForum_like() + "");

                    presenter.onLoadReplyData(dbRef, forum, prefConfig.getMID());

                    Utils.hideSoftKeyboard(mActivity);
                }

                if (bundles.getParcelable(GET_MERCHANT) != null) {
                    merchant = bundles.getParcelable(GET_MERCHANT);
                    Picasso.get().load(merchant.getMerchant_profile_picture()).into(img_profile);
                    merchant_name.setText(merchant.getMerchant_name());
                    reply_to.setText(merchant.getMerchant_name());
                    merchant_loc.setText(merchant.getMerchant_location());
                }
            }

            presenter.onCheckFavorite(prefConfig.getMID(), forum.getFid());
            return null;
        }
    }

    private int getPageNumber(int reply_amount) {
        int pageAll = reply_amount / AMOUNT_REPLY;
        if (reply_amount % AMOUNT_REPLY > 0) {
            pageAll++;
        }
        return pageAll;
    }

    private void setList(int pos) {
        List<Forum.ForumReply> threads = new ArrayList<>();
        PAGE_NUMBER_STATE = pos;
        int thread_start = PAGE_NUMBER_STATE * AMOUNT_REPLY;
        int thread_end = thread_start + AMOUNT_REPLY;
        for (int i = thread_start; i < thread_end; i++) {
            if (i >= replyList.size()) break;
            threads.add(replyList.get(i));
        }
        pageNumberAdapter.setPage_active(PAGE_NUMBER_STATE);
        replyAdapter.setList(threads);
        replyAdapter.notifyDataSetChanged();
        if (pageNumberAdapter.getPageList() != getPageNumber(replyList.size())) {
            pageNumberAdapter.setPageList(getPageNumber(replyList.size()));
        }
    }

    private void setDeletedList(int pos) {
        List<Forum.ForumReply> threads = new ArrayList<>();
        PAGE_NUMBER_STATE = pos;
        int thread_start = PAGE_NUMBER_STATE * AMOUNT_REPLY;
        int thread_end = thread_start + AMOUNT_REPLY;
        for (int i = thread_start; i < thread_end; i++) {
            if (i >= replyList.size()) break;
            threads.add(replyList.get(i));
        }
        pageNumberAdapter.setPage_active(PAGE_NUMBER_STATE);
        replyAdapter.setList(replyList);
        if (pageNumberAdapter.getPageList() != getPageNumber(replyList.size())) {
            pageNumberAdapter.setPageList(getPageNumber(replyList.size()));
        }
    }

    private int getLastPosition() {
        if (replyList.size() / AMOUNT_REPLY == 0) {
            return 0;
        } else if (replyList.size() % AMOUNT_REPLY == 0) {
            return replyList.size() / AMOUNT_REPLY == 0 ? 0 : (replyList.size() / AMOUNT_REPLY) - 1;
        } else {
            return replyList.size() / AMOUNT_REPLY;
        }
    }

    private void setLayoutManager(RecyclerView recyclerView, RecyclerView.Adapter adapter, int STATE) {
        if (STATE == STATE_LINEAR_VERTICAL)
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        else if (STATE == STATE_LINEAR_HORIZONTAL)
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        else recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setAdapter(adapter);
    }

    private void setAdapter() {
        imageGridAdapter = new ImageGridAdapter(mContext, forumImageList, this);
        setLayoutManager(recycler_main_forum, imageGridAdapter, STATE_GRID);

        replyAdapter = new ReplyAdapter(replyList, merchantMap, forumImageReplyMap, mContext, this, this, this);
        setLayoutManager(recycler_reply, replyAdapter, STATE_LINEAR_VERTICAL);

        pickerAdapter = new ImagePickerAdapter(mContext, imageReply, this, "");
        setLayoutManager(recycler_img_reply, pickerAdapter, STATE_LINEAR_HORIZONTAL);

        pageNumberAdapter = new PageNumberAdapter(mContext, getPageNumber(threadList.size()), this);
        setLayoutManager(recycler_page_number, pageNumberAdapter, STATE_LINEAR_HORIZONTAL);
        setLayoutManager(recycler_page_number2, pageNumberAdapter, STATE_LINEAR_HORIZONTAL);

        replyAdapter.setImageFrame(frame_close, img_download, merchant_name, img_frame_selected);
    }

    private boolean isChecked(List<Report> list) {
        for (Report reportIsChecked : list) {
            if (reportIsChecked.isReport_checked()) return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
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
            case Constant.PERMISSION_WRITE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Bitmap bitmap = ((BitmapDrawable) img_frame_selected.getDrawable()).getBitmap();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.download_success), Toast.LENGTH_SHORT).show();
                    MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bitmap
                            , merchant_name.getText().toString(), "");
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
            if (requestCode == Constant.ACTIVITY_CHOOSE_IMAGE) {
                Bitmap bitmap;
                if (data.getData() != null) {
                    Uri targetUri = data.getData();
                    File f = new File("" + targetUri);
                    try {
                        bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(targetUri));
                        imageReply.add(new ImagePicker(DecodeBitmap.compressBitmap(bitmap), "IMG", f.getName()));
                        pickerAdapter.setImageList(imageReply);
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
                            imageReply.add(new ImagePicker(DecodeBitmap.compressBitmap(bitmap), "IMG", f.getName()));
                            pickerAdapter.setImageList(imageReply);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
