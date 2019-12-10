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
    private int lastPosition, MAX_CATEGORY;

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
        MAX_CATEGORY = 6;
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
        final int pos = holder.getAdapterPosition();


        if (MAX_CATEGORY > 7) {
            /*
             * I will think about it later, but it should show more option,
             * and when user click more, it will pop up a bottom navigation
             * then it will show all of category list like Gojek apps
             * */
            if (pos == 8) {
                // here to add less
                holder.text_more.setVisibility(View.VISIBLE);
                holder.image_category.setVisibility(View.GONE);
                holder.text_name.setVisibility(View.GONE);
                holder.text_more.setText("Less");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MAX_CATEGORY = 6;
                        notifyDataSetChanged();
                    }
                });
            } else {
                if (lastPosition == pos) {
                    holder.relativeLayout.setBackground(mContext.getDrawable(R.drawable.rectangle_rounded_stroke_blue_fill_iron));
                } else {
                    holder.relativeLayout.setBackground(mContext.getDrawable(R.drawable.rectangle_rounded_stroke_blue));
                }
                holder.text_more.setVisibility(View.GONE);
                holder.image_category.setVisibility(View.VISIBLE);
                holder.text_name.setVisibility(View.VISIBLE);
                final Forum.ForumCategory category = forumCategoryList.get(i);
                holder.text_name.setText(category.getCategory_name());

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
            }
        } else {
            if (pos == 5) {
                // here to add more
                holder.text_more.setText("More");
                holder.text_more.setVisibility(View.VISIBLE);
                holder.image_category.setVisibility(View.GONE);
                holder.text_name.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MAX_CATEGORY = forumCategoryList.size() + 1;
                        notifyDataSetChanged();
                    }
                });
            } else {
                if (lastPosition == pos) {
                    holder.relativeLayout.setBackground(mContext.getDrawable(R.drawable.rectangle_rounded_stroke_blue_fill_iron));
                } else {
                    holder.relativeLayout.setBackground(mContext.getDrawable(R.drawable.rectangle_rounded_stroke_blue));
                }
                holder.text_more.setVisibility(View.GONE);
                holder.image_category.setVisibility(View.VISIBLE);
                holder.text_name.setVisibility(View.VISIBLE);
                final Forum.ForumCategory category = forumCategoryList.get(i);
                holder.text_name.setText(category.getCategory_name());

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
            }
        }
    }


    @Override
    public int getItemCount() {
        if (forumCategoryList.size() == 0)
            return 0;
        return MAX_CATEGORY;
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView text_name, text_more;
        private RoundedImageView image_category;
        private RelativeLayout relativeLayout;

        Holder(@NonNull View itemView) {
            super(itemView);
            text_name = itemView.findViewById(R.id.recycler_name_category);
            image_category = itemView.findViewById(R.id.recycler_img_category);
            relativeLayout = itemView.findViewById(R.id.recycler_relative_category);
            text_more = itemView.findViewById(R.id.recycler_text_more_category);
        }
    }

    public void setForumCategoryList(List<Forum.ForumCategory> forumCategoryList) {
        this.forumCategoryList = forumCategoryList;
    }
}
