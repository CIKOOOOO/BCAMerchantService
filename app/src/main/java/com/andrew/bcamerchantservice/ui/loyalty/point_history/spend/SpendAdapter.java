package com.andrew.bcamerchantservice.ui.loyalty.point_history.spend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpendAdapter extends RecyclerView.Adapter<SpendAdapter.Holder> {

    private Context mContext;
    private List<Loyalty.Spend> spendList;
    private Map<String, Loyalty.Rewards> rewardsMap;

    public SpendAdapter(Context mContext) {
        this.mContext = mContext;
        spendList = new ArrayList<>();
        rewardsMap = new HashMap<>();
    }

    public void setSpendList(List<Loyalty.Spend> spendList) {
        this.spendList = spendList;
    }

    public void setRewardsMap(Map<String, Loyalty.Rewards> rewardsMap) {
        this.rewardsMap = rewardsMap;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        if (i == 0)
            v = LayoutInflater.from(mContext).inflate(R.layout.recycler_parent_spend, viewGroup, false);
        else
            v = LayoutInflater.from(mContext).inflate(R.layout.recycler_child_spend, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return spendList.get(position).getSpend_id().equals("parent") ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final int pos = holder.getAdapterPosition();
        final Loyalty.Spend spend = spendList.get(pos);

        if (getItemViewType(pos) == 1) {
            if (rewardsMap.get(spend.getRewards_id()) != null) {
                Loyalty.Rewards rewards = rewardsMap.get(spend.getRewards_id());
                holder.text_title.setText(rewards.getRewards_name());
            }
            holder.text_point.setText(spend.getSpend_point() + " Points");
            if (pos == spendList.size() - 1) {
                holder.line.setVisibility(View.GONE);
            } else if (pos + 1 < spendList.size() - 1 && spendList.get(pos + 1).getSpend_id().equals("parent")) {
                holder.line.setVisibility(View.GONE);
            } else {
                holder.line.setVisibility(View.VISIBLE);
            }
        } else {
            try {
                String date = spend.getSpend_date().equals(Utils.getTime("dd/MM/yyyy")) ? "Today" : Utils.formatDateFromDateString("dd/MM/yyyy", "EEEE, dd MMM yyyy", spend.getSpend_date());
                holder.text_parent.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return spendList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView text_parent, text_title, text_spend_type, text_point;
        View line;

        Holder(@NonNull View itemView) {
            super(itemView);
            text_parent = itemView.findViewById(R.id.recycler_date_parent_spend);
            text_title = itemView.findViewById(R.id.recycler_text_title_spend);
            text_point = itemView.findViewById(R.id.recycler_text_point_spend);
            line = itemView.findViewById(R.id.recycler_line_spend);
        }
    }
}
