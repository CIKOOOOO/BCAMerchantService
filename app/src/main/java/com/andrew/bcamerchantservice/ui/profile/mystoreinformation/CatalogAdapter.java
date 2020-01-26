package com.andrew.bcamerchantservice.ui.profile.mystoreinformation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.Holder> {

    private Context mContext;
    private boolean isMyCatalog;
    private List<Merchant.MerchantCatalog> catalogList;
    private onItemClick onItemClick;
    private int lastPosition, linear_height;

    public CatalogAdapter(Context mContext, boolean isMyCatalog, onItemClick onItemClick) {
        this.mContext = mContext;
        this.isMyCatalog = isMyCatalog;
        this.onItemClick = onItemClick;
        catalogList = new ArrayList<>();
        lastPosition = -1;
    }

    public void setCatalogList(List<Merchant.MerchantCatalog> catalogList) {
        this.catalogList = catalogList;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    public interface onItemClick {
        void onClick(Merchant.MerchantCatalog merchantCatalog);

        void onDelete(Merchant.MerchantCatalog merchantCatalog, int pos);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_catalog, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        final int pos = holder.getAdapterPosition();
        final Merchant.MerchantCatalog merchantCatalog = catalogList.get(pos);

        holder.text_price.setText("Rp " + Utils.priceFormat(merchantCatalog.getCatalog_price()));
        holder.text_description.setText(merchantCatalog.getCatalog_description());
        holder.text_name.setText(merchantCatalog.getCatalog_name());

        Picasso.get()
                .load(merchantCatalog.getCatalog_image())
                .into(holder.image_stuff);

        if (pos != lastPosition) {
            holder.linearLayout.setVisibility(View.GONE);
        } else {
            holder.linearLayout.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = holder.linearLayout.getLayoutParams();
            layoutParams.height = linear_height;
            holder.linearLayout.setLayoutParams(layoutParams);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onClick(merchantCatalog);
            }
        });

        if (isMyCatalog) {
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int tempLast = lastPosition;
                    lastPosition = -1;
                    notifyItemChanged(tempLast);
                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onDelete(merchantCatalog, pos);
                }
            });

//            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    if (lastPosition == pos)
//                        return true;
//
//                    if (lastPosition != -1) {
//                        int tempLast = lastPosition;
//                        notifyItemChanged(tempLast);
//                    }
//
//                    lastPosition = pos;
//                    linear_height = holder.itemView.getHeight();
//                    notifyItemChanged(lastPosition);
//                    return true;
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return catalogList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView text_name, text_description, text_price;
        ImageView image_stuff;
        LinearLayout linearLayout;
        Button cancel, delete;

        Holder(@NonNull View itemView) {
            super(itemView);
            text_name = itemView.findViewById(R.id.recycler_text_title_catalog);
            text_description = itemView.findViewById(R.id.recycler_text_description_catalog);
            text_price = itemView.findViewById(R.id.recycler_text_price_catalog);
            image_stuff = itemView.findViewById(R.id.recycler_image_catalog);
            linearLayout = itemView.findViewById(R.id.recycler_linear_catalog);
            cancel = itemView.findViewById(R.id.recycler_button_cancel_catalog);
            delete = itemView.findViewById(R.id.recycler_button_delete_catalog);
        }
    }
}
