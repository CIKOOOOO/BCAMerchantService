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
import com.andrew.bcamerchantservice.model.Merchant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchProfileAdapter extends RecyclerView.Adapter<SearchProfileAdapter.Holder> {

    private List<Merchant> merchantList;
    private Context mContext;
    private onClick onClick;

    public SearchProfileAdapter(Context mContext, SearchProfileAdapter.onClick onClick) {
        this.mContext = mContext;
        this.onClick = onClick;
        merchantList = new ArrayList<>();
    }

    public void setMerchantList(List<Merchant> merchantList) {
        this.merchantList = merchantList;
    }

    public interface onClick {
        void profileOnClick(Merchant merchant);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_profile_search, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        final int pos = holder.getAdapterPosition();
        final Merchant merchant = merchantList.get(pos);
        Picasso.get()
                .load(merchant.getMerchant_profile_picture())
                .into(holder.image_profile);
        holder.text_description.setText(merchant.getMerchant_description());
        holder.text_name.setText(merchant.getMerchant_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.profileOnClick(merchant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return merchantList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView text_name, text_description;
        ImageView image_profile;

        Holder(@NonNull View itemView) {
            super(itemView);
            text_name = itemView.findViewById(R.id.recycler_text_name_search);
            text_description = itemView.findViewById(R.id.recycler_text_description_search);
            image_profile = itemView.findViewById(R.id.recycler_image_profile_search);
        }
    }
}
