package com.andrew.bcamerchantservice.ui.selectedthread;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.Report;
import com.andrew.bcamerchantservice.ui.main.MainActivity;
import com.andrew.bcamerchantservice.ui.mainforum.ReportAdapter;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.Holder> implements
        ImageSyncAdapter.onImageSyncClicked {
    private static final long DELAY_ANIMATION_START = 600;
    private List<Forum.ForumReply> list;
    private Context context;
    private Boolean check = false, newReplyIsAvailable;
    private ImageButton btnClose, btnDownload;
    private TextView merchantName;
    private ImageView imgFrame;
    private Map<String, Merchant> merchantMap;
    private Map<String, List<Forum.ForumImageReply>> replyImageMap;
    private PrefConfig prefConfig;

    private onReplyClick onReplyClick;

    public void setImageFrame(ImageButton btnClose, ImageButton btnDownload, TextView merchantName, ImageView imgFrame) {
        this.btnClose = btnClose;
        this.btnDownload = btnDownload;
        this.merchantName = merchantName;
        this.imgFrame = imgFrame;
    }

    public void setMerchantMap(Map<String, Merchant> merchantMap) {
        this.merchantMap = merchantMap;
    }

    public void setList(List<Forum.ForumReply> list, Map<String, List<Forum.ForumImageReply>> replyImageMap) {
        this.list = list;
        this.replyImageMap = replyImageMap;
    }

    public void setReplyImageMap(Map<String, List<Forum.ForumImageReply>> replyImageMap) {
        this.replyImageMap = replyImageMap;
    }

    public void setReplyLike(Forum.ForumReply reply, int pos, Map<String, List<Forum.ForumImageReply>> replyImageMap) {
        list.set(pos, reply);
        this.replyImageMap = replyImageMap;
    }

    public void setList(List<Forum.ForumReply> list) {
        this.list = list;
    }

    public interface onReplyClick {
        void onReply(int pos);

        void onReplyLike(int pos);

        void onThreadEdit(int pos);

        void onDelete(int pos);

        void onReport(String FRID);
    }

    public ReplyAdapter(List<Forum.ForumReply> list, Map<String, Merchant> merchantMap, Map<String
            , List<Forum.ForumImageReply>> replyImageMap, Context context, onReplyClick onReplyClick) {
        this.list = list;
        this.merchantMap = merchantMap;
        this.replyImageMap = replyImageMap;
        this.onReplyClick = onReplyClick;
        this.context = context;
        newReplyIsAvailable = false;
        prefConfig = new PrefConfig(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_reply, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        final Forum.ForumReply forumThread = list.get(i);
        final Merchant merchant = merchantMap.get(forumThread.getMid());
        final int position = holder.getAdapterPosition();
        if (merchant != null) {
            holder.merchant_name.setText(merchant.getMerchant_name());
            holder.loc.setText(merchant.getMerchant_location());
            Picasso.get().load(merchant.getMerchant_profile_picture()).into(holder.roundedImageView);
        }

        holder.time.setText(forumThread.getForum_reply_date() + " WIB");
        holder.like.setText(forumThread.getForum_like_amount() + "");
        holder.content.setText(forumThread.getForum_content());

        ImageSyncAdapter imageSyncAdapter;

        if (replyImageMap.containsKey(forumThread.getFrid())) {
            imageSyncAdapter = new ImageSyncAdapter(replyImageMap.get(forumThread.getFrid()), context, this);
        } else {
            imageSyncAdapter = new ImageSyncAdapter(new ArrayList<Forum.ForumImageReply>(), context, this);
        }

        holder.recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        holder.recyclerView.setAdapter(imageSyncAdapter);

        if (forumThread.isLike()) {
            holder.smile.setBackground(context.getResources().getDrawable(R.drawable.smile_press));
        } else
            holder.smile.setBackground(context.getResources().getDrawable(R.drawable.smile));

        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReplyClick.onReply(list.size());
            }
        });

        holder.option_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context wrap = new ContextThemeWrapper(context, R.style.PopupMenu);
                PopupMenu popupMenu = new PopupMenu(wrap, holder.option_menu);
                if (prefConfig.getMID().equals(merchant.getMid())) {
                    popupMenu.inflate(R.menu.option_menu_forum_owner);
                } else popupMenu.inflate(R.menu.option_menu_forum_reply_general);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_delete:
                                int position = SelectedThread.PAGE_NUMBER_STATE * 5 + holder.getAdapterPosition();
                                onReplyClick.onDelete(position);
//                                deleteAnimation(holder.itemView);
                                break;
//                            case R.id.menu_dont_show:
////                                onAccountDelete.accountDelete(forumThread.getUsername());
//                                Toast.makeText(context, context.getResources().getString(R.string.thread_not_appear), Toast.LENGTH_SHORT).show();
//                                break;
                            case R.id.menu_edit:
                                onReplyClick.onThreadEdit(SelectedThread.PAGE_NUMBER_STATE * 5 + holder.getAdapterPosition());
                                break;
//                            case R.id.menu_hide:
//                                int position_hide = SelectedThread.PAGE_NUMBER_STATE * 5 + holder.getAdapterPosition();
//                                onReplyDelete.onDelete(position_hide);
//                                deleteAnimation(holder.itemView);
//                                Toast.makeText(context, context.getResources().getString(R.string.thread_hidden), Toast.LENGTH_SHORT).show();
//                                break;
                            case R.id.menu_report:
                                onReplyClick.onReport(forumThread.getFrid());
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        holder.smile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReplyClick.onReplyLike(position);
                if (forumThread.isLike()) {
                    forumThread.setForum_like_amount(forumThread.getForum_like_amount() - 1);
                    forumThread.setLike(false);
                } else {
                    forumThread.setLike(true);
                    forumThread.setForum_like_amount(forumThread.getForum_like_amount() + 1);
                }
                notifyDataSetChanged();
            }
        });

        setAnimation(holder.itemView, position, holder.linearLayout);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView merchant_name, loc, time, like, content;
        ImageButton option_menu, smile;
        Button reply;
        RoundedImageView roundedImageView;
        RecyclerView recyclerView;
        LinearLayout linearLayout;

        Holder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.ll_recyclerview_reply);
            recyclerView = itemView.findViewById(R.id.recycler_img_inside_reply);
            merchant_name = itemView.findViewById(R.id.merchantName_Recycler);
            loc = itemView.findViewById(R.id.merchantLoc_Recycler);
            time = itemView.findViewById(R.id.time_recycler);
            like = itemView.findViewById(R.id.smile_amount_recycler);
            option_menu = itemView.findViewById(R.id.thread_more_recycler);
            reply = itemView.findViewById(R.id.btn_reply_recycler);
            smile = itemView.findViewById(R.id.img_smile_recycler);
            content = itemView.findViewById(R.id.content_recycler);
            roundedImageView = itemView.findViewById(R.id.merchantPic_Recycler);
        }
    }

//    private void hideAccount(String username) {
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).getUsername().toLowerCase().trim().equals(username.toLowerCase().trim())) {
//                int position = SelectedThread.PAGE_NUMBER_STATE * 5 + i;
//                onReplyDelete.onDelete(position);
//            }
//        }
//    }

    private boolean isChecked(List<Report> list) {
        for (Report reportIsChecked : list) {
            if (reportIsChecked.isReport_is_checked()) return true;
        }
        return false;
    }

    private void downloadImage(Forum.ForumImageReply reply) {
        Toast.makeText(context, context.getResources().getString(R.string.download_success), Toast.LENGTH_SHORT).show();
        MediaStore.Images.Media.insertImage(context.getContentResolver(), ((BitmapDrawable) imgFrame.getDrawable()).getBitmap()
                , reply.getImage_name(), "");
    }

    private void deleteAnimation(View v) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        v.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 400);
    }

    private void setAnimation(View v, int pos, LinearLayout linearLayout) {
        int lastPosition = list.size() - 1;
        if (pos >= lastPosition && newReplyIsAvailable) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up_recyclerview);
            final ValueAnimator animator = ObjectAnimator
                    .ofInt(linearLayout
                            , "backgroundColor"
                            , context.getResources().getColor(R.color.blue_palette)
                            , context.getResources().getColor(R.color.soft_blue_palette));
            animator.setDuration(300);
            animator.setEvaluator(new ArgbEvaluator());
            animator.setRepeatCount(9);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            v.startAnimation(animation);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animator.start();
                }
            }, DELAY_ANIMATION_START);
            newReplyIsAvailable = false;
        }
    }

    @Override
    public void imageIsClicked(final Forum.ForumImageReply reply) {
        SelectedThread.frameLayout.setVisibility(View.VISIBLE);
        SelectedThread.frameIsVisible = true;
        MainActivity.bottomNavigationView.setVisibility(View.GONE);

        SelectedThread.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectedThread.frameIsVisible = false;
                MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                SelectedThread.frameLayout.setVisibility(View.GONE);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectedThread.frameIsVisible = false;
                MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                SelectedThread.frameLayout.setVisibility(View.GONE);
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ((Activity) context).requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                Constant.PERMISSION_WRITE_EXTERNAL);
                    }
                } else {
                    downloadImage(reply);
                }
            }
        });

        Picasso.get().load(reply.getImage_url()).into(imgFrame);

//        merchantName.setText("Merchant Name : " + mer);
    }
}
