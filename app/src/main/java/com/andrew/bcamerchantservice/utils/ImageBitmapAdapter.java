package com.andrew.bcamerchantservice.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.bumptech.glide.Glide;

import java.util.List;

public class ImageBitmapAdapter extends RecyclerView.Adapter<ImageBitmapAdapter.Holder> {

    private Context mContext;
    private List<ImagePicker> imagePickerList;

    public ImageBitmapAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setImagePickerList(List<ImagePicker> imagePickerList) {
        this.imagePickerList = imagePickerList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_image_only, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        ImagePicker imagePicker = imagePickerList.get(i);
        Glide.with(mContext)
                .load(imagePicker.getImage_bitmap())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imagePickerList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView imageView;

        Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recycler_image_only);
        }
    }
}

