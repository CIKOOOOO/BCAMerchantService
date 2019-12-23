package com.andrew.bcamerchantservice.ui.mainforum.search;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;

public class SearchCategoryAdapter extends RecyclerView.Adapter<SearchCategoryAdapter.Holder> {

    private String[] category_array;
    private Context mContext;
    private int currentPosition;
    private onClick onClick;

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public interface onClick {
        void categoryOnClick(int pos);
    }

    public SearchCategoryAdapter(String[] category_array, Context mContext, SearchCategoryAdapter.onClick onClick) {
        this.category_array = category_array;
        this.mContext = mContext;
        this.onClick = onClick;
        currentPosition = 0;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_search_category, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final int pos = holder.getAdapterPosition();

        holder.text_category.setText(category_array[pos]);

        Drawable background_drawable = currentPosition == pos ? mContext.getDrawable(R.drawable.rectangle_rounded_fill_blue_5dp)
                : mContext.getDrawable(R.drawable.rectangle_rounded_stroke_blue_5dp);
        int color = currentPosition == pos ? mContext.getResources().getColor(R.color.white_color)
                : mContext.getResources().getColor(R.color.blue_palette);

        holder.text_category.setBackground(background_drawable);
        holder.text_category.setTextColor(color);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPosition = pos;
                onClick.categoryOnClick(pos);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return category_array.length;
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView text_category;

        Holder(@NonNull View itemView) {
            super(itemView);
            text_category = itemView.findViewById(R.id.recycler_text_category_search);
        }
    }

}
