package com.andrew.bcamerchantservice.ui.loyalty.rewards.allrewards;

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
import com.andrew.bcamerchantservice.model.Loyalty;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AllRewardsAdapter extends RecyclerView.Adapter<AllRewardsAdapter.Holder> {

    private List<Loyalty.Rewards> rewardsList;
    private Context mContext;
    private onClick onClick;
    private Boolean isRankEnough;
    private int myPoint;

    public AllRewardsAdapter(Context mContext, int myPoint, boolean isRankEnough, AllRewardsAdapter.onClick onClick) {
        rewardsList = new ArrayList<>();
        this.mContext = mContext;
        this.myPoint = myPoint;
        this.isRankEnough = isRankEnough;
        this.onClick = onClick;
    }

    public interface onClick {
        void onRewardsClick(Loyalty.Rewards rewards);
    }

    public void setRankEnough(Boolean rankEnough) {
        isRankEnough = rankEnough;
    }

    public void setRewardsList(List<Loyalty.Rewards> rewardsList) {
        this.rewardsList = rewardsList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        if (i == 1)
            v = LayoutInflater.from(mContext).inflate(R.layout.recycler_rewards, viewGroup, false);
        else
            v = LayoutInflater.from(mContext).inflate(R.layout.custom_loading, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        if (getItemViewType(i) == 1) {
            final int pos = holder.getAdapterPosition();
            final Loyalty.Rewards rewards = rewardsList.get(pos);

            holder.text_point.setText(rewards.getRewards_point() + " Points");
            holder.text_title.setText(rewards.getRewards_name());

            if (!rewards.getRewards_thumbnail().isEmpty())
                Picasso.get()
                        .load(rewards.getRewards_thumbnail())
                        .into(holder.thumbnail_image);
            else
                Picasso.get()
                        .load(R.color.iron_palette)
                        .into(holder.thumbnail_image);

            String redeem_status;
            int text_color;
            Drawable drawable_status;

            if (myPoint >= rewards.getRewards_point()) {
                redeem_status = "Redeem";
                text_color = mContext.getResources().getColor(R.color.white_color);
                drawable_status = mContext.getDrawable(R.drawable.rectangle_fill_blue);
            } else {
                redeem_status = "Point not enough";
                text_color = mContext.getResources().getColor(R.color.iron_palette);
                drawable_status = mContext.getDrawable(R.drawable.rectangle_rounded_stroke_iron);
            }

            holder.text_redeem.setText(redeem_status);
            holder.text_redeem.setTextColor(text_color);
            holder.text_redeem.setBackground(drawable_status);

            holder.relativeLayout.setVisibility(View.GONE);
            holder.text_locked.setVisibility(View.GONE);

            if (isRankEnough) {
                holder.text_redeem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClick.onRewardsClick(rewards);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClick.onRewardsClick(rewards);
                    }
                });
            } else {
                holder.relativeLayout.setVisibility(View.VISIBLE);
                holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                holder.text_locked.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return rewardsList.size() == 0 ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return rewardsList.size() == 0 ? 1 : rewardsList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        RoundedImageView thumbnail_image;
        TextView text_title, text_point, text_redeem, text_locked;
        RelativeLayout relativeLayout;

        Holder(@NonNull View itemView) {
            super(itemView);
            thumbnail_image = itemView.findViewById(R.id.recycler_image_thumbnail_rewards);
            text_title = itemView.findViewById(R.id.recycler_text_title_rewards);
            text_point = itemView.findViewById(R.id.recycler_text_point_rewards);
            text_redeem = itemView.findViewById(R.id.recycler_text_redeem_rewards);
            relativeLayout = itemView.findViewById(R.id.recycler_frame_rewards);
            text_locked = itemView.findViewById(R.id.recycler_text_lock_rewards);
        }
    }
}
