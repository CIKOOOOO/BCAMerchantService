package com.andrew.bcamerchantservice.ui.tabpromorequest.detailpromorequest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.PromoRequest;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LogoAdapter  extends RecyclerView.Adapter<LogoAdapter.Holder> {
    private Context mContext;
    private List<PromoRequest.Logo> imagePickerList;

    public LogoAdapter(Context mContext, List<PromoRequest.Logo> imagePickerList) {
        this.mContext = mContext;
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
        PromoRequest.Logo imagePicker = imagePickerList.get(i);
        Picasso.get()
                .load(imagePicker.getMerchant_logo_url())
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
