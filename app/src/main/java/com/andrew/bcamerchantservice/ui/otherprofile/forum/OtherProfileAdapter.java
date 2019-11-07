package com.andrew.bcamerchantservice.ui.otherprofile.forum;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.mainforum.ThreadAdapter;
import com.andrew.bcamerchantservice.ui.newthread.NewThread;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.andrew.bcamerchantservice.utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class OtherProfileAdapter extends RecyclerView.Adapter<OtherProfileAdapter.ViewHolder> {
    private Context context;
    private List<Forum> forumList;
    private Map<String, Merchant> map;
    private PrefConfig prefConfig;
    private Transformation transformation;

    public void setForumMerchantMap(List<Forum> forumList, Map<String, Merchant> map) {
        this.forumList = forumList;
        this.map = map;
    }

    public void setForumList(List<Forum> forumList) {
        this.forumList = forumList;
    }

    public void setMerchantMap(Map<String, Merchant> map) {
        this.map = map;
    }

    public interface onItemClick {
        void onClick(int pos);

        void onDelete(int pos, Forum forum);

        void profileOnClick(int pos, Merchant merchant);

        void onHide(String FID);

        void onShowReport(Merchant merchant, Forum forum);
    }

    private onItemClick onItemClick;

    public OtherProfileAdapter(Context context, List<Forum> forumList, Map<String, Merchant> map
            , onItemClick onItemClick) {
        this.context = context;
        this.map = map;
        this.forumList = forumList;
        this.onItemClick = onItemClick;
        prefConfig = new PrefConfig(context);
        transformation = new RoundedTransformationBuilder()
                .borderColor(context.getResources().getColor(R.color.blue_palette))
                .borderWidthDp(1)
                .cornerRadiusDp(5)
                .oval(false)
                .build();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
//        if (viewType == 0)
//            view = LayoutInflater.from(context).inflate(R.layout.nothing_found, viewGroup, false);
//        else
        view = LayoutInflater.from(context).inflate(R.layout.recycler_other_profile, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return (forumList.size() == 0 ? 0 : 1);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        if (forumList.size() != 0) {
            final int position = viewHolder.getAdapterPosition();
            final Forum forumThread = forumList.get(i);
            final Merchant merchant = map.get(forumThread.getMid());

            viewHolder.title.setText(forumThread.getForum_title());
            viewHolder.view_count.setText(forumThread.getView_count() + "");

            String URL = "";

            if (!forumThread.getForum_thumbnail().isEmpty()) {
                URL = forumThread.getForum_thumbnail();
            } else {
                URL = Constant.SOLID_COLOR;
            }

            Picasso.get()
                    .load(URL)
                    .resize(300, 180)
                    .centerCrop()
                    .transform(transformation)
                    .into(viewHolder.rounded_thumbnail);

            viewHolder.date.setText(forumThread.getForum_date());

            viewHolder.like.setText(String.valueOf(forumThread.getForum_like()));
            viewHolder.content.setText(forumThread.getForum_content());

            viewHolder.rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    onItemClick.onClick(position);
                }
            });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    int pos = viewHolder.getAdapterPosition();
//                    onItemClick.onClick(position);
//                    onItemClick.onClick(position, merchant);

                }
            });

            viewHolder.option_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Context wrapper = new ContextThemeWrapper(context, R.style.PopupMenu);
                    PopupMenu popupMenu = new PopupMenu(wrapper, viewHolder.option_menu);
                    if (forumThread.getMid().equals(prefConfig.getMID())) {
                        popupMenu.inflate(R.menu.option_menu_forum_owner);
                    } else {
                        popupMenu.inflate(R.menu.option_menu_forum_general);
                    }
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.menu_delete:
                                    onItemClick.onDelete(position, forumList.get(position));
                                    break;
                                case R.id.menu_edit:
                                    NewThread newThread = new NewThread();

                                    AppCompatActivity activity = (AppCompatActivity) context;

                                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable(NewThread.EDIT_THREAD, forumList.get(position));

                                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                                    fragmentTransaction.replace(R.id.main_frame, newThread);

                                    newThread.setArguments(bundle);
                                    fragmentTransaction.commit();
                                    break;
                                case R.id.menu_report:
                                    onItemClick.onShowReport(merchant, forumThread);
                                    break;
                                case R.id.menu_hide:
                                    onItemClick.onHide(forumThread.getFid());
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return forumList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, content, like, view_count;
        ImageButton option_menu;
        ImageView rounded_thumbnail;
        RippleView rippleView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.recycler_title_other_profile);
            date = itemView.findViewById(R.id.recycler_date_other_profile);
            content = itemView.findViewById(R.id.recycler_content_other_profile);
            like = itemView.findViewById(R.id.recycler_like_other_profile);
            option_menu = itemView.findViewById(R.id.img_btn_more_other_profile);
            rippleView = itemView.findViewById(R.id.ripple_other_profile);
            view_count = itemView.findViewById(R.id.recycler_view_count_other_profile);
            rounded_thumbnail = itemView.findViewById(R.id.recycler_image_thumbnail_other_profile);
        }
    }
}
