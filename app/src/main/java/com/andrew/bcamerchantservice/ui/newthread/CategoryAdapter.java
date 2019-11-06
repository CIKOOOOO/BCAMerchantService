package com.andrew.bcamerchantservice.ui.newthread;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Holder> {

    private Context mContext;
    private List<Forum.ForumCategory> forumCategoryList;
    private onCategoryClick onCategoryClick;
    private int lastPosition;

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public CategoryAdapter(Context mContext, List<Forum.ForumCategory> forumCategoryList, CategoryAdapter.onCategoryClick onCategoryClick) {
        this.mContext = mContext;
        this.forumCategoryList = forumCategoryList;
        this.onCategoryClick = onCategoryClick;
        lastPosition = -1;
    }

    public interface onCategoryClick {
        void onClick(Forum.ForumCategory forumCategory, int i);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_category, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final Forum.ForumCategory category = forumCategoryList.get(i);
        final int pos = holder.getAdapterPosition();

        holder.text_name.setText(category.getCategory_name());

        if (lastPosition == pos) {
            holder.relativeLayout.setBackground(mContext.getDrawable(R.drawable.rectangle_rounded_stroke_blue_fill));
        } else {
            holder.relativeLayout.setBackground(mContext.getDrawable(R.drawable.rectangle_rounded_stroke_blue));
        }

        Picasso.get()
                .load(category.getCategory_url())
                .into(holder.image_category);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategoryClick.onClick(category, pos);
                notifyItemChanged(lastPosition);
                lastPosition = pos;
                notifyItemChanged(pos);
            }
        });

        if (forumCategoryList.size() > 6) {
            /*
             * I will think about it later, but it should show more option,
             * and when user click more, it will pop up a bottom navigation
             * then it will show all of category list like Gojek apps
             * */
            if (pos == 5) {
                // here to add more
            }
        }
    }


    @Override
    public int getItemCount() {
        return forumCategoryList.size() > 5 ? 6 : forumCategoryList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView text_name;
        private RoundedImageView image_category;
        private RelativeLayout relativeLayout;

        Holder(@NonNull View itemView) {
            super(itemView);
            text_name = itemView.findViewById(R.id.recycler_name_category);
            image_category = itemView.findViewById(R.id.recycler_img_category);
            relativeLayout = itemView.findViewById(R.id.recycler_relative_category);
        }
    }

    public void setForumCategoryList(List<Forum.ForumCategory> forumCategoryList) {
        this.forumCategoryList = forumCategoryList;
    }
}
