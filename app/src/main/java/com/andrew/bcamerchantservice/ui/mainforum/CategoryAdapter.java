package com.andrew.bcamerchantservice.ui.mainforum;

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
    private List<Forum.ForumCategory> categoryList;
    private int position;
    private onCategoryClick onCategoryClick;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public interface onCategoryClick {
        void onClickCategory(int pos);
    }

    public void setCategoryList(List<Forum.ForumCategory> categoryList) {
        this.categoryList = categoryList;
    }

    public CategoryAdapter(Context mContext, List<Forum.ForumCategory> categoryList, CategoryAdapter.onCategoryClick onCategoryClick) {
        this.mContext = mContext;
        this.categoryList = categoryList;
        this.onCategoryClick = onCategoryClick;
        position = 0;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_category, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final int position = holder.getAdapterPosition();
        if (position == 0) {
            Picasso.get()
                    .load(R.drawable.ic_globe)
                    .into(holder.imageView);
            holder.text_name.setText("All Category");
        } else {
            Forum.ForumCategory category = categoryList.get(position - 1);
            Picasso.get()
                    .load(category.getCategory_url())
                    .into(holder.imageView);
            holder.text_name.setText(category.getCategory_name());
        }

        if (this.position == position)
            holder.relative.setBackground(mContext.getDrawable(R.drawable.rectangle_rounded_stroke_blue_fill_iron));
        else
            holder.relative.setBackground(mContext.getDrawable(R.drawable.rectangle_rounded_stroke_blue));

        holder.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategoryClick.onClickCategory(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size() + 1;
    }

    class Holder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        TextView text_name;
        RelativeLayout relative;

        Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recycler_img_category);
            text_name = itemView.findViewById(R.id.recycler_name_category);
            relative = itemView.findViewById(R.id.recycler_relative_category);
        }
    }
}
