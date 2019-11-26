package com.andrew.bcamerchantservice.ui.loyalty.rewards.myrewards;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.model.Merchant;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRewardsAdapter extends RecyclerView.Adapter<MyRewardsAdapter.Holder> {

    private Context mContext;
    private Map<String, Loyalty.Rewards> rewardsMap;
    private List<Merchant.Rewards> merchantRewardList;
    private onClick onClick;

    public void setRewardsMap(Map<String, Loyalty.Rewards> rewardsMap) {
        this.rewardsMap = rewardsMap;
    }

    public void setMerchantRewardList(List<Merchant.Rewards> merchantRewardList) {
        this.merchantRewardList = merchantRewardList;
    }

    public MyRewardsAdapter(Context mContext, MyRewardsAdapter.onClick onClick) {
        this.mContext = mContext;
        this.onClick = onClick;
        rewardsMap = new HashMap<>();
        merchantRewardList = new ArrayList<>();
    }

    public interface onClick {
        void onItemClick(Merchant.Rewards merchant_rewards, Loyalty.Rewards loyalty_rewards);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_rewards, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final int pos = holder.getAdapterPosition();

        if (merchantRewardList.size() > 0) {
            final Merchant.Rewards merchant_rewards = merchantRewardList.get(pos);
            String status;
            int text_color;
            Drawable text_drawable;

            if (merchant_rewards.isRewards_is_used()) {
                status = "Used";
                text_color = mContext.getResources().getColor(R.color.silver_palette);
                text_drawable = mContext.getDrawable(R.drawable.rectangle_rounded_stroke_iron);
            } else {
                status = "Use";
                text_color = mContext.getResources().getColor(R.color.white_color);
                text_drawable = mContext.getDrawable(R.drawable.background_reply);
            }

            holder.text_use_rewards.setText(status);
            holder.text_use_rewards.setTextColor(text_color);
            holder.text_use_rewards.setBackground(text_drawable);

            if (rewardsMap.size() > 0) {
                final Loyalty.Rewards loyalty_rewards = rewardsMap.get(merchant_rewards.getRewards_id());
                if (loyalty_rewards.getRewards_thumbnail().isEmpty())
                    Picasso.get()
                            .load(R.color.iron_palette)
                            .into(holder.rounded_image);
                else
                    Picasso.get()
                            .load(loyalty_rewards.getRewards_thumbnail())
                            .into(holder.rounded_image);

                holder.text_point.setText(loyalty_rewards.getRewards_point() + " Points");
                holder.text_title.setText(loyalty_rewards.getRewards_name());
            }
        }

        if (merchantRewardList.size() > 0 && rewardsMap.size() > 0)
            holder.text_use_rewards.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.onItemClick(merchantRewardList.get(pos), rewardsMap.get(merchantRewardList.get(pos).getRewards_id()));
                }
            });
    }

    @Override
    public int getItemCount() {
        return merchantRewardList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        RoundedImageView rounded_image;
        TextView text_title, text_point, text_use_rewards;

        Holder(@NonNull View itemView) {
            super(itemView);
            rounded_image = itemView.findViewById(R.id.recycler_image_thumbnail_rewards);
            text_title = itemView.findViewById(R.id.recycler_text_title_rewards);
            text_point = itemView.findViewById(R.id.recycler_text_point_rewards);
            text_use_rewards = itemView.findViewById(R.id.recycler_text_redeem_rewards);
        }
    }
}
