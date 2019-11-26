package com.andrew.bcamerchantservice.ui.loyalty;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Loyalty;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LoyaltyAdapter extends RecyclerView.Adapter<LoyaltyAdapter.Holder> {

    private Context mContext;
    private List<Loyalty> loyaltyList;
    private String loyalty_id;
    private onItemClick onItemClick;
    private int lastPosition, myPosition;

    public interface onItemClick {
        void onClick(Loyalty loyalty, boolean isMyLoyalty);
    }

    public void setLoyaltyList(List<Loyalty> loyaltyList) {
        this.loyaltyList = loyaltyList;
        for (int i = 0; i < loyaltyList.size(); i++) {
            if (loyaltyList.get(i).getLoyalty_id().equals(loyalty_id)) {
                myPosition = lastPosition = i;
                break;
            }
        }
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = this.myPosition = lastPosition;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public LoyaltyAdapter(Context mContext, List<Loyalty> loyaltyList, String loyalty_id, LoyaltyAdapter.onItemClick onItemClick) {
        this.mContext = mContext;
        this.loyaltyList = loyaltyList;
        this.loyalty_id = loyalty_id;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_rank_type_loyalty, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        final int pos = holder.getAdapterPosition();
        final Loyalty loyalty = loyaltyList.get(pos);

        if (pos <= myPosition)
            holder.image_lock.setVisibility(View.GONE);
        else
            holder.image_lock.setVisibility(View.VISIBLE);

        if (lastPosition == pos)
            holder.relative_chosen.setBackground(mContext.getDrawable(R.drawable.rectangle_stroke_blue));
        else
            holder.relative_chosen.setBackground(null);

        Picasso.get()
                .load(loyalty.getLoyalty_logo())
                .into(holder.image_rank);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isMyLoyalty = holder.image_lock.getVisibility() == View.GONE;
                onItemClick.onClick(loyalty, isMyLoyalty);
                lastPosition = pos;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return loyaltyList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView image_rank, image_lock;
        RelativeLayout relative_chosen;

        Holder(@NonNull View itemView) {
            super(itemView);
            image_rank = itemView.findViewById(R.id.recycler_rank_loyalty);
            image_lock = itemView.findViewById(R.id.recycler_rank_lock_loyalty);
            relative_chosen = itemView.findViewById(R.id.recycler_relative_chosen_loyalty);
        }
    }
}
