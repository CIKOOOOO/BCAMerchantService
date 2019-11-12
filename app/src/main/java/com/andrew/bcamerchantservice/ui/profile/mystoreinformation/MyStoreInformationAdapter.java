package com.andrew.bcamerchantservice.ui.profile.mystoreinformation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.andrew.bcamerchantservice.model.Merchant;

import java.util.List;

public class MyStoreInformationAdapter extends RecyclerView.Adapter<MyStoreInformationAdapter.Holder> {

    private Context mContext;
    private List<Merchant.MerchantCatalog> catalogList;

    public MyStoreInformationAdapter(Context mContext, List<Merchant.MerchantCatalog> catalogList) {
        this.mContext = mContext;
        this.catalogList = catalogList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {

    }

    @Override
    public int getItemCount() {
        return catalogList.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        public Holder(@NonNull View itemView) {
            super(itemView);


        }
    }
}
