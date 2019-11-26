package com.andrew.bcamerchantservice.ui.loyalty;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.bumptech.glide.Glide;

public class LoyaltyBenefitAdapter extends RecyclerView.Adapter<LoyaltyBenefitAdapter.Holder> {

    private String[] loyaltyList;
    private String loyaltyId;
    private Context mContext;

    public void setLoyaltyArray(String[] loyaltyList, String loyaltyId) {
        this.loyaltyList = loyaltyList;
        this.loyaltyId = loyaltyId;
    }

    public LoyaltyBenefitAdapter(Context mContext) {
        this.mContext = mContext;
        loyaltyList = new String[0];
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_benefit_loyalty, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        int drawable = 0;
        switch (loyaltyId) {
            case "loyalty_type_1":
                drawable = R.drawable.circle_fill_bronze;
                break;
            case "loyalty_type_2":
                drawable = R.drawable.circle_fill_silver;
                break;
            case "loyalty_type_3":
                drawable = R.drawable.circler_fill_gold;
                break;
        }

        Glide.with(mContext)
                .load(drawable)
                .into(holder.image);
        holder.text.setText(loyaltyList[i]);
    }

    @Override
    public int getItemCount() {
        return loyaltyList.length;
    }

    class Holder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView text;

        Holder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.recycler_image_benefit_loyalty);
            text = itemView.findViewById(R.id.recycler_text_benefit_loyalty);
        }
    }
}
