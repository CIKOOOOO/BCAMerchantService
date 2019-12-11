package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.PromoRequest;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PromoTypeAdapter extends RecyclerView.Adapter<PromoTypeAdapter.Holder> {

    private Context mContext;
    private onClick onClick;
    private List<PromoRequest.PromoType> promoTypeList;
    private int chosenPosition;

    public PromoTypeAdapter(Context mContext, onClick onClick) {
        this.mContext = mContext;
        this.onClick = onClick;
        promoTypeList = new ArrayList<>();
        chosenPosition = -1;
    }

    public void setChosenPosition(int chosenPosition) {
        this.chosenPosition = chosenPosition;
    }

    public int getChosenPosition() {
        return chosenPosition;
    }

    public void setPromoTypeList(List<PromoRequest.PromoType> promoTypeList) {
        this.promoTypeList = promoTypeList;
    }

    public interface onClick {
        void onPromoClick(PromoRequest.PromoType promoType);
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
        final PromoRequest.PromoType promoType = promoTypeList.get(pos);

        Drawable drawable = chosenPosition == pos ?
                mContext.getDrawable(R.drawable.rectangle_rounded_fill_blue) : mContext.getDrawable(R.drawable.rectangle_rounded_stroke_blue);

        holder.relativeLayout.setBackground(drawable);
        holder.text_name.setText(promoType.getPromo_name());
        Picasso.get()
                .load(promoType.getPromo_image())
                .into(holder.image_category);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onPromoClick(promoType);
                notifyItemChanged(chosenPosition);
                chosenPosition = pos;
                notifyItemChanged(chosenPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return promoTypeList.size();
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
}
