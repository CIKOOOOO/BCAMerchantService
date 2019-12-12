package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.logo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class LogoRequestAdapter extends RecyclerView.Adapter<LogoRequestAdapter.Holder> {

    private Context mContext;
    private List<ImagePicker> imagePickerList;
    private onClick onClick;

    public LogoRequestAdapter(Context mContext, LogoRequestAdapter.onClick onClick) {
        this.mContext = mContext;
        this.onClick = onClick;
        imagePickerList = new ArrayList<>();
    }

    public void setImagePickerList(List<ImagePicker> imagePickerList) {
        this.imagePickerList = imagePickerList;
    }

    public interface onClick {
        void onImageClick();

        void onImageDelete(int pos);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_logo_request, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.image_logo.setImageResource(0);
        if (imagePickerList.size() == 0) {
            holder.image_gallery.setVisibility(View.VISIBLE);
            holder.textView.setVisibility(View.GONE);
            holder.image_gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.onImageClick();
                }
            });
        } else {
            holder.image_gallery.setVisibility(View.GONE);
            holder.textView.setVisibility(View.VISIBLE);
            final int pos = holder.getAdapterPosition();
            ImagePicker imagePicker = imagePickerList.get(pos);

            Glide.with(mContext)
                    .load(imagePicker.getImage_bitmap())
                    .into(holder.image_logo);

            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.onImageDelete(pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imagePickerList.size() == 0 ? 1 : imagePickerList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView image_logo;
        ImageButton image_gallery;

        Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.recycler_text_delete_logo_request);
            image_gallery = itemView.findViewById(R.id.recycler_gallery_logo_request);
            image_logo = itemView.findViewById(R.id.recycler_image_logo_request);
        }
    }
}
