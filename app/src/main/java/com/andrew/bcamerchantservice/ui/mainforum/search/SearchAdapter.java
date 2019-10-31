package com.andrew.bcamerchantservice.ui.mainforum.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Holder> {

    private Context mContext;
    private List<Forum> forumList;
    private Map<String, Merchant> merchantMap;
    private onClickListener onClickListener;
    private Transformation transformation;

    public void setMerchantMap(Map<String, Merchant> merchantMap) {
        this.merchantMap = merchantMap;
    }

    public void setForumList(List<Forum> forumList) {
        this.forumList = forumList;
    }

    public interface onClickListener {
        void onSearchClick(Forum forum, Merchant merchant);
    }

    public SearchAdapter(Context mContext, List<Forum> forumList, Map<String, Merchant> merchantMap, SearchAdapter.onClickListener onClickListener) {
        this.mContext = mContext;
        this.forumList = forumList;
        this.merchantMap = merchantMap;
        this.onClickListener = onClickListener;
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
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_search_fragment, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final Forum forum = forumList.get(i);
        if (merchantMap != null) {
            if (merchantMap.containsKey(forum.getMid())) {
                Merchant merchant = merchantMap.get(forum.getMid());
                holder.text_writer.setText("Written by : " + merchant.getMerchant_name());
            }
        }
        if (forum.getForum_thumbnail().isEmpty()) {
            Picasso.get()
                    .load(Constant.SOLID_COLOR)
                    .resize(300, 180)
                    .centerCrop()
                    .transform(transformation)
                    .into(holder.image_thumbnail);
        } else
            Picasso.get()
                    .load(forum.getForum_thumbnail())
                    .resize(300, 180)
                    .centerCrop()
                    .transform(transformation)
                    .into(holder.image_thumbnail);

        holder.text_like.setText(forum.getForum_like() + "");
        holder.text_view_count.setText(forum.getView_count() + "");
        holder.text_title.setText(forum.getForum_title());
        try {
            holder.text_date.setText("Posted on : " + Utils.formatDateFromDateString("EEEE, dd/MM/yyyy HH:mm", "dd-MM-yyyy", forum.getForum_date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (merchantMap.get(forum.getMid()) != null) {
                    onClickListener.onSearchClick(forum, merchantMap.get(forum.getMid()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return forumList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private ImageView image_thumbnail;
        private TextView text_title, text_writer, text_date, text_view_count, text_like;

        public Holder(@NonNull View itemView) {
            super(itemView);
            image_thumbnail = itemView.findViewById(R.id.recycler_image_thumbnail_search_fragment);
            text_title = itemView.findViewById(R.id.recycler_title_search_fragment);
            text_writer = itemView.findViewById(R.id.recycler_writer_search_fragment);
            text_date = itemView.findViewById(R.id.recycler_date_search_fragment);
            text_view_count = itemView.findViewById(R.id.recycler_view_count_search_fragment);
            text_like = itemView.findViewById(R.id.recycler_like_search_fragment);
        }
    }
}
