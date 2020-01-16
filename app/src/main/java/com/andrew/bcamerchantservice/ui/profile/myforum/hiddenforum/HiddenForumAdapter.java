package com.andrew.bcamerchantservice.ui.profile.myforum.hiddenforum;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class HiddenForumAdapter extends RecyclerView.Adapter<HiddenForumAdapter.Holder> {

    private Context mContext;
    private List<Forum> forumList;
    private Map<String, Merchant> merchantMap;
    private forumOnClick forumOnClick;
    private Transformation transformation;

    public void setForumList(List<Forum> forumList) {
        this.forumList = forumList;
    }

    public void setMerchantMap(Map<String, Merchant> merchantMap) {
        this.merchantMap = merchantMap;
    }

    public interface forumOnClick {
        void onClick(Forum forum, Merchant merchant);

        void onUnHide(String FID);
    }

    public HiddenForumAdapter(Context mContext, List<Forum> forumList, Map<String, Merchant> merchantMap, HiddenForumAdapter.forumOnClick forumOnClick) {
        this.mContext = mContext;
        this.forumList = forumList;
        this.merchantMap = merchantMap;
        this.forumOnClick = forumOnClick;
        transformation = new RoundedTransformationBuilder()
                .borderColor(mContext.getResources().getColor(R.color.blue_palette))
                .borderWidthDp(1)
                .cornerRadiusDp(5)
                .oval(false)
                .build();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_hidden_forum, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final int pos = holder.getAdapterPosition();

        final Forum forum = forumList.get(pos);

        if (merchantMap.get(forum.getMid()) != null) {
            Merchant merchant = merchantMap.get(forum.getMid());
            Picasso.get()
                    .load(merchant.getMerchant_profile_picture())
                    .into(holder.image_profile);
            holder.username.setText(merchant.getMerchant_name());
        }

        try {
            holder.time.setText(Utils.formatDateFromDateString("EEEE, dd/MM/yyyy HH:mm", "dd MMM yyyy HH:mm", forum.getForum_date()) + " WIB");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.count_view.setText(forum.getView_count() + "");
        holder.count_smile.setText(forum.getForum_like() + "");
        holder.forum_title.setText(forum.getForum_title());
        holder.forum_content.setText(forum.getForum_content());

        String URL = forum.getForum_thumbnail().isEmpty() ? Constant.SOLID_COLOR : forum.getForum_thumbnail();

        Picasso.get()
                .load(URL)
                .resize(300, 180)
                .centerCrop()
                .transform(transformation)
                .into(holder.image_thumbnail);

        holder.unhide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forumOnClick.onUnHide(forum.getFid());
            }
        });

        holder.rippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (merchantMap.get(forum.getMid()) != null)
//                    forumOnClick.onClick(forum, merchantMap.get(forum.getMid()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return forumList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        RippleView rippleView;
        RoundedImageView image_profile;
        ImageView image_thumbnail;
        TextView username, time, unhide, forum_title, forum_content, count_view, count_smile;

        Holder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.recycler_profile_hidden_forum);
            image_thumbnail = itemView.findViewById(R.id.recycler_image_thumbnail_hidden_forum);
            username = itemView.findViewById(R.id.recycler_text_username_hidden_forum);
            time = itemView.findViewById(R.id.recycler_time_hidden_forum);
            unhide = itemView.findViewById(R.id.recycler_text_hide_hidden_forum);
            forum_title = itemView.findViewById(R.id.recycler_title_hidden_forum);
            forum_content = itemView.findViewById(R.id.recycler_content_hidden_forum);
            count_view = itemView.findViewById(R.id.recycler_view_count_hidden_forum);
            count_smile = itemView.findViewById(R.id.recycler_smile_hidden_forum);
            rippleView = itemView.findViewById(R.id.ripple_hidden_forum);
        }
    }
}
