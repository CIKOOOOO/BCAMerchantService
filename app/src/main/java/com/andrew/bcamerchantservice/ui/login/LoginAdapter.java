package com.andrew.bcamerchantservice.ui.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Merchant;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LoginAdapter extends RecyclerView.Adapter<LoginAdapter.Holder> {
    private Context mContext;
    private List<Merchant> merchantList;
    private onClickItem onClickItem;

    public LoginAdapter(Context mContext, List<Merchant> merchantList, LoginAdapter.onClickItem onClickItem) {
        this.mContext = mContext;
        this.merchantList = merchantList;
        this.onClickItem = onClickItem;
    }

    public void setMerchantList(List<Merchant> merchantList) {
        this.merchantList = merchantList;
    }

    public interface onClickItem {
        void onClick(Merchant merchant);
    }

    @Override
    public int getItemViewType(int position) {
        return merchantList.size() == 0 ? 0 : 1;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        if (i == 1)
            v = LayoutInflater.from(mContext).inflate(R.layout.recycler_merchant_list, viewGroup, false);
        else
            v = LayoutInflater.from(mContext).inflate(R.layout.custom_loading, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        if (getItemViewType(i) == 1) {
            final Merchant merchant = merchantList.get(i);

            Picasso.get()
                    .load(merchant.getMerchant_profile_picture())
                    .into(holder.imageView);

            holder.text_address.setText(" : " + merchant.getMerchant_address());
            holder.text_merchant_name.setText(" : " + merchant.getMerchant_name());
            holder.text_name.setText(" : " + merchant.getMerchant_owner_name());
            holder.text_mid.setText(" : " + merchant.getMid());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickItem.onClick(merchant);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return merchantList.size() == 0 ? 1 : merchantList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        TextView text_mid, text_name, text_merchant_name, text_address;

        Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recycler_picture_merchant_list);
            text_mid = itemView.findViewById(R.id.recycler_mid_merchant_list);
            text_name = itemView.findViewById(R.id.recycler_owner_name_merchant_list);
            text_merchant_name = itemView.findViewById(R.id.recycler_name_merchant_list);
            text_address = itemView.findViewById(R.id.recycler_address_merchant_list);
        }
    }
}
